package com.detective.domain.investigacion;

import com.detective.domain.model.Evidencia;

public final class AnalizarEvidencia implements AccionInvestigacion {

    private final String evidenciaId;

    public AnalizarEvidencia(String evidenciaId) {
        this.evidenciaId = evidenciaId;
    }

    @Override
    public String descripcion(Investigacion inv) {
        return "Laboratorio: " + inv.getExpediente().evidencia(evidenciaId).nombre();
    }

    @Override
    public int costoHoras(Investigacion inv) {
        return inv.getExpediente().evidencia(evidenciaId).horasAnalisis();
    }

    @Override
    public void validar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Evidencia e = inv.getExpediente().evidencia(evidenciaId);
        if (!inv.fueRecolectada(evidenciaId)) {
            throw new AccionInvalidaException("Sólo se puede analizar evidencia recolectada.");
        }
        if (!e.requiereAnalisis()) {
            throw new AccionInvalidaException("Esta evidencia no necesita análisis de laboratorio.");
        }
        if (inv.fueAnalizada(evidenciaId)) {
            throw new AccionInvalidaException("El laboratorio ya entregó su informe.");
        }
    }

    @Override
    public ResultadoAccion aplicar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Evidencia e = inv.getExpediente().evidencia(evidenciaId);
        inv.marcarAnalisis(e);
        return new ResultadoAccion("Informe pericial: " + e.tecnicaAnalisis(), e.resultadoAnalisis(), costoHoras(inv));
    }
}
