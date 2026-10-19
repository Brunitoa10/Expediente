package com.detective.ui.componentes;

import com.detective.ui.Tema;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Barra de tiempo: las horas disponibles se ponen rojas al acercarse el límite. */
public final class RelojInvestigacion extends JComponent {

    private int usadas;
    private int maximas = 1;

    public RelojInvestigacion() {
        setPreferredSize(new Dimension(260, 30));
    }

    public void actualizar(int usadas, int maximas) {
        this.usadas = usadas;
        this.maximas = Math.max(1, maximas);
        setToolTipText("Horas de investigación usadas: " + usadas + " de " + maximas);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth() - 1;
        int h = getHeight() - 1;
        g2.setColor(Tema.FONDO_ELEVADO);
        g2.fillRoundRect(0, 0, w, h, h, h);
        double proporcion = Math.min(1.0, usadas / (double) maximas);
        Color color = proporcion < 0.6 ? Tema.AMBAR : proporcion < 0.85 ? new Color(0xD9823B) : Tema.ROJO;
        g2.setColor(color);
        g2.fillRoundRect(0, 0, (int) (w * proporcion), h, h, h);
        g2.setColor(Tema.BORDE);
        g2.drawRoundRect(0, 0, w, h, h, h);
        String texto = "" + Math.max(0, maximas - usadas) + " h restantes de " + maximas;
        g2.setFont(Tema.negrita(13));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Color.BLACK);
        g2.drawString(texto, (w - fm.stringWidth(texto)) / 2 + 1, (h + fm.getAscent()) / 2 - 1);
        g2.setColor(Color.WHITE);
        g2.drawString(texto, (w - fm.stringWidth(texto)) / 2, (h + fm.getAscent()) / 2 - 2);
        g2.dispose();
    }
}
