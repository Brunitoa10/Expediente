package com.detective.domain.investigacion;

import com.detective.domain.model.Sospechoso;

public final class InterrogarSospechoso implements AccionInvestigacion {

    public static final int HORAS = 2;

    private final String sospechosoId;

    public InterrogarSospechoso(String sospechosoId) {
        this.sospechosoId = sospechosoId;
    }

    @Override
    public String descripcion(Investigacion inv) {
        return "Entrevista a " + inv.getExpediente().sospechoso(sospechosoId).nombre();
    }

    @Override
    public int costoHoras(Investigacion inv) {
        return HORAS;
    }

    @Override
    public void validar(ContextoAccion ctx) {
        if (ctx.investigacion().fueInterrogado(sospechosoId)) {
            throw new AccionInvalidaException("Ya tomaste su declaración. Para avanzar, confrontalo con evidencia.");
        }
    }

    @Override
    public ResultadoAccion aplicar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Sospechoso s = inv.getExpediente().sospechoso(sospechosoId);
        inv.marcarInterrogatorio(s);
        return new ResultadoAccion("Declaración de " + s.nombre(), "«" + s.declaracion() + "»", HORAS);
    }
}
