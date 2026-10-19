package com.detective.razonamiento.prolog;

import java.util.Map;

/**
 * Variable lógica. Se compara por identidad (dos variables con el mismo nombre
 * en cláusulas distintas son variables distintas).
 */
public final class Variable implements Termino {

    private final String nombre;
    private Termino ligadura;

    public Variable(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean estaLigada() {
        return ligadura != null;
    }

    public void ligar(Termino valor) {
        this.ligadura = valor;
    }

    public void desligar() {
        this.ligadura = null;
    }

    @Override
    public Termino resolver() {
        Termino actual = this;
        while (actual instanceof Variable v && v.ligadura != null) {
            actual = v.ligadura;
        }
        return actual;
    }

    @Override
    public Termino copiar(Map<Variable, Variable> mapa) {
        Termino valor = resolver();
        if (valor != this) {
            return valor.copiar(mapa);
        }
        return mapa.computeIfAbsent(this, v -> new Variable(v.nombre));
    }

    @Override
    public Termino instanciar() {
        Termino valor = resolver();
        return valor == this ? this : valor.instanciar();
    }

    @Override
    public String toString() {
        Termino valor = resolver();
        return valor == this ? "_" + nombre : valor.toString();
    }
}
