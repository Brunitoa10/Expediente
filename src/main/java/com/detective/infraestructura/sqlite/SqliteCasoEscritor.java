package com.detective.infraestructura.sqlite;

import com.detective.domain.model.Caso;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.Lugar;
import com.detective.domain.model.Sospechoso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/** Inserta expedientes completos en lote (usado para cargar los casos generados). */
public final class SqliteCasoEscritor {

    private static final int TAMANIO_LOTE = 500;

    private SqliteCasoEscritor() {
    }

    public static void insertar(Connection c, List<ExpedienteCaso> expedientes) throws SQLException {
        try (PreparedStatement pc = c.prepareStatement("INSERT INTO caso(id, titulo, subtitulo, lugar_epoca, resumen, "
                + "objetivo, inspiracion_real, procedimiento_clave, epilogo, dificultad, horas_maximas, culpable_id, "
                + "hechos_iniciales, orden, categoria) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
             PreparedStatement pl = c.prepareStatement("INSERT INTO lugar(id, caso_id, nombre, descripcion, "
                     + "horas_inspeccion) VALUES (?,?,?,?,?)");
             PreparedStatement ps = c.prepareStatement("INSERT INTO sospechoso(id, caso_id, nombre, rol, descripcion, "
                     + "declaracion, hechos_declaracion, evidencia_confrontacion, texto_confrontacion, "
                     + "hechos_confrontacion) VALUES (?,?,?,?,?,?,?,?,?,?)");
             PreparedStatement pe = c.prepareStatement("INSERT INTO evidencia(id, caso_id, lugar_id, nombre, tipo, "
                     + "descripcion, hechos_recoleccion, requiere_analisis, horas_analisis, tecnica_analisis, "
                     + "resultado_analisis, hechos_analisis) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)")) {
            int contador = 0;
            for (ExpedienteCaso exp : expedientes) {
                agregarCaso(pc, exp.caso());
                for (Lugar l : exp.lugares()) {
                    agregarLugar(pl, l);
                }
                for (Sospechoso s : exp.sospechosos()) {
                    agregarSospechoso(ps, s);
                }
                for (Evidencia e : exp.evidencias()) {
                    agregarEvidencia(pe, e);
                }
                if (++contador % TAMANIO_LOTE == 0) {
                    ejecutar(pc, pl, ps, pe);
                }
            }
            ejecutar(pc, pl, ps, pe);
        }
    }

    private static void ejecutar(PreparedStatement... sentencias) throws SQLException {
        for (PreparedStatement s : sentencias) {
            s.executeBatch();
        }
    }

    private static void agregarCaso(PreparedStatement p, Caso k) throws SQLException {
        p.setString(1, k.id());
        p.setString(2, k.titulo());
        p.setString(3, k.subtitulo());
        p.setString(4, k.lugarYEpoca());
        p.setString(5, k.resumen());
        p.setString(6, k.objetivo());
        p.setString(7, k.inspiracionReal());
        p.setString(8, k.procedimientoClave());
        p.setString(9, k.epilogo());
        p.setString(10, k.dificultad().name());
        p.setInt(11, k.horasMaximas());
        p.setString(12, k.culpableId());
        p.setString(13, k.hechosIniciales());
        p.setInt(14, k.orden());
        p.setString(15, k.categoria());
        p.addBatch();
    }

    private static void agregarLugar(PreparedStatement p, Lugar l) throws SQLException {
        p.setString(1, l.id());
        p.setString(2, l.casoId());
        p.setString(3, l.nombre());
        p.setString(4, l.descripcion());
        p.setInt(5, l.horasInspeccion());
        p.addBatch();
    }

    private static void agregarSospechoso(PreparedStatement p, Sospechoso s) throws SQLException {
        p.setString(1, s.id());
        p.setString(2, s.casoId());
        p.setString(3, s.nombre());
        p.setString(4, s.rol());
        p.setString(5, s.descripcion());
        p.setString(6, s.declaracion());
        p.setString(7, s.hechosDeclaracion());
        p.setString(8, s.evidenciaConfrontacionId());
        p.setString(9, s.textoConfrontacion());
        p.setString(10, s.hechosConfrontacion());
        p.addBatch();
    }

    private static void agregarEvidencia(PreparedStatement p, Evidencia e) throws SQLException {
        p.setString(1, e.id());
        p.setString(2, e.casoId());
        p.setString(3, e.lugarId());
        p.setString(4, e.nombre());
        p.setString(5, e.tipo());
        p.setString(6, e.descripcion());
        p.setString(7, e.hechosRecoleccion());
        p.setInt(8, e.requiereAnalisis() ? 1 : 0);
        p.setInt(9, e.horasAnalisis());
        p.setString(10, e.tecnicaAnalisis());
        p.setString(11, e.resultadoAnalisis());
        p.setString(12, e.hechosAnalisis());
        p.addBatch();
    }
}
