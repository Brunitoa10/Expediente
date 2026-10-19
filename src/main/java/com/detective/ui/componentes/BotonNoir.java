package com.detective.ui.componentes;

import com.detective.ui.Tema;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Botón dibujado a mano con estilo noir: borde ámbar al pasar el mouse. */
public class BotonNoir extends JButton {

    public enum Estilo { PRIMARIO, SECUNDARIO, PELIGRO }

    private final Estilo estilo;
    private boolean encima;

    public BotonNoir(String texto) {
        this(texto, Estilo.SECUNDARIO);
    }

    public BotonNoir(String texto, Estilo estilo) {
        super(texto);
        this.estilo = estilo;
        setFont(Tema.negrita(14));
        setForeground(Tema.TEXTO);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                encima = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                encima = false;
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(getText()) + 40, fm.getHeight() + 18);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Color acento = switch (estilo) {
            case PRIMARIO -> Tema.AMBAR;
            case PELIGRO -> Tema.ROJO;
            case SECUNDARIO -> Tema.BORDE;
        };
        boolean activo = isEnabled();
        Color fondo = estilo == Estilo.PRIMARIO
                ? (encima ? Tema.AMBAR : new Color(0x2A2418))
                : (encima ? Tema.FONDO_ELEVADO.brighter() : Tema.FONDO_ELEVADO);
        if (!activo) {
            fondo = Tema.FONDO_PANEL;
        }
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.setColor(activo ? (encima ? Tema.AMBAR : acento) : Tema.BORDE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        Color texto = !activo ? Tema.TEXTO_SUAVE.darker()
                : (estilo == Estilo.PRIMARIO && encima) ? Tema.FONDO
                : estilo == Estilo.PRIMARIO ? Tema.AMBAR : Tema.TEXTO;
        g2.setColor(texto);
        g2.drawString(getText(), x, y);
        g2.dispose();
    }
}
