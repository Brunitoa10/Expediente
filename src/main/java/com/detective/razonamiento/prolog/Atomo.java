package com.detective.razonamiento.prolog;

import java.util.Map;

public record Atomo(String nombre) implements Termino {

    public static final Atomo VERDADERO = new Atomo("true");

    @Override
    public Termino resolver() {
        return this;
    }

    @Override
    public Termino copiar(Map<Variable, Variable> mapa) {
        return this;
    }

    @Override
    public Termino instanciar() {
        return this;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
