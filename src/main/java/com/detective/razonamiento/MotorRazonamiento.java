package com.detective.razonamiento;

import java.util.List;
import java.util.Map;

/**
 * Puerto del motor de inferencia lógica (Inversión de Dependencias).
 * La aplicación sólo conoce esta interfaz; hoy la implementa un intérprete Prolog embebido,
 * mañana podría ser SWI-Prolog (JPL) o tuProlog sin tocar el resto del código.
 */
public interface MotorRazonamiento {

    /** Carga un programa (reglas y/o hechos) en sintaxis Prolog. */
    public void cargar(String programa);

    /** Devuelve las soluciones de la consulta: cada mapa asocia nombre de variable con su valor textual. */
    public List<Map<String, String>> consultar(String consulta, int limite);

    /** Indica si la consulta tiene al menos una solución. */
    public boolean demostrar(String consulta);
}
