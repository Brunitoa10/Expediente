package com.detective.ui.componentes;

import com.detective.ui.Tema;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;

/** Fábrica de componentes con el estilo del juego (evita repetir configuración en cada pantalla). */
public final class Componentes {

    private Componentes() {
    }

    public static JLabel titulo(String texto, float tam) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.titulo(tam));
        l.setForeground(Tema.AMBAR);
        return l;
    }

    public static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(fuente);
        l.setForeground(color);
        return l;
    }

    public static JLabel suave(String texto) {
        return etiqueta(texto, Tema.texto(13), Tema.TEXTO_SUAVE);
    }

    /** Texto de solo lectura con aspecto de hoja de expediente mecanografiada. */
    public static JTextArea papel(String texto) {
        JTextArea a = areaBase(texto);
        a.setFont(Tema.maquina(14));
        a.setBackground(Tema.PAPEL);
        a.setForeground(Tema.TINTA);
        a.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        return a;
    }

    /** Texto de solo lectura sobre fondo oscuro. */
    public static JTextArea texto(String texto) {
        JTextArea a = areaBase(texto);
        a.setFont(Tema.texto(14));
        a.setBackground(Tema.FONDO_PANEL);
        a.setForeground(Tema.TEXTO);
        a.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return a;
    }

    public static JScrollPane desplazable(JComponent c) {
        JScrollPane sp = new JScrollPane(c, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        sp.getViewport().setBackground(Tema.FONDO_PANEL);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    public static JPanel panel(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Tema.FONDO);
        return p;
    }

    public static JPanel panelOscuro(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(Tema.FONDO_PANEL);
        return p;
    }

    private static JTextArea areaBase(String texto) {
        JTextArea a = new JTextArea(texto);
        a.setEditable(false);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setCaretPosition(0);
        return a;
    }
}
