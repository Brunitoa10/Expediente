package com.detective.servicio;

import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.Sospechoso;
import com.detective.razonamiento.FabricaMotorRazonamiento;
import com.detective.razonamiento.MotorRazonamiento;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Builder: arma la base de conocimiento Prolog con SOLO lo que el detective descubrió.
 * Así el motor razona igual que un investigador real: sin acceso a la verdad oculta.
 */
public final class ConstructorBaseConocimiento {

    private final List<String> bloques = new ArrayList<>();

    public ConstructorBaseConocimiento conReglas(String reglas) {
        bloques.add(reglas);
        return this;
    }

    /** Nombres legibles: sospechosos y lugares, y sólo las evidencias ya descubiertas (no adelanta nada). */
    public ConstructorBaseConocimiento conNombres(Investigacion inv) {
        ExpedienteCaso expediente = inv.getExpediente();
        StringBuilder sb = new StringBuilder();
        expediente.sospechosos().forEach(s -> sb.append(hechoNombre(s.id(), s.nombre())));
        inv.evidenciasRecolectadas().forEach(e -> sb.append(hechoNombre(e.id(), e.nombre())));
        expediente.lugares().forEach(l -> sb.append(hechoNombre(l.id(), l.nombre())));
        expediente.sospechosos().forEach(s -> sb.append("sospechoso(").append(s.id()).append(").\n"));
        bloques.add(sb.toString());
        return this;
    }

    public ConstructorBaseConocimiento conHechosIniciales(ExpedienteCaso expediente) {
        return conHechos(expediente.caso().hechosIniciales());
    }

    public ConstructorBaseConocimiento conDescubrimientos(Investigacion inv) {
        ExpedienteCaso exp = inv.getExpediente();
        for (Evidencia e : exp.evidencias()) {
            if (inv.fueRecolectada(e.id())) {
                conHechos("recolectada(" + e.id() + ").");
                if (inv.custodiaRota(e.id())) {
                    conHechos("custodia_rota(" + e.id() + ").");
                }
                conHechos(e.hechosRecoleccion());
            }
            if (inv.fueAnalizada(e.id())) {
                conHechos(e.hechosAnalisis());
            }
        }
        for (Sospechoso s : exp.sospechosos()) {
            if (inv.fueInterrogado(s.id())) {
                conHechos(s.hechosDeclaracion());
            }
            if (inv.fueConfrontado(s.id())) {
                conHechos(s.hechosConfrontacion());
            }
        }
        return this;
    }

    public ConstructorBaseConocimiento conHechos(String hechos) {
        if (hechos != null && !hechos.isBlank()) {
            bloques.add(hechos);
        }
        return this;
    }

    public MotorRazonamiento construir(FabricaMotorRazonamiento fabrica) {
        MotorRazonamiento motor = fabrica.crear();
        for (String bloque : bloques) {
            motor.cargar(bloque);
        }
        return motor;
    }

    /** Programa completo como texto (útil para exportar y probar en SWI-Prolog). */
    public String comoTexto() {
        return String.join("\n", bloques);
    }

    private static String hechoNombre(String id, String nombre) {
        return "nombre(" + TextoProlog.atomo(id) + ", " + TextoProlog.cadena(nombre) + ").\n";
    }
}
