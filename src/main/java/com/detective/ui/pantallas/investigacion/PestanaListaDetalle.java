package com.detective.ui.pantallas.investigacion;

import com.detective.ui.Tema;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.Objects;

/**
 * Plantilla maestro-detalle reutilizable (patrón Template Method): las subclases sólo dicen
 * qué elementos listar, cómo rotularlos y qué mostrar del seleccionado.
 */
public abstract class PestanaListaDetalle<T> implements Pestana {

    protected final ContextoPestanas ctx;
    private final DefaultListModel<T> modelo = new DefaultListModel<>();
    private final JList<T> lista = new JList<>(modelo);
    private final JPanel detalle = Componentes.panel(new BorderLayout());
    private final JSplitPane raiz;
    private boolean actualizando;

    protected PestanaListaDetalle(ContextoPestanas ctx) {
        this.ctx = ctx;
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setBackground(Tema.FONDO_PANEL);
        lista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> l, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel c = (JLabel) super.getListCellRendererComponent(l, rotulo((T) value), index,
                        isSelected, cellHasFocus);
                c.setBorder(BorderFactory.createEmptyBorder(9, 10, 9, 10));
                c.setFont(Tema.texto(14));
                return c;
            }
        });
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !actualizando) {
                mostrarDetalle();
            }
        });
        detalle.setBorder(Tema.margen(12));
        raiz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, Componentes.desplazable(lista), detalle);
        raiz.setDividerLocation(330);
        raiz.setBorder(null);
    }

    protected abstract List<T> elementos();

    protected abstract String rotulo(T elemento);

    protected abstract JComponent detalleDe(T elemento);

    protected String mensajeVacio() {
        return "Nada para mostrar todavía.";
    }

    @Override
    public JComponent componente() {
        return raiz;
    }

    @Override
    public void actualizar() {
        T seleccionado = lista.getSelectedValue();
        actualizando = true;
        modelo.clear();
        elementos().forEach(modelo::addElement);
        int indice = -1;
        for (int i = 0; i < modelo.size(); i++) {
            if (seleccionado != null && Objects.equals(clave(modelo.get(i)), clave(seleccionado))) {
                indice = i;
            }
        }
        if (indice < 0 && !modelo.isEmpty()) {
            indice = 0;
        }
        if (indice >= 0) {
            lista.setSelectedIndex(indice);
        }
        actualizando = false;
        lista.repaint();
        mostrarDetalle();
    }

    /** Identidad estable del elemento para conservar la selección entre actualizaciones. */
    protected abstract Object clave(T elemento);

    private void mostrarDetalle() {
        detalle.removeAll();
        T sel = lista.getSelectedValue();
        if (sel == null) {
            detalle.add(Componentes.suave(mensajeVacio()), BorderLayout.NORTH);
        } else {
            detalle.add(detalleDe(sel), BorderLayout.CENTER);
        }
        detalle.revalidate();
        detalle.repaint();
    }
}
