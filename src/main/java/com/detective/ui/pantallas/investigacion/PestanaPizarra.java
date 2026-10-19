package com.detective.ui.pantallas.investigacion;

import com.detective.domain.model.Deduccion;
import com.detective.domain.model.Sospechoso;
import com.detective.domain.model.TipoDeduccion;
import com.detective.ui.Tema;
import com.detective.ui.componentes.Componentes;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.stream.Collectors;

/** Pizarra de deducciones: lo que el motor Prolog infiere de lo descubierto, sin acceso a la verdad. */
public final class PestanaPizarra implements Pestana {

    private static final String SI = "√";
    private static final String NO = "—";

    private final ContextoPestanas ctx;
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Sospechoso", "Motivo", "Medio", "Oportunidad", "Contradicciones", "Coartada", "Vínculos", "Conclusión"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTextArea detalle = Componentes.papel("");
    private final JPanel raiz = Componentes.panel(new BorderLayout(0, 10));

    public PestanaPizarra(ContextoPestanas ctx) {
        this.ctx = ctx;
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(34);
        tabla.setFont(Tema.texto(14));
        tabla.getTableHeader().setFont(Tema.negrita(13));
        tabla.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setHorizontalAlignment(c == 0 || c == 7 ? SwingConstants.LEFT : SwingConstants.CENTER);
                String txt = String.valueOf(v);
                if (!sel) {
                    comp.setForeground(txt.startsWith(SI) || txt.startsWith("SOSPECHOSO") ? Tema.AMBAR
                            : txt.startsWith("Verificada") || txt.startsWith("Descartado") ? Tema.VERDE
                            : txt.equals(NO) ? Tema.TEXTO_SUAVE : Tema.TEXTO);
                }
                return comp;
            }
        };
        tabla.setDefaultRenderer(Object.class, render);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(220);

        JPanel norte = Componentes.panel(new BorderLayout());
        norte.add(Componentes.suave("Motivo + Medio + Oportunidad sin coartada verificada = sospechoso principal. "
                + "Las inferencias usan sólo lo que vos descubriste."), BorderLayout.CENTER);
        raiz.setBorder(Tema.margen(12));
        raiz.add(norte, BorderLayout.NORTH);
        JScrollPane spTabla = new JScrollPane(tabla);
        spTabla.getViewport().setBackground(Tema.FONDO_PANEL);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spTabla, Componentes.desplazable(detalle));
        split.setResizeWeight(0.45);
        split.setBorder(null);
        raiz.add(split, BorderLayout.CENTER);
    }

    @Override
    public String titulo() {
        return "Pizarra de deducciones";
    }

    @Override
    public JComponent componente() {
        return raiz;
    }

    @Override
    public void actualizar() {
        List<Deduccion> deducciones = ctx.deduccion().deducciones(ctx.investigacion());
        modelo.setRowCount(0);
        for (Sospechoso s : ctx.investigacion().getExpediente().sospechosos()) {
            List<Deduccion> propias = deducciones.stream().filter(d -> s.id().equals(d.sospechosoId())).toList();
            boolean principal = contiene(propias, TipoDeduccion.SOSPECHOSO_PRINCIPAL);
            boolean descartado = contiene(propias, TipoDeduccion.DESCARTE);
            String conclusion = principal ? "SOSPECHOSO PRINCIPAL" : descartado ? "Descartado (coartada)"
                    : ctx.investigacion().fueInterrogado(s.id()) ? "Faltan elementos" : "Sin entrevistar";
            modelo.addRow(new Object[]{
                    s.nombre(),
                    marca(propias, TipoDeduccion.MOTIVO),
                    marca(propias, TipoDeduccion.MEDIO),
                    marca(propias, TipoDeduccion.OPORTUNIDAD),
                    cantidad(propias, TipoDeduccion.CONTRADICCION),
                    descartado ? "Verificada" : NO,
                    cantidad(propias, TipoDeduccion.VINCULO),
                    conclusion});
        }
        StringBuilder sb = new StringBuilder("CONCLUSIONES DEL MOTOR LÓGICO\n\n");
        if (deducciones.isEmpty()) {
            sb.append("Todavía no hay hechos suficientes para deducir nada. Salí a investigar.");
        }
        for (TipoDeduccion tipo : TipoDeduccion.values()) {
            List<Deduccion> deTipo = deducciones.stream().filter(d -> d.tipo() == tipo).toList();
            if (deTipo.isEmpty()) {
                continue;
            }
            sb.append(tipo.getEtiqueta().toUpperCase()).append('\n');
            sb.append(deTipo.stream()
                    .map(d -> "  • " + ctx.deduccion().nombreDe(ctx.investigacion(), d.sospechosoId()) + ": " + d.detalle())
                    .distinct()
                    .collect(Collectors.joining("\n")));
            sb.append("\n\n");
        }
        detalle.setText(sb.toString());
        detalle.setCaretPosition(0);
    }

    private static boolean contiene(List<Deduccion> l, TipoDeduccion t) {
        return l.stream().anyMatch(d -> d.tipo() == t);
    }

    private static String marca(List<Deduccion> l, TipoDeduccion t) {
        return contiene(l, t) ? SI : NO;
    }

    private static String cantidad(List<Deduccion> l, TipoDeduccion t) {
        long n = l.stream().filter(d -> d.tipo() == t).count();
        return n == 0 ? NO : SI + " " + n;
    }
}
