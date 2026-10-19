package com.detective.herramientas;

import com.detective.app.ContenedorAplicacion;
import com.detective.domain.investigacion.AnalizarEvidencia;
import com.detective.domain.investigacion.ConfrontarSospechoso;
import com.detective.domain.investigacion.InspeccionarLugar;
import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.investigacion.InterrogarSospechoso;
import com.detective.domain.investigacion.RecolectarEvidencia;
import com.detective.domain.model.Caso;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.Lugar;
import com.detective.domain.model.Sospechoso;
import com.detective.domain.model.Veredicto;
import com.detective.servicio.ResultadoJuicio;
import com.detective.servicio.ServicioDeduccion;
import com.detective.servicio.ServicioFiscal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Herramienta de control de calidad del CONTENIDO: juega cada caso completo de forma
 * automática y verifica que sea resoluble y que no haya inocentes "culpables" por error.
 * Uso: java -cp ExpedientesDetective.jar com.detective.herramientas.VerificadorCasos
 */
public final class VerificadorCasos {

    public static void main(String[] args) throws Exception {
        Path temporal = Files.createTempFile("verificador", ".db");
        int errores = 0;
        try (ContenedorAplicacion app = new ContenedorAplicacion(temporal)) {
            ServicioDeduccion deduccion = app.investigacion().deduccion();
            ServicioFiscal fiscal = new ServicioFiscal(deduccion);
            java.util.List<Caso> casos = app.casos().listarCasos();
            long inicio = System.currentTimeMillis();
            int n = 0;
            for (Caso caso : casos) {
                errores += verificar(app, deduccion, fiscal, caso, args.length > 0 && args[0].equals("-v"));
                if (++n % 100 == 0) {
                    System.out.println("  ... " + n + " / " + casos.size() + " casos verificados");
                }
            }
            System.out.println("Casos verificados: " + casos.size() + " en "
                    + (System.currentTimeMillis() - inicio) / 1000 + " s");
        } finally {
            Files.deleteIfExists(temporal);
        }
        System.out.println(errores == 0 ? "\nTODOS LOS CASOS SON CONSISTENTES" : "\nERRORES: " + errores);
        System.exit(errores == 0 ? 0 : 1);
    }

    /** Ejecuta ignorando el límite de horas: se quiere probar el contenido completo. */
    private static void ejecutar(ContenedorAplicacion app, Investigacion inv,
                                 com.detective.domain.investigacion.AccionInvestigacion accion) {
        inv.getEstado().setHorasUsadas(0);
        app.investigacion().ejecutar(inv, accion);
    }

    private static int verificar(ContenedorAplicacion app, ServicioDeduccion deduccion, ServicioFiscal fiscal,
                                 Caso caso, boolean detallado) {
        StringBuilder log = new StringBuilder("== [" + caso.id() + "] " + caso.titulo() + "\n");
        int errores = 0;
        Investigacion inv = app.investigacion().iniciar("verificador", caso.id(), false);
        ExpedienteCaso exp = inv.getExpediente();
        int horasTotales = 0;
        for (Lugar l : exp.lugares()) {
            horasTotales += l.horasInspeccion();
            ejecutar(app, inv, new InspeccionarLugar(l.id()));
        }
        for (Evidencia e : exp.evidencias()) {
            ejecutar(app, inv, new RecolectarEvidencia(e.id(), true));
            horasTotales += RecolectarEvidencia.HORAS_PROTOCOLO;
        }
        for (Evidencia e : exp.evidencias()) {
            if (e.requiereAnalisis()) {
                ejecutar(app, inv, new AnalizarEvidencia(e.id()));
                horasTotales += e.horasAnalisis();
            }
        }
        for (Sospechoso s : exp.sospechosos()) {
            ejecutar(app, inv, new InterrogarSospechoso(s.id()));
            horasTotales += InterrogarSospechoso.HORAS;
        }
        for (Sospechoso s : exp.sospechosos()) {
            if (s.evidenciaParaConfrontar().isPresent()) {
                if (deduccion.puedeConfrontar(inv, s.id(), s.evidenciaConfrontacionId())) {
                    ejecutar(app, inv, new ConfrontarSospechoso(s.id()));
                    horasTotales += ConfrontarSospechoso.HORAS;
                } else {
                    log.append("  × No se puede confrontar a ").append(s.nombre()).append('\n');
                    errores++;
                }
            }
        }
        log.append("  Horas para hacer TODO: ").append(horasTotales).append(" / ").append(caso.horasMaximas()).append('\n');
        if (horasTotales <= caso.horasMaximas()) {
            log.append("  × El plazo alcanza para hacer todo: no obliga a elegir\n");
            errores++;
        }

        Set<String> principales = exp.sospechosos().stream()
                .filter(s -> deduccion.esSospechosoPrincipal(inv, s.id()))
                .map(Sospechoso::id).collect(Collectors.toSet());
        log.append("  Sospechosos principales: ").append(principales).append('\n');
        if (!principales.equals(Set.of(caso.culpableId()))) {
            log.append("  × Debería haber un único sospechoso principal: ").append(caso.culpableId()).append('\n');
            errores++;
        }
        Set<String> todas = new HashSet<>();
        exp.evidencias().forEach(e -> todas.add(e.id()));
        for (Sospechoso s : exp.sospechosos()) {
            ResultadoJuicio j = fiscal.evaluar(inv, s.id(), todas);
            Veredicto esperado = s.id().equals(caso.culpableId()) ? Veredicto.CONDENA : Veredicto.ERROR_JUDICIAL;
            String marca = j.veredicto() == esperado ? "√" : "×";
            if (j.veredicto() != esperado) {
                errores++;
            }
            log.append("  ").append(marca).append(" Acusar a ").append(s.nombre()).append(" -> ")
                    .append(j.veredicto()).append(" (pruebas válidas: ").append(j.pruebasValidas()).append(")\n");
        }
        if (detallado || errores > 0) {
            deduccion.deducciones(inv).forEach(d -> log.append("     · ").append(d.tipo().getEtiqueta())
                    .append(" [").append(d.sospechosoId()).append("] ").append(d.detalle()).append('\n'));
            System.out.print(log);
        }
        app.partidas().eliminar("verificador", caso.id());
        return errores;
    }
}
