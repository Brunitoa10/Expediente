package com.detective.infraestructura.sqlite;

import com.detective.domain.repositorio.AjustesRepositorio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public final class SqliteAjustesRepositorio implements AjustesRepositorio {

    private final ProveedorConexion proveedor;

    public SqliteAjustesRepositorio(ProveedorConexion proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public Optional<String> obtener(String clave) {
        try (PreparedStatement ps = proveedor.obtener().prepareStatement("SELECT valor FROM ajuste WHERE clave = ?")) {
            ps.setString(1, clave);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.ofNullable(rs.getString(1)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo leer el ajuste " + clave, e);
        }
    }

    @Override
    public void guardar(String clave, String valor) {
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(
                "INSERT INTO ajuste(clave, valor) VALUES(?, ?) ON CONFLICT(clave) DO UPDATE SET valor = excluded.valor")) {
            ps.setString(1, clave);
            ps.setString(2, valor);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo guardar el ajuste " + clave, e);
        }
    }
}
