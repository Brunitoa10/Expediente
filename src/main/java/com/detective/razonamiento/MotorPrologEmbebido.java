package com.detective.razonamiento;

import com.detective.razonamiento.prolog.MotorProlog;
import com.detective.razonamiento.prolog.Termino;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Adaptador (patrón Adapter) entre el puerto {@link MotorRazonamiento} y el intérprete {@link MotorProlog}. */
public final class MotorPrologEmbebido implements MotorRazonamiento {

    private final MotorProlog prolog = new MotorProlog();

    @Override
    public void cargar(String programa) {
        prolog.consultar(programa);
    }

    @Override
    public List<Map<String, String>> consultar(String consulta, int limite) {
        return prolog.todasLasSoluciones(consulta, limite).stream()
                .map(MotorPrologEmbebido::aTexto)
                .toList();
    }

    @Override
    public boolean demostrar(String consulta) {
        return prolog.demostrar(consulta);
    }

    private static Map<String, String> aTexto(Map<String, Termino> solucion) {
        Map<String, String> resultado = new LinkedHashMap<>();
        solucion.forEach((variable, valor) -> resultado.put(variable, valor.toString()));
        return resultado;
    }
}
