package com.detective.config;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Resuelve dónde guardar datos de forma PORTABLE: junto al .jar (carpeta "datos"),
 * así el juego puede llevarse en un pendrive con su progreso. Si esa carpeta no es
 * escribible, usa el directorio del usuario.
 */
public final class RutasAplicacion {

    private static final String CARPETA_DATOS = "datos";
    private static final String ARCHIVO_BD = "detective.db";

    private RutasAplicacion() {
    }

    public static Path archivoBaseDatos() {
        return carpetaDatos().resolve(ARCHIVO_BD);
    }

    public static Path carpetaDatos() {
        Path junto = directorioEjecutable().resolve(CARPETA_DATOS);
        if (esEscribible(junto)) {
            return junto;
        }
        Path usuario = Path.of(System.getProperty("user.home"), ".expedientes-detective");
        crear(usuario);
        return usuario;
    }

    private static Path directorioEjecutable() {
        try {
            File origen = new File(RutasAplicacion.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return origen.isFile() ? origen.getParentFile().toPath() : Path.of(".").toAbsolutePath();
        } catch (URISyntaxException | SecurityException e) {
            return Path.of(".").toAbsolutePath();
        }
    }

    private static boolean esEscribible(Path carpeta) {
        try {
            crear(carpeta);
            Path prueba = Files.createTempFile(carpeta, "w", ".tmp");
            Files.deleteIfExists(prueba);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void crear(Path carpeta) {
        try {
            Files.createDirectories(carpeta);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo crear la carpeta de datos " + carpeta, e);
        }
    }
}
