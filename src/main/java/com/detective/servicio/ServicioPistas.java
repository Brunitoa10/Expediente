package com.detective.servicio;

import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.Lugar;
import com.detective.domain.model.Sospechoso;

import java.util.Optional;

/** El "comisario" sugiere el próximo paso lógico siguiendo el orden del procedimiento real. */
public final class ServicioPistas {

    private final ServicioDeduccion deduccion;

    public ServicioPistas(ServicioDeduccion deduccion) {
        this.deduccion = deduccion;
    }

    public String sugerir(Investigacion inv) {
        ExpedienteCaso exp = inv.getExpediente();
        return lugarConRastros(inv, exp)
                .or(() -> evidenciaSinRecolectar(inv, exp))
                .or(() -> evidenciaSinAnalizar(inv, exp))
                .or(() -> sospechosoSinEntrevistar(inv, exp))
                .or(() -> confrontacionPendiente(inv, exp))
                .orElseGet(() -> cierre(inv, exp));
    }

    private Optional<String> lugarConRastros(Investigacion inv, ExpedienteCaso exp) {
        return exp.lugares().stream()
                .filter(l -> !inv.fueInspeccionado(l.id()) && !exp.evidenciasEn(l.id()).isEmpty())
                .findFirst()
                .map(Lugar::nombre)
                .map(n -> "«Todavía no fuiste a " + n + ". Una escena sin inspeccionar es una pregunta sin hacer.»");
    }

    private Optional<String> evidenciaSinRecolectar(Investigacion inv, ExpedienteCaso exp) {
        return exp.evidencias().stream()
                .filter(e -> inv.fueInspeccionado(e.lugarId()) && !inv.fueRecolectada(e.id()))
                .findFirst()
                .map(Evidencia::nombre)
                .map(n -> "«Dejaste " + n + " en el lugar. Recolectala con protocolo: la defensa siempre ataca la cadena de custodia.»");
    }

    private Optional<String> evidenciaSinAnalizar(Investigacion inv, ExpedienteCaso exp) {
        return exp.evidencias().stream()
                .filter(e -> inv.fueRecolectada(e.id()) && e.requiereAnalisis() && !inv.fueAnalizada(e.id()))
                .findFirst()
                .map(e -> "«Mandá " + e.nombre() + " al laboratorio (" + e.tecnicaAnalisis() + "). La ciencia no miente.»");
    }

    private Optional<String> sospechosoSinEntrevistar(Investigacion inv, ExpedienteCaso exp) {
        return exp.sospechosos().stream()
                .filter(s -> !inv.fueInterrogado(s.id()))
                .findFirst()
                .map(Sospechoso::nombre)
                .map(n -> "«Falta la declaración de " + n + ". Escuchá primero, preguntá abierto, y anotá todo.»");
    }

    private Optional<String> confrontacionPendiente(Investigacion inv, ExpedienteCaso exp) {
        return exp.sospechosos().stream()
                .filter(s -> !inv.fueConfrontado(s.id()) && s.evidenciaParaConfrontar().isPresent())
                .filter(s -> deduccion.puedeConfrontar(inv, s.id(), s.evidenciaConfrontacionId()))
                .findFirst()
                .map(s -> "«Tenés con qué confrontar a " + s.nombre() + ". Mostrale la contradicción.»");
    }

    private String cierre(Investigacion inv, ExpedienteCaso exp) {
        return exp.sospechosos().stream()
                .filter(s -> deduccion.esSospechosoPrincipal(inv, s.id()))
                .findFirst()
                .map(s -> "«La pizarra habla sola. Si vas a acusar, llevá la prueba que vincule y que esté bien custodiada.»")
                .orElse("«Revisá la pizarra: buscá a quien tenga motivo, medio y oportunidad a la vez.»");
    }
}
