package com.detective.domain.repositorio;

import com.detective.domain.model.Caso;
import com.detective.domain.model.EntradaManual;
import com.detective.domain.model.ExpedienteCaso;

import java.util.List;
import java.util.Optional;

public interface CasoRepositorio {

    public List<Caso> listarCasos();

    public Optional<ExpedienteCaso> cargarExpediente(String casoId);

    public List<EntradaManual> manualDelDetective();
}
