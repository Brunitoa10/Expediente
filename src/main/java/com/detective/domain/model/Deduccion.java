package com.detective.domain.model;

/** Conclusión inferida por el motor lógico a partir de lo que el jugador descubrió. */
public record Deduccion(TipoDeduccion tipo, String sospechosoId, String detalle) {
}
