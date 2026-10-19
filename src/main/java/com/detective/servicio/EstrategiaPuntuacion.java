package com.detective.servicio;

import com.detective.domain.investigacion.Investigacion;

/** Patrón Strategy: distintas formas de puntuar sin modificar el cierre del caso. */
public interface EstrategiaPuntuacion {

    public DesglosePuntaje calcular(Investigacion investigacion, ResultadoJuicio juicio);
}
