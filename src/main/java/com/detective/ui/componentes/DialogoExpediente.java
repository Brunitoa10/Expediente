package com.detective.ui.componentes;

import com.detective.ui.Tema;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

/** Ventana modal con estilo de hoja de expediente para mostrar resultados de acciones. */
public final class DialogoExpediente {

    private DialogoExpediente() {
    }

    public static void mostrar(Component padre, String titulo, String texto) {
        mostrar(padre, titulo, texto, Tema.AMBAR);
    }

    public static void advertir(Component padre, String texto) {
        mostrar(padre, "Procedimiento", texto, Tema.ROJO);
    }

    public static void mostrar(Component padre, String titulo, String texto, java.awt.Color acento) {
        Window ventana = padre == null ? null : SwingUtilities.getWindowAncestor(padre);
        JDialog d = new JDialog(ventana, titulo, JDialog.ModalityType.APPLICATION_MODAL);
        JPanel raiz = Componentes.panel(new BorderLayout(0, 12));
        raiz.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, acento), Tema.margen(18)));
        raiz.add(Componentes.titulo(titulo, 20), BorderLayout.NORTH);
        JTextArea area = Componentes.papel(texto);
        JScrollPane sp = Componentes.desplazable(area);
        sp.setPreferredSize(new Dimension(560, Math.min(420, 120 + texto.length() / 3)));
        raiz.add(sp, BorderLayout.CENTER);
        BotonNoir ok = new BotonNoir("Entendido", BotonNoir.Estilo.PRIMARIO);
        ok.addActionListener(e -> d.dispose());
        JPanel sur = Componentes.panel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(ok);
        raiz.add(sur, BorderLayout.SOUTH);
        d.setContentPane(raiz);
        d.getRootPane().setDefaultButton(ok);
        d.pack();
        d.setLocationRelativeTo(ventana);
        d.setVisible(true);
    }
}
