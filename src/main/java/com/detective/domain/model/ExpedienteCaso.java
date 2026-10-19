package com.detective.domain.model;

import java.util.List;
import java.util.NoSuchElementException;

/** Agregado de solo lectura con todo el material de un caso. */
public record ExpedienteCaso(Caso caso, List<Lugar> lugares, List<Sospechoso> sospechosos, List<Evidencia> evidencias) {

    public ExpedienteCaso {
        lugares = List.copyOf(lugares);
        sospechosos = List.copyOf(sospechosos);
        evidencias = List.copyOf(evidencias);
    }

    public Lugar lugar(String id) {
        return lugares.stream().filter(l -> l.id().equals(id)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Lugar inexistente: " + id));
    }

    public Sospechoso sospechoso(String id) {
        return sospechosos.stream().filter(s -> s.id().equals(id)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Sospechoso inexistente: " + id));
    }

    public Evidencia evidencia(String id) {
        return evidencias.stream().filter(e -> e.id().equals(id)).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Evidencia inexistente: " + id));
    }

    public List<Evidencia> evidenciasEn(String lugarId) {
        return evidencias.stream().filter(e -> e.lugarId().equals(lugarId)).toList();
    }
}
