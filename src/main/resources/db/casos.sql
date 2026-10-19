-- =====================================================================
--  CONTENIDO DE LOS CASOS
--  Cada caso es ficción INSPIRADA en un caso real resuelto y en el
--  procedimiento que lo resolvió. Nombres y detalles fueron cambiados.
--  Los campos hechos_* son cláusulas Prolog que se agregan a la base de
--  conocimiento SOLO cuando el jugador descubre esa información.
-- =====================================================================

-- ---------------------------------------------------------------------
-- CASO 1 · La huella del almacén  (inspirado en el caso Rojas, Necochea 1892)
-- ---------------------------------------------------------------------
INSERT INTO caso(id, titulo, subtitulo, lugar_epoca, resumen, objetivo, inspiracion_real, procedimiento_clave,
                 epilogo, dificultad, horas_maximas, culpable_id, hechos_iniciales, orden) VALUES (
'huella_almacen',
'La huella del almacén',
'El primer caso resuelto por una huella dactilar',
'Pueblo costero bonaerense · fines del siglo XIX',
'Don Ramón Cáceres, dueño del almacén de ramos generales, apareció sin vida en la trastienda. El médico de policía estima que murió entre las 21 y las 23 horas. Rosa Ibarra, su socia y compañera, llegó a la comisaría pasada la medianoche señalando a Benito Velázquez, un peón al que Ramón le debía jornales. El comisario quiere un culpable rápido; vos querés al culpable correcto.',
'Identificá al responsable y presentá al fiscal prueba admisible que lo vincule con el hecho.',
'En junio de 1892, en Necochea, Francisca Rojas acusó a un vecino del crimen de sus dos hijos. El inspector Eduardo Álvarez, formado por Juan Vucetich en La Plata, halló una huella digital ensangrentada en el marco de una puerta. La comparación mostró que era del pulgar de la propia Francisca, que terminó confesando. Fue el primer crimen del mundo resuelto mediante dactiloscopía, y Argentina adoptó el sistema de Vucetich antes que casi cualquier otro país.',
'Dactiloscopía (sistema Vucetich): las crestas papilares son únicas, perennes e inmutables. Se comparan puntos característicos (bifurcaciones, finales de cresta, islotes) entre la huella dubitada y la ficha indubitada.',
'Rosa Ibarra fue condenada. La huella del marco, invisible para el comisario que buscaba confirmar su primera impresión, fue la prueba que nadie podía discutir: la ciencia contra la palabra.',
'FACIL', 24, 'rosa',
'lugar_crimen(almacen). hora_crimen(21, 23).',
1);

INSERT INTO lugar(id, caso_id, nombre, descripcion, horas_inspeccion) VALUES
('almacen', 'huella_almacen', 'Almacén de ramos generales (escena)', 'Estanterías con yerba y tabaco, un mostrador de madera gastada y, al fondo, la trastienda donde vivía Ramón. Huele a querosén y a algo más.', 2),
('casa_hermana', 'huella_almacen', 'Casa de Amalia Ibarra', 'Un rancho de adobe a seis cuadras, con un patio de tierra y ropa tendida.', 1),
('estancia_aurora', 'huella_almacen', 'Estancia La Aurora', 'A dos leguas del pueblo. El capataz lleva un libro de jornales prolijo.', 3),
('fonda', 'huella_almacen', 'Fonda del Vasco', 'Mesas largas, vino de damajuana y un cuaderno de fiado detrás del mostrador.', 1);

INSERT INTO evidencia(id, caso_id, lugar_id, nombre, tipo, descripcion, hechos_recoleccion, requiere_analisis,
                      horas_analisis, tecnica_analisis, resultado_analisis, hechos_analisis) VALUES
('huella_marco', 'huella_almacen', 'almacen', 'Huella ensangrentada en el marco de la puerta', 'Dactilar',
 'Una marca parda en el marco de la puerta de la trastienda, a la altura de una mano. Con la lupa se distinguen crestas papilares.',
 '', 1, 3, 'Comparación dactiloscópica (Vucetich)',
 'La huella corresponde a un pulgar derecho. Coincide en 17 puntos característicos con la ficha de ROSA IBARRA y en ninguno con Benito ni Julián. La sangre estaba fresca: se dejó durante el hecho, cerca de las 22 h.',
 'vincula(huella_marco, rosa). ubicado_por(huella_marco, rosa, almacen, 22).'),
('cuchillo', 'huella_almacen', 'almacen', 'Cuchillo de cocina en el balde', 'Arma',
 'Un cuchillo de hoja ancha sumergido en un balde con agua rosada, como si alguien hubiera intentado lavarlo apurado.',
 '', 1, 2, 'Examen del arma y del lugar de guardado',
 'Es el cuchillo de la cocina de la trastienda. Se guardaba en un cajón oculto que sólo conocían quienes vivían o trabajaban ahí: Rosa, y Julián, que había arreglado ese mueble.',
 'tiene_medio(rosa, "Vivía en la trastienda y sabía dónde se guardaba el cuchillo"). tiene_medio(julian, "Arregló el mueble donde se guardaba el cuchillo").'),
('libro_cuentas', 'huella_almacen', 'almacen', 'Libro de cuentas del almacén', 'Documento',
 'En la última página: "Debo a B. Velázquez tres meses de jornal". Y una anotación de Ramón: "No firmo nada a nombre de Rosa".',
 'tiene_motivo(benito, "Ramón le debía tres meses de jornales").', 0, 0, '', '', ''),
('carta_julian', 'huella_almacen', 'casa_hermana', 'Carta escondida en un costurero', 'Documento',
 'Una carta de Julián Ortega para Rosa: "Cuando seas libre de él nos iremos lejos. No aguanto más esperar".',
 'tiene_motivo(rosa, "Quería librarse de Ramón para irse con Julián"). tiene_motivo(julian, "Quería que Rosa quedara libre para casarse con ella").', 0, 0, '', '', ''),
('delantal', 'huella_almacen', 'casa_hermana', 'Delantal recién lavado en el patio', 'Biológica',
 'Un delantal de mujer, todavía húmedo, colgado de madrugada. Nadie lava a esa hora.',
 '', 1, 2, 'Prueba de orientación de sangre',
 'La prueba da positiva en las costuras: restos de sangre humana que el lavado no logró eliminar. El delantal es de Rosa.',
 'vincula(delantal, rosa).'),
('libro_jornales', 'huella_almacen', 'estancia_aurora', 'Libro de jornales del capataz', 'Documento',
 'Firmado por el capataz: Benito Velázquez trabajó en el arreo nocturno desde las 18 h hasta el amanecer, con otros cuatro peones.',
 'descarta(libro_jornales, benito).', 0, 0, '', '', ''),
('cuaderno_fiado', 'huella_almacen', 'fonda', 'Cuaderno de fiado de la fonda', 'Documento',
 'Julián Ortega firmó consumos a las 21:30 y a las 22:45. El fondero y tres parroquianos lo recuerdan cantando hasta tarde.',
 'descarta(cuaderno_fiado, julian).', 0, 0, '', '', '');

INSERT INTO sospechoso(id, caso_id, nombre, rol, descripcion, declaracion, hechos_declaracion,
                       evidencia_confrontacion, texto_confrontacion, hechos_confrontacion) VALUES
('rosa', 'huella_almacen', 'Rosa Ibarra', 'Socia y compañera de la víctima',
 'Treinta años, mirada firme. Fue ella quien hizo la denuncia y señaló a Benito.',
 'Estuve toda la noche en lo de mi hermana Amalia. Cuando volví, pasada la medianoche, encontré a Ramón tirado. Fue Benito: lo amenazó por la plata delante de todos.',
 'declara_en(rosa, casa_hermana, 20, 24).',
 'huella_marco',
 'Ponés sobre la mesa la ampliación de la huella y la ficha con su pulgar. Rosa la mira un largo rato. "Ramón no me iba a dejar ir nunca", dice al fin, y se quiebra. Confiesa que esa noche volvió al almacén.',
 'admite_en(rosa, almacen, 22).'),
('benito', 'huella_almacen', 'Benito Velázquez', 'Peón rural acusado por Rosa',
 'Manos curtidas, poncho raído. Está asustado: sabe que un peón acusado tiene las de perder.',
 'Yo estaba en La Aurora, en el arreo de la noche. Es cierto que don Ramón me debía, y que le grité, pero jamás le haría daño.',
 'declara_en(benito, estancia_aurora, 18, 24).',
 '', '', ''),
('julian', 'huella_almacen', 'Julián Ortega', 'Carpintero del pueblo',
 'Joven, nervioso. Hizo trabajos en el almacén y se lo ve seguido cerca de Rosa.',
 'Estuve en la fonda con los muchachos hasta muy tarde. ¿Qué tengo que ver yo con el almacenero?',
 'declara_en(julian, fonda, 20, 24).',
 '', '', ''),
('amalia', 'huella_almacen', 'Amalia Ibarra', 'Hermana de Rosa',
 'Mujer callada que retuerce el delantal mientras habla. Quiere proteger a su hermana, pero no sabe mentir.',
 'Rosa vino a casa, sí... pero recién llegó pasadas las once, agitada. Se fue derecho al patio a lavar ropa. No me dijo nada y yo no pregunté.',
 'declara_en(amalia, casa_hermana, 18, 24). desmiente(testimonio_amalia, rosa, casa_hermana, 22). nombre(testimonio_amalia, "Testimonio de Amalia Ibarra").',
 '', '', '');

-- ---------------------------------------------------------------------
-- CASO 2 · El golpe del pluvial  (inspirado en el robo al Banco Río de Acassuso, 2006)
-- ---------------------------------------------------------------------
INSERT INTO caso(id, titulo, subtitulo, lugar_epoca, resumen, objetivo, inspiracion_real, procedimiento_clave,
                 epilogo, dificultad, horas_maximas, culpable_id, hechos_iniciales, orden) VALUES (
'golpe_pluvial',
'El golpe del pluvial',
'Sin armas ni rencores: un robo que parecía perfecto',
'Zona norte del Gran Buenos Aires · actualidad',
'Una banda tomó la sucursal del Banco del Plata con rehenes. Durante siete horas un negociador "de traje gris" entretuvo a la policía mientras el resto vaciaba las cajas de seguridad. Cuando el grupo especial entró, sólo quedaban rehenes ilesos, pistolas de juguete y una nota burlona. La banda escapó por un túnel hacia el desagüe pluvial. El hecho ocurrió entre las 12 y las 19 horas. Hay que identificar al organizador.',
'Identificá al ideólogo del golpe y sostené la acusación con prueba admisible.',
'El 13 de enero de 2006 una banda tomó la sucursal Acassuso del Banco Río. Mientras negociaban durante horas, escaparon por un túnel al pluvial en gomones, llevándose el contenido de las cajas de seguridad. Dejaron armas de juguete y la nota "En barrio de ricachones, sin armas ni rencores, es sólo plata y no amores". El caso se resolvió combinando el entrecruzamiento masivo de llamadas que impactaron en la antena cercana (aun con chips cambiados) y la denuncia de la esposa de uno de los integrantes ante la fiscalía.',
'Análisis de comunicaciones: las "sábanas" de llamadas y la celda que toma cada teléfono ubican aparatos en tiempo y lugar. Se complementa con fuentes humanas (informantes) que deben ser corroboradas con prueba independiente.',
'Esteban Varela fue detenido y condenado como organizador. La nota burlona no lo delató; lo delataron su teléfono, el motor del gomón y alguien que lo conocía demasiado bien.',
'MEDIA', 36, 'varela',
'lugar_crimen(banco). hora_crimen(12, 19).',
2);

INSERT INTO lugar(id, caso_id, nombre, descripcion, horas_inspeccion) VALUES
('banco', 'golpe_pluvial', 'Sucursal Banco del Plata (escena)', 'Hall vacío, cajas de seguridad abiertas como dientes arrancados, cajas de pizza que la banda pidió para los rehenes.', 2),
('tunel', 'golpe_pluvial', 'Túnel y desagüe pluvial', 'Un boquete en el vestuario lleva a un túnel apuntalado que desemboca en el pluvial. Hay barro, agua y silencio.', 3),
('casa_lucia', 'golpe_pluvial', 'Casa de Lucía Ferraro', 'Una casa de barrio. Lucía pidió hablar con la fiscalía "antes de que sea tarde".', 1),
('guarderia', 'golpe_pluvial', 'Guardería náutica del río', 'Galpones con lanchas y gomones. El encargado anota todo en un libro.', 2),
('monitoreo', 'golpe_pluvial', 'Centro de análisis de comunicaciones', 'Pantallas, mapas de celdas y técnicos que hablan de "impactos" en antenas.', 1);

INSERT INTO evidencia(id, caso_id, lugar_id, nombre, tipo, descripcion, hechos_recoleccion, requiere_analisis,
                      horas_analisis, tecnica_analisis, resultado_analisis, hechos_analisis) VALUES
('nota_burlona', 'golpe_pluvial', 'banco', 'Nota dejada en la bóveda', 'Documento',
 '"En barrio de ricachones, sin armas ni rencores, es sólo plata y no amores." Escrita a mano en un papel cuadriculado.',
 '', 1, 2, 'Pericia papiloscópica y caligráfica',
 'Sin huellas: usaron guantes. La letra está deliberadamente deformada. La nota revela personalidad (gusto por el espectáculo) pero no identifica a nadie. No toda evidencia vistosa es evidencia útil.',
 ''),
('declaraciones_rehenes', 'golpe_pluvial', 'banco', 'Declaraciones de los rehenes', 'Testimonial',
 'Veintitrés rehenes coinciden: el gerente Quiroga estuvo maniatado con ellos todo el tiempo. El negociador era un hombre de unos cuarenta años, de traje gris, muy tranquilo.',
 'descarta(declaraciones_rehenes, quiroga). ubicado_por(declaraciones_rehenes, quiroga, banco, 13).', 0, 0, '', '', ''),
('grabacion_negociador', 'golpe_pluvial', 'banco', 'Grabación de la voz del negociador', 'Audio',
 'Horas de diálogo telefónico entre el negociador policial y el hombre del traje gris.',
 '', 1, 3, 'Comparación forense de voz',
 'Comparada con audios de Esteban Varela (videos de su gimnasio publicados en redes): coincidencia alta en frecuencia fundamental, formantes y muletillas. Sola, es un indicio fuerte; con otras pruebas, ubica a Varela dentro del banco a las 14 h.',
 'ubicado_por(grabacion_negociador, varela, banco, 14).'),
('gomon', 'golpe_pluvial', 'tunel', 'Gomón con motor fuera de borda', 'Objeto',
 'Abandonado en la boca del pluvial. Tiene el número de serie del motor intacto.',
 '', 1, 2, 'Rastreo de número de serie',
 'El motor fue vendido a una sociedad pantalla cuyo único apoderado es ESTEBAN VARELA. Lo pagó en efectivo un mes antes del golpe.',
 'vincula(gomon, varela). tiene_medio(varela, "Compró el gomón y el motor usados en la fuga").'),
('boquete', 'golpe_pluvial', 'tunel', 'Túnel apuntalado hacia el pluvial', 'Escena',
 'Ocho metros de túnel, apuntalado con madera, con un dique improvisado para nivelar el agua.',
 '', 1, 2, 'Peritaje de ingeniería',
 'Obra de semanas, hecha por alguien que conocía los planos del desagüe municipal. El contratista que trabajó ahí el año pasado, Daniel Benítez, tenía ese conocimiento.',
 'tiene_medio(benitez, "Conocía los planos del desagüe pluvial por una obra anterior").'),
('planos_garaje', 'golpe_pluvial', 'casa_lucia', 'Planos del pluvial hallados en un garaje', 'Documento',
 'Fotocopias de planos municipales del desagüe, con marcas en rojo sobre la sucursal. Lucía los encontró en el garaje de su ex, Esteban Varela, y los entregó.',
 'tiene_medio(varela, "Tenía los planos del desagüe marcados sobre la sucursal").', 1, 2, 'Revelado de huellas latentes',
 'Huellas latentes de ESTEBAN VARELA en varias hojas. El acta de entrega de Lucía se labró ante la fiscalía, de modo que la prueba es válida.',
 'vincula(planos_garaje, varela).'),
('libro_guarderia', 'golpe_pluvial', 'guarderia', 'Libro de la guardería náutica', 'Documento',
 'Dos gomones guardados a nombre de "E. V." durante el mes previo. Retirados la mañana del golpe.',
 '', 0, 0, '', '', ''),
('sabanas_llamadas', 'golpe_pluvial', 'monitoreo', 'Registro de antenas ("sábanas" telefónicas)', 'Digital',
 'Cientos de miles de comunicaciones que impactaron en la antena frente al banco durante el hecho.',
 '', 1, 4, 'Entrecruzamiento de celdas e IMEI',
 'Un aparato cambió de chip tres veces, pero el IMEI es el mismo que usa ESTEBAN VARELA a diario: impactó en la antena del banco a las 15 h. El teléfono de Daniel Benítez estuvo todo el día en Rosario.',
 'ubicado_por(sabanas_llamadas, varela, banco, 15). descarta(sabanas_llamadas, benitez).');

INSERT INTO sospechoso(id, caso_id, nombre, rol, descripcion, declaracion, hechos_declaracion,
                       evidencia_confrontacion, texto_confrontacion, hechos_confrontacion) VALUES
('varela', 'golpe_pluvial', 'Esteban Varela', 'Instructor de artes marciales',
 'Cuarenta y tantos, sonrisa ensayada, traje bien cortado. Responde sin apuro.',
 'Ese viernes estuve todo el día en mi gimnasio de Palermo dando clases. Pregunten a mis alumnos. ¿Un banco? Yo enseño disciplina, comisario.',
 'declara_en(varela, gimnasio, 9, 21).',
 'sabanas_llamadas',
 'Le mostrás el mapa: su IMEI encendido frente al banco a las 15 h, con chips cambiados. Varela sonríe. "Sin armas ni rencores, comisario. Era sólo plata." Admite que estaba adentro.',
 'admite_en(varela, banco, 14).'),
('quiroga', 'golpe_pluvial', 'Héctor Quiroga', 'Gerente de la sucursal',
 'Ojeroso, la corbata floja. Tiene marcas de precintos en las muñecas.',
 'Fui rehén como todos. Sí, tengo deudas de juego, y sé que eso me hace sospechoso, pero jamás arriesgaría a mi gente.',
 'declara_en(quiroga, banco, 9, 20). tiene_motivo(quiroga, "Deudas de juego importantes").',
 '', '', ''),
('benitez', 'golpe_pluvial', 'Daniel Benítez', 'Contratista de obras hidráulicas',
 'Hombre robusto, manos de obra. Trabajó en el pluvial municipal el año pasado.',
 'Hice obras en ese desagüe, sí, y vendí copias de planos a quien me pagara. Pero ese día estaba en Rosario, en otra obra. Tengo deudas, no voy a mentir.',
 'declara_en(benitez, rosario, 8, 22). tiene_motivo(benitez, "Deudas con proveedores").',
 '', '', ''),
('lucia', 'golpe_pluvial', 'Lucía Ferraro', 'Ex esposa de Esteban Varela',
 'Nerviosa, decidida. Pidió hablar ella. Dice que no busca venganza, sino no ser cómplice.',
 'Esteban hablaba del "golpe perfecto" desde hace años. Estaba fundido. Encontré planos raros en el garaje y gomones que nunca usó. Cuando vi el noticiero, supe.',
 'declara_en(lucia, casa_lucia, 0, 24). tiene_motivo(varela, "Estaba fundido y obsesionado con dar el golpe perfecto").',
 '', '', '');

-- ---------------------------------------------------------------------
-- CASO 3 · Arsénico en la quinta  (inspirado en el caso Lafarge, Francia 1840)
-- ---------------------------------------------------------------------
INSERT INTO caso(id, titulo, subtitulo, lugar_epoca, resumen, objetivo, inspiracion_real, procedimiento_clave,
                 epilogo, dificultad, horas_maximas, culpable_id, hechos_iniciales, orden) VALUES (
'arsenico_quinta',
'Arsénico en la quinta',
'Cuando la química entró por primera vez a un juicio',
'Una quinta en las sierras de Córdoba · siglo XIX',
'Carlos Lafuente, dueño de una fábrica de ladrillos, murió tras días de vómitos y calambres. El médico de la familia habló de "cólera", pero el hermano de la víctima exige una investigación: dice que Carlos empeoró justo después de un ponche servido en la cena, entre las 19 y las 22 horas. Todos en la quinta tenían algo que ganar.',
'Determiná si hubo envenenamiento, quién lo cometió, y llevá prueba admisible al juicio.',
'En 1840, en Francia, Marie Lafarge fue juzgada por envenenar con arsénico a su esposo Charles. Los primeros análisis fueron contradictorios, hasta que el toxicólogo Mathieu Orfila aplicó correctamente la prueba de Marsh y detectó arsénico en el cuerpo. Se sumaron testimonios de empleados que la vieron mezclar un polvo blanco en la comida y sus compras de arsénico "para las ratas". Fue condenada: el primer gran juicio con toxicología forense, seguido por la prensa de toda Europa.',
'Toxicología forense (prueba de Marsh): la muestra se trata con zinc y ácido; si hay arsénico se forma arsina, que al calentarse deja un "espejo" metálico negro. Se complementa con el registro de venta de venenos y testimonios sobre quién tuvo acceso a la comida.',
'María Cappelli de Lafuente fue condenada. Nunca confesó. La química, el libro de venenos de la farmacia y lo que vio la cocinera bastaron para que el tribunal no tuviera dudas razonables.',
'MEDIA', 28, 'maria',
'lugar_crimen(quinta). hora_crimen(19, 22).',
3);

INSERT INTO lugar(id, caso_id, nombre, descripcion, horas_inspeccion) VALUES
('quinta', 'arsenico_quinta', 'Comedor de la quinta (escena)', 'Cortinas pesadas, una mesa larga y la taza de ponche que nadie levantó.', 2),
('cocina', 'arsenico_quinta', 'Cocina y despensa', 'Ollas de cobre, una despensa con trampas para ratas y el cajón personal de la señora.', 1),
('farmacia', 'arsenico_quinta', 'Farmacia del pueblo', 'El boticario guarda bajo llave el libro de venta de venenos, como exige la ley.', 2),
('fabrica', 'arsenico_quinta', 'Fábrica de ladrillos', 'Hornos apagados y una oficina con libros contables desordenados.', 2),
('hotel', 'arsenico_quinta', 'Hotel de la ciudad', 'A cuatro horas de carruaje. Libro de pasajeros en la recepción.', 2);

INSERT INTO evidencia(id, caso_id, lugar_id, nombre, tipo, descripcion, hechos_recoleccion, requiere_analisis,
                      horas_analisis, tecnica_analisis, resultado_analisis, hechos_analisis) VALUES
('taza_ponche', 'arsenico_quinta', 'quinta', 'Restos de ponche en la taza', 'Toxicológica',
 'Un residuo blanquecino en el fondo de la taza que bebió la víctima.',
 '', 1, 4, 'Prueba de Marsh',
 'Espejo de arsénico claramente positivo. El ponche contenía una dosis muy superior a la letal. No fue cólera: fue envenenamiento.',
 'causa_muerte(arsenico).'),
('cartas_maria', 'arsenico_quinta', 'quinta', 'Cartas de María a una amiga', 'Documento',
 'En un escritorio: "Vivo en una tumba con un hombre que no amo. Daría cualquier cosa por ser libre."',
 'tiene_motivo(maria, "Matrimonio arreglado e infeliz: quería ser libre").', 0, 0, '', '', ''),
('frasco_raticida', 'arsenico_quinta', 'cocina', 'Frasco de "polvo para ratas"', 'Toxicológica',
 'Casi vacío, escondido en el cajón personal de la señora, no en la despensa con las trampas.',
 '', 1, 3, 'Análisis químico y papiloscópico',
 'Arsénico blanco puro, sin el colorante que la ley exige en los raticidas para evitar confusiones. Huellas de MARÍA en el frasco. Las trampas de la despensa, en cambio, tenían cebo sin veneno.',
 'vincula(frasco_raticida, maria). tiene_medio(maria, "Guardaba arsénico puro en su cajón personal").'),
('libro_venenos', 'arsenico_quinta', 'farmacia', 'Libro de venta de venenos', 'Documento',
 'Dos compras de arsénico firmadas por María Cappelli "para las ratas". Una compra de Dionisio Barbier "para la fábrica".',
 'tiene_medio(maria, "Compró arsénico dos veces en la farmacia"). tiene_medio(dionisio, "Compró arsénico para la fábrica").', 0, 0, '', '', ''),
('libros_fabrica', 'arsenico_quinta', 'fabrica', 'Libros contables de la fábrica', 'Documento',
 'Columnas que no cierran y firmas de Dionisio en los retiros.',
 '', 1, 3, 'Peritaje contable',
 'Faltantes sistemáticos firmados por Dionisio Barbier. Una carta sin enviar de Carlos anuncia que lo iba a denunciar.',
 'tiene_motivo(dionisio, "Carlos iba a denunciar sus desfalcos").'),
('libro_hotel', 'arsenico_quinta', 'hotel', 'Libro de pasajeros del hotel', 'Documento',
 'Dionisio Barbier se registró dos días antes de la cena y no salió: el conserje le subió la cena a la habitación esa misma noche.',
 'descarta(libro_hotel, dionisio).', 0, 0, '', '', '');

INSERT INTO sospechoso(id, caso_id, nombre, rol, descripcion, declaracion, hechos_declaracion,
                       evidencia_confrontacion, texto_confrontacion, hechos_confrontacion) VALUES
('maria', 'arsenico_quinta', 'María Cappelli de Lafuente', 'Esposa de la víctima',
 'Educada, elegante, de luto riguroso. Contesta con serenidad estudiada.',
 'Cuidé a mi esposo con devoción hasta el final. El arsénico lo compré para las ratas, como todo el mundo. Y yo jamás toqué su comida: de eso se ocupaba Clementina.',
 'niega(maria, manipular_comida).',
 'frasco_raticida',
 'Le mostrás el frasco de su cajón y el informe: arsénico puro, sin colorante, con sus huellas. María sostiene que es inocente, pero se contradice: admite que esa noche fue ella quien llevó el ponche al comedor.',
 'admite_en(maria, quinta, 20).'),
('clementina', 'arsenico_quinta', 'Clementina Ruiz', 'Cocinera de la quinta',
 'Lleva veinte años en la casa. Tiene miedo de perder el trabajo, y más miedo de callar.',
 'Yo preparé el ponche, sí, pero la señora María me lo sacó de las manos y dijo que ella se lo llevaba. La vi revolverle un polvito blanco. Dijo que era "para el estómago". La señorita Ana nunca se acercó a la cocina.',
 'prueba_de(testimonio_clementina, maria, manipular_comida). ubicado_por(testimonio_clementina, maria, quinta, 20). descarta(testimonio_clementina, ana). nombre(testimonio_clementina, "Testimonio de la cocinera Clementina").',
 '', '', ''),
('ana', 'arsenico_quinta', 'Ana Lafuente', 'Hermana de la víctima',
 'Soltera, práctica, heredará parte de la fábrica. No disimula su antipatía por María.',
 'Cené con ellos esa noche, claro. Sé que heredo, y sé lo que eso parece. Pero yo quería a mi hermano, y nunca entendí qué hacía con esa mujer.',
 'admite_en(ana, quinta, 20). tiene_motivo(ana, "Hereda la mitad de la fábrica").',
 '', '', ''),
('dionisio', 'arsenico_quinta', 'Dionisio Barbier', 'Administrador de la fábrica',
 'Sonrisa nerviosa, uñas comidas. Cambia de tema cada vez que se habla de números.',
 'Esa semana estaba en la ciudad por negocios de la fábrica. Me enteré de la muerte de don Carlos por telegrama.',
 'declara_en(dionisio, ciudad, 0, 48).',
 '', '', '');

-- ---------------------------------------------------------------------
-- CASO 4 · Las cartas del Ermitaño  (inspirado en el caso Unabomber, EE.UU. 1978-1996)
-- ---------------------------------------------------------------------
INSERT INTO caso(id, titulo, subtitulo, lugar_epoca, resumen, objetivo, inspiracion_real, procedimiento_clave,
                 epilogo, dificultad, horas_maximas, culpable_id, hechos_iniciales, orden) VALUES (
'cartas_ermitano',
'Las cartas del Ermitaño',
'Las palabras también dejan huellas',
'Mendoza, entre la ciudad y la cordillera · actualidad',
'Durante meses, la empresa Andina Software recibió cartas anónimas firmadas por "El Ermitaño": amenazas cada vez más violentas contra "las máquinas que esclavizan al hombre". El último envío salió de la oficina de correo de Uspallata, entre las 9 y las 17 horas. Un diario publicó el manifiesto completo del autor. Al día siguiente, un hombre llamó a la fiscalía: cree reconocer la voz de alguien en esas palabras.',
'Identificá al autor de las cartas y probalo con prueba admisible.',
'Entre 1978 y 1995 el llamado Unabomber envió bombas por correo en Estados Unidos. En 1995 el FBI permitió la publicación de su manifiesto. David Kaczynski reconoció en él ideas y giros de su hermano Ted, como la frase invertida "you can''t eat your cake and have it too", y aportó cartas antiguas. La comparación lingüística forense de esos textos fue clave para obtener la orden de allanamiento de la cabaña en Montana, donde se encontraron pruebas materiales. Fue uno de los hitos de la lingüística forense.',
'Lingüística forense: el idiolecto (vocabulario, giros, ortografía, puntuación) funciona como una huella. Se comparan textos dubitados contra textos indubitados de autoría conocida; se busca la coincidencia de varios rasgos raros, no de uno solo.',
'Tomás Kessler fue detenido en su cabaña. Su hermano Ricardo vivió con la carga de haberlo entregado, pero la evidencia que aportó evitó nuevas víctimas. La máquina de escribir hizo el resto.',
'DIFICIL', 34, 'tomas',
'lugar_crimen(correo_uspallata). hora_crimen(9, 17).',
4);

INSERT INTO lugar(id, caso_id, nombre, descripcion, horas_inspeccion) VALUES
('oficina_andina', 'cartas_ermitano', 'Oficinas de Andina Software', 'Vidrio, pantallas y miedo. Seguridad guardó todas las cartas recibidas.', 1),
('correo_uspallata', 'cartas_ermitano', 'Correo de Uspallata', 'Una sucursal pequeña al pie de la cordillera. La empleada conoce a todos.', 3),
('casa_ricardo', 'cartas_ermitano', 'Casa de Ricardo Kessler', 'Una casa de profesor jubilado, llena de libros y cajas de cartas viejas.', 2),
('cabana', 'cartas_ermitano', 'Cabaña en la montaña', 'Una cabaña de madera sin electricidad, a una hora de caminata del camino.', 4),
('paso_fronterizo', 'cartas_ermitano', 'Paso fronterizo Cristo Redentor', 'Registros migratorios de entradas y salidas hacia Chile.', 2);

INSERT INTO evidencia(id, caso_id, lugar_id, nombre, tipo, descripcion, hechos_recoleccion, requiere_analisis,
                      horas_analisis, tecnica_analisis, resultado_analisis, hechos_analisis) VALUES
('cartas_amenaza', 'cartas_ermitano', 'oficina_andina', 'Cartas de amenaza del Ermitaño', 'Documento',
 'Cinco cartas mecanografiadas y el manifiesto publicado. Escritura culta, obsesiva, con un tono de profeta.',
 '', 1, 4, 'Análisis lingüístico forense',
 'Rasgos idiolectales raros y estables: (1) usa el refrán al revés, "no se puede comer la torta y tenerla", (2) escribe con ortografía arcaica ("obscuro", "substancia"), (3) abusa del punto y coma. Las letras "e" y "t" aparecen levemente desalineadas: misma máquina en todas las cartas.',
 'estilo(cartas_amenaza, refran_invertido). estilo(cartas_amenaza, ortografia_arcaica). estilo(cartas_amenaza, punto_y_coma).'),
('mails_valeria', 'cartas_ermitano', 'oficina_andina', 'Correos de Valeria Ruiz', 'Documento',
 'Mails furiosos que la ex empleada envió el día de su despido.',
 'escrita_por(mails_valeria, valeria).', 1, 2, 'Análisis lingüístico forense',
 'Estilo opuesto: frases cortas, anglicismos ("deadline", "feedback"), sin punto y coma ni arcaísmos. No hay coincidencias significativas con las cartas.',
 'estilo(mails_valeria, anglicismos).'),
('registro_correo', 'cartas_ermitano', 'correo_uspallata', 'Testimonio y planilla de la empleada del correo', 'Testimonial',
 'La empleada recuerda a "un hombre barbudo de la montaña" que compra estampillas y paga con monedas cada vez que llega una de esas cartas.',
 '', 1, 1, 'Reconocimiento fotográfico',
 'Ante un set de diez fotografías similares, la empleada reconoce sin dudar a TOMÁS KESSLER. La planilla registra su compra a las 11 h del día del último envío.',
 'ubicado_por(registro_correo, tomas, correo_uspallata, 11).'),
('cartas_familiares', 'cartas_ermitano', 'casa_ricardo', 'Cartas antiguas de Tomás a su hermano', 'Documento',
 'Treinta años de correspondencia. Ricardo las entrega con las manos temblorosas.',
 'escrita_por(cartas_familiares, tomas).', 1, 3, 'Análisis lingüístico forense',
 'Los mismos rasgos raros: el refrán invertido ("no se puede comer la torta y tenerla"), "obscuro" y "substancia", y el punto y coma obsesivo. La coincidencia de varios rasgos poco frecuentes es muy significativa.',
 'estilo(cartas_familiares, refran_invertido). estilo(cartas_familiares, ortografia_arcaica). estilo(cartas_familiares, punto_y_coma).'),
('maquina_escribir', 'cartas_ermitano', 'cabana', 'Máquina de escribir en la cabaña', 'Objeto',
 'Una máquina mecánica antigua sobre una mesa de pino, junto a borradores.',
 '', 1, 3, 'Pericia documentológica',
 'Las letras "e" y "t" de esta máquina presentan exactamente el mismo desalineamiento que las cartas del Ermitaño. Los borradores contienen fragmentos del manifiesto.',
 'vincula(maquina_escribir, tomas). tiene_medio(tomas, "Poseía la máquina con la que se escribieron las cartas").'),
('diario_tomas', 'cartas_ermitano', 'cabana', 'Diario personal de Tomás', 'Documento',
 '"Las máquinas nos esclavizan. Andina es el símbolo de todo lo que hay que detener."',
 'tiene_motivo(tomas, "Odio obsesivo a la tecnología; consideraba a Andina un símbolo a destruir").', 0, 0, '', '', ''),
('registro_migraciones', 'cartas_ermitano', 'paso_fronterizo', 'Registro migratorio de Valeria Ruiz', 'Documento',
 'Valeria salió hacia Chile diez días antes del último envío y regresó una semana después.',
 'descarta(registro_migraciones, valeria).', 0, 0, '', '', '');

INSERT INTO sospechoso(id, caso_id, nombre, rol, descripcion, declaracion, hechos_declaracion,
                       evidencia_confrontacion, texto_confrontacion, hechos_confrontacion) VALUES
('tomas', 'cartas_ermitano', 'Tomás Kessler', 'Ex profesor de matemática, vive aislado',
 'Barba larga, ropa remendada, mirada que atraviesa. Habla poco y con frases perfectas.',
 'Vivo aislado; no bajo al pueblo hace meses. No tengo teléfono ni electricidad, ni ganas de tenerlos. No sé nada de ninguna empresa.',
 'declara_en(tomas, cabana, 0, 24).',
 'maquina_escribir',
 'Ponés lado a lado una carta del Ermitaño y una hoja escrita en su máquina: la misma "e" torcida. Tomás guarda silencio un largo rato. "Ustedes no entienden lo que viene", murmura. Admite que bajó a Uspallata a despachar la última carta.',
 'admite_en(tomas, correo_uspallata, 11).'),
('ricardo', 'cartas_ermitano', 'Ricardo Kessler', 'Hermano de Tomás, profesor jubilado',
 'Hombre amable y destrozado. Fue él quien llamó a la fiscalía.',
 'Leí el manifiesto en el diario y se me heló la sangre: eran las ideas de mi hermano, sus palabras, hasta sus manías al escribir. Tengo sus cartas de toda una vida. No quiero que nadie más salga lastimado.',
 'declara_en(ricardo, ciudad_mendoza, 0, 24).',
 '', '', ''),
('valeria', 'cartas_ermitano', 'Valeria Ruiz', 'Ex empleada despedida de Andina',
 'Programadora brillante, despedida tras un conflicto. Está furiosa, y no lo esconde.',
 'Me echaron injustamente, sí, y los insulté por mail. Pero ese mes estuve en Chile. Escribir cartitas a máquina no es lo mío.',
 'declara_en(valeria, chile, 0, 24). tiene_motivo(valeria, "Despido conflictivo de Andina Software"). tiene_medio(valeria, "Conocía la estructura interna de la empresa").',
 '', '', ''),
('ignacio', 'cartas_ermitano', 'Ignacio Pons', 'CEO de la competencia',
 'Traje caro, sonrisa de entrevista. Le conviene que Andina tenga problemas.',
 '¿Competencia? A Andina le gano en el mercado, no con cartitas. Ese día estaba en una conferencia, transmitida en vivo.',
 'declara_en(ignacio, conferencia, 8, 18). tiene_motivo(ignacio, "Competencia comercial con Andina").',
 '', '', '');

-- Categorías de los casos escritos a mano (los generados la traen del arquetipo)
UPDATE caso SET categoria = 'Homicidio' WHERE id = 'huella_almacen';
UPDATE caso SET categoria = 'Robo' WHERE id = 'golpe_pluvial';
UPDATE caso SET categoria = 'Envenenamiento' WHERE id = 'arsenico_quinta';
UPDATE caso SET categoria = 'Extorsión' WHERE id = 'cartas_ermitano';
