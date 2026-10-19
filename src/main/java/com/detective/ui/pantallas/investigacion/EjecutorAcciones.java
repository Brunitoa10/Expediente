package com.detective.ui.pantallas.investigacion;

import com.detective.domain.investigacion.AccionInvestigacion;

/** Las pestañas piden ejecutar acciones sin conocer al servicio (Inversión de Dependencias). */
@FunctionalInterface
public interface EjecutorAcciones {

    public void ejecutar(AccionInvestigacion accion);
}
