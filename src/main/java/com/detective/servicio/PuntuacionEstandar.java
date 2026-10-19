package com.detective.servicio;

import com.detective.domain.investigacion.Investigacion;
import com.detective.servicio.DesglosePuntaje.Concepto;

import java.util.ArrayList;
import java.util.List;

public final class PuntuacionEstandar implements EstrategiaPuntuacion {

    private static final int BASE_CONDENA = 1000;
    private static final int BASE_ABSOLUCION = 250;
    private static final int POR_HORA_RESTANTE = 15;
    private static final int POR_PRUEBA_VALIDA = 60;
    private static final int PENALIDAD_PISTA = 75;
    private static final int PENALIDAD_CUSTODIA = 50;

    @Override
    public DesglosePuntaje calcular(Investigacion inv, ResultadoJuicio juicio) {
        List<Concepto> conceptos = new ArrayList<>();
        switch (juicio.veredicto()) {
            case CONDENA -> conceptos.add(new Concepto("Condena firme", BASE_CONDENA));
            case ABSOLUCION_FALTA_PRUEBAS -> conceptos.add(new Concepto("Culpable correcto, prueba insuficiente", BASE_ABSOLUCION));
            case ERROR_JUDICIAL -> conceptos.add(new Concepto("Acusación a un inocente", 0));
        }
        if (juicio.veredicto() != com.detective.domain.model.Veredicto.ERROR_JUDICIAL) {
            conceptos.add(new Concepto("Horas restantes (" + inv.horasRestantes() + " h)",
                    inv.horasRestantes() * POR_HORA_RESTANTE));
            conceptos.add(new Concepto("Pruebas válidas presentadas (" + juicio.pruebasValidas() + ")",
                    juicio.pruebasValidas() * POR_PRUEBA_VALIDA));
        }
        int pistas = inv.getEstado().getPistasUsadas();
        if (pistas > 0) {
            conceptos.add(new Concepto("Pistas del comisario (" + pistas + ")", -pistas * PENALIDAD_PISTA));
        }
        int rotas = inv.getEstado().getCustodiaRota().size();
        if (rotas > 0) {
            conceptos.add(new Concepto("Evidencias con custodia rota (" + rotas + ")", -rotas * PENALIDAD_CUSTODIA));
        }
        int subtotal = Math.max(0, conceptos.stream().mapToInt(Concepto::puntos).sum());
        double multiplicador = inv.getExpediente().caso().dificultad().getMultiplicador();
        int total = (int) Math.round(subtotal * multiplicador);
        conceptos.add(new Concepto("Multiplicador de dificultad ×" + multiplicador, total - subtotal));
        return new DesglosePuntaje(conceptos, total);
    }
}
