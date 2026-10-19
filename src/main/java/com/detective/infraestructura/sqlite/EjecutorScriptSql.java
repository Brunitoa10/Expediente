package com.detective.infraestructura.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Divide un script SQL en sentencias respetando literales entre comillas y comentarios "--". */
public final class EjecutorScriptSql {

    private EjecutorScriptSql() {
    }

    public static void ejecutar(Connection conexion, String script) throws SQLException {
        try (Statement st = conexion.createStatement()) {
            for (String sentencia : dividir(script)) {
                st.execute(sentencia);
            }
        }
    }

    public static List<String> dividir(String script) {
        List<String> sentencias = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enLiteral = false;
        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (!enLiteral && c == '-' && i + 1 < script.length() && script.charAt(i + 1) == '-') {
                while (i < script.length() && script.charAt(i) != '\n') {
                    i++;
                }
                actual.append('\n');
                continue;
            }
            if (c == '\'') {
                enLiteral = !enLiteral;
            }
            if (c == ';' && !enLiteral) {
                agregarSiNoVacia(sentencias, actual);
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        agregarSiNoVacia(sentencias, actual);
        return sentencias;
    }

    private static void agregarSiNoVacia(List<String> destino, StringBuilder sb) {
        String s = sb.toString().trim();
        if (!s.isEmpty()) {
            destino.add(s);
        }
    }
}
