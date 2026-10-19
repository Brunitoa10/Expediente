package com.detective.infraestructura.sqlite;

import com.detective.domain.investigacion.EstadoInvestigacion;
import com.detective.domain.repositorio.PartidaRepositorio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public final class SqlitePartidaRepositorio implements PartidaRepositorio {

    private static final String SEPARADOR = ",";

    private final ProveedorConexion proveedor;

    public SqlitePartidaRepositorio(ProveedorConexion proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public void guardar(EstadoInvestigacion e) {
        String sql = "INSERT INTO partida(jugador, caso_id, lugares, recolectadas, custodia_rota, analizadas, "
                + "interrogados, confrontados, bitacora, horas_usadas, pistas, actualizada) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT(jugador, caso_id) DO UPDATE SET "
                + "lugares=excluded.lugares, recolectadas=excluded.recolectadas, custodia_rota=excluded.custodia_rota, "
                + "analizadas=excluded.analizadas, interrogados=excluded.interrogados, confrontados=excluded.confrontados, "
                + "bitacora=excluded.bitacora, horas_usadas=excluded.horas_usadas, pistas=excluded.pistas, "
                + "actualizada=excluded.actualizada";
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(sql)) {
            ps.setString(1, e.getJugador());
            ps.setString(2, e.getCasoId());
            ps.setString(3, unir(e.getLugaresInspeccionados()));
            ps.setString(4, unir(e.getEvidenciasRecolectadas()));
            ps.setString(5, unir(e.getCustodiaRota()));
            ps.setString(6, unir(e.getEvidenciasAnalizadas()));
            ps.setString(7, unir(e.getSospechososInterrogados()));
            ps.setString(8, unir(e.getConfrontaciones()));
            ps.setString(9, String.join("\n", e.getBitacora()));
            ps.setInt(10, e.getHorasUsadas());
            ps.setInt(11, e.getPistasUsadas());
            ps.setString(12, LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new ErrorPersistencia("No se pudo guardar la partida", ex);
        }
    }

    @Override
    public Optional<EstadoInvestigacion> cargar(String jugador, String casoId) {
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(
                "SELECT * FROM partida WHERE jugador = ? AND caso_id = ?")) {
            ps.setString(1, jugador);
            ps.setString(2, casoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                EstadoInvestigacion e = new EstadoInvestigacion(jugador, casoId);
                e.getLugaresInspeccionados().addAll(separar(rs.getString("lugares")));
                e.getEvidenciasRecolectadas().addAll(separar(rs.getString("recolectadas")));
                e.getCustodiaRota().addAll(separar(rs.getString("custodia_rota")));
                e.getEvidenciasAnalizadas().addAll(separar(rs.getString("analizadas")));
                e.getSospechososInterrogados().addAll(separar(rs.getString("interrogados")));
                e.getConfrontaciones().addAll(separar(rs.getString("confrontados")));
                String bitacora = rs.getString("bitacora");
                if (bitacora != null && !bitacora.isBlank()) {
                    e.getBitacora().addAll(Arrays.asList(bitacora.split("\n")));
                }
                e.setHorasUsadas(rs.getInt("horas_usadas"));
                e.setPistasUsadas(rs.getInt("pistas"));
                return Optional.of(e);
            }
        } catch (SQLException ex) {
            throw new ErrorPersistencia("No se pudo cargar la partida", ex);
        }
    }

    @Override
    public Set<String> casosEnCurso(String jugador) {
        Set<String> casos = new LinkedHashSet<>();
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(
                "SELECT caso_id FROM partida WHERE jugador = ? ORDER BY actualizada DESC")) {
            ps.setString(1, jugador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    casos.add(rs.getString(1));
                }
            }
            return casos;
        } catch (SQLException ex) {
            throw new ErrorPersistencia("No se pudieron listar las partidas", ex);
        }
    }

    @Override
    public void eliminar(String jugador, String casoId) {
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(
                "DELETE FROM partida WHERE jugador = ? AND caso_id = ?")) {
            ps.setString(1, jugador);
            ps.setString(2, casoId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new ErrorPersistencia("No se pudo eliminar la partida", ex);
        }
    }

    private static String unir(Collection<String> valores) {
        return String.join(SEPARADOR, valores);
    }

    private static Set<String> separar(String texto) {
        Set<String> set = new LinkedHashSet<>();
        if (texto != null && !texto.isBlank()) {
            set.addAll(Arrays.asList(texto.split(SEPARADOR)));
        }
        return set;
    }
}
