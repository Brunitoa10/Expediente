package com.detective.servicio;

import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.model.Veredicto;
import com.detective.razonamiento.MotorRazonamiento;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Simula la evaluación del fiscal: la acusación debe sostenerse con prueba admisible
 * (cadena de custodia íntegra) que vincule al acusado, más motivo, medio y oportunidad.
 */
public final class ServicioFiscal {

    private final ServicioDeduccion deduccion;

    public ServicioFiscal(ServicioDeduccion deduccion) {
        this.deduccion = deduccion;
    }

    public ResultadoJuicio evaluar(Investigacion inv, String acusadoId, Set<String> pruebas) {
        String s = TextoProlog.atomo(acusadoId);
        MotorRazonamiento m = deduccion.motorConHechosExtra(inv, ServicioDeduccion.hechosPresentacion(pruebas));
        List<String> fundamentos = new ArrayList<>();

        fundamentos.add(linea(m.demostrar("motivo(" + s + ", _)"), "Motivo acreditado", "No se acreditó motivo"));
        fundamentos.add(linea(m.demostrar("medio(" + s + ", _)"), "Medio acreditado", "No se acreditó el medio"));
        fundamentos.add(linea(m.demostrar("oportunidad(" + s + ", _, _)"),
                "Oportunidad acreditada", "No se probó que estuviera en el lugar y momento del hecho"));
        if (m.demostrar("descartado(" + s + ", _)")) {
            fundamentos.add("× Su coartada fue verificada por fuentes independientes");
        }

        int validas = 0;
        for (String e : pruebas) {
            String nombre = deduccion.nombreDe(inv, e);
            boolean admisible = m.demostrar("prueba_admisible(" + e + ")");
            boolean vincula = m.demostrar("vinculo(" + e + ", " + s + ")");
            if (!admisible) {
                fundamentos.add("× " + nombre + ": inadmisible (cadena de custodia rota)");
            } else if (vincula) {
                validas++;
                fundamentos.add("√ " + nombre + ": prueba admisible y vinculante");
            } else {
                fundamentos.add("· " + nombre + ": admisible, pero no vincula al acusado");
            }
        }
        if (pruebas.isEmpty()) {
            fundamentos.add("× No se presentó ninguna prueba");
        }

        boolean sostenible = m.demostrar("acusacion_sostenible(" + s + ")");
        boolean esCulpable = inv.getExpediente().caso().culpableId().equals(acusadoId);
        Veredicto veredicto = !esCulpable ? Veredicto.ERROR_JUDICIAL
                : sostenible ? Veredicto.CONDENA : Veredicto.ABSOLUCION_FALTA_PRUEBAS;
        return new ResultadoJuicio(veredicto, acusadoId, Set.copyOf(pruebas), fundamentos, validas);
    }

    private static String linea(boolean ok, String si, String no) {
        return ok ? "√ " + si : "× " + no;
    }
}
