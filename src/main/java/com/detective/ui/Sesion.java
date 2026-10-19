package com.detective.ui;

/** Datos de la sesión de juego actual (el detective que está jugando). */
public final class Sesion {

    private String jugador = "Detective";

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        if (jugador != null && !jugador.isBlank()) {
            this.jugador = jugador.trim();
        }
    }
}
