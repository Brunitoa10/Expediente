package com.detective.domain.investigacion;

/**
 * Patrón Command: cada acción del detective es un objeto con su costo, validación y efecto.
 * Agregar una acción nueva no obliga a modificar el servicio que las ejecuta (Abierto/Cerrado).
 */
public interface AccionInvestigacion {

    public String descripcion(Investigacion investigacion);

    public int costoHoras(Investigacion investigacion);

    /** Lanza {@link AccionInvalidaException} si la acción no puede realizarse. */
    public void validar(ContextoAccion contexto);

    public ResultadoAccion aplicar(ContextoAccion contexto);
}
