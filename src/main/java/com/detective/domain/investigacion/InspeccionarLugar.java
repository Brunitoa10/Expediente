package com.detective.domain.investigacion;

import com.detective.domain.model.Evidencia;
import com.detective.domain.model.Lugar;

import java.util.List;
import java.util.stream.Collectors;

public final class InspeccionarLugar implements AccionInvestigacion {

    private final String lugarId;

    public InspeccionarLugar(String lugarId) {
        this.lugarId = lugarId;
    }

    @Override
    public String descripcion(Investigacion inv) {
        return "Inspección de " + inv.getExpediente().lugar(lugarId).nombre();
    }

    @Override
    public int costoHoras(Investigacion inv) {
        return inv.getExpediente().lugar(lugarId).horasInspeccion();
    }

    @Override
    public void validar(ContextoAccion ctx) {
        if (ctx.investigacion().fueInspeccionado(lugarId)) {
            throw new AccionInvalidaException("Ese lugar ya fue inspeccionado.");
        }
    }

    @Override
    public ResultadoAccion aplicar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Lugar lugar = inv.getExpediente().lugar(lugarId);
        inv.marcarInspeccion(lugar);
        List<Evidencia> halladas = inv.getExpediente().evidenciasEn(lugarId);
        String texto = halladas.isEmpty()
                ? "Recorrés el lugar con método, en espiral, pero no encontrás nada relevante."
                : "Siguiendo el principio de Locard (todo contacto deja un rastro) encontrás:\n"
                + halladas.stream().map(e -> "  • " + e.nombre()).collect(Collectors.joining("\n"))
                + "\n\nRecolectalas desde la pestaña Lugares.";
        return new ResultadoAccion(lugar.nombre(), texto, costoHoras(inv));
    }
}
