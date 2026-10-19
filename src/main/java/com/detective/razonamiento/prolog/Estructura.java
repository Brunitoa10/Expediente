package com.detective.razonamiento.prolog;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/** Término compuesto: functor(arg1, ..., argN). */
public final class Estructura implements Termino {

    private final String functor;
    private final Termino[] argumentos;

    public Estructura(String functor, Termino... argumentos) {
        this.functor = functor;
        this.argumentos = argumentos;
    }

    public String getFunctor() {
        return functor;
    }

    public int aridad() {
        return argumentos.length;
    }

    public Termino argumento(int i) {
        return argumentos[i];
    }

    public String clave() {
        return functor + "/" + argumentos.length;
    }

    @Override
    public Termino resolver() {
        return this;
    }

    @Override
    public Termino copiar(Map<Variable, Variable> mapa) {
        Termino[] copia = new Termino[argumentos.length];
        for (int i = 0; i < argumentos.length; i++) {
            copia[i] = argumentos[i].copiar(mapa);
        }
        return new Estructura(functor, copia);
    }

    @Override
    public Termino instanciar() {
        Termino[] copia = new Termino[argumentos.length];
        for (int i = 0; i < argumentos.length; i++) {
            copia[i] = argumentos[i].instanciar();
        }
        return new Estructura(functor, copia);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Estructura otra)) {
            return false;
        }
        return functor.equals(otra.functor) && Arrays.equals(argumentos, otra.argumentos);
    }

    @Override
    public int hashCode() {
        return 31 * functor.hashCode() + Arrays.hashCode(argumentos);
    }

    @Override
    public String toString() {
        return functor + "(" + Arrays.stream(argumentos).map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }
}
