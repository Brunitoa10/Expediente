package com.detective.razonamiento.prolog;

import java.util.ArrayList;
import java.util.List;

/** Convierte texto Prolog en tokens. Soporta comentarios %, átomos 'citados' y cadenas "dobles". */
public final class AnalizadorLexico {

    public enum Tipo { ATOMO, VARIABLE, NUMERO, CADENA, PUNTUACION, FIN_CLAUSULA, EOF }

    public record Token(Tipo tipo, String texto, boolean pegadoAParentesis) {
        public boolean es(String valor) {
            return (tipo == Tipo.ATOMO || tipo == Tipo.PUNTUACION) && texto.equals(valor);
        }
    }

    private static final String SIMBOLOS = "+-*/\\^<>=~:.?@#&$";

    private final String fuente;
    private int pos;

    public AnalizadorLexico(String fuente) {
        this.fuente = fuente;
    }

    public List<Token> tokenizar() {
        List<Token> tokens = new ArrayList<>();
        while (true) {
            saltarEspaciosYComentarios();
            if (pos >= fuente.length()) {
                tokens.add(new Token(Tipo.EOF, "", false));
                return tokens;
            }
            tokens.add(siguiente());
        }
    }

    private void saltarEspaciosYComentarios() {
        while (pos < fuente.length()) {
            char c = fuente.charAt(pos);
            if (Character.isWhitespace(c)) {
                pos++;
            } else if (c == '%') {
                while (pos < fuente.length() && fuente.charAt(pos) != '\n') {
                    pos++;
                }
            } else {
                return;
            }
        }
    }

    private Token siguiente() {
        char c = fuente.charAt(pos);
        if (Character.isDigit(c)) {
            return leerNumero();
        }
        if (Character.isLetter(c) || c == '_') {
            return leerIdentificador();
        }
        if (c == '\'') {
            return conParentesis(Tipo.ATOMO, leerCitado('\''));
        }
        if (c == '"') {
            return new Token(Tipo.CADENA, leerCitado('"'), false);
        }
        if ("(),|[]".indexOf(c) >= 0) {
            pos++;
            return new Token(Tipo.PUNTUACION, String.valueOf(c), false);
        }
        if (c == '!' || c == ';') {
            pos++;
            return new Token(Tipo.ATOMO, String.valueOf(c), false);
        }
        if (c == '.' && esFinDeClausula()) {
            pos++;
            return new Token(Tipo.FIN_CLAUSULA, ".", false);
        }
        if (SIMBOLOS.indexOf(c) >= 0) {
            int inicio = pos;
            while (pos < fuente.length() && SIMBOLOS.indexOf(fuente.charAt(pos)) >= 0) {
                if (fuente.charAt(pos) == '.' && esFinDeClausula()) {
                    break;
                }
                pos++;
            }
            return conParentesis(Tipo.ATOMO, fuente.substring(inicio, pos));
        }
        throw new ExcepcionProlog("Carácter inesperado '" + c + "' en posición " + pos);
    }

    private boolean esFinDeClausula() {
        int siguiente = pos + 1;
        return siguiente >= fuente.length()
                || Character.isWhitespace(fuente.charAt(siguiente))
                || fuente.charAt(siguiente) == '%';
    }

    private Token leerNumero() {
        int inicio = pos;
        while (pos < fuente.length() && Character.isDigit(fuente.charAt(pos))) {
            pos++;
        }
        return new Token(Tipo.NUMERO, fuente.substring(inicio, pos), false);
    }

    private Token leerIdentificador() {
        int inicio = pos;
        while (pos < fuente.length()
                && (Character.isLetterOrDigit(fuente.charAt(pos)) || fuente.charAt(pos) == '_')) {
            pos++;
        }
        String texto = fuente.substring(inicio, pos);
        char primero = texto.charAt(0);
        Tipo tipo = (Character.isUpperCase(primero) || primero == '_') ? Tipo.VARIABLE : Tipo.ATOMO;
        return tipo == Tipo.ATOMO ? conParentesis(tipo, texto) : new Token(tipo, texto, false);
    }

    private String leerCitado(char delimitador) {
        pos++;
        StringBuilder sb = new StringBuilder();
        while (pos < fuente.length()) {
            char c = fuente.charAt(pos++);
            if (c == delimitador) {
                if (pos < fuente.length() && fuente.charAt(pos) == delimitador) {
                    sb.append(c);
                    pos++;
                    continue;
                }
                return sb.toString();
            }
            if (c == '\\' && pos < fuente.length()) {
                char escapado = fuente.charAt(pos++);
                sb.append(escapado == 'n' ? '\n' : escapado);
                continue;
            }
            sb.append(c);
        }
        throw new ExcepcionProlog("Texto citado sin cerrar");
    }

    private Token conParentesis(Tipo tipo, String texto) {
        boolean pegado = pos < fuente.length() && fuente.charAt(pos) == '(';
        return new Token(tipo, texto, pegado);
    }
}
