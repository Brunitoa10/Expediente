-- Esquema de Expedientes: Casos Reales (SQLite)
CREATE TABLE IF NOT EXISTS ajuste (
    clave TEXT PRIMARY KEY,
    valor TEXT
);

CREATE TABLE IF NOT EXISTS caso (
    id                  TEXT PRIMARY KEY,
    titulo              TEXT NOT NULL,
    subtitulo           TEXT,
    lugar_epoca         TEXT,
    resumen             TEXT NOT NULL,
    objetivo            TEXT,
    inspiracion_real    TEXT,
    procedimiento_clave TEXT,
    epilogo             TEXT,
    dificultad          TEXT NOT NULL CHECK (dificultad IN ('FACIL','MEDIA','DIFICIL')),
    horas_maximas       INTEGER NOT NULL,
    culpable_id         TEXT NOT NULL,
    hechos_iniciales    TEXT,
    orden               INTEGER NOT NULL DEFAULT 0,
    categoria           TEXT
);

CREATE TABLE IF NOT EXISTS lugar (
    id               TEXT PRIMARY KEY,
    caso_id          TEXT NOT NULL REFERENCES caso(id) ON DELETE CASCADE,
    nombre           TEXT NOT NULL,
    descripcion      TEXT,
    horas_inspeccion INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS sospechoso (
    id                      TEXT PRIMARY KEY,
    caso_id                 TEXT NOT NULL REFERENCES caso(id) ON DELETE CASCADE,
    nombre                  TEXT NOT NULL,
    rol                     TEXT,
    descripcion             TEXT,
    declaracion             TEXT,
    hechos_declaracion      TEXT,
    evidencia_confrontacion TEXT,
    texto_confrontacion     TEXT,
    hechos_confrontacion    TEXT
);

CREATE TABLE IF NOT EXISTS evidencia (
    id                 TEXT PRIMARY KEY,
    caso_id            TEXT NOT NULL REFERENCES caso(id) ON DELETE CASCADE,
    lugar_id           TEXT NOT NULL REFERENCES lugar(id) ON DELETE CASCADE,
    nombre             TEXT NOT NULL,
    tipo               TEXT,
    descripcion        TEXT,
    hechos_recoleccion TEXT,
    requiere_analisis  INTEGER NOT NULL DEFAULT 0,
    horas_analisis     INTEGER NOT NULL DEFAULT 0,
    tecnica_analisis   TEXT,
    resultado_analisis TEXT,
    hechos_analisis    TEXT
);

CREATE TABLE IF NOT EXISTS manual (
    id        INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo    TEXT NOT NULL,
    contenido TEXT NOT NULL,
    orden     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS puntaje (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    jugador      TEXT NOT NULL,
    caso_id      TEXT NOT NULL,
    caso_titulo  TEXT,
    veredicto    TEXT NOT NULL,
    puntos       INTEGER NOT NULL,
    horas_usadas INTEGER NOT NULL,
    fecha        TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_puntaje_puntos ON puntaje(puntos DESC);

CREATE TABLE IF NOT EXISTS partida (
    jugador       TEXT NOT NULL,
    caso_id       TEXT NOT NULL,
    lugares       TEXT,
    recolectadas  TEXT,
    custodia_rota TEXT,
    analizadas    TEXT,
    interrogados  TEXT,
    confrontados  TEXT,
    bitacora      TEXT,
    horas_usadas  INTEGER NOT NULL DEFAULT 0,
    pistas        INTEGER NOT NULL DEFAULT 0,
    actualizada   TEXT,
    PRIMARY KEY (jugador, caso_id)
);

CREATE INDEX IF NOT EXISTS idx_lugar_caso ON lugar(caso_id);
CREATE INDEX IF NOT EXISTS idx_sospechoso_caso ON sospechoso(caso_id);
CREATE INDEX IF NOT EXISTS idx_evidencia_caso ON evidencia(caso_id);
CREATE INDEX IF NOT EXISTS idx_puntaje_jugador ON puntaje(jugador, caso_id);
