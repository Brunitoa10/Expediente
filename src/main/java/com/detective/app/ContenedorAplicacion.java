package com.detective.app;

import com.detective.config.Recursos;
import com.detective.config.RutasAplicacion;
import com.detective.domain.repositorio.AjustesRepositorio;
import com.detective.generacion.GeneradorCasos;
import com.detective.domain.repositorio.CasoRepositorio;
import com.detective.domain.repositorio.PartidaRepositorio;
import com.detective.domain.repositorio.PuntajeRepositorio;
import com.detective.infraestructura.sqlite.MigradorBaseDatos;
import com.detective.infraestructura.sqlite.ProveedorConexion;
import com.detective.infraestructura.sqlite.SqliteAjustesRepositorio;
import com.detective.infraestructura.sqlite.SqliteCasoRepositorio;
import com.detective.infraestructura.sqlite.SqlitePartidaRepositorio;
import com.detective.infraestructura.sqlite.SqlitePuntajeRepositorio;
import com.detective.razonamiento.FabricaMotorRazonamiento;
import com.detective.razonamiento.MotorPrologEmbebido;
import com.detective.servicio.AutoguardadoPartida;
import com.detective.servicio.PuntuacionEstandar;
import com.detective.servicio.ServicioDeduccion;
import com.detective.servicio.ServicioFiscal;
import com.detective.servicio.ServicioInvestigacion;
import com.detective.servicio.ServicioPistas;

import java.nio.file.Path;

/**
 * Raíz de composición: el ÚNICO lugar donde se eligen implementaciones concretas.
 * Inyección de dependencias manual por constructor, sin frameworks.
 */
public final class ContenedorAplicacion implements AutoCloseable {

    private final ProveedorConexion conexion;
    private final CasoRepositorio casos;
    private final PuntajeRepositorio puntajes;
    private final PartidaRepositorio partidas;
    private final AjustesRepositorio ajustes;
    private final ServicioInvestigacion investigacion;
    private final Path archivoBaseDatos;

    public ContenedorAplicacion() {
        this(RutasAplicacion.archivoBaseDatos());
    }

    public ContenedorAplicacion(Path archivoBaseDatos) {
        this.archivoBaseDatos = archivoBaseDatos;
        this.conexion = new ProveedorConexion(archivoBaseDatos);
        GeneradorCasos generador = new GeneradorCasos();
        new MigradorBaseDatos(conexion, () -> generador.generarVarios(GeneradorCasos.CANTIDAD_POR_DEFECTO)).migrar();

        this.casos = new SqliteCasoRepositorio(conexion);
        this.puntajes = new SqlitePuntajeRepositorio(conexion);
        this.partidas = new SqlitePartidaRepositorio(conexion);
        this.ajustes = new SqliteAjustesRepositorio(conexion);

        FabricaMotorRazonamiento fabrica = MotorPrologEmbebido::new;
        ServicioDeduccion deduccion = new ServicioDeduccion(fabrica, Recursos.leerTexto("/prolog/reglas.pl"));
        this.investigacion = new ServicioInvestigacion(casos, partidas, puntajes, deduccion,
                new ServicioFiscal(deduccion), new ServicioPistas(deduccion), new PuntuacionEstandar());
        this.investigacion.suscribir(new AutoguardadoPartida(partidas));
    }

    public CasoRepositorio casos() {
        return casos;
    }

    public PuntajeRepositorio puntajes() {
        return puntajes;
    }

    public PartidaRepositorio partidas() {
        return partidas;
    }

    public AjustesRepositorio ajustes() {
        return ajustes;
    }

    public ServicioInvestigacion investigacion() {
        return investigacion;
    }

    public Path archivoBaseDatos() {
        return archivoBaseDatos;
    }

    @Override
    public void close() {
        conexion.close();
    }
}
