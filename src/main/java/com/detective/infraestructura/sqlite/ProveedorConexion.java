package com.detective.infraestructura.sqlite;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Mantiene una única conexión SQLite (base embebida de un solo archivo, ideal para portabilidad). */
public final class ProveedorConexion implements AutoCloseable {

    private final String url;
    private Connection conexion;

    public ProveedorConexion(Path archivo) {
        this.url = "jdbc:sqlite:" + archivo.toAbsolutePath();
        registrarDriver();
    }

    /** Registro explícito: funciona aunque el empaquetado pierda META-INF/services. */
    private static void registrarDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ErrorPersistencia("Falta el driver SQLite (sqlite-jdbc) en el classpath", e);
        }
    }

    public synchronized Connection obtener() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(url);
                try (Statement st = conexion.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
            }
            return conexion;
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo abrir la base de datos " + url, e);
        }
    }

    @Override
    public synchronized void close() {
        try {
            if (conexion != null) {
                conexion.close();
            }
        } catch (SQLException ignored) {
            // cierre best-effort
        }
    }
}
