package com.detective.ui.pantallas;

import com.detective.domain.model.EntradaManual;
import com.detective.domain.repositorio.CasoRepositorio;
import com.detective.ui.Navegador;
import com.detective.ui.Tema;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

/** Manual con los procedimientos reales que inspiran cada mecánica del juego. */
public final class PantallaManual extends JPanel {

    public PantallaManual(Navegador navegador, CasoRepositorio casos) {
        super(new BorderLayout());
        setBackground(Tema.FONDO);
        add(Encabezado.crear("Manual del detective",
                "Procedimientos reales de investigación criminal en los que se basa el juego",
                "← Portada", navegador::irAPortada), BorderLayout.NORTH);

        List<EntradaManual> entradas = casos.manualDelDetective();
        JList<EntradaManual> indice = new JList<>(entradas.toArray(new EntradaManual[0]));
        indice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        indice.setBackground(Tema.FONDO_PANEL);
        indice.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, ((EntradaManual) value).titulo(),
                        index, isSelected, cellHasFocus);
                l.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
                l.setFont(Tema.serif(15));
                return l;
            }
        });

        JTextArea contenido = Componentes.papel("");
        contenido.setFont(Tema.maquina(15));
        indice.addListSelectionListener(e -> {
            EntradaManual sel = indice.getSelectedValue();
            if (sel != null) {
                contenido.setText(sel.titulo().toUpperCase() + "\n\n" + sel.contenido());
                contenido.setCaretPosition(0);
            }
        });
        if (!entradas.isEmpty()) {
            indice.setSelectedIndex(0);
        }
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                Componentes.desplazable(indice), Componentes.desplazable(contenido));
        split.setDividerLocation(360);
        split.setBorder(Tema.margen(16));
        split.setBackground(Tema.FONDO);
        add(split, BorderLayout.CENTER);
    }
}
