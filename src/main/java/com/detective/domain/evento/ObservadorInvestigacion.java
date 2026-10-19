package com.detective.domain.evento;

/** Patrón Observer: la UI, el autoguardado o un logger reaccionan a lo que ocurre en la investigación. */
@FunctionalInterface
public interface ObservadorInvestigacion {

    public void alOcurrir(EventoInvestigacion evento);
}
