package com.detective.ui.pantallas.investigacion;

import com.detective.domain.investigacion.AnalizarEvidencia;
import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.model.Evidencia;
import com.detective.ui.componentes.BotonNoir;

import javax.swing.JComponent;
import java.util.List;

public final class PestanaEvidencias extends PestanaListaDetalle<Evidencia> {

    public PestanaEvidencias(ContextoPestanas ctx) {
        super(ctx);
    }

    @Override
    public String titulo() {
        return "Evidencias";
    }

    @Override
    protected List<Evidencia> elementos() {
        return ctx.investigacion().evidenciasRecolectadas();
    }

    @Override
    protected Object clave(Evidencia e) {
        return e.id();
    }

    @Override
    protected String mensajeVacio() {
        return "El depósito judicial está vacío. Inspeccioná lugares y recolectá evidencia.";
    }

    @Override
    protected String rotulo(Evidencia e) {
        Investigacion inv = ctx.investigacion();
        String icono = inv.custodiaRota(e.id()) ? "▲  " : "■  ";
        String sufijo = e.requiereAnalisis() && !inv.fueAnalizada(e.id()) ? "   [laboratorio pendiente]" : "";
        return icono + e.nombre() + sufijo;
    }

    @Override
    protected JComponent detalleDe(Evidencia e) {
        Investigacion inv = ctx.investigacion();
        StringBuilder sb = new StringBuilder();
        sb.append("TIPO: ").append(e.tipo()).append('\n');
        sb.append("ORIGEN: ").append(inv.getExpediente().lugar(e.lugarId()).nombre()).append('\n');
        sb.append("CADENA DE CUSTODIA: ").append(inv.custodiaRota(e.id())
                ? "ROTA — la defensa la hará excluir del juicio." : "Íntegra.").append("\n\n");
        sb.append(e.descripcion());
        if (e.requiereAnalisis()) {
            sb.append(Fichas.separador()).append("INFORME PERICIAL — ").append(e.tecnicaAnalisis().toUpperCase()).append("\n\n");
            sb.append(inv.fueAnalizada(e.id()) ? e.resultadoAnalisis() : "Pendiente. Enviala al laboratorio.");
        }
        boolean puedeAnalizar = e.requiereAnalisis() && !inv.fueAnalizada(e.id());
        String texto = e.requiereAnalisis() ? "Enviar a laboratorio (" + e.horasAnalisis() + " h)" : "No requiere laboratorio";
        return Fichas.ficha(e.nombre(), "Evidencia N.º " + (inv.evidenciasRecolectadas().indexOf(e) + 1),
                sb.toString(),
                Fichas.boton(texto, BotonNoir.Estilo.PRIMARIO, puedeAnalizar, e.tecnicaAnalisis(),
                        () -> ctx.ejecutor().ejecutar(new AnalizarEvidencia(e.id()))));
    }
}
