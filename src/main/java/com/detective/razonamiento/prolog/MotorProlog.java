package com.detective.razonamiento.prolog;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Intérprete Prolog embebido, 100 % Java y sin dependencias (portable).
 * Implementa resolución SLD con backtracking mediante continuaciones, unificación con
 * "trail" para deshacer ligaduras, negación por falla (\+), disyunción, if-then-else,
 * corte local (!), aritmética entera y comparación.
 */
public final class MotorProlog {

    /** Continuación: se invoca por cada solución. Devuelve true para detener la búsqueda. */
    @FunctionalInterface
    private interface Continuacion {
        boolean ejecutar();
    }

    /** Señal interna para propagar el corte hasta la cláusula que lo introdujo. */
    private static final class Corte extends RuntimeException {
        private final Object marca;

        Corte(Object marca) {
            super(null, null, false, false);
            this.marca = marca;
        }
    }

    private static final int PROFUNDIDAD_MAXIMA = 4000;

    private final Map<String, List<Termino>> clausulas = new LinkedHashMap<>();
    private final Deque<Variable> trail = new ArrayDeque<>();

    // ------------------------------------------------------------------ carga

    public void consultar(String textoPrograma) {
        for (Termino clausula : new AnalizadorSintactico(textoPrograma).leerClausulas()) {
            agregar(clausula);
        }
    }

    public void agregar(Termino clausula) {
        clausulas.computeIfAbsent(claveDe(cabeza(clausula)), k -> new ArrayList<>()).add(clausula);
    }

    public void limpiar() {
        clausulas.clear();
    }

    // ------------------------------------------------------------------ consultas

    /** Devuelve todas las soluciones (sin duplicados) con los valores de las variables nombradas. */
    public List<Map<String, Termino>> todasLasSoluciones(String consulta, int limite) {
        Map<String, Variable> variables = new LinkedHashMap<>();
        Termino objetivo = new AnalizadorSintactico(consulta).leerConsulta(variables);
        List<Map<String, Termino>> soluciones = new ArrayList<>();
        int marca = trail.size();
        try {
            resolver(objetivo, 0, new Object(), () -> {
                Map<String, Termino> sol = new LinkedHashMap<>();
                variables.forEach((nombre, var) -> {
                    if (!nombre.startsWith("_")) {
                        sol.put(nombre, var.instanciar());
                    }
                });
                if (!soluciones.contains(sol)) {
                    soluciones.add(sol);
                }
                return soluciones.size() >= limite;
            });
        } catch (Corte corte) {
            // corte a nivel de consulta: simplemente termina
        } finally {
            deshacer(marca);
        }
        return soluciones;
    }

    public boolean demostrar(String consulta) {
        return !todasLasSoluciones(consulta, 1).isEmpty();
    }

    // ------------------------------------------------------------------ resolución

    private boolean resolver(Termino objetivo, int profundidad, Object marcaCorte, Continuacion k) {
        if (profundidad > PROFUNDIDAD_MAXIMA) {
            throw new ExcepcionProlog("Profundidad máxima excedida (¿recursión infinita?)");
        }
        Termino meta = objetivo.resolver();
        if (meta instanceof Variable) {
            throw new ExcepcionProlog("Objetivo no instanciado");
        }
        if (meta instanceof Numero) {
            throw new ExcepcionProlog("Un número no es un objetivo válido: " + meta);
        }
        if (meta instanceof Atomo a) {
            return switch (a.nombre()) {
                case "true" -> k.ejecutar();
                case "fail", "false" -> false;
                case "!" -> {
                    if (k.ejecutar()) {
                        yield true;
                    }
                    throw new Corte(marcaCorte);
                }
                default -> resolverUsuario(a, profundidad, k);
            };
        }
        Estructura e = (Estructura) meta;
        switch (e.clave()) {
            case ",/2":
                return resolver(e.argumento(0), profundidad + 1, marcaCorte,
                        () -> resolver(e.argumento(1), profundidad + 1, marcaCorte, k));
            case ";/2":
                return disyuncion(e, profundidad, marcaCorte, k);
            case "->/2":
                return siEntonces(e.argumento(0), e.argumento(1), new Atomo("fail"), profundidad, marcaCorte, k);
            case "\\+/1":
                return negacion(e.argumento(0), profundidad, k);
            case "=/2":
                return unificarYContinuar(e.argumento(0), e.argumento(1), k);
            case "\\=/2":
                return noUnifica(e.argumento(0), e.argumento(1)) && k.ejecutar();
            case "==/2":
                return e.argumento(0).instanciar().equals(e.argumento(1).instanciar()) && k.ejecutar();
            case "\\==/2":
                return !e.argumento(0).instanciar().equals(e.argumento(1).instanciar()) && k.ejecutar();
            case "is/2":
                return unificarYContinuar(e.argumento(0), new Numero(evaluar(e.argumento(1))), k);
            case "</2":
                return evaluar(e.argumento(0)) < evaluar(e.argumento(1)) && k.ejecutar();
            case ">/2":
                return evaluar(e.argumento(0)) > evaluar(e.argumento(1)) && k.ejecutar();
            case "=</2":
                return evaluar(e.argumento(0)) <= evaluar(e.argumento(1)) && k.ejecutar();
            case ">=/2":
                return evaluar(e.argumento(0)) >= evaluar(e.argumento(1)) && k.ejecutar();
            case "=:=/2":
                return evaluar(e.argumento(0)) == evaluar(e.argumento(1)) && k.ejecutar();
            case "=\\=/2":
                return evaluar(e.argumento(0)) != evaluar(e.argumento(1)) && k.ejecutar();
            default:
                return resolverUsuario(e, profundidad, k);
        }
    }

    private boolean disyuncion(Estructura e, int profundidad, Object marcaCorte, Continuacion k) {
        Termino izquierda = e.argumento(0).resolver();
        if (izquierda instanceof Estructura cond && cond.clave().equals("->/2")) {
            return siEntonces(cond.argumento(0), cond.argumento(1), e.argumento(1), profundidad, marcaCorte, k);
        }
        int marca = trail.size();
        if (resolver(izquierda, profundidad + 1, marcaCorte, k)) {
            return true;
        }
        deshacer(marca);
        return resolver(e.argumento(1), profundidad + 1, marcaCorte, k);
    }

    private boolean siEntonces(Termino condicion, Termino entonces, Termino sino,
                               int profundidad, Object marcaCorte, Continuacion k) {
        int marca = trail.size();
        boolean[] cumplida = {false};
        boolean[] resultado = {false};
        resolverAislado(condicion, profundidad + 1, () -> {
            cumplida[0] = true;
            resultado[0] = resolver(entonces, profundidad + 1, marcaCorte, k);
            return true;
        });
        if (cumplida[0]) {
            return resultado[0];
        }
        deshacer(marca);
        return resolver(sino, profundidad + 1, marcaCorte, k);
    }

    private boolean negacion(Termino objetivo, int profundidad, Continuacion k) {
        int marca = trail.size();
        boolean demostrado = resolverAislado(objetivo, profundidad + 1, () -> true);
        deshacer(marca);
        return !demostrado && k.ejecutar();
    }

    /** Resuelve un objetivo cuyo corte (!) no debe escapar de él (condiciones y negaciones). */
    private boolean resolverAislado(Termino objetivo, int profundidad, Continuacion k) {
        Object marca = new Object();
        try {
            return resolver(objetivo, profundidad, marca, k);
        } catch (Corte corte) {
            if (corte.marca != marca) {
                throw corte;
            }
            return false;
        }
    }

    private boolean resolverUsuario(Termino meta, int profundidad, Continuacion k) {
        List<Termino> candidatas = clausulas.get(claveDe(meta));
        if (candidatas == null) {
            return false;
        }
        Object marcaCorte = new Object();
        try {
            for (Termino clausula : List.copyOf(candidatas)) {
                int marca = trail.size();
                Termino copia = clausula.copiar(new IdentityHashMap<>());
                if (unificar(cabeza(copia), meta)) {
                    if (resolver(cuerpo(copia), profundidad + 1, marcaCorte, k)) {
                        return true;
                    }
                }
                deshacer(marca);
            }
        } catch (Corte corte) {
            if (corte.marca != marcaCorte) {
                throw corte;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ unificación

    private boolean unificarYContinuar(Termino a, Termino b, Continuacion k) {
        int marca = trail.size();
        if (unificar(a, b) && k.ejecutar()) {
            return true;
        }
        deshacer(marca);
        return false;
    }

    private boolean noUnifica(Termino a, Termino b) {
        int marca = trail.size();
        boolean unifica = unificar(a, b);
        deshacer(marca);
        return !unifica;
    }

    private boolean unificar(Termino a, Termino b) {
        Termino x = a.resolver();
        Termino y = b.resolver();
        if (x == y) {
            return true;
        }
        if (x instanceof Variable vx) {
            ligar(vx, y);
            return true;
        }
        if (y instanceof Variable vy) {
            ligar(vy, x);
            return true;
        }
        if (x instanceof Estructura ex && y instanceof Estructura ey) {
            if (!ex.getFunctor().equals(ey.getFunctor()) || ex.aridad() != ey.aridad()) {
                return false;
            }
            for (int i = 0; i < ex.aridad(); i++) {
                if (!unificar(ex.argumento(i), ey.argumento(i))) {
                    return false;
                }
            }
            return true;
        }
        return x.equals(y);
    }

    private void ligar(Variable v, Termino valor) {
        v.ligar(valor);
        trail.push(v);
    }

    private void deshacer(int marca) {
        while (trail.size() > marca) {
            trail.pop().desligar();
        }
    }

    // ------------------------------------------------------------------ aritmética

    private long evaluar(Termino t) {
        Termino v = t.resolver();
        if (v instanceof Numero n) {
            return n.valor();
        }
        if (v instanceof Estructura e) {
            if (e.aridad() == 1 && e.getFunctor().equals("-")) {
                return -evaluar(e.argumento(0));
            }
            if (e.aridad() == 2) {
                long a = evaluar(e.argumento(0));
                long b = evaluar(e.argumento(1));
                return switch (e.getFunctor()) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    case "//" -> a / b;
                    case "mod" -> Math.floorMod(a, b);
                    default -> throw new ExcepcionProlog("Operador aritmético desconocido: " + e.getFunctor());
                };
            }
        }
        throw new ExcepcionProlog("No es una expresión aritmética: " + v);
    }

    // ------------------------------------------------------------------ utilidades

    private static Termino cabeza(Termino clausula) {
        if (clausula instanceof Estructura e && e.clave().equals(":-/2")) {
            return e.argumento(0);
        }
        return clausula;
    }

    private static Termino cuerpo(Termino clausula) {
        if (clausula instanceof Estructura e && e.clave().equals(":-/2")) {
            return e.argumento(1);
        }
        return Atomo.VERDADERO;
    }

    private static String claveDe(Termino t) {
        Termino r = t.resolver();
        if (r instanceof Atomo a) {
            return a.nombre() + "/0";
        }
        if (r instanceof Estructura e) {
            return e.clave();
        }
        throw new ExcepcionProlog("Cabeza de cláusula inválida: " + r);
    }

    /** Cantidad de cláusulas por predicado (útil para depuración y para la consola Prolog). */
    public Map<String, Integer> estadisticas() {
        Map<String, Integer> resultado = new HashMap<>();
        clausulas.forEach((k, v) -> resultado.put(k, v.size()));
        return resultado;
    }
}
