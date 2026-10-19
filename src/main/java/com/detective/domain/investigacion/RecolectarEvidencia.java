package com.detective.domain.investigacion;

import com.detective.domain.model.Evidencia;

/**
 * Recolección con protocolo (fotografía, rotulado, embalaje y acta: cadena de custodia íntegra)
 * o rápida (sin costo de tiempo, pero la prueba queda inadmisible ante el fiscal).
 */
public final class RecolectarEvidencia implements AccionInvestigacion {

    public static final int HORAS_PROTOCOLO = 1;

    private final String evidenciaId;
    private final boolean conProtocolo;

    public RecolectarEvidencia(String evidenciaId, boolean conProtocolo) {
        this.evidenciaId = evidenciaId;
        this.conProtocolo = conProtocolo;
    }

    @Override
    public String descripcion(Investigacion inv) {
        return (conProtocolo ? "Recolección con protocolo: " : "Recolección rápida: ")
                + inv.getExpediente().evidencia(evidenciaId).nombre();
    }

    @Override
    public int costoHoras(Investigacion inv) {
        return conProtocolo ? HORAS_PROTOCOLO : 0;
    }

    @Override
    public void validar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Evidencia e = inv.getExpediente().evidencia(evidenciaId);
        if (!inv.fueInspeccionado(e.lugarId())) {
            throw new AccionInvalidaException("Primero hay que inspeccionar el lugar.");
        }
        if (inv.fueRecolectada(evidenciaId)) {
            throw new AccionInvalidaException("Esa evidencia ya está en el depósito judicial.");
        }
    }

    @Override
    public ResultadoAccion aplicar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Evidencia e = inv.getExpediente().evidencia(evidenciaId);
        inv.marcarRecoleccion(e, conProtocolo);
        String custodia = conProtocolo
                ? "Fotografiada in situ, rotulada, embalada y registrada en el acta. Cadena de custodia íntegra."
                : "La guardaste en el bolsillo sin acta ni rótulo. La defensa podrá impugnarla: cadena de custodia ROTA.";
        String analisis = e.requiereAnalisis()
                ? "\n\nRequiere laboratorio: " + e.tecnicaAnalisis() + " (" + e.horasAnalisis() + " h)."
                : "";
        return new ResultadoAccion(e.nombre(), e.descripcion() + "\n\n" + custodia + analisis, costoHoras(inv));
    }
}
