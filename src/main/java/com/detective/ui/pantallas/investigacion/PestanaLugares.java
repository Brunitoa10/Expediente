package com.detective.ui.pantallas.investigacion;

import com.detective.domain.investigacion.InspeccionarLugar;
import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.investigacion.RecolectarEvidencia;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.Lugar;
import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public final class PestanaLugares extends PestanaListaDetalle<Lugar> {

    public PestanaLugares(ContextoPestanas ctx) {
        super(ctx);
    }

    @Override
    public String titulo() {
        return "Lugares";
    }

    @Override
    protected List<Lugar> elementos() {
        return ctx.investigacion().getExpediente().lugares();
    }

    @Override
    protected Object clave(Lugar l) {
        return l.id();
    }

    @Override
    protected String rotulo(Lugar l) {
        Investigacion inv = ctx.investigacion();
        if (!inv.fueInspeccionado(l.id())) {
            return "□  " + l.nombre() + "   (" + l.horasInspeccion() + " h)";
        }
        long pendientes = inv.getExpediente().evidenciasEn(l.id()).stream()
                .filter(e -> !inv.fueRecolectada(e.id())).count();
        return "√  " + l.nombre() + (pendientes > 0 ? "   [" + pendientes + " sin recolectar]" : "");
    }

    @Override
    protected JComponent detalleDe(Lugar l) {
        Investigacion inv = ctx.investigacion();
        boolean inspeccionado = inv.fueInspeccionado(l.id());
        JPanel p = Componentes.panel(new BorderLayout(0, 12));
        p.add(Fichas.ficha(l.nombre(), inspeccionado ? "Lugar inspeccionado" : "Sin inspeccionar",
                l.descripcion(),
                Fichas.boton("Inspeccionar (" + l.horasInspeccion() + " h)", BotonNoir.Estilo.PRIMARIO,
                        !inspeccionado, "Recorrer el lugar con método y documentarlo",
                        () -> ctx.ejecutor().ejecutar(new InspeccionarLugar(l.id())))), BorderLayout.NORTH);
        if (inspeccionado) {
            p.add(panelEvidencias(inv, l), BorderLayout.CENTER);
        }
        return p;
    }

    private JComponent panelEvidencias(Investigacion inv, Lugar l) {
        JPanel lista = Componentes.panel(null);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.add(Componentes.etiqueta("RASTROS HALLADOS", Tema.negrita(12), Tema.TEXTO_SUAVE));
        List<Evidencia> evidencias = inv.getExpediente().evidenciasEn(l.id());
        if (evidencias.isEmpty()) {
            lista.add(Componentes.suave("No hay rastros relevantes en este lugar."));
        }
        for (Evidencia e : evidencias) {
            lista.add(filaEvidencia(inv, e));
        }
        return Componentes.desplazable(lista);
    }

    private JPanel filaEvidencia(Investigacion inv, Evidencia e) {
        JPanel fila = Componentes.panelOscuro(new BorderLayout(10, 0));
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.BORDE), Tema.margen(10)));
        JPanel textos = Componentes.panelOscuro(new BorderLayout());
        textos.add(Componentes.etiqueta(e.nombre(), Tema.negrita(14), Tema.TEXTO), BorderLayout.NORTH);
        String estado = !inv.fueRecolectada(e.id()) ? "Tipo: " + e.tipo() + " · en el lugar"
                : inv.custodiaRota(e.id()) ? "Recolectada SIN protocolo (inadmisible)" : "En depósito judicial · custodia íntegra";
        textos.add(Componentes.suave(estado), BorderLayout.SOUTH);
        fila.add(textos, BorderLayout.CENTER);
        if (!inv.fueRecolectada(e.id())) {
            JPanel botones = Componentes.panelOscuro(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            botones.add(Fichas.boton("Con protocolo (" + RecolectarEvidencia.HORAS_PROTOCOLO + " h)",
                    BotonNoir.Estilo.PRIMARIO, true, "Foto, rótulo, embalaje y acta: la prueba será admisible",
                    () -> ctx.ejecutor().ejecutar(new RecolectarEvidencia(e.id(), true))));
            botones.add(Fichas.boton("Rápido (0 h)", BotonNoir.Estilo.PELIGRO, true,
                    "Ahorra tiempo pero rompe la cadena de custodia",
                    () -> ctx.ejecutor().ejecutar(new RecolectarEvidencia(e.id(), false))));
            fila.add(botones, BorderLayout.EAST);
        }
        fila.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, fila.getPreferredSize().height));
        fila.setAlignmentX(0f);
        return fila;
    }
}
