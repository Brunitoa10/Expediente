package com.detective.domain.model;

/** Datos generales de un caso (expediente de portada). */
public record Caso(
        String id,
        String titulo,
        String subtitulo,
        String lugarYEpoca,
        String resumen,
        String objetivo,
        String inspiracionReal,
        String procedimientoClave,
        String epilogo,
        Dificultad dificultad,
        int horasMaximas,
        String culpableId,
        String hechosIniciales,
        int orden,
        String categoria) {
}
