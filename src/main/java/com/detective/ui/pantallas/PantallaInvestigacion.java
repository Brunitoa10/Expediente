package com.detective.ui.pantallas;

import com.detective.domain.evento.EventoInvestigacion;
import com.detective.domain.evento.ObservadorInvestigacion;
import com.detective.domain.evento.TipoEvento;
import com.detective.domain.investigacion.AccionInvalidaException;
import com.detective.domain.investigacion.AccionInvestigacion;
import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.investigacion.ResultadoAccion;
import com.detective.servicio.ResultadoCierre;
import com.detective.servicio.ServicioInvestigacion;
import com.detective.ui.Liberable;
import com.detective.ui.Navegador;
import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.DialogoExpediente;
import com.detective.ui.componentes.RelojInvestigacion;
import com.detective.ui.pantallas.investigacion.ContextoPestanas;
import com.detective.ui.pantallas.investigacion.Pestana;
import com.detective.ui.pantallas.investigacion.PestanaBitacora;
import com.detective.ui.pantallas.investigacion.PestanaConsolaProlog;
import com.detective.ui.pantallas.investigacion.PestanaEvidencias;
import com.detective.ui.pantallas.investigacion.PestanaExpediente;
import com.detective.ui.pantallas.investigacion.PestanaLugares;
import com.detective.ui.pantallas.investigacion.PestanaPizarra;
import com.detective.ui.pantallas.investigacion.PestanaSospechosos;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.util.List;

/** Mesa de trabajo del detective: pestañas modulares + barra de tiempo y acciones globales. */
public final class PantallaInvestigacion extends JPanel implements Liberable, ObservadorInvestigacion {

    private final Navegador navegador;
    private final ServicioInvestigacion servicio;
    private final Investigacion investigacion;
    private final RelojInvestigacion reloj = new RelojInvestigacion();
    private final List<Pestana> pestanas;
    private boolean avisoTiempoPendiente;

    public PantallaInvestigacion(Navegador navegador, ServicioInvestigacion servicio, Investigacion investigacion) {
        super(new BorderLayout());
        this.navegador = navegador;
        this.servicio = servicio;
        this.investigacion = investigacion;
        setBackground(Tema.FONDO);

        ContextoPestanas ctx = new ContextoPestanas(investigacion, servicio.deduccion(), this::ejecutar);
        pestanas = List.of(new PestanaExpediente(ctx), new PestanaLugares(ctx), new PestanaEvidencias(ctx),
                new PestanaSospechosos(ctx), new PestanaPizarra(ctx), new PestanaConsolaProlog(ctx),
                new PestanaBitacora(ctx));

        add(barraSuperior(), BorderLayout.NORTH);
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Tema.negrita(14));
        tabs.setForeground(Tema.FONDO);
        pestanas.forEach(p -> tabs.addTab(p.titulo(), p.componente()));
        tabs.setBorder(Tema.margen(10));
        add(tabs, BorderLayout.CENTER);

        servicio.suscribir(this);
        actualizarTodo();
    }

    private JPanel barraSuperior() {
        var caso = investigacion.getExpediente().caso();
        BotonNoir pista = new BotonNoir("Consultar al comisario");
        pista.setToolTipText("Sugiere el próximo paso (resta puntos)");
        pista.addActionListener(e -> pedirPista());
        BotonNoir acusar = new BotonNoir("Acusar", BotonNoir.Estilo.PELIGRO);
        acusar.addActionListener(e -> acusar());
        JPanel barra = Encabezado.crear(caso.titulo(), caso.subtitulo() + " · " + caso.lugarYEpoca(),
                "Guardar y salir", () -> navegador.irASeleccionDeCaso(), reloj, pista, acusar);
        return barra;
    }

    // ------------------------------------------------------------ acciones

    private void ejecutar(AccionInvestigacion accion) {
        try {
            ResultadoAccion r = servicio.ejecutar(investigacion, accion);
            actualizarTodo();
            DialogoExpediente.mostrar(this, r.titulo(), r.texto() + "\n\n(" + r.horasConsumidas() + " h)");
            if (avisoTiempoPendiente) {
                avisoTiempoPendiente = false;
                DialogoExpediente.mostrar(this, "Se terminó el plazo",
                        "Se agotaron las horas de investigación. Tenés que acusar con lo que reuniste.", Tema.ROJO);
                acusar();
            }
        } catch (AccionInvalidaException ex) {
            DialogoExpediente.advertir(this, ex.getMessage());
        }
    }

    private void pedirPista() {
        if (JOptionPane.showConfirmDialog(this, "El comisario te va a orientar, pero cada consulta resta 75 puntos. ¿Consultar?",
                "Consultar al comisario", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            DialogoExpediente.mostrar(this, "El comisario dice...", servicio.pedirPista(investigacion));
            actualizarTodo();
        }
    }

    private void acusar() {
        DialogoAcusacion.pedir(this, investigacion).ifPresent(a -> {
            ResultadoCierre cierre = servicio.acusar(investigacion, a.sospechosoId(), a.pruebas());
            navegador.irAResultado(investigacion, cierre);
        });
    }

    private void actualizarTodo() {
        reloj.actualizar(investigacion.getEstado().getHorasUsadas(), investigacion.getExpediente().caso().horasMaximas());
        pestanas.forEach(Pestana::actualizar);
    }

    // ------------------------------------------------------------ observador

    @Override
    public void alOcurrir(EventoInvestigacion evento) {
        if (evento.investigacion() == investigacion && evento.tipo() == TipoEvento.TIEMPO_AGOTADO) {
            avisoTiempoPendiente = true;
        }
    }

    @Override
    public void liberar() {
        servicio.desuscribir(this);
    }
}
