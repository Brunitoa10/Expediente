package com.detective.servicio;

/** Utilidades para generar texto Prolog seguro. */
public final class TextoProlog {

    private TextoProlog() {
    }

    /** Convierte un texto libre en una cadena Prolog entre comillas dobles, escapando caracteres especiales. */
    public static String cadena(String texto) {
        return "\"" + texto.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    /** Valida que un identificador pueda usarse como átomo Prolog sin comillas. */
    public static String atomo(String id) {
        if (!id.matches("[a-z][a-zA-Z0-9_]*")) {
            throw new IllegalArgumentException("Identificador inválido para Prolog: " + id);
        }
        return id;
    }
}
