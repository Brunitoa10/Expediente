package com.detective.domain.repositorio;

import java.util.Optional;

public interface AjustesRepositorio {

    public Optional<String> obtener(String clave);

    public void guardar(String clave, String valor);
}
