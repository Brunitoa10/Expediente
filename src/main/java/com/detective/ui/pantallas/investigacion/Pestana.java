package com.detective.ui.pantallas.investigacion;

import javax.swing.JComponent;

/** Contrato de cada pestaña de la investigación (Segregación de Interfaces: sólo lo necesario). */
public interface Pestana {

    public String titulo();

    public JComponent componente();

    /** Vuelve a dibujarse a partir del estado actual de la investigación. */
    public void actualizar();
}
