package com.detective.domain.investigacion;

/** Se lanza cuando el jugador intenta una acción que las reglas del procedimiento no permiten. */
public class AccionInvalidaException extends RuntimeException {

    public AccionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
