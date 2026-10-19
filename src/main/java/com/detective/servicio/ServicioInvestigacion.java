package com.detective.servicio;

import com.detective.domain.evento.EventoInvestigacion;
import com.detective.domain.evento.ObservadorInvestigacion;
import com.detective.domain.evento.TipoEvento;
import com.detective.domain.investigacion.AccionInvestigacion;
import com.detective.domain.investigacion.ContextoAccion;
import com.detective.domain.investigacion.EstadoInvestigacion;
import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.investigacion.ResultadoAccion;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.RegistroPuntaje;
import com.detective.domain.repositorio.CasoRepositorio;
import com.detective.domain.repositorio.PartidaRepositorio;
import com.detective.domain.repositorio.PuntajeRepositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Fachada (patrón Facade) de los casos de uso del juego: iniciar, actuar, pedir pista y acusar.
 * La UI sólo habla con esta clase.
 */
public final class ServicioInvestigacion {

    private final CasoRepositorio casos;
    private final PartidaRepositorio partidas;
    private final PuntajeRepositorio puntajes;
    private final ServicioDeduccion deduccion;
    private final ServicioFiscal fiscal;
    private final ServicioPistas pistas;
    private final EstrategiaPuntuacion puntuacion;
    private final List<ObservadorInvestigacion> observadores = new CopyOnWriteArrayList<>();

    public ServicioInvestigacion(CasoRepositorio casos, PartidaRepositorio partidas, PuntajeRepositorio puntajes,
                                 ServicioDeduccion deduccion, ServicioFiscal fiscal, ServicioPistas pistas,
                                 EstrategiaPuntuacion puntuacion) {
        this.casos = casos;
        this.partidas = partidas;
        this.puntajes = puntajes;
        this.deduccion = deduccion;
        this.fiscal = fiscal;
        this.pistas = pistas;
        this.puntuacion = puntuacion;
    }

    public void suscribir(ObservadorInvestigacion observador) {
        observadores.add(observador);
    }

    public void desuscribir(ObservadorInvestigacion observador) {
        observadores.remove(observador);
    }

    public ServicioDeduccion deduccion() {
        return deduccion;
    }

    // ------------------------------------------------------------ casos de uso

    public Investigacion iniciar(String jugador, String casoId, boolean continuarSiExiste) {
        ExpedienteCaso exp = casos.cargarExpediente(casoId)
                .orElseThrow(() -> new NoSuchElementException("No existe el caso " + casoId));
        EstadoInvestigacion estado = continuarSiExiste
                ? partidas.cargar(jugador, casoId).orElseGet(() -> new EstadoInvestigacion(jugador, casoId))
                : new EstadoInvestigacion(jugador, casoId);
        if (!continuarSiExiste) {
            partidas.eliminar(jugador, casoId);
        }
        Investigacion inv = new Investigacion(exp, estado);
        if (estado.getBitacora().isEmpty()) {
            inv.anotar("[00 h] Se abre el expediente: " + exp.caso().titulo());
            partidas.guardar(estado);
        }
        return inv;
    }

    public ResultadoAccion ejecutar(Investigacion inv, AccionInvestigacion accion) {
        ContextoAccion ctx = new ContextoAccion(inv, deduccion);
        accion.validar(ctx);
        int costo = accion.costoHoras(inv);
        inv.verificarTiempo(costo);
        ResultadoAccion resultado = accion.aplicar(ctx);
        inv.consumirHoras(costo);
        inv.anotar(String.format("[%02d h] %s (%d h)", inv.getEstado().getHorasUsadas(),
                accion.descripcion(inv), costo));
        publicar(new EventoInvestigacion(TipoEvento.ACCION_REALIZADA, inv, accion.descripcion(inv)));
        if (inv.tiempoAgotado()) {
            publicar(new EventoInvestigacion(TipoEvento.TIEMPO_AGOTADO, inv,
                    "Se agotó el plazo de la investigación. Debés acusar con lo que tengas."));
        }
        return resultado;
    }

    public String pedirPista(Investigacion inv) {
        String pista = pistas.sugerir(inv);
        inv.registrarPista();
        inv.anotar(String.format("[%02d h] Consulta al comisario", inv.getEstado().getHorasUsadas()));
        publicar(new EventoInvestigacion(TipoEvento.PISTA_SOLICITADA, inv, pista));
        return pista;
    }

    public ResultadoCierre acusar(Investigacion inv, String acusadoId, Set<String> pruebas) {
        ResultadoJuicio juicio = fiscal.evaluar(inv, acusadoId, pruebas);
        DesglosePuntaje desglose = puntuacion.calcular(inv, juicio);
        EstadoInvestigacion e = inv.getEstado();
        puntajes.guardar(new RegistroPuntaje(e.getJugador(), e.getCasoId(), inv.getExpediente().caso().titulo(),
                juicio.veredicto(), desglose.total(), e.getHorasUsadas(), LocalDateTime.now()));
        partidas.eliminar(e.getJugador(), e.getCasoId());
        publicar(new EventoInvestigacion(TipoEvento.CASO_CERRADO, inv, juicio.veredicto().getTitulo()));
        return new ResultadoCierre(juicio, desglose);
    }

    private void publicar(EventoInvestigacion evento) {
        observadores.forEach(o -> o.alOcurrir(evento));
    }
}
