package com.detective.infraestructura.sqlite;

public class ErrorPersistencia extends RuntimeException {

    public ErrorPersistencia(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
