package com.detective.ui;

import com.detective.app.ContenedorAplicacion;
import com.detective.domain.investigacion.Investigacion;
import com.detective.servicio.ResultadoCierre;
import com.detective.ui.pantallas.PantallaInvestigacion;
import com.detective.ui.pantallas.PantallaManual;
import com.detective.ui.pantallas.PantallaPortada;
import com.detective.ui.pantallas.PantallaRanking;
import com.detective.ui.pantallas.PantallaResultado;
import com.detective.ui.pantallas.PantallaSeleccionCaso;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Ventana única: cambia de pantalla liberando la anterior (timers, suscripciones). */
public final class VentanaPrincipal extends JFrame implements Navegador {

    private final ContenedorAplicacion app;
    private final Sesion sesion = new Sesion();
    private JComponent actual;

    public VentanaPrincipal(ContenedorAplicacion app) {
        super("Expedientes: Casos Reales");
        this.app = app;
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });
        setIconImage(IconoAplicacion.crear());
        setMinimumSize(new Dimension(1100, 720));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        irAPortada();
    }

    private void mostrar(JComponent pantalla) {
        if (actual instanceof Liberable l) {
            l.liberar();
        }
        actual = pantalla;
        setContentPane(pantalla);
        revalidate();
        repaint();
    }

    @Override
    public void irAPortada() {
        mostrar(new PantallaPortada(this, sesion, app.ajustes()));
    }

    @Override
    public void irASeleccionDeCaso() {
        mostrar(new PantallaSeleccionCaso(this, sesion, app.casos(), app.partidas(), app.puntajes(),
                app.investigacion()));
    }

    @Override
    public void irAInvestigacion(Investigacion investigacion) {
        mostrar(new PantallaInvestigacion(this, app.investigacion(), investigacion));
    }

    @Override
    public void irAResultado(Investigacion investigacion, ResultadoCierre cierre) {
        mostrar(new PantallaResultado(this, investigacion, cierre));
    }

    @Override
    public void irARanking() {
        mostrar(new PantallaRanking(this, app.casos(), app.puntajes()));
    }

    @Override
    public void irAManual() {
        mostrar(new PantallaManual(this, app.casos()));
    }

    @Override
    public void salir() {
        if (actual instanceof Liberable l) {
            l.liberar();
        }
        dispose();
        app.close();
        System.exit(0);
    }
}
