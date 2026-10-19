package com.detective.ui.pantallas;

import com.detective.domain.repositorio.AjustesRepositorio;
import com.detective.ui.Navegador;
import com.detective.ui.Sesion;
import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * Portada animada: ciudad nocturna bajo la lluvia, un farol y la silueta del detective.
 * Todo se dibuja con Java2D (sin imágenes externas), así el juego sigue siendo un único .jar portable.
 */
public final class PantallaPortada extends JPanel {

    private static final String CLAVE_JUGADOR = "ultimo_jugador";
    private static final int GOTAS = 260;

    private final float[] gotaX = new float[GOTAS];
    private final float[] gotaY = new float[GOTAS];
    private final float[] gotaV = new float[GOTAS];
    private final int[][] edificios;
    private final Random azar = new Random(1892);
    private final Timer animacion;
    private float relampago;
    private long cuadro;

    public PantallaPortada(Navegador navegador, Sesion sesion, AjustesRepositorio ajustes) {
        super(new GridBagLayout());
        setBackground(Tema.FONDO);
        edificios = generarEdificios();
        for (int i = 0; i < GOTAS; i++) {
            reiniciarGota(i, true);
        }

        JTextField nombre = new JTextField(ajustes.obtener(CLAVE_JUGADOR).orElse(sesion.getJugador()), 16);
        nombre.setFont(Tema.maquina(16));
        nombre.setForeground(Tema.TINTA);
        nombre.setBackground(Tema.PAPEL);
        nombre.setCaretColor(Tema.TINTA);
        nombre.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        nombre.setMaximumSize(new Dimension(280, 36));

        Runnable confirmarNombre = () -> {
            sesion.setJugador(nombre.getText());
            ajustes.guardar(CLAVE_JUGADOR, sesion.getJugador());
        };

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.add(alinear(Componentes.etiqueta("NOMBRE DEL DETECTIVE", Tema.negrita(12), Tema.TEXTO_SUAVE)));
        menu.add(Box.createVerticalStrut(6));
        menu.add(alinear(nombre));
        menu.add(Box.createVerticalStrut(22));
        menu.add(boton("Abrir expedientes", BotonNoir.Estilo.PRIMARIO, () -> {
            confirmarNombre.run();
            navegador.irASeleccionDeCaso();
        }));
        menu.add(Box.createVerticalStrut(10));
        menu.add(boton("Manual del detective", BotonNoir.Estilo.SECUNDARIO, () -> {
            confirmarNombre.run();
            navegador.irAManual();
        }));
        menu.add(Box.createVerticalStrut(10));
        menu.add(boton("Ranking de casos cerrados", BotonNoir.Estilo.SECUNDARIO, navegador::irARanking));
        menu.add(Box.createVerticalStrut(10));
        menu.add(boton("Salir", BotonNoir.Estilo.PELIGRO, navegador::salir));
        nombre.addActionListener(e -> {
            confirmarNombre.run();
            navegador.irASeleccionDeCaso();
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.insets = new Insets(0, 90, 90, 0);
        add(menu, c);

        animacion = new Timer(33, e -> avanzar());
        animacion.start();
    }

    /** Detiene la animación cuando la portada deja de verse (no gasta CPU en segundo plano). */
    public void detener() {
        animacion.stop();
    }

    private static Component alinear(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private Component boton(String texto, BotonNoir.Estilo estilo, Runnable accion) {
        BotonNoir b = new BotonNoir(texto, estilo);
        b.setFont(Tema.negrita(15));
        Dimension d = new Dimension(280, 44);
        b.setPreferredSize(d);
        b.setMaximumSize(d);
        b.addActionListener(e -> accion.run());
        return alinear(b);
    }

    // ------------------------------------------------------------------ animación

    private void avanzar() {
        cuadro++;
        int w = Math.max(getWidth(), 1);
        int h = Math.max(getHeight(), 1);
        for (int i = 0; i < GOTAS; i++) {
            gotaY[i] += gotaV[i];
            gotaX[i] -= gotaV[i] * 0.25f;
            if (gotaY[i] > h || gotaX[i] < -20) {
                reiniciarGota(i, false);
                gotaX[i] = azar.nextFloat() * (w + 200);
            }
        }
        if (relampago > 0) {
            relampago = Math.max(0, relampago - 0.06f);
        } else if (azar.nextInt(260) == 0) {
            relampago = 0.55f;
        }
        repaint();
    }

    private void reiniciarGota(int i, boolean inicial) {
        gotaX[i] = azar.nextFloat() * 1600;
        gotaY[i] = inicial ? azar.nextFloat() * 900 : -20 - azar.nextFloat() * 100;
        gotaV[i] = 9 + azar.nextFloat() * 9;
    }

    private int[][] generarEdificios() {
        Random r = new Random(1840);
        int[][] lista = new int[34][3];
        int x = 0;
        for (int i = 0; i < lista.length; i++) {
            int ancho = 50 + r.nextInt(90);
            lista[i][0] = x;
            lista[i][1] = ancho;
            lista[i][2] = 120 + r.nextInt(280);
            x += ancho + r.nextInt(8);
        }
        return lista;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();

        g2.setPaint(new GradientPaint(0, 0, new Color(0x07080C), 0, h, new Color(0x1B2130)));
        g2.fillRect(0, 0, w, h);
        dibujarLuna(g2, w);
        dibujarCiudad(g2, w, h);
        int faroX = (int) (w * 0.74);
        dibujarFarol(g2, faroX, h);
        dibujarDetective(g2, faroX - 70, h);
        dibujarLluvia(g2);
        dibujarNiebla(g2, w, h);
        dibujarTitulo(g2, w, h);
        if (relampago > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, relampago * 0.5f));
            g2.setColor(new Color(0xCFD8FF));
            g2.fillRect(0, 0, w, h);
        }
        g2.dispose();
    }

    private void dibujarLuna(Graphics2D g2, int w) {
        int x = (int) (w * 0.6);
        g2.setPaint(new RadialGradientPaint(new Point2D.Float(x + 40, 110), 140,
                new float[]{0f, 1f}, new Color[]{new Color(255, 245, 220, 60), new Color(0, 0, 0, 0)}));
        g2.fillOval(x - 100, -30, 280, 280);
        g2.setColor(new Color(0xE9E2CF));
        g2.fillOval(x, 70, 80, 80);
        g2.setColor(new Color(0x0B0D13));
        g2.fillOval(x + 22, 62, 80, 80);
    }

    private void dibujarCiudad(Graphics2D g2, int w, int h) {
        int base = h - 120;
        Random ventanas = new Random(7);
        for (int[] e : edificios) {
            if (e[0] > w) {
                break;
            }
            g2.setColor(new Color(0x0C0F16));
            g2.fillRect(e[0], base - e[2], e[1], e[2] + 120);
            for (int vy = base - e[2] + 14; vy < base - 10; vy += 22) {
                for (int vx = e[0] + 8; vx < e[0] + e[1] - 12; vx += 18) {
                    boolean encendida = ventanas.nextInt(9) == 0;
                    boolean parpadeo = encendida && ((cuadro / 45 + vx + vy) % 37 == 0);
                    if (encendida && !parpadeo) {
                        g2.setColor(new Color(212, 162, 76, 150));
                        g2.fillRect(vx, vy, 8, 11);
                    }
                }
            }
        }
        g2.setColor(new Color(0x08090D));
        g2.fillRect(0, base, w, h - base);
    }

    private void dibujarFarol(Graphics2D g2, int x, int h) {
        int suelo = h - 60;
        g2.setPaint(new RadialGradientPaint(new Point2D.Float(x, suelo - 300), 360,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(255, 200, 110, 110), new Color(212, 162, 76, 35), new Color(0, 0, 0, 0)}));
        Polygon cono = new Polygon(new int[]{x - 18, x + 18, x + 220, x - 220},
                new int[]{suelo - 300, suelo - 300, suelo, suelo}, 4);
        g2.fill(cono);
        g2.setColor(new Color(0x050608));
        g2.fillRect(x - 5, suelo - 300, 10, 300);
        g2.fillRect(x - 22, suelo - 6, 44, 8);
        g2.fillRoundRect(x - 20, suelo - 330, 40, 34, 8, 8);
        g2.setColor(new Color(255, 214, 140));
        g2.fillRect(x - 14, suelo - 322, 28, 22);
        g2.setPaint(new RadialGradientPaint(new Point2D.Float(x, suelo), 230,
                new float[]{0f, 1f}, new Color[]{new Color(212, 162, 76, 70), new Color(0, 0, 0, 0)}));
        g2.fillOval(x - 230, suelo - 30, 460, 60);
    }

    private void dibujarDetective(Graphics2D g2, int x, int h) {
        int suelo = h - 60;
        g2.setColor(new Color(0x030305));
        // sombra en el piso
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2.fillOval(x - 60, suelo - 8, 150, 16);
        g2.setComposite(AlphaComposite.SrcOver);
        // piernas
        g2.fillRect(x - 14, suelo - 70, 12, 70);
        g2.fillRect(x + 6, suelo - 70, 12, 70);
        // gabardina
        Polygon saco = new Polygon(new int[]{x - 30, x + 34, x + 46, x - 42},
                new int[]{suelo - 190, suelo - 190, suelo - 60, suelo - 60}, 4);
        g2.fill(saco);
        // hombros y cabeza
        g2.fillRoundRect(x - 36, suelo - 200, 72, 30, 26, 26);
        g2.fillOval(x - 15, suelo - 236, 30, 38);
        // sombrero
        g2.fillOval(x - 34, suelo - 236, 68, 14);
        g2.fillRoundRect(x - 18, suelo - 260, 36, 30, 10, 10);
        // borde iluminado por el farol
        g2.setColor(new Color(212, 162, 76, 120));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x + 34, suelo - 188, x + 46, suelo - 62);
        g2.drawArc(x - 34, suelo - 236, 68, 14, -60, 120);
        // brasa del cigarrillo
        float brillo = 0.5f + 0.5f * (float) Math.sin(cuadro / 9.0);
        g2.setColor(new Color(1f, 0.45f, 0.15f, 0.4f + 0.6f * brillo));
        g2.fillOval(x + 16, suelo - 210, 5, 5);
    }

    private void dibujarLluvia(Graphics2D g2) {
        g2.setStroke(new BasicStroke(1.1f));
        g2.setColor(new Color(170, 185, 210, 80));
        for (int i = 0; i < GOTAS; i++) {
            int x = (int) gotaX[i];
            int y = (int) gotaY[i];
            g2.drawLine(x, y, x - 4, y + 14);
        }
    }

    private void dibujarNiebla(Graphics2D g2, int w, int h) {
        g2.setPaint(new GradientPaint(0, h - 220, new Color(15, 17, 21, 0), 0, h, new Color(15, 17, 21, 235)));
        g2.fillRect(0, h - 220, w, 220);
    }

    private void dibujarTitulo(Graphics2D g2, int w, int h) {
        int x = 90;
        int y = Math.max(200, h / 2 - 140);
        g2.setFont(Tema.negrita(14));
        g2.setColor(Tema.TEXTO_SUAVE);
        g2.drawString("ARCHIVO DE INVESTIGACIONES · CONFIDENCIAL", x + 4, y - fmTitulo(g2, w) - 12);

        Font fuente = Tema.titulo(Math.min(96, w / 11f));
        g2.setFont(fuente);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(0, 0, 0, 180));
        g2.drawString("EXPEDIENTES", x + 4, y + 4);
        g2.setPaint(new GradientPaint(0, y - fm.getAscent(), new Color(0xF2CE85), 0, y, Tema.AMBAR_OSCURO));
        g2.drawString("EXPEDIENTES", x, y);

        g2.setFont(Tema.serif(26));
        g2.setColor(Tema.TEXTO);
        g2.drawString("Casos reales · Método real", x + 6, y + 44);

        // sello
        g2.setFont(Tema.negrita(13));
        g2.setStroke(new BasicStroke(2.2f));
        g2.setColor(new Color(192, 71, 63, 200));
        int sx = x + 440;
        g2.rotate(Math.toRadians(-8), sx + 70, y + 80);
        g2.drawRoundRect(sx, y + 64, 170, 30, 6, 6);
        g2.drawString("RESUELTO EN 1892", sx + 22, y + 84);
        g2.rotate(Math.toRadians(8), sx + 70, y + 80);

        g2.setFont(Tema.texto(13));
        g2.setColor(Tema.TEXTO_SUAVE);
        String pie = "Java · Prolog · SQLite  —  Inspirado en investigaciones históricas resueltas";
        g2.drawString(pie, w - g2.getFontMetrics().stringWidth(pie) - 30, h - 24);
    }

    private static int fmTitulo(Graphics2D g2, int w) {
        return g2.getFontMetrics(Tema.titulo(Math.min(96, w / 11f))).getAscent();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1200, 760);
    }
}
