package com.detective.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

public final class Recursos {

    private Recursos() {
    }

    public static String leerTexto(String ruta) {
        try (InputStream in = Recursos.class.getResourceAsStream(ruta)) {
            if (in == null) {
                throw new IllegalStateException("Recurso no encontrado: " + ruta);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
