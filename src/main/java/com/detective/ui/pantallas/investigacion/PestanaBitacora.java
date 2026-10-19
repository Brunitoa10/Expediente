package com.detective.ui.pantallas.investigacion;

import com.detective.ui.componentes.Componentes;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class PestanaBitacora implements Pestana {

    private final ContextoPestanas ctx;
    private final JTextArea area = Componentes.papel("");
    private final JScrollPane raiz = Componentes.desplazable(area);

    public PestanaBitacora(ContextoPestanas ctx) {
        this.ctx = ctx;
    }

    @Override
    public String titulo() {
        return "Bitácora";
    }

    @Override
    public JComponent componente() {
        return raiz;
    }

    @Override
    public void actualizar() {
        area.setText("LIBRO DE ACTUACIONES\n\n" + String.join("\n", ctx.investigacion().getEstado().getBitacora()));
        area.setCaretPosition(area.getDocument().getLength());
    }
}
