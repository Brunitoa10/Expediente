package com.detective.ui;

/** Pantallas que tienen recursos (timers, suscripciones) que liberar al dejar de mostrarse. */
public interface Liberable {

    public void liberar();
}
