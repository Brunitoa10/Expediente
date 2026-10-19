package com.detective.generacion;

import java.util.Map;

/** Reemplazo simple de marcadores {CLAVE} en textos de plantilla. */
final class Plantilla {

    private Plantilla() {
    }

    static String aplicar(String texto, Map<String, String> valores) {
        String resultado = texto;
        for (Map.Entry<String, String> e : valores.entrySet()) {
            resultado = resultado.replace("{" + e.getKey() + "}", e.getValue());
        }
        return resultado;
    }
}
