package com.detective.domain.evento;

import com.detective.domain.investigacion.Investigacion;

public record EventoInvestigacion(TipoEvento tipo, Investigacion investigacion, String mensaje) {
}
