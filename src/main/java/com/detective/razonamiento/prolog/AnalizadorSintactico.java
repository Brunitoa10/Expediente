package com.detective.razonamiento.prolog;

import com.detective.razonamiento.prolog.AnalizadorLexico.Tipo;
import com.detective.razonamiento.prolog.AnalizadorLexico.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser por precedencia de operadores (estilo Pratt) para un subconjunto de Prolog estándar.
 */
final class AnalizadorSintactico {

    private enum Asociatividad { XFX, XFY, YFX }

    private record OperadorInfijo(int precedencia, Asociatividad tipo) { }

    private static final Map<String, OperadorInfijo> INFIJOS = new HashMap<>();
    private static final Map<String, Integer> PREFIJOS = new HashMap<>();
    public static final String LISTA = "[|]";
    public static final Atomo LISTA_VACIA = new Atomo("[]");

    static {
        INFIJOS.put(":-", new OperadorInfijo(1200, Asociatividad.XFX));
        INFIJOS.put(";", new OperadorInfijo(1100, Asociatividad.XFY));
        INFIJOS.put("->", new OperadorInfijo(1050, Asociatividad.XFY));
        INFIJOS.put(",", new OperadorInfijo(1000, Asociatividad.XFY));
        for (String op : List.of("=", "\\=", "==", "\\==", "<", ">", "=<", ">=", "is", "=:=", "=\\=")) {
            INFIJOS.put(op, new OperadorInfijo(700, Asociatividad.XFX));
        }
        INFIJOS.put("+", new OperadorInfijo(500, Asociatividad.YFX));
        INFIJOS.put("-", new OperadorInfijo(500, Asociatividad.YFX));
        INFIJOS.put("*", new OperadorInfijo(400, Asociatividad.YFX));
        INFIJOS.put("//", new OperadorInfijo(400, Asociatividad.YFX));
        INFIJOS.put("mod", new OperadorInfijo(400, Asociatividad.YFX));
        PREFIJOS.put("\\+", 900);
        PREFIJOS.put("-", 200);
    }

    private final List<Token> tokens;
    private int pos;
    private Map<String, Variable> variables = new HashMap<>();

    AnalizadorSintactico(String fuente) {
        this.tokens = new AnalizadorLexico(fuente).tokenizar();
    }

    /** Lee todas las cláusulas del texto. */
    public List<Termino> leerClausulas() {
        List<Termino> clausulas = new ArrayList<>();
        while (actual().tipo() != Tipo.EOF) {
            variables = new HashMap<>();
            clausulas.add(expresion(1200));
            esperar(Tipo.FIN_CLAUSULA);
        }
        return clausulas;
    }

    /** Lee una consulta (un único término, con o sin punto final) y devuelve sus variables nombradas. */
    public Termino leerConsulta(Map<String, Variable> variablesConsulta) {
        variables = variablesConsulta;
        Termino t = expresion(1200);
        if (actual().tipo() == Tipo.FIN_CLAUSULA) {
            pos++;
        }
        if (actual().tipo() != Tipo.EOF) {
            throw new ExcepcionProlog("Texto sobrante en la consulta: " + actual().texto());
        }
        return t;
    }

    private Termino expresion(int maxPrecedencia) {
        Termino izquierdo = primario(maxPrecedencia);
        int precedenciaIzquierda = 0;
        while (true) {
            Token t = actual();
            String texto = textoOperador(t);
            OperadorInfijo op = texto == null ? null : INFIJOS.get(texto);
            if (op == null || op.precedencia() > maxPrecedencia) {
                return izquierdo;
            }
            int maxIzq = op.tipo() == Asociatividad.YFX ? op.precedencia() : op.precedencia() - 1;
            if (precedenciaIzquierda > maxIzq) {
                return izquierdo;
            }
            pos++;
            int maxDer = op.tipo() == Asociatividad.XFY ? op.precedencia() : op.precedencia() - 1;
            Termino derecho = expresion(maxDer);
            izquierdo = new Estructura(texto, izquierdo, derecho);
            precedenciaIzquierda = op.precedencia();
        }
    }

    private String textoOperador(Token t) {
        if (t.tipo() == Tipo.ATOMO) {
            return t.texto();
        }
        if (t.tipo() == Tipo.PUNTUACION && t.texto().equals(",")) {
            return ",";
        }
        return null;
    }

    private Termino primario(int maxPrecedencia) {
        Token t = actual();
        pos++;
        switch (t.tipo()) {
            case NUMERO:
                return new Numero(Long.parseLong(t.texto()));
            case CADENA:
                return new Atomo(t.texto());
            case VARIABLE:
                if (t.texto().equals("_")) {
                    return new Variable("_");
                }
                return variables.computeIfAbsent(t.texto(), Variable::new);
            case PUNTUACION:
                if (t.texto().equals("(")) {
                    Termino interno = expresion(1200);
                    esperarPuntuacion(")");
                    return interno;
                }
                if (t.texto().equals("[")) {
                    return lista();
                }
                throw new ExcepcionProlog("Símbolo inesperado: " + t.texto());
            case ATOMO:
                return atomoOCompuesto(t, maxPrecedencia);
            default:
                throw new ExcepcionProlog("Fin inesperado de la expresión");
        }
    }

    private Termino atomoOCompuesto(Token t, int maxPrecedencia) {
        if (t.pegadoAParentesis()) {
            pos++;
            List<Termino> args = new ArrayList<>();
            args.add(expresion(999));
            while (actual().es(",")) {
                pos++;
                args.add(expresion(999));
            }
            esperarPuntuacion(")");
            return new Estructura(t.texto(), args.toArray(new Termino[0]));
        }
        Integer prefijo = PREFIJOS.get(t.texto());
        if (prefijo != null && prefijo <= maxPrecedencia && puedeIniciarTermino(actual())) {
            if (t.texto().equals("-") && actual().tipo() == Tipo.NUMERO) {
                pos++;
                return new Numero(-Long.parseLong(tokens.get(pos - 1).texto()));
            }
            Termino operando = expresion(prefijo);
            return new Estructura(t.texto(), operando);
        }
        return new Atomo(t.texto());
    }

    private boolean puedeIniciarTermino(Token t) {
        return switch (t.tipo()) {
            case NUMERO, CADENA, VARIABLE -> true;
            case ATOMO -> !INFIJOS.containsKey(t.texto()) || t.pegadoAParentesis();
            case PUNTUACION -> t.texto().equals("(") || t.texto().equals("[");
            default -> false;
        };
    }

    private Termino lista() {
        if (actual().es("]")) {
            pos++;
            return LISTA_VACIA;
        }
        List<Termino> elementos = new ArrayList<>();
        elementos.add(expresion(999));
        while (actual().es(",")) {
            pos++;
            elementos.add(expresion(999));
        }
        Termino cola = LISTA_VACIA;
        if (actual().es("|")) {
            pos++;
            cola = expresion(999);
        }
        esperarPuntuacion("]");
        for (int i = elementos.size() - 1; i >= 0; i--) {
            cola = new Estructura(LISTA, elementos.get(i), cola);
        }
        return cola;
    }

    private Token actual() {
        return tokens.get(pos);
    }

    private void esperar(Tipo tipo) {
        if (actual().tipo() != tipo) {
            throw new ExcepcionProlog("Se esperaba " + tipo + " y se encontró '" + actual().texto() + "'");
        }
        pos++;
    }

    private void esperarPuntuacion(String texto) {
        if (!actual().es(texto)) {
            throw new ExcepcionProlog("Se esperaba '" + texto + "' y se encontró '" + actual().texto() + "'");
        }
        pos++;
    }
}
