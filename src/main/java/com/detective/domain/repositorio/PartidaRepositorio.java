package com.detective.domain.repositorio;

import com.detective.domain.investigacion.EstadoInvestigacion;

import java.util.Optional;
import java.util.Set;

public interface PartidaRepositorio {

    public void guardar(EstadoInvestigacion estado);

    public Optional<EstadoInvestigacion> cargar(String jugador, String casoId);

    public Set<String> casosEnCurso(String jugador);

    public void eliminar(String jugador, String casoId);
}
