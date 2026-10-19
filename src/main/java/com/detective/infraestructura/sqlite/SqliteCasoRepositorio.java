package com.detective.infraestructura.sqlite;

import com.detective.domain.model.Caso;
import com.detective.domain.model.Dificultad;
import com.detective.domain.model.EntradaManual;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.Lugar;
import com.detective.domain.model.Sospechoso;
import com.detective.domain.repositorio.CasoRepositorio;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SqliteCasoRepositorio implements CasoRepositorio {

    @FunctionalInterface
    private interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    private final ProveedorConexion proveedor;

    public SqliteCasoRepositorio(ProveedorConexion proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public List<Caso> listarCasos() {
        return consultar("SELECT * FROM caso ORDER BY orden", null, SqliteCasoRepositorio::mapearCaso);
    }

    @Override
    public Optional<ExpedienteCaso> cargarExpediente(String casoId) {
        List<Caso> encontrados = consultar("SELECT * FROM caso WHERE id = ?", casoId, SqliteCasoRepositorio::mapearCaso);
        if (encontrados.isEmpty()) {
            return Optional.empty();
        }
        List<Lugar> lugares = consultar("SELECT * FROM lugar WHERE caso_id = ? ORDER BY rowid", casoId,
                rs -> new Lugar(rs.getString("id"), rs.getString("caso_id"), rs.getString("nombre"),
                        rs.getString("descripcion"), rs.getInt("horas_inspeccion")));
        List<Sospechoso> sospechosos = consultar("SELECT * FROM sospechoso WHERE caso_id = ? ORDER BY rowid", casoId,
                rs -> new Sospechoso(rs.getString("id"), rs.getString("caso_id"), rs.getString("nombre"),
                        rs.getString("rol"), rs.getString("descripcion"), rs.getString("declaracion"),
                        rs.getString("hechos_declaracion"), rs.getString("evidencia_confrontacion"),
                        rs.getString("texto_confrontacion"), rs.getString("hechos_confrontacion")));
        List<Evidencia> evidencias = consultar("SELECT * FROM evidencia WHERE caso_id = ? ORDER BY rowid", casoId,
                rs -> new Evidencia(rs.getString("id"), rs.getString("caso_id"), rs.getString("lugar_id"),
                        rs.getString("nombre"), rs.getString("tipo"), rs.getString("descripcion"),
                        rs.getString("hechos_recoleccion"), rs.getInt("requiere_analisis") == 1,
                        rs.getInt("horas_analisis"), rs.getString("tecnica_analisis"),
                        rs.getString("resultado_analisis"), rs.getString("hechos_analisis")));
        return Optional.of(new ExpedienteCaso(encontrados.get(0), lugares, sospechosos, evidencias));
    }

    @Override
    public List<EntradaManual> manualDelDetective() {
        return consultar("SELECT titulo, contenido FROM manual ORDER BY orden", null,
                rs -> new EntradaManual(rs.getString(1), rs.getString(2)));
    }

    private static Caso mapearCaso(ResultSet rs) throws SQLException {
        return new Caso(rs.getString("id"), rs.getString("titulo"), rs.getString("subtitulo"),
                rs.getString("lugar_epoca"), rs.getString("resumen"), rs.getString("objetivo"),
                rs.getString("inspiracion_real"), rs.getString("procedimiento_clave"), rs.getString("epilogo"),
                Dificultad.valueOf(rs.getString("dificultad")), rs.getInt("horas_maximas"),
                rs.getString("culpable_id"), rs.getString("hechos_iniciales"), rs.getInt("orden"),
                rs.getString("categoria") == null ? "Otros" : rs.getString("categoria"));
    }

    private <T> List<T> consultar(String sql, String parametro, Mapeador<T> mapeador) {
        try (PreparedStatement ps = proveedor.obtener().prepareStatement(sql)) {
            if (parametro != null) {
                ps.setString(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error consultando: " + sql, e);
        }
    }
}
