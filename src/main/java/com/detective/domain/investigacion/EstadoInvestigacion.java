package com.detective.domain.investigacion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Estado mutable y persistible de una partida (patrón Memento simplificado):
 * sólo datos, sin reglas. Las reglas viven en {@link Investigacion}.
 */
public final class EstadoInvestigacion {

    private final String jugador;
    private final String casoId;
    private final Set<String> lugaresInspeccionados = new LinkedHashSet<>();
    private final Set<String> evidenciasRecolectadas = new LinkedHashSet<>();
    private final Set<String> custodiaRota = new LinkedHashSet<>();
    private final Set<String> evidenciasAnalizadas = new LinkedHashSet<>();
    private final Set<String> sospechososInterrogados = new LinkedHashSet<>();
    private final Set<String> confrontaciones = new LinkedHashSet<>();
    private final List<String> bitacora = new ArrayList<>();
    private int horasUsadas;
    private int pistasUsadas;

    public EstadoInvestigacion(String jugador, String casoId) {
        this.jugador = jugador;
        this.casoId = casoId;
    }

    public String getJugador() {
        return jugador;
    }

    public String getCasoId() {
        return casoId;
    }

    public Set<String> getLugaresInspeccionados() {
        return lugaresInspeccionados;
    }

    public Set<String> getEvidenciasRecolectadas() {
        return evidenciasRecolectadas;
    }

    public Set<String> getCustodiaRota() {
        return custodiaRota;
    }

    public Set<String> getEvidenciasAnalizadas() {
        return evidenciasAnalizadas;
    }

    public Set<String> getSospechososInterrogados() {
        return sospechososInterrogados;
    }

    public Set<String> getConfrontaciones() {
        return confrontaciones;
    }

    public List<String> getBitacora() {
        return bitacora;
    }

    public int getHorasUsadas() {
        return horasUsadas;
    }

    public void setHorasUsadas(int horasUsadas) {
        this.horasUsadas = horasUsadas;
    }

    public int getPistasUsadas() {
        return pistasUsadas;
    }

    public void setPistasUsadas(int pistasUsadas) {
        this.pistasUsadas = pistasUsadas;
    }
}
