# Expedientes: Casos Reales

Juego de escritorio de investigación criminal en **Java 17 + Swing**, con **razonamiento lógico en Prolog** y persistencia en **SQLite**. Trae **1204 casos**: 4 escritos a mano y 1200 generados a partir de 14 arquetipos basados en técnicas que resolvieron investigaciones reales. Todos están verificados automáticamente: tienen una única solución.

## Casos escritos a mano

| # | Caso | Inspirado en | Técnica clave | Dificultad |
|---|------|--------------|---------------|-----------|
| 1 | La huella del almacén | Caso Rojas, Necochea 1892 | Dactiloscopía (Vucetich) | Fácil |
| 2 | El golpe del pluvial | Robo al Banco Río de Acassuso, 2006 | Análisis de antenas + informante | Media |
| 3 | Arsénico en la quinta | Caso Lafarge, Francia 1840 | Toxicología (prueba de Marsh) | Media |
| 4 | Las cartas del Ermitaño | Caso Unabomber, EE.UU. 1996 | Lingüística forense | Difícil |

## Casos generados (1200)

El paquete `generacion` combina un **arquetipo** (técnica real + escenas + evidencias típicas) con personajes, roles, motivos, ciudades, épocas y coartadas. La generación es **determinística** (mismo número, mismo caso) y se carga en SQLite la primera vez que se abre el juego.

| Arquetipo | Técnica | Caso real de referencia |
|-----------|---------|-------------------------|
| Homicidio | Dactiloscopía | Rojas, Necochea 1892 |
| Homicidio | Perfil genético (ADN) | Pitchfork, Inglaterra 1988 |
| Envenenamiento | Toxicología forense | Lafarge, Francia 1840 |
| Robo de bóveda | Análisis de antenas | Banco Río de Acassuso, 2006 |
| Extorsión | Lingüística forense | Unabomber, EE.UU. 1996 |
| Fraude | Contabilidad forense | Al Capone, Chicago 1931 |
| Secuestro extorsivo | Rastreo de billetes y peritaje de madera | Lindbergh, EE.UU. 1932-1934 |
| Homicidio | Balística comparativa | San Valentín, Chicago 1929 |
| Homicidio rural | Entomología forense | Caso de la hoz, Song Ci, China 1247 |
| Sabotaje informático | Informática forense (metadatos y logs) | BTK, EE.UU. 2005 |
| Robo de joyería | Cámaras y lectura de patentes | Hatton Garden, Londres 2015 |
| Homicidio | Análisis de fibras | Asesinatos de Atlanta, 1981-1982 |
| Incendio intencional | Pericia de incendios (NFPA 921) | Estandarización del método científico |
| Atropello con fuga | Capas de pintura automotriz | Base PDQ de la Policía Montada de Canadá |

Esqueleto lógico de cada caso generado (lo que garantiza una solución única):

- **Responsable:** motivo, medio, oportunidad, coartada falsa y prueba vinculante (más una segunda en media y difícil).
- **Señalado:** motivo y medio, pero coartada verificada por un registro independiente.
- **Presente** (media y difícil): estuvo en la escena y tiene motivo, pero no tenía el medio; el testigo lo descarta.
- **Testigo:** desmiente la coartada del responsable.
- **Extra** (difícil): motivo y medio, sin oportunidad.
- En el 25 % de los casos los rumores apuntan al verdadero responsable: no hay atajos, hay que probarlo.

En el archivo de expedientes se puede buscar por título, ciudad, año o número, filtrar por categoría, dificultad y estado (nuevo, en curso, resuelto, sin condena), y abrir un **caso al azar**.

## Cómo jugar

- **Con Java 17+ instalado:** doble clic en `Jugar.bat` (o `java -jar dist/ExpedientesDetective.jar`).
- **Sin Java (portable):** ejecutá una vez `crear-portable-sin-java.bat` en una PC con JDK 17+. Genera `portable/ExpedientesDetective/` con Java incluido; esa carpeta se copia a un pendrive y anda en cualquier Windows.
- El progreso, el ranking y la base de datos se guardan en `datos/` **junto al ejecutable**. Si esa carpeta no se puede escribir, se usa `~/.expedientes-detective`.

## Compilar

- **Maven:** `mvn package` (genera `target/ExpedientesDetective.jar`) o `mvn exec:java`.
- **Sin Maven:** `compilar.bat` (usa `lib/sqlite-jdbc-3.46.1.3.jar`, genera `dist/ExpedientesDetective.jar` y verifica los casos).
- **NetBeans / IntelliJ:** abrir la carpeta como proyecto Maven.

## Mecánicas basadas en procedimientos reales

| Mecánica | Procedimiento real |
|----------|-------------------|
| Las acciones consumen horas y el plazo no alcanza para todo | Gestión de las primeras horas de una investigación |
| Inspeccionar lugares revela rastros | Principio de intercambio de Locard |
| Recolectar **con protocolo** (1 h) o **rápido** (0 h, prueba inadmisible) | Cadena de custodia |
| Laboratorio: huellas, química, voz, documentos, antenas | Criminalística y pericias |
| Primero declaración, después confrontación (sólo con base) | Entrevista investigativa, modelo PEACE |
| Pizarra: motivo + medio + oportunidad, coartadas verificadas | Triángulo clásico + verificación independiente |
| El fiscal exige prueba admisible **y** vinculante | Estándar probatorio |
| Siempre hay un sospechoso "obvio" que es inocente | Sesgo de confirmación / visión de túnel |

## Arquitectura

```
com.detective
├── app/                    Main y ContenedorAplicacion (raíz de composición, DI manual)
├── config/                 Rutas portables y lectura de recursos
├── domain/
│   ├── model/              Caso, Lugar, Sospechoso, Evidencia, Veredicto... (records inmutables)
│   ├── investigacion/      Investigacion (raíz de agregado), EstadoInvestigacion y acciones (Command)
│   ├── evento/             Eventos y ObservadorInvestigacion (Observer)
│   └── repositorio/        Puertos de persistencia (interfaces)
├── razonamiento/
│   ├── MotorRazonamiento   Puerto del motor lógico
│   ├── MotorPrologEmbebido Adaptador (Adapter)
│   └── prolog/             Intérprete Prolog propio: lexer, parser por precedencia, unificación, backtracking
├── servicio/               Fachada ServicioInvestigacion, deducción, fiscal, pistas, puntuación (Strategy)
├── infraestructura/sqlite/ Repositorios SQLite, migrador versionado, ejecutor de scripts
├── ui/                     Tema, navegación, componentes y pantallas Swing
│   └── pantallas/investigacion/  Una clase por pestaña (Template Method para maestro-detalle)
└── herramientas/           VerificadorCasos: juega cada caso solo y valida el contenido
```

### Patrones de diseño

- **Command:** `AccionInvestigacion` (Inspeccionar, Recolectar, Analizar, Interrogar, Confrontar). Cada acción conoce su costo, validación y efecto.
- **Facade:** `ServicioInvestigacion` es lo único que la UI usa para jugar.
- **Observer:** `ObservadorInvestigacion` → autoguardado en SQLite y aviso de fin de plazo.
- **Strategy:** `EstrategiaPuntuacion` (`PuntuacionEstandar`), intercambiable sin tocar el cierre del caso.
- **Builder:** `ConstructorBaseConocimiento` arma la base Prolog sólo con lo descubierto.
- **Adapter:** `MotorPrologEmbebido` adapta el intérprete al puerto `MotorRazonamiento`.
- **Repository:** interfaces en el dominio, implementaciones SQLite en infraestructura.
- **Template Method:** `PestanaListaDetalle` para las pestañas maestro-detalle.
- **Factory:** `FabricaMotorRazonamiento`, `Componentes`, `GeneradorCasos` (fábrica procedural de expedientes).

### SOLID

- **S:** cada acción, pestaña, repositorio y servicio tiene una única responsabilidad.
- **O:** se agregan acciones, estrategias de puntaje, casos o motores lógicos sin modificar el código existente.
- **L:** cualquier `MotorRazonamiento` o repositorio puede sustituir al actual.
- **I:** interfaces chicas (`OraculoLogico`, `Pestana`, `EjecutorAcciones`, `Liberable`).
- **D:** dominio y servicios dependen de interfaces; las clases concretas se eligen sólo en `ContenedorAplicacion`.

### ¿Por qué un intérprete Prolog propio?

Para que el juego sea **100 % portable**: SWI-Prolog (JPL) exige instalar Prolog y configurar librerías nativas. El intérprete embebido soporta hechos, reglas, unificación, backtracking, `\+`, `;`, `->`, `!`, aritmética y comparación. Las reglas están en `src/main/resources/prolog/reglas.pl` en sintaxis estándar, y desde la **Consola Prolog** del juego podés exportar la base de una partida y abrirla en SWI-Prolog. Si mañana querés usar SWI o tuProlog, alcanza con otra implementación de `MotorRazonamiento`.

## Cómo agregar casos

**Más casos generados:** agregá un arquetipo en `CatalogoArquetipos` (o más nombres, roles, ciudades y coartadas en `BancoDatos`), o cambiá `GeneradorCasos.CANTIDAD_POR_DEFECTO`. Después subí `VERSION_CONTENIDO` y corré el verificador.

**Un caso escrito a mano:**

1. En `src/main/resources/db/casos.sql` agregá los `INSERT` de `caso`, `lugar`, `evidencia` y `sospechoso`. Los campos `hechos_*` son cláusulas Prolog que se suman cuando el jugador descubre esa información (ver los predicados documentados al inicio de `reglas.pl`).
2. Subí `VERSION_CONTENIDO` en `MigradorBaseDatos` (recarga los casos sin borrar ranking ni partidas).
3. Corré `java -cp dist/ExpedientesDetective.jar com.detective.herramientas.VerificadorCasos` (agregá `-v` para ver el detalle de cada caso): comprueba que sólo el culpable resulte sospechoso principal, que la acusación correcta prospere y que ningún inocente pueda ser condenado.

## Mejoras propuestas

- Tests JUnit 5 para el motor Prolog, el fiscal y los repositorios (con SQLite en memoria).
- Más arquetipos: Golden State Killer (genealogía genética), caso Dreyfus (pericia caligráfica errónea, ideal para enseñar el sesgo), odontología forense como ejemplo de técnica hoy cuestionada.
- Variantes del esqueleto lógico en el generador: dos cómplices, testigo que miente, coartada falsa respaldada por un cómplice.
- Línea de tiempo visual arrastrable para ordenar declaraciones y evidencias.
- Explicación del "por qué" de cada deducción (traza de la demostración Prolog).
- Modo contrarreloj y modo "sin pistas" como nuevas `EstrategiaPuntuacion`.
- Editor de casos dentro del juego, que escriba en SQLite y ejecute el verificador.
- Sonido ambiente (lluvia, máquina de escribir) y retratos de los sospechosos generados con Java2D.
- Exportar el informe final del caso a PDF.
