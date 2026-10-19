package com.detective.infraestructura.sqlite;

import com.detective.domain.model.RegistroPuntaje;
import com.detective.domain.model.Veredicto;
import com.detective.domain.repositorio.PuntajeRepositorio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SqlitePuntajeRepositorio implements PuntajeRepositorio {

    private final ProveedorConexion proveedor;

    public SqlitePuntajeRepositorio(ProveedorConexion proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public void guardar(RegistroPuntaje r) {
        String sql = "INSERT INTO puntaje(jugador, caso_id, caso_titulo, veredicto, puntos, horas_usadas, fecha) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(sql)) {
            ps.setString(1, r.jugador());
            ps.setString(2, r.casoId());
            ps.setString(3, r.casoTitulo());
            ps.setString(4, r.veredicto().name());
            ps.setInt(5, r.puntos());
            ps.setInt(6, r.horasUsadas());
            ps.setString(7, r.fecha().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo guardar el puntaje", e);
        }
    }

    @Override
    public List<RegistroPuntaje> mejores(int limite, String casoId) {
        String sql = "SELECT * FROM puntaje " + (casoId == null ? "" : "WHERE caso_id = ? ")
                + "ORDER BY puntos DESC, horas_usadas ASC LIMIT ?";
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(sql)) {
            int i = 1;
            if (casoId != null) {
                ps.setString(i++, casoId);
            }
            ps.setInt(i, limite);
            try (ResultSet rs = ps.executeQuery()) {
                List<RegistroPuntaje> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(new RegistroPuntaje(rs.getString("jugador"), rs.getString("caso_id"),
                            rs.getString("caso_titulo"), Veredicto.valueOf(rs.getString("veredicto")),
                            rs.getInt("puntos"), rs.getInt("horas_usadas"), LocalDateTime.parse(rs.getString("fecha"))));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo leer el ranking", e);
        }
    }

    @Override
    public Map<String, Veredicto> mejorVeredictoPorCaso(String jugador) {
        Map<String, Veredicto> mejores = new HashMap<>();
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(
                "SELECT caso_id, veredicto FROM puntaje WHERE jugador = ?")) {
            ps.setString(1, jugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Veredicto v = Veredicto.valueOf(rs.getString(2));
                    mejores.merge(rs.getString(1), v, (a, b) -> a.ordinal() <= b.ordinal() ? a : b);
                }
            }
            return mejores;
        } catch (SQLException e) {
            throw new ErrorPersistencia("No se pudo leer el historial del jugador", e);
        }
    }
}
