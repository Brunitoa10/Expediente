package com.detective.razonamiento.prolog;

import java.util.Map;

public record Numero(long valor) implements Termino {

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
        return Long.toString(valor);
    }
}
