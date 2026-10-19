package com.detective.servicio;

import java.util.List;

public record DesglosePuntaje(List<Concepto> conceptos, int total) {

    public record Concepto(String descripcion, int puntos) {
    }
}
