package com.detective.ui.pantallas;

import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/** Barra superior común a las pantallas secundarias. */
public final class Encabezado {

    private Encabezado() {
    }

    public static JPanel crear(String titulo, String subtitulo, String textoVolver, Runnable volver, JComponent... extras) {
        JPanel barra = Componentes.panel(new BorderLayout(16, 0));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE), Tema.margen(16)));
        JPanel textos = Componentes.panel(new BorderLayout(0, 2));
        textos.add(Componentes.titulo(titulo, 26), BorderLayout.NORTH);
        if (subtitulo != null) {
            textos.add(Componentes.suave(subtitulo), BorderLayout.SOUTH);
        }
        barra.add(textos, BorderLayout.CENTER);
        JPanel acciones = Componentes.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        for (JComponent extra : extras) {
            acciones.add(extra);
        }
        BotonNoir b = new BotonNoir(textoVolver);
        b.addActionListener(e -> volver.run());
        acciones.add(b);
        barra.add(acciones, BorderLayout.EAST);
        return barra;
    }
}
