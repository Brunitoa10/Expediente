package com.detective.ui.pantallas.investigacion;

import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/** Construcción de la "ficha" de detalle: título, hoja de expediente y botonera. */
public final class Fichas {

    private Fichas() {
    }

    public static JPanel ficha(String titulo, String subtitulo, String cuerpo, JComponent... botones) {
        JPanel p = Componentes.panel(new BorderLayout(0, 10));
        JPanel cab = Componentes.panel(new BorderLayout(0, 2));
        cab.add(Componentes.titulo(titulo, 22), BorderLayout.NORTH);
        if (subtitulo != null && !subtitulo.isBlank()) {
            cab.add(Componentes.suave(subtitulo), BorderLayout.SOUTH);
        }
        p.add(cab, BorderLayout.NORTH);
        JTextArea hoja = Componentes.papel(cuerpo);
        p.add(Componentes.desplazable(hoja), BorderLayout.CENTER);
        if (botones.length > 0) {
            JPanel b = Componentes.panel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            for (JComponent boton : botones) {
                b.add(boton);
                b.add(javax.swing.Box.createHorizontalStrut(8));
            }
            p.add(b, BorderLayout.SOUTH);
        }
        return p;
    }

    public static BotonNoir boton(String texto, BotonNoir.Estilo estilo, boolean habilitado, String ayuda, Runnable accion) {
        BotonNoir b = new BotonNoir(texto, estilo);
        b.setEnabled(habilitado);
        b.setToolTipText(ayuda);
        b.addActionListener(e -> accion.run());
        return b;
    }

    public static String marca(boolean ok) {
        return ok ? "√ " : "• ";
    }

    public static String separador() {
        return "\n\n────────────────────────────────────────\n\n";
    }
}
