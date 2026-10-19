package com.detective.domain.investigacion;

import com.detective.domain.model.Evidencia;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.Lugar;
import com.detective.domain.model.Sospechoso;

import java.util.List;

/**
 * Raíz de agregado: une el expediente (inmutable) con el estado de la partida y
 * protege sus invariantes (tiempo disponible, orden lógico de las acciones).
 */
public final class Investigacion {

    private final ExpedienteCaso expediente;
    private final EstadoInvestigacion estado;
    private long version;

    public Investigacion(ExpedienteCaso expediente, EstadoInvestigacion estado) {
        this.expediente = expediente;
        this.estado = estado;
    }

    public ExpedienteCaso getExpediente() {
        return expediente;
    }

    public EstadoInvestigacion getEstado() {
        return estado;
    }

    /** Cambia cada vez que el estado se modifica; permite cachear inferencias. */
    public long getVersion() {
        return version;
    }

    // ---------------------------------------------------------------- consultas

    public int horasRestantes() {
        return Math.max(0, expediente.caso().horasMaximas() - estado.getHorasUsadas());
    }

    public boolean tiempoAgotado() {
        return horasRestantes() == 0;
    }

    public boolean fueInspeccionado(String lugarId) {
        return estado.getLugaresInspeccionados().contains(lugarId);
    }

    public boolean fueRecolectada(String evidenciaId) {
        return estado.getEvidenciasRecolectadas().contains(evidenciaId);
    }

    public boolean fueAnalizada(String evidenciaId) {
        return estado.getEvidenciasAnalizadas().contains(evidenciaId);
    }

    public boolean custodiaRota(String evidenciaId) {
        return estado.getCustodiaRota().contains(evidenciaId);
    }

    public boolean fueInterrogado(String sospechosoId) {
        return estado.getSospechososInterrogados().contains(sospechosoId);
    }

    public boolean fueConfrontado(String sospechosoId) {
        return estado.getConfrontaciones().contains(sospechosoId);
    }

    public List<Evidencia> evidenciasRecolectadas() {
        return expediente.evidencias().stream().filter(e -> fueRecolectada(e.id())).toList();
    }

    public List<Evidencia> evidenciasVisibles(Lugar lugar) {
        return fueInspeccionado(lugar.id()) ? expediente.evidenciasEn(lugar.id()) : List.of();
    }

    // ---------------------------------------------------------------- comandos de dominio

    public void verificarTiempo(int horasNecesarias) {
        if (horasNecesarias > horasRestantes()) {
            throw new AccionInvalidaException("No queda tiempo suficiente (" + horasNecesarias
                    + " h necesarias, " + horasRestantes() + " h disponibles). Es momento de acusar.");
        }
    }

    public void consumirHoras(int horas) {
        estado.setHorasUsadas(estado.getHorasUsadas() + horas);
        version++;
    }

    public void marcarInspeccion(Lugar lugar) {
        estado.getLugaresInspeccionados().add(lugar.id());
        version++;
    }

    public void marcarRecoleccion(Evidencia evidencia, boolean conProtocolo) {
        estado.getEvidenciasRecolectadas().add(evidencia.id());
        if (!conProtocolo) {
            estado.getCustodiaRota().add(evidencia.id());
        }
        version++;
    }

    public void marcarAnalisis(Evidencia evidencia) {
        estado.getEvidenciasAnalizadas().add(evidencia.id());
        version++;
    }

    public void marcarInterrogatorio(Sospechoso sospechoso) {
        estado.getSospechososInterrogados().add(sospechoso.id());
        version++;
    }

    public void marcarConfrontacion(Sospechoso sospechoso) {
        estado.getConfrontaciones().add(sospechoso.id());
        version++;
    }

    public void registrarPista() {
        estado.setPistasUsadas(estado.getPistasUsadas() + 1);
        version++;
    }

    public void anotar(String linea) {
        estado.getBitacora().add(linea);
    }
}
