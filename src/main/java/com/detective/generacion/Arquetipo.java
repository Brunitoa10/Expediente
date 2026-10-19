package com.detective.generacion;

import java.util.List;

/**
 * Tipo de caso basado en una técnica de investigación real. El generador combina un arquetipo
 * con personajes, lugares y épocas para producir casos distintos pero siempre resolubles.
 *
 * Marcadores disponibles en los textos: {V} víctima, {ROLV} rol de la víctima, {ESC} escena,
 * {CIUDAD}, {EMPRESA}, {H1}/{H2} ventana del hecho, {HC} hora clave, {C} responsable,
 * {H} sospechoso señalado, {ARMA} medio empleado.
 */
public record Arquetipo(
        String id,
        String categoria,
        int anioMinimo,
        int anioMaximo,
        List<String> titulos,
        List<Escenario> escenas,
        List<String> rolesVictima,
        String hecho,
        String tecnica,
        String procedimiento,
        String inspiracion,
        String arma,
        PlantillaEvidencia vinculo,
        Escenario lugarTecnico,
        PlantillaEvidencia oportunidad,
        PlantillaEvidencia medio,
        PlantillaEvidencia extra,
        PlantillaEvidencia distractor,
        String epilogo) {

    /** Lugar con nombre y descripción. */
    public record Escenario(String nombre, String descripcion) {
    }

    /** Evidencia de plantilla: el resultado se completa con los marcadores del caso. */
    public record PlantillaEvidencia(String nombre, String tipo, String descripcion,
                                     String tecnica, int horas, String resultado) {
    }
}
