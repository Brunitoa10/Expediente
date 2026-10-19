package com.detective.servicio;

import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.investigacion.OraculoLogico;
import com.detective.domain.model.Deduccion;
import com.detective.domain.model.TipoDeduccion;
import com.detective.razonamiento.FabricaMotorRazonamiento;
import com.detective.razonamiento.MotorRazonamiento;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Traduce el estado de la investigación a consultas lógicas y las respuestas a deducciones legibles. */
public final class ServicioDeduccion implements OraculoLogico {

    private static final int LIMITE = 200;

    private final FabricaMotorRazonamiento fabrica;
    private final String reglas;

    private Investigacion ultimaInvestigacion;
    private long ultimaVersion = -1;
    private MotorRazonamiento motorCacheado;

    public ServicioDeduccion(FabricaMotorRazonamiento fabrica, String reglas) {
        this.fabrica = fabrica;
        this.reglas = reglas;
    }

    // ------------------------------------------------------------ motor

    public ConstructorBaseConocimiento constructorPara(Investigacion inv) {
        return new ConstructorBaseConocimiento()
                .conReglas(reglas)
                .conNombres(inv)
                .conHechosIniciales(inv.getExpediente())
                .conDescubrimientos(inv);
    }

    /** Motor con la base de conocimiento actual (cacheado mientras el estado no cambie). */
    public synchronized MotorRazonamiento motor(Investigacion inv) {
        if (inv != ultimaInvestigacion || inv.getVersion() != ultimaVersion) {
            motorCacheado = constructorPara(inv).construir(fabrica);
            ultimaInvestigacion = inv;
            ultimaVersion = inv.getVersion();
        }
        return motorCacheado;
    }

    public MotorRazonamiento motorConHechosExtra(Investigacion inv, String hechosExtra) {
        return constructorPara(inv).conHechos(hechosExtra).construir(fabrica);
    }

    // ------------------------------------------------------------ oráculo

    @Override
    public boolean puedeConfrontar(Investigacion inv, String sospechosoId, String evidenciaId) {
        return motor(inv).demostrar("confrontable(" + TextoProlog.atomo(sospechosoId) + ", "
                + TextoProlog.atomo(evidenciaId) + ")");
    }

    // ------------------------------------------------------------ deducciones

    public List<Deduccion> deducciones(Investigacion inv) {
        MotorRazonamiento m = motor(inv);
        Function<String, String> nombre = id -> nombreDe(m, id);
        List<Deduccion> resultado = new ArrayList<>();

        agregar(resultado, m, "sospechoso_principal(S)", TipoDeduccion.SOSPECHOSO_PRINCIPAL,
                s -> "Reúne motivo, medio y oportunidad, y nada lo descarta.");
        agregar(resultado, m, "motivo(S, D)", TipoDeduccion.MOTIVO, s -> s.get("D"));
        agregar(resultado, m, "medio(S, D)", TipoDeduccion.MEDIO, s -> s.get("D"));
        agregar(resultado, m, "oportunidad(S, L, H)", TipoDeduccion.OPORTUNIDAD,
                s -> "Estuvo en " + nombre.apply(s.get("L")) + " a las " + FormatoHora.formatear(s.get("H"))
                        + ", dentro de la ventana del hecho.");
        agregar(resultado, m, "contradiccion(S, E)", TipoDeduccion.CONTRADICCION,
                s -> "Su declaración choca con: " + nombre.apply(s.get("E")) + ".");
        agregar(resultado, m, "descartado(S, E)", TipoDeduccion.DESCARTE,
                s -> "Coartada confirmada por: " + nombre.apply(s.get("E")) + ".");
        agregar(resultado, m, "vinculo(E, S)", TipoDeduccion.VINCULO,
                s -> nombre.apply(s.get("E")) + " lo vincula directamente con el hecho.");
        return resultado;
    }

    public boolean esSospechosoPrincipal(Investigacion inv, String sospechosoId) {
        return motor(inv).demostrar("sospechoso_principal(" + TextoProlog.atomo(sospechosoId) + ")");
    }

    /** Consulta libre desde la consola Prolog del juego. */
    public List<Map<String, String>> consultaLibre(Investigacion inv, String consulta) {
        return motor(inv).consultar(consulta, LIMITE);
    }

    public String nombreDe(Investigacion inv, String id) {
        return nombreDe(motor(inv), id);
    }

    /** Hechos para presentar pruebas ante el fiscal. */
    public static String hechosPresentacion(Collection<String> evidencias) {
        StringBuilder sb = new StringBuilder();
        evidencias.forEach(e -> sb.append("presentada(").append(TextoProlog.atomo(e)).append(").\n"));
        return sb.toString();
    }

    private static String nombreDe(MotorRazonamiento m, String id) {
        if (!id.matches("[a-z][a-zA-Z0-9_]*")) {
            return id;
        }
        List<Map<String, String>> sol = m.consultar("nombre(" + id + ", N)", 1);
        return sol.isEmpty() ? id.replace('_', ' ') : sol.get(0).get("N");
    }

    private static void agregar(List<Deduccion> destino, MotorRazonamiento m, String consulta,
                                TipoDeduccion tipo, Function<Map<String, String>, String> detalle) {
        for (Map<String, String> s : m.consultar(consulta, LIMITE)) {
            destino.add(new Deduccion(tipo, s.get("S"), detalle.apply(s)));
        }
    }
}
