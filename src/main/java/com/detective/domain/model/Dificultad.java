package com.detective.domain.model;

public enum Dificultad {
    FACIL("Fácil", 1.0),
    MEDIA("Media", 1.5),
    DIFICIL("Difícil", 2.0);

    private final String etiqueta;
    private final double multiplicador;

    private Dificultad(String etiqueta, double multiplicador) {
        this.etiqueta = etiqueta;
        this.multiplicador = multiplicador;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public double getMultiplicador() {
        return multiplicador;
    }
}
