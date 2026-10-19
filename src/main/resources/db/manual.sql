-- Manual del detective: procedimientos reales en los que se basan las mecánicas del juego.
INSERT INTO manual(titulo, contenido, orden) VALUES
('1. Principio de intercambio de Locard',
'Edmond Locard, pionero de la criminalística en Lyon (1910), formuló que "todo contacto deja un rastro": quien comete un hecho deja algo en la escena y se lleva algo de ella.

En el juego: cada lugar inspeccionado revela rastros. Inspeccionar cuesta horas, igual que en la realidad: una escena se recorre con método (en espiral, por cuadrículas o por sectores), se fotografía y se documenta antes de tocar nada.', 1),

('2. Cadena de custodia',
'Es el registro documentado de quién recolectó una evidencia, cómo, cuándo y por qué manos pasó hasta llegar al juicio. Si se rompe, la defensa puede pedir que la prueba sea excluida, aunque sea verdadera.

En el juego: "Recolectar con protocolo" cuesta 1 hora (foto, rótulo, embalaje y acta). "Recolectar rápido" no cuesta tiempo, pero la prueba queda INADMISIBLE ante el fiscal y te resta puntos.', 2),

('3. Motivo, medio y oportunidad',
'El triángulo clásico de la investigación criminal:
• Motivo: por qué alguien querría cometer el hecho.
• Medio: con qué pudo hacerlo (arma, conocimiento, herramientas, acceso).
• Oportunidad: si estuvo en el lugar y en el momento del hecho.

Tener los tres no prueba culpabilidad, pero quien no tiene alguno difícilmente sea el autor. El motor Prolog del juego aplica esta regla en la "Pizarra de deducciones".', 3),

('4. Coartadas y verificación independiente',
'Una coartada no se cree ni se descarta: se verifica con fuentes independientes (registros, cámaras, terceros sin interés en el caso). Una coartada confirmada descarta a un sospechoso; una coartada desmentida es una contradicción que conviene confrontar.

En el juego: la regla contradiccion/2 compara lo que cada uno declaró con lo que la evidencia demuestra.', 4),

('5. La entrevista investigativa (modelo PEACE)',
'Desarrollado en el Reino Unido en los años 90 tras varios casos de confesiones falsas. Sus etapas: Planificación, Encuadre (Engage and explain), Relato (Account), Cierre (Closure) y Evaluación.

La idea central: buscar información, no confesiones. Primero se deja hablar con preguntas abiertas; recién después se confronta con evidencia concreta. Por eso en el juego no podés confrontar sin una contradicción o una prueba vinculante.', 5),

('6. Dactiloscopía: el sistema Vucetich',
'Juan Vucetich, en la Policía de la Provincia de Buenos Aires, desarrolló en 1891 un sistema de clasificación de huellas dactilares. En 1892 permitió resolver el caso de Francisca Rojas en Necochea, el primer crimen del mundo esclarecido por una huella.

Las crestas papilares son perennes (existen desde el sexto mes de gestación), inmutables y diversiformes (no hay dos iguales). La identificación se basa en coincidencias de puntos característicos.', 6),

('7. Toxicología forense: Orfila y la prueba de Marsh',
'Mathieu Orfila es considerado el padre de la toxicología. En 1836 James Marsh desarrolló un método muy sensible para detectar arsénico, el "veneno de la herencia" del siglo XIX. En el juicio a Marie Lafarge (1840), Orfila aplicó la prueba y demostró la presencia de arsénico en el cuerpo de la víctima.

Las leyes de la época obligaron a llevar libros de venta de venenos y a colorear el arsénico vendido como raticida.', 7),

('8. Análisis de comunicaciones',
'Cada teléfono móvil se conecta a una antena (celda). Las empresas registran qué aparato (IMEI) y qué línea (chip) impactaron en cada celda y a qué hora. Cambiar el chip no cambia el IMEI.

El entrecruzamiento masivo de estos registros (las "sábanas" telefónicas) fue clave en el robo al Banco Río de Acassuso (2006).', 8),

('9. Lingüística forense',
'Cada persona tiene un idiolecto: un uso particular del lenguaje (vocabulario, giros, errores, puntuación). Comparando textos de autoría desconocida con textos de autoría conocida pueden encontrarse coincidencias significativas.

Lo importante no es un rasgo aislado, sino la coincidencia de varios rasgos poco frecuentes. Así se identificó al Unabomber en 1996, a partir de cartas aportadas por su propio hermano.', 9),

('10. El estándar del fiscal',
'Un detective puede estar convencido; un tribunal necesita prueba. Para sostener una acusación en el juego, el fiscal (implementado como regla Prolog acusacion_sostenible/1) exige:
• que el acusado sea sospechoso principal (motivo + medio + oportunidad, sin coartada verificada), y
• al menos una prueba presentada que sea ADMISIBLE (custodia íntegra) y VINCULANTE (lo une directamente al hecho).

Acusar al culpable con prueba débil termina en absolución. Acusar a un inocente es un error judicial.', 10),

('11. Sesgo de confirmación y visión de túnel',
'El enemigo silencioso del investigador: decidir quién es el culpable y luego buscar sólo lo que lo confirma. Muchos errores judiciales comenzaron así.

Todos los casos del juego tienen un sospechoso "obvio" al principio. Desconfiá de lo obvio hasta que la evidencia hable.', 11);
