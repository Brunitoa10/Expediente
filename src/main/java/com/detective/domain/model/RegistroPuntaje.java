package com.detective.domain.model;

import java.time.LocalDateTime;

public record RegistroPuntaje(String jugador, String casoId, String casoTitulo, Veredicto veredicto,
                              int puntos, int horasUsadas, LocalDateTime fecha) {
}
