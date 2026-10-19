package com.detective.app;

import com.detective.ui.Tema;
import com.detective.ui.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/** Punto de entrada: Expedientes - Casos Reales. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> {
            error.printStackTrace();
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null,
                    "Ocurrió un error inesperado:\n" + error, "Expedientes", JOptionPane.ERROR_MESSAGE));
        });
        SwingUtilities.invokeLater(() -> {
            Tema.instalar();
            ContenedorAplicacion app;
            try {
                app = new ContenedorAplicacion();
            } catch (RuntimeException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "No se pudo iniciar la base de datos:\n" + e.getMessage(),
                        "Expedientes", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new VentanaPrincipal(app).setVisible(true);
        });
    }
}
