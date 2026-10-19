package com.detective.domain.model;

public enum Veredicto {
    CONDENA("CASO CERRADO: CONDENA",
            "Acusaste al responsable y el fiscal pudo sostener la acusación con prueba admisible."),
    ABSOLUCION_FALTA_PRUEBAS("ABSUELTO POR FALTA DE PRUEBAS",
            "Señalaste a la persona correcta, pero la prueba presentada no alcanzó para probar motivo, medio, oportunidad y un vínculo directo."),
    ERROR_JUDICIAL("ERROR JUDICIAL",
            "Acusaste a una persona inocente. El verdadero responsable sigue libre.");

    private final String titulo;
    private final String descripcion;

    Veredicto(String titulo, String descripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
