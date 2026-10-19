package com.detective.razonamiento;

/** Fábrica de motores: cada investigación reconstruye su base de conocimiento en un motor limpio. */
@FunctionalInterface
public interface FabricaMotorRazonamiento {

    public MotorRazonamiento crear();
}
