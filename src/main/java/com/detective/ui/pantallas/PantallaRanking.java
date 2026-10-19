package com.detective.ui.pantallas;

import com.detective.domain.model.Caso;
import com.detective.domain.model.RegistroPuntaje;
import com.detective.domain.model.Veredicto;
import com.detective.domain.repositorio.CasoRepositorio;
import com.detective.domain.repositorio.PuntajeRepositorio;
import com.detective.ui.Navegador;
import com.detective.ui.Tema;
import com.detective.ui.componentes.Componentes;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class PantallaRanking extends JPanel {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private record OpcionCaso(String id, String titulo) {
        @Override
        public String toString() {
            return titulo;
        }
    }

    public PantallaRanking(Navegador navegador, CasoRepositorio casos, PuntajeRepositorio puntajes) {
        super(new BorderLayout());
        setBackground(Tema.FONDO);
        add(Encabezado.crear("Ranking de casos cerrados", "Los mejores detectives de este equipo",
                "← Portada", navegador::irAPortada), BorderLayout.NORTH);

        List<OpcionCaso> opciones = new ArrayList<>();
        opciones.add(new OpcionCaso(null, "Todos los casos"));
        java.util.Set<String> conPuntaje = new java.util.HashSet<>();
        puntajes.mejores(10_000, null).forEach(r -> conPuntaje.add(r.casoId()));
        for (Caso c : casos.listarCasos()) {
            if (conPuntaje.contains(c.id())) {
                opciones.add(new OpcionCaso(c.id(), "N.º " + c.orden() + " · " + c.titulo()));
            }
        }
        JComboBox<OpcionCaso> filtro = new JComboBox<>(opciones.toArray(new OpcionCaso[0]));

        DefaultTableModel modelo = new DefaultTableModel(
                new Object[]{"#", "Detective", "Caso", "Veredicto", "Puntos", "Horas", "Fecha"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setFont(Tema.texto(14));
        tabla.getTableHeader().setFont(Tema.negrita(13));
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel && c == 3) {
                    String txt = String.valueOf(v);
                    comp.setForeground(txt.startsWith("CASO") ? Tema.VERDE : txt.startsWith("ERROR") ? Tema.ROJO : Tema.AMBAR);
                } else if (!sel) {
                    comp.setForeground(Tema.TEXTO);
                }
                return comp;
            }
        });
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);

        Runnable recargar = () -> {
            modelo.setRowCount(0);
            OpcionCaso sel = (OpcionCaso) filtro.getSelectedItem();
            int pos = 1;
            for (RegistroPuntaje r : puntajes.mejores(50, sel == null ? null : sel.id())) {
                modelo.addRow(new Object[]{pos++, r.jugador(), r.casoTitulo(), titulo(r.veredicto()),
                        r.puntos(), r.horasUsadas(), r.fecha().format(FECHA)});
            }
        };
        filtro.addActionListener(e -> recargar.run());
        recargar.run();

        JPanel centro = Componentes.panel(new BorderLayout(0, 10));
        centro.setBorder(Tema.margen(20));
        JPanel barraFiltro = Componentes.panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barraFiltro.add(Componentes.suave("Filtrar por caso:"));
        barraFiltro.add(filtro);
        centro.add(barraFiltro, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(tabla);
        sp.getViewport().setBackground(Tema.FONDO_PANEL);
        centro.add(sp, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    private static String titulo(Veredicto v) {
        return v.getTitulo();
    }
}
