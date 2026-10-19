package com.detective.ui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Ícono de lupa dibujado en tiempo de ejecución (sin archivos de imagen). */
public final class IconoAplicacion {

    private IconoAplicacion() {
    }

    public static Image crear() {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Tema.FONDO);
        g.fillRoundRect(0, 0, 64, 64, 14, 14);
        g.setColor(Tema.AMBAR);
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(12, 10, 28, 28);
        g.drawLine(37, 36, 52, 52);
        g.dispose();
        return img;
    }
}
