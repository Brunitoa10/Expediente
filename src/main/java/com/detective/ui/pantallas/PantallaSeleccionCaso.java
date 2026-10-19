package com.detective.ui.pantallas;

import com.detective.domain.model.Caso;
import com.detective.domain.model.Dificultad;
import com.detective.domain.model.Veredicto;
import com.detective.domain.repositorio.CasoRepositorio;
import com.detective.domain.repositorio.PartidaRepositorio;
import com.detective.domain.repositorio.PuntajeRepositorio;
import com.detective.servicio.ServicioInvestigacion;
import com.detective.ui.Navegador;
import com.detective.ui.Sesion;
import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/** Archivo de expedientes: más de mil casos con búsqueda, filtros y estado por jugador. */
public final class PantallaSeleccionCaso extends JPanel {

    private static final String TODAS = "Todas";

    private enum Estado {
        NUEVO("Nuevo"), EN_CURSO("En curso"), RESUELTO("Resuelto"), INCOMPLETO("Sin condena");

        private final String etiqueta;

        private Estado(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    private record Fila(Caso caso, Estado estado, String textoBusqueda) {
    }

    private final Navegador navegador;
    private final Sesion sesion;
    private final ServicioInvestigacion servicio;
    private final List<Fila> filas = new ArrayList<>();
    private final ModeloCasos modelo = new ModeloCasos();
    private final JTable tabla = new JTable(modelo);
    private final TableRowSorter<ModeloCasos> ordenador = new TableRowSorter<>(modelo);
    private final JPanel detalle = Componentes.panel(new BorderLayout());
    private final JLabel contador = Componentes.suave("");
    private final JTextField busqueda = new JTextField(18);
    private final JComboBox<String> categoria = new JComboBox<>();
    private final JComboBox<Object> dificultad = new JComboBox<>();
    private final JComboBox<Object> estado = new JComboBox<>();

    public PantallaSeleccionCaso(Navegador navegador, Sesion sesion, CasoRepositorio casos,
                                 PartidaRepositorio partidas, PuntajeRepositorio puntajes,
                                 ServicioInvestigacion servicio) {
        super(new BorderLayout());
        this.navegador = navegador;
        this.sesion = sesion;
        this.servicio = servicio;
        setBackground(Tema.FONDO);

        cargarFilas(casos.listarCasos(), partidas.casosEnCurso(sesion.getJugador()),
                puntajes.mejorVeredictoPorCaso(sesion.getJugador()));

        BotonNoir azar = new BotonNoir("Caso al azar", BotonNoir.Estilo.PRIMARIO);
        azar.setToolTipText("Abre un expediente nuevo elegido al azar entre los filtrados");
        azar.addActionListener(e -> casoAlAzar());
        add(Encabezado.crear("Archivo de expedientes",
                "A cargo: " + sesion.getJugador() + " · " + filas.size()
                        + " casos inspirados en técnicas que resolvieron investigaciones reales",
                "← Portada", navegador::irAPortada, azar), BorderLayout.NORTH);

        JPanel centro = Componentes.panel(new BorderLayout(0, 10));
        centro.setBorder(Tema.margen(14));
        centro.add(barraFiltros(), BorderLayout.NORTH);
        configurarTabla();
        JScrollPane sp = new JScrollPane(tabla);
        sp.getViewport().setBackground(Tema.FONDO_PANEL);
        detalle.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, detalle);
        split.setResizeWeight(0.62);
        split.setBorder(null);
        centro.add(split, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        aplicarFiltros();
        if (tabla.getRowCount() > 0) {
            tabla.setRowSelectionInterval(0, 0);
        }
    }

    // ------------------------------------------------------------------ datos

    private void cargarFilas(List<Caso> casos, Set<String> enCurso, Map<String, Veredicto> cerrados) {
        Set<String> categorias = new TreeSet<>();
        for (Caso c : casos) {
            Estado e = enCurso.contains(c.id()) ? Estado.EN_CURSO
                    : cerrados.get(c.id()) == Veredicto.CONDENA ? Estado.RESUELTO
                    : cerrados.containsKey(c.id()) ? Estado.INCOMPLETO : Estado.NUEVO;
            String texto = normalizar(String.join(" ", c.titulo(), c.subtitulo(), c.lugarYEpoca(),
                    c.categoria(), c.resumen(), String.valueOf(c.orden())));
            filas.add(new Fila(c, e, texto));
            categorias.add(c.categoria());
        }
        categoria.addItem(TODAS);
        categorias.forEach(categoria::addItem);
        dificultad.addItem(TODAS);
        for (Dificultad d : Dificultad.values()) {
            dificultad.addItem(d);
        }
        estado.addItem(TODAS);
        for (Estado e : Estado.values()) {
            estado.addItem(e);
        }
    }

    private JPanel barraFiltros() {
        JPanel barra = Componentes.panel(new BorderLayout());
        JPanel filtros = Componentes.panel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        busqueda.setFont(Tema.texto(14));
        busqueda.setToolTipText("Buscá por título, ciudad, año, técnica o número");
        busqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltros();
            }
        });
        filtros.add(Componentes.suave("Buscar:"));
        filtros.add(busqueda);
        filtros.add(Componentes.suave("Categoría:"));
        filtros.add(categoria);
        filtros.add(Componentes.suave("Dificultad:"));
        filtros.add(dificultad);
        filtros.add(Componentes.suave("Estado:"));
        filtros.add(estado);
        categoria.addActionListener(e -> aplicarFiltros());
        dificultad.addActionListener(e -> aplicarFiltros());
        estado.addActionListener(e -> aplicarFiltros());
        barra.add(filtros, BorderLayout.CENTER);
        barra.add(contador, BorderLayout.EAST);
        return barra;
    }

    private void configurarTabla() {
        tabla.setRowSorter(ordenador);
        tabla.setRowHeight(28);
        tabla.setFont(Tema.texto(14));
        tabla.getTableHeader().setFont(Tema.negrita(13));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int[] anchos = {55, 340, 140, 220, 75, 55, 95};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (v instanceof Dificultad d) {
                    setText(d.getEtiqueta());
                }
                if (!sel) {
                    comp.setForeground(v instanceof Estado e ? colorEstado(e)
                            : v instanceof Dificultad d ? colorDificultad(d) : Tema.TEXTO);
                }
                return comp;
            }
        });
        tabla.setDefaultRenderer(Integer.class, tabla.getDefaultRenderer(Object.class));
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && seleccionada() != null) {
                    abrir(seleccionada(), seleccionada().estado() == Estado.EN_CURSO);
                }
            }
        });
    }

    private void aplicarFiltros() {
        String texto = normalizar(busqueda.getText().trim());
        Object cat = categoria.getSelectedItem();
        Object dif = dificultad.getSelectedItem();
        Object est = estado.getSelectedItem();
        ordenador.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends ModeloCasos, ? extends Integer> entrada) {
                Fila f = filas.get(entrada.getIdentifier());
                return (texto.isEmpty() || f.textoBusqueda().contains(texto))
                        && (TODAS.equals(cat) || f.caso().categoria().equals(cat))
                        && (TODAS.equals(dif) || f.caso().dificultad() == dif)
                        && (TODAS.equals(est) || f.estado() == est);
            }
        });
        long resueltos = filas.stream().filter(f -> f.estado() == Estado.RESUELTO).count();
        contador.setText("Mostrando " + tabla.getRowCount() + " de " + filas.size()
                + "  ·  Resueltos: " + resueltos + "  ");
        if (tabla.getRowCount() > 0 && tabla.getSelectedRow() < 0) {
            tabla.setRowSelectionInterval(0, 0);
        }
        mostrarDetalle();
    }

    // ------------------------------------------------------------------ detalle y acciones

    private Fila seleccionada() {
        int vista = tabla.getSelectedRow();
        return vista < 0 ? null : filas.get(tabla.convertRowIndexToModel(vista));
    }

    private void mostrarDetalle() {
        detalle.removeAll();
        Fila f = seleccionada();
        if (f == null) {
            detalle.add(Componentes.suave("Ningún expediente coincide con los filtros."), BorderLayout.NORTH);
        } else {
            detalle.add(tarjeta(f), BorderLayout.CENTER);
        }
        detalle.revalidate();
        detalle.repaint();
    }

    private JPanel tarjeta(Fila f) {
        Caso caso = f.caso();
        JPanel t = Componentes.panelOscuro(new BorderLayout(0, 10));
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, colorDificultad(caso.dificultad())), Tema.tarjeta()));
        JPanel cab = Componentes.panelOscuro(new BorderLayout(0, 4));
        cab.add(Componentes.etiqueta("EXPEDIENTE N.º " + caso.orden() + "  ·  " + caso.lugarYEpoca().toUpperCase(),
                Tema.negrita(11), Tema.TEXTO_SUAVE), BorderLayout.NORTH);
        cab.add(Componentes.titulo(caso.titulo(), 22), BorderLayout.CENTER);
        cab.add(Componentes.etiqueta(caso.subtitulo(), Tema.serif(15), Tema.TEXTO), BorderLayout.SOUTH);
        t.add(cab, BorderLayout.NORTH);
        JTextArea resumen = Componentes.texto(caso.resumen() + "\n\nDificultad: " + caso.dificultad().getEtiqueta()
                + "   ·   Plazo: " + caso.horasMaximas() + " h   ·   Estado: " + f.estado());
        t.add(Componentes.desplazable(resumen), BorderLayout.CENTER);

        JPanel botones = Componentes.panelOscuro(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        if (f.estado() == Estado.EN_CURSO) {
            BotonNoir continuar = new BotonNoir("Continuar", BotonNoir.Estilo.PRIMARIO);
            continuar.addActionListener(e -> abrir(f, true));
            botones.add(continuar);
        }
        BotonNoir nueva = new BotonNoir(f.estado() == Estado.NUEVO ? "Abrir expediente" : "Empezar de nuevo",
                f.estado() == Estado.EN_CURSO ? BotonNoir.Estilo.SECUNDARIO : BotonNoir.Estilo.PRIMARIO);
        nueva.addActionListener(e -> abrir(f, false));
        botones.add(nueva);
        t.add(botones, BorderLayout.SOUTH);
        return t;
    }

    private void abrir(Fila f, boolean continuar) {
        if (!continuar && f.estado() == Estado.EN_CURSO && JOptionPane.showConfirmDialog(this,
                "Se perderá el progreso guardado de este caso. ¿Empezar de nuevo?", "Reiniciar caso",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        navegador.irAInvestigacion(servicio.iniciar(sesion.getJugador(), f.caso().id(), continuar));
    }

    private void casoAlAzar() {
        List<Fila> candidatas = new ArrayList<>();
        for (int i = 0; i < tabla.getRowCount(); i++) {
            Fila f = filas.get(tabla.convertRowIndexToModel(i));
            if (f.estado() == Estado.NUEVO) {
                candidatas.add(f);
            }
        }
        if (candidatas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No quedan casos nuevos con estos filtros.");
            return;
        }
        abrir(candidatas.get(new Random().nextInt(candidatas.size())), false);
    }

    // ------------------------------------------------------------------ utilidades

    private static String normalizar(String s) {
        return Normalizer.normalize(s.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private static Color colorDificultad(Dificultad d) {
        return switch (d) {
            case FACIL -> Tema.VERDE;
            case MEDIA -> Tema.AMBAR;
            case DIFICIL -> Tema.ROJO;
        };
    }

    private static Color colorEstado(Estado e) {
        return switch (e) {
            case NUEVO -> Tema.TEXTO_SUAVE;
            case EN_CURSO -> Tema.AMBAR;
            case RESUELTO -> Tema.VERDE;
            case INCOMPLETO -> Tema.ROJO;
        };
    }

    /** Modelo de tabla de sólo lectura sobre la lista de filas. */
    private final class ModeloCasos extends AbstractTableModel {

        private final String[] columnas = {"N.º", "Título", "Categoría", "Lugar y época", "Dificultad", "Plazo", "Estado"};

        @Override
        public int getRowCount() {
            return filas.size();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int c) {
            return columnas[c];
        }

        @Override
        public Class<?> getColumnClass(int c) {
            return c == 0 || c == 5 ? Integer.class : Object.class;
        }

        @Override
        public Object getValueAt(int fila, int c) {
            Fila f = filas.get(fila);
            Caso caso = f.caso();
            return switch (c) {
                case 0 -> caso.orden();
                case 1 -> caso.titulo();
                case 2 -> caso.categoria();
                case 3 -> caso.lugarYEpoca();
                case 4 -> caso.dificultad();
                case 5 -> caso.horasMaximas();
                default -> f.estado();
            };
        }
    }
}
