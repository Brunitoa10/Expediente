package com.detective.domain.repositorio;

import com.detective.domain.model.RegistroPuntaje;

import com.detective.domain.model.Veredicto;

import java.util.List;
import java.util.Map;

public interface PuntajeRepositorio {

    public void guardar(RegistroPuntaje registro);

    /** @param casoId filtro opcional (null = todos los casos) */
    public List<RegistroPuntaje> mejores(int limite, String casoId);

    /** Mejor veredicto obtenido por el jugador en cada caso que cerró. */
    public Map<String, Veredicto> mejorVeredictoPorCaso(String jugador);
}
