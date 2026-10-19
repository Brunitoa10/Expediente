package com.detective.servicio;

import com.detective.domain.model.Veredicto;

import java.util.List;
import java.util.Set;

public record ResultadoJuicio(Veredicto veredicto, String acusadoId, Set<String> pruebasPresentadas,
                              List<String> fundamentos, int pruebasValidas) {
}
