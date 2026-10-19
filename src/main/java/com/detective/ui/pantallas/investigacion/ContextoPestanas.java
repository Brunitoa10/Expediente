package com.detective.ui.pantallas.investigacion;

import com.detective.domain.investigacion.Investigacion;
import com.detective.servicio.ServicioDeduccion;

/** Dependencias compartidas por todas las pestañas de la investigación. */
public record ContextoPestanas(Investigacion investigacion, ServicioDeduccion deduccion, EjecutorAcciones ejecutor) {
}
