package com.detective.domain.model;

public enum TipoDeduccion {
    MOTIVO("Motivo"),
    MEDIO("Medio"),
    OPORTUNIDAD("Oportunidad"),
    CONTRADICCION("Contradicción"),
    DESCARTE("Coartada verificada"),
    VINCULO("Prueba vinculante"),
    SOSPECHOSO_PRINCIPAL("Sospechoso principal");

    private final String etiqueta;

    private TipoDeduccion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
