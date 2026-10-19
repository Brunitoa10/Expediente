package com.detective.razonamiento.prolog;

import java.util.Map;

/**
 * Término de Prolog: átomo, número, variable o estructura compuesta.
 * Interfaz sellada: el conjunto de términos es cerrado y conocido.
 */
public sealed interface Termino permits Atomo, Numero, Variable, Estructura {

    /** Sigue la cadena de ligaduras hasta el valor real del término. */
    public Termino resolver();

    /** Copia el término reemplazando variables por variables frescas (renombrado de cláusulas). */
    public Termino copiar(Map<Variable, Variable> mapa);

    /** Devuelve el término totalmente instanciado (sin referencias a variables ligadas). */
    public Termino instanciar();
}
