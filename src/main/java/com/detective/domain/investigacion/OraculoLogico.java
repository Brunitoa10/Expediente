package com.detective.domain.investigacion;

/** Preguntas lógicas que el dominio necesita responder (implementado por el servicio de deducción). */
public interface OraculoLogico {

    public boolean puedeConfrontar(Investigacion investigacion, String sospechosoId, String evidenciaId);
}
