package com.detective.domain.investigacion;

import com.detective.domain.model.Sospechoso;

/** Confrontación: sólo es posible si el motor lógico detecta una contradicción o un vínculo con la evidencia clave. */
public final class ConfrontarSospechoso implements AccionInvestigacion {

    public static final int HORAS = 1;

    private final String sospechosoId;

    public ConfrontarSospechoso(String sospechosoId) {
        this.sospechosoId = sospechosoId;
    }

    @Override
    public String descripcion(Investigacion inv) {
        return "Confrontación de " + inv.getExpediente().sospechoso(sospechosoId).nombre();
    }

    @Override
    public int costoHoras(Investigacion inv) {
        return HORAS;
    }

    @Override
    public void validar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Sospechoso s = inv.getExpediente().sospechoso(sospechosoId);
        if (!inv.fueInterrogado(sospechosoId)) {
            throw new AccionInvalidaException("Primero tomale declaración.");
        }
        if (inv.fueConfrontado(sospechosoId)) {
            throw new AccionInvalidaException("Ya lo confrontaste.");
        }
        boolean fundada = s.evidenciaParaConfrontar()
                .map(clave -> ctx.oraculo().puedeConfrontar(inv, sospechosoId, clave))
                .orElse(false);
        if (!fundada) {
            throw new AccionInvalidaException("No tenés una contradicción o prueba firme que lo comprometa. "
                    + "Una confrontación sin base sólo lo pone a la defensiva.");
        }
    }

    @Override
    public ResultadoAccion aplicar(ContextoAccion ctx) {
        Investigacion inv = ctx.investigacion();
        Sospechoso s = inv.getExpediente().sospechoso(sospechosoId);
        inv.marcarConfrontacion(s);
        return new ResultadoAccion("Confrontación con " + s.nombre(), s.textoConfrontacion(), HORAS);
    }
}
