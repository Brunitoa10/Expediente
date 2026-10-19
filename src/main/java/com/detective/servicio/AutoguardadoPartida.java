package com.detective.servicio;

import com.detective.domain.evento.EventoInvestigacion;
import com.detective.domain.evento.ObservadorInvestigacion;
import com.detective.domain.evento.TipoEvento;
import com.detective.domain.repositorio.PartidaRepositorio;

/** Observador que persiste la partida después de cada acción. */
public final class AutoguardadoPartida implements ObservadorInvestigacion {

    private final PartidaRepositorio partidas;

    public AutoguardadoPartida(PartidaRepositorio partidas) {
        this.partidas = partidas;
    }

    @Override
    public void alOcurrir(EventoInvestigacion evento) {
        if (evento.tipo() != TipoEvento.CASO_CERRADO) {
            partidas.guardar(evento.investigacion().getEstado());
        }
    }
}
