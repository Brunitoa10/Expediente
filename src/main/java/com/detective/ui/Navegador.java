package com.detective.ui;

import com.detective.domain.investigacion.Investigacion;
import com.detective.servicio.ResultadoCierre;

/** Navegación entre pantallas. Las pantallas dependen de esta abstracción, no de la ventana concreta. */
public interface Navegador {

    public void irAPortada();

    public void irASeleccionDeCaso();

    public void irAInvestigacion(Investigacion investigacion);

    public void irAResultado(Investigacion investigacion, ResultadoCierre cierre);

    public void irARanking();

    public void irAManual();

    public void salir();
}
