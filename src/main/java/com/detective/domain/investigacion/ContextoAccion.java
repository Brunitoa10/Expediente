package com.detective.domain.investigacion;

/**
 * Lo que una acción necesita para ejecutarse: la investigación y un oráculo lógico
 * (abstracción que evita que el dominio dependa del motor Prolog concreto).
 */
public record ContextoAccion(Investigacion investigacion, OraculoLogico oraculo) {
}
