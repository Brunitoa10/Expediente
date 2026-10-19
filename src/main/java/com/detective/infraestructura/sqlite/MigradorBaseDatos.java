package com.detective.infraestructura.sqlite;

import com.detective.config.Recursos;
import com.detective.domain.model.ExpedienteCaso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Crea el esquema y carga el contenido de los casos. El contenido está versionado:
 * al subir {@link #VERSION_CONTENIDO} se recargan los casos sin perder ranking ni partidas.
 */
public final class MigradorBaseDatos {

    public static final int VERSION_CONTENIDO = 2;
    private static final String CLAVE_VERSION = "version_contenido";

    private final ProveedorConexion proveedor;
    private final Supplier<List<ExpedienteCaso>> casosGenerados;

    public MigradorBaseDatos(ProveedorConexion proveedor) {
        this(proveedor, List::of);
    }

    /**
     * @param casosGenerados fuente de casos adicionales (por ejemplo, el generador procedural).
     *                       Sólo se invoca cuando hay que recargar el contenido.
     */
    public MigradorBaseDatos(ProveedorConexion proveedor, Supplier<List<ExpedienteCaso>> casosGenerados) {
        this.proveedor = proveedor;
        this.casosGenerados = casosGenerados;
    }

    public void migrar() {
        Connection c = proveedor.obtener();
        try {
            asegurarColumna(c, "caso", "categoria", "TEXT");
            EjecutorScriptSql.ejecutar(c, Recursos.leerTexto("/db/esquema.sql"));
            if (versionActual(c) < VERSION_CONTENIDO) {
                recargarContenido(c);
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error migrando la base de datos", e);
        }
    }

    private void recargarContenido(Connection c) throws SQLException {
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            EjecutorScriptSql.ejecutar(c, "DELETE FROM evidencia; DELETE FROM sospechoso; DELETE FROM lugar; "
                    + "DELETE FROM caso; DELETE FROM manual;");
            EjecutorScriptSql.ejecutar(c, Recursos.leerTexto("/db/casos.sql"));
            EjecutorScriptSql.ejecutar(c, Recursos.leerTexto("/db/manual.sql"));
            SqliteCasoEscritor.insertar(c, casosGenerados.get());
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO ajuste(clave, valor) VALUES(?, ?) ON CONFLICT(clave) DO UPDATE SET valor = excluded.valor")) {
                ps.setString(1, CLAVE_VERSION);
                ps.setString(2, Integer.toString(VERSION_CONTENIDO));
                ps.executeUpdate();
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
    }

    /** Agrega una columna a una tabla existente si falta (bases creadas por versiones anteriores). */
    private void asegurarColumna(Connection c, String tabla, String columna, String tipo) throws SQLException {
        boolean existeTabla;
        boolean existeColumna = false;
        try (PreparedStatement ps = c.prepareStatement("SELECT name FROM pragma_table_info(?)")) {
            ps.setString(1, tabla);
            try (ResultSet rs = ps.executeQuery()) {
                existeTabla = false;
                while (rs.next()) {
                    existeTabla = true;
                    existeColumna |= columna.equalsIgnoreCase(rs.getString(1));
                }
            }
        }
        if (existeTabla && !existeColumna) {
            EjecutorScriptSql.ejecutar(c, "ALTER TABLE " + tabla + " ADD COLUMN " + columna + " " + tipo);
        }
    }

    private int versionActual(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT valor FROM ajuste WHERE clave = ?")) {
            ps.setString(1, CLAVE_VERSION);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Integer.parseInt(rs.getString(1)) : 0;
            }
        }
    }
}
