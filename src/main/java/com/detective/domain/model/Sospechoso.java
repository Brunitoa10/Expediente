package com.detective.domain.model;

import java.util.Optional;

public record Sospechoso(
        String id,
        String casoId,
        String nombre,
        String rol,
        String descripcion,
        String declaracion,
        String hechosDeclaracion,
        String evidenciaConfrontacionId,
        String textoConfrontacion,
        String hechosConfrontacion) {

    public Optional<String> evidenciaParaConfrontar() {
        return Optional.ofNullable(evidenciaConfrontacionId).filter(s -> !s.isBlank());
    }
}
