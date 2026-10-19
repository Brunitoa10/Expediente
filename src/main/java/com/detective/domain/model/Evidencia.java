package com.detective.domain.model;

public record Evidencia(
        String id,
        String casoId,
        String lugarId,
        String nombre,
        String tipo,
        String descripcion,
        String hechosRecoleccion,
        boolean requiereAnalisis,
        int horasAnalisis,
        String tecnicaAnalisis,
        String resultadoAnalisis,
        String hechosAnalisis) {
}
