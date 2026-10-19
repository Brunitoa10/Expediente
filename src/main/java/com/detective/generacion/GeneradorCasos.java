package com.detective.generacion;

import com.detective.domain.investigacion.ConfrontarSospechoso;
import com.detective.domain.investigacion.InterrogarSospechoso;
import com.detective.domain.investigacion.RecolectarEvidencia;
import com.detective.domain.model.Caso;
import com.detective.domain.model.Dificultad;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.ExpedienteCaso;
import com.detective.domain.model.Lugar;
import com.detective.domain.model.Sospechoso;
import com.detective.generacion.Arquetipo.Escenario;
import com.detective.generacion.Arquetipo.PlantillaEvidencia;
import com.detective.generacion.BancoDatos.Coartada;
import com.detective.generacion.BancoDatos.Nombre;
import com.detective.generacion.BancoDatos.Rol;
import com.detective.servicio.FormatoHora;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Genera casos de forma DETERMINÍSTICA (misma semilla, mismo caso), combinando un arquetipo
 * con personajes, lugares y épocas. Todos siguen el mismo esqueleto lógico, que garantiza
 * que el caso tiene solución única:
 * <ul>
 *   <li>Responsable: motivo, medio, oportunidad, coartada falsa y prueba vinculante.</li>
 *   <li>Señalado: motivo y medio, pero coartada verificada por un registro independiente.</li>
 *   <li>Presente (media y difícil): estuvo en la escena y tiene motivo, sin medio; un testigo lo descarta.</li>
 *   <li>Testigo: desmiente la coartada del responsable.</li>
 *   <li>Extra (difícil): motivo y medio, sin oportunidad.</li>
 * </ul>
 */
public final class GeneradorCasos {

    public static final int CANTIDAD_POR_DEFECTO = 1200;
    private static final long SEMILLA_BASE = 1892L;
    private static final int ORDEN_INICIAL = 100;

    private final List<Arquetipo> arquetipos;

    public GeneradorCasos() {
        this(CatalogoArquetipos.todos());
    }

    public GeneradorCasos(List<Arquetipo> arquetipos) {
        this.arquetipos = List.copyOf(arquetipos);
    }

    public List<ExpedienteCaso> generarVarios(int cantidad) {
        List<ExpedienteCaso> casos = new ArrayList<>(cantidad);
        for (int i = 1; i <= cantidad; i++) {
            casos.add(generarCaso(i));
        }
        return casos;
    }

    /** Genera el caso número {@code numero} (siempre el mismo para el mismo número). */
    public ExpedienteCaso generarCaso(int numero) {
        return new Construccion(numero, arquetipos.get((numero - 1) % arquetipos.size()),
                new Random(SEMILLA_BASE * 31 + numero)).construir();
    }

    // =====================================================================================

    /** Estado de construcción de un caso (una instancia por caso, no se comparte). */
    private static final class Construccion {

        private final int numero;
        private final Arquetipo a;
        private final Random r;
        private final String pre;
        private final Map<String, String> v = new HashMap<>();
        private final Set<String> nombresUsados = new HashSet<>();
        private final List<Lugar> lugares = new ArrayList<>();
        private final List<Evidencia> evidencias = new ArrayList<>();
        private final List<Sospechoso> sospechosos = new ArrayList<>();
        private int contadorPersonas;
        private boolean culpableFemenino;

        public Construccion(int numero, Arquetipo arquetipo, Random random) {
            this.numero = numero;
            this.a = arquetipo;
            this.r = random;
            this.pre = String.format("g%04d", numero);
        }

        public ExpedienteCaso construir() {
            Dificultad dificultad = elegirDificultad();
            int anio = a.anioMinimo() + r.nextInt(Math.max(1, Math.min(a.anioMaximo(), 2025) - a.anioMinimo() + 1));
            String ciudad = elegir(BancoDatos.CIUDADES);
            Escenario escena = elegir(a.escenas());
            int h1 = 8 + r.nextInt(13);
            int h2 = h1 + 2 + r.nextInt(3);
            int hc = h1 + 1 + r.nextInt(h2 - h1 - 1);

            boolean apuntaAlCulpable = r.nextInt(4) == 0;
            Nombre victima = nombre(false);
            v.put("V", victima.nombre() + " " + elegir(BancoDatos.APELLIDOS));
            Persona c = persona(elegirRol());
            culpableFemenino = c.femenino;
            Persona h = persona(elegirRolDistinto(c.rol));
            boolean conPresente = dificultad != Dificultad.FACIL;
            boolean conExtra = dificultad == Dificultad.DIFICIL;
            Persona p = conPresente ? persona(elegirRolDistinto(c.rol, h.rol)) : null;
            Persona e = conExtra ? persona(elegirRolDistinto(c.rol, h.rol, p.rol)) : null;
            Persona w = persona(elegir(BancoDatos.TESTIGOS));

            v.put("ROLV", elegir(a.rolesVictima()));
            v.put("ESC", escena.nombre());
            v.put("CIUDAD", ciudad.split(",")[0]);
            v.put("EMPRESA", elegir(BancoDatos.EMPRESAS));
            v.put("H1", FormatoHora.formatear(h1));
            v.put("H2", FormatoHora.formatear(h2));
            v.put("HC", FormatoHora.formatear(hc));
            v.put("C", c.nombre);
            v.put("H", h.nombre);
            v.put("ARMA", a.arma());

            // ------------------------------------------------------------------ lugares
            String idEscena = pre + "_escena";
            String idCasaC = pre + "_casa_c";
            String idCoartadaH = pre + "_coartada_h";
            String idTecnico = pre + "_tecnico";
            Coartada coartadaH = elegirCoartada(anio);
            lugares.add(new Lugar(idEscena, "", capitalizar(escena.nombre()) + " (escena)", escena.descripcion(), 2));
            lugares.add(new Lugar(idCasaC, "", "Allanamientos con orden judicial",
                    "El juez autorizó registrar los domicilios de las personas involucradas. Hay que revisar cajón por cajón.", 2));
            lugares.add(new Lugar(idCoartadaH, "", coartadaH.lugar(), coartadaH.descripcionLugar(), 1 + r.nextInt(2)));
            lugares.add(new Lugar(idTecnico, "", a.lugarTecnico().nombre(), a.lugarTecnico().descripcion(), 1 + r.nextInt(2)));
            Collections.shuffle(lugares.subList(1, lugares.size()), r);

            // ------------------------------------------------------------------ evidencias
            String evVinculo = pre + "_vinculo";
            String evOportunidad = pre + "_oportunidad";
            String evMedio = pre + "_medio";
            String evMotivoC = pre + "_motivo_c";
            String evMotivoH = pre + "_motivo_h";
            String evCoartada = pre + "_coartada";
            String evExtra = pre + "_extra";
            String evDistractor = pre + "_distractor";

            agregarAnalizable(evVinculo, idEscena, a.vinculo(), 0,
                    "vincula(" + evVinculo + ", " + c.id + ").");
            agregarAnalizable(evOportunidad, idTecnico, a.oportunidad(), 0,
                    "ubicado_por(" + evOportunidad + ", " + c.id + ", " + idEscena + ", " + hc + ").");
            StringBuilder hechosMedio = new StringBuilder()
                    .append(medio(c)).append(medio(h));
            String sufijoMedio = "";
            if (conExtra) {
                hechosMedio.append(medio(e));
                sufijoMedio = " También " + e.nombre + " tenía acceso.";
            }
            agregarEvidencia(evMedio, idEscena, a.medio(), true, a.medio().horas(),
                    texto(a.medio().resultado()) + sufijoMedio, hechosMedio.toString(), "");
            agregarEvidencia(evMotivoC, idCasaC,
                    new PlantillaEvidencia("Cartas y cuentas guardadas bajo llave", "Documento",
                            "En el domicilio de " + c.nombre + ", un cajón con llave guarda cartas y anotaciones: \""
                                    + c.motivo + "\".", "", 0, ""),
                    false, 0, "", "",
                    "tiene_motivo(" + c.id + ", " + cadena(c.motivo) + ").");
            agregarEvidencia(evMotivoH, idEscena,
                    new PlantillaEvidencia("Denuncia previa contra " + h.nombre, "Documento",
                            "Semanas atrás, " + v.get("V") + " dejó asentado un conflicto grave con " + h.nombre
                                    + ". Motivo: " + h.motivo + ".", "", 0, ""),
                    false, 0, "", "",
                    "tiene_motivo(" + h.id + ", " + cadena(h.motivo) + ").");
            agregarEvidencia(evCoartada, idCoartadaH,
                    new PlantillaEvidencia(coartadaH.evidencia(), coartadaH.tipo(), generizar(texto(coartadaH.detalle()), h.femenino), "", 0, ""),
                    false, 0, "", "",
                    "descarta(" + evCoartada + ", " + h.id + ").");
            if (conPresente) {
                agregarAnalizable(evExtra, idCasaC, a.extra(), 0, "vincula(" + evExtra + ", " + c.id + ").");
            }
            agregarAnalizable(evDistractor, idEscena, a.distractor(), 0, "");

            // ------------------------------------------------------------------ sospechosos
            String alibiC = pre + "_alibi_c";
            String alibiH = pre + "_alibi_h";
            String lugarAlibiC = elegir(BancoDatos.COARTADAS_FALSAS);
            String testimonio = pre + "_testimonio";
            int hw = h1 + r.nextInt(h2 - h1 + 1);
            int hp = h1 + r.nextInt(h2 - h1 + 1);

            String confrontacion = generizar(Plantilla.aplicar(elegir(BancoDatos.CONFRONTACIONES),
                    Map.of("EVID", "\"" + a.vinculo().nombre().toLowerCase() + "\"", "C", c.nombre,
                            "ESC", escena.nombre(), "HC", FormatoHora.formatear(hc))), c.femenino);
            sospechosos.add(new Sospechoso(c.id, "", c.nombre, capitalizar(c.rolTexto) + " de " + v.get("V"),
                    descripcion(c),
                    "A esa hora estuve en " + lugarAlibiC + ". " + defensa(c),
                    "declara_en(" + c.id + ", " + alibiC + ", " + (h1 - 1) + ", " + (h2 + 1) + ").",
                    evVinculo, confrontacion,
                    "admite_en(" + c.id + ", " + idEscena + ", " + hc + ")."));
            sospechosos.add(new Sospechoso(h.id, "", h.nombre, capitalizar(h.rolTexto) + " de " + v.get("V"),
                    descripcion(h) + (apuntaAlCulpable ? "" : " Es la persona a la que todos señalan."),
                    texto(h.admision) + ". Pero a esa hora yo estaba lejos. Pregunten en \"" + coartadaH.lugar()
                            + "\": pueden comprobarlo.",
                    "declara_en(" + h.id + ", " + alibiH + ", " + (h1 - 2) + ", " + (h2 + 2) + ").",
                    "", "", ""));
            if (conPresente) {
                sospechosos.add(new Sospechoso(p.id, "", p.nombre, capitalizar(p.rolTexto) + " de " + v.get("V"),
                        descripcion(p),
                        "Estuve en " + escena.nombre() + " ese día, no lo voy a negar: llegué cerca de las "
                                + FormatoHora.formatear(hp) + ". " + texto(p.admision) + ". Pero yo no hice nada; "
                                + w.nombre + " estuvo conmigo todo el tiempo.",
                        "admite_en(" + p.id + ", " + idEscena + ", " + hp + "). tiene_motivo(" + p.id + ", "
                                + cadena(p.motivo) + ").",
                        "", "", ""));
            }
            if (conExtra) {
                sospechosos.add(new Sospechoso(e.id, "", e.nombre, capitalizar(e.rolTexto) + " de " + v.get("V"),
                        descripcion(e),
                        texto(e.admision) + ". Ese día no salí de " + elegir(BancoDatos.COARTADAS_FALSAS) + ". "
                                + defensa(e),
                        "tiene_motivo(" + e.id + ", " + cadena(e.motivo) + ").",
                        "", "", ""));
            }
            String declaracionTestigo = "Dice que estuvo en " + lugarAlibiC + ", pero a las " + FormatoHora.formatear(hw)
                    + " yo pasé por ahí y no estaba. Un rato después vi a " + c.nombre + " cerca de "
                    + escena.nombre() + ", con prisa."
                    + (conPresente ? generizar(" Y a " + p.nombre + " lo tuve a la vista todo el tiempo: no se acercó a "
                    + v.get("V") + ".", p.femenino) : "");
            String hechosTestigo = "desmiente(" + testimonio + ", " + c.id + ", " + alibiC + ", " + hw + "). nombre("
                    + testimonio + ", " + cadena("Testimonio de " + w.nombre) + ")."
                    + (conPresente ? " descarta(" + testimonio + ", " + p.id + ")." : "");
            sospechosos.add(new Sospechoso(w.id, "", w.nombre, capitalizar(w.rolTexto),
                    "No tiene relación de interés con " + v.get("V") + ". Habla con cautela, pero recuerda detalles.",
                    declaracionTestigo,
                    "declara_en(" + w.id + ", " + pre + "_alibi_w, 0, 24). " + hechosTestigo,
                    "", "", ""));
            Collections.shuffle(sospechosos, r);

            // ------------------------------------------------------------------ caso
            String senalado = apuntaAlCulpable ? c.nombre : h.nombre;
            String resumen = texto(a.hecho()) + " " + (apuntaAlCulpable
                    ? "Todos los rumores apuntan a " + senalado + ", pero los rumores no son prueba."
                    : "Todos señalan a " + senalado + ". El comisario quiere un culpable rápido; vos querés al correcto.");
            int horasMaximas = calcularHoras(dificultad, conPresente, conExtra);
            Caso caso = new Caso(pre, String.format("%s", texto(elegir(a.titulos()))),
                    a.tecnica() + " · " + a.categoria(),
                    ciudad + " · " + anio, resumen,
                    "Identificá al responsable y presentá al fiscal prueba admisible que lo vincule con el hecho.",
                    a.inspiracion(), a.procedimiento(), generizar(texto(a.epilogo()), c.femenino),
                    dificultad, horasMaximas, c.id,
                    "lugar_crimen(" + idEscena + "). hora_crimen(" + h1 + ", " + h2 + ").",
                    ORDEN_INICIAL + numero, a.categoria());
            return new ExpedienteCaso(caso, conCaso(lugares), conCasoS(sospechosos), conCasoE(evidencias));
        }

        // ------------------------------------------------------------------ horas

        private int calcularHoras(Dificultad d, boolean conPresente, boolean conExtra) {
            int lugaresTotal = lugares.stream().mapToInt(Lugar::horasInspeccion).sum();
            int recoleccion = evidencias.size() * RecolectarEvidencia.HORAS_PROTOCOLO;
            int analisis = evidencias.stream().filter(Evidencia::requiereAnalisis).mapToInt(Evidencia::horasAnalisis).sum();
            int interrogatorios = sospechosos.size() * InterrogarSospechoso.HORAS;
            int total = lugaresTotal + recoleccion + analisis + interrogatorios + ConfrontarSospechoso.HORAS;

            int minimo = lugar(pre + "_escena") + lugar(pre + "_tecnico") + lugar(pre + "_casa_c")
                    + 4 * RecolectarEvidencia.HORAS_PROTOCOLO
                    + evidencia(pre + "_vinculo") + evidencia(pre + "_oportunidad") + evidencia(pre + "_medio")
                    + InterrogarSospechoso.HORAS;
            int holgura = switch (d) {
                case FACIL -> 12;
                case MEDIA -> 8;
                case DIFICIL -> 5;
            };
            return Math.max(minimo + 2, Math.min(total - 4, minimo + holgura));
        }

        private int lugar(String id) {
            return lugares.stream().filter(l -> l.id().equals(id)).findFirst().orElseThrow().horasInspeccion();
        }

        private int evidencia(String id) {
            return evidencias.stream().filter(e -> e.id().equals(id)).findFirst().orElseThrow().horasAnalisis();
        }

        // ------------------------------------------------------------------ evidencias

        private void agregarAnalizable(String id, String lugarId, PlantillaEvidencia p, int extraHoras, String hechos) {
            agregarEvidencia(id, lugarId, p, true, p.horas() + extraHoras, texto(p.resultado()), "", hechos);
        }

        private void agregarEvidencia(String id, String lugarId, PlantillaEvidencia p, boolean analisis, int horas,
                                      String resultado, String hechosAnalisisPrevios, String hechos) {
            String hechosRecoleccion = analisis ? "" : hechos;
            String hechosAnalisis = analisis ? (hechosAnalisisPrevios + hechos) : "";
            evidencias.add(new Evidencia(id, "", lugarId, texto(p.nombre()), p.tipo(), texto(p.descripcion()),
                    hechosRecoleccion, analisis, analisis ? horas : 0, analisis ? p.tecnica() : "",
                    analisis ? generizar(resultado, culpableFemenino) : "", hechosAnalisis));
        }

        private String medio(Persona persona) {
            return "tiene_medio(" + persona.id + ", " + cadena("Tenía acceso a " + a.arma()) + "). ";
        }

        // ------------------------------------------------------------------ personas

        private final class Persona {
            final String id;
            final String nombre;
            final boolean femenino;
            final Rol rol;
            final String rolTexto;
            final String motivo;
            final String admision;

            public Persona(Rol rol) {
                Nombre n = nombre(null);
                this.id = pre + "_s" + (++contadorPersonas);
                this.nombre = n.nombre() + " " + elegir(BancoDatos.APELLIDOS);
                this.femenino = n.femenino();
                this.rol = rol;
                this.rolTexto = rol.para(femenino);
                this.motivo = generizar(texto(rol.motivo()), femenino);
                this.admision = rol.admision();
            }
        }

        private Persona persona(Rol rol) {
            return new Persona(rol);
        }

        private Nombre nombre(Boolean femenino) {
            while (true) {
                Nombre n = elegir(BancoDatos.NOMBRES);
                if ((femenino == null || n.femenino() == femenino) && nombresUsados.add(n.nombre())) {
                    return n;
                }
            }
        }

        private Rol elegirRol() {
            return elegir(BancoDatos.ROLES);
        }

        private Rol elegirRolDistinto(Rol... usados) {
            List<Rol> disponibles = new ArrayList<>(BancoDatos.ROLES);
            disponibles.removeAll(List.of(usados));
            return elegir(disponibles);
        }

        private String descripcion(Persona p) {
            String[] rasgos = {
                    "Mirada firme y respuestas cortas.", "Nervioso, no deja de mover las manos.",
                    "Elegante, habla con cuidado de cada palabra.", "Cansado, con ojeras de varias noches.",
                    "Se muestra colaborador, quizá demasiado.", "Desconfiado, contesta con preguntas.",
                    "Tranquilo, casi indiferente.", "Irritable: cualquier pregunta le parece una acusación."};
            return generizar(rasgos[r.nextInt(rasgos.length)], p.femenino);
        }

        private String defensa(Persona p) {
            return generizar(texto(elegir(BancoDatos.DEFENSAS)), p.femenino);
        }

        private Coartada elegirCoartada(int anio) {
            List<Coartada> posibles = BancoDatos.COARTADAS_VERIFICABLES.stream().filter(c -> c.desde() <= anio).toList();
            return elegir(posibles);
        }

        private Dificultad elegirDificultad() {
            int x = r.nextInt(100);
            return x < 35 ? Dificultad.FACIL : x < 75 ? Dificultad.MEDIA : Dificultad.DIFICIL;
        }

        // ------------------------------------------------------------------ utilidades

        private <T> T elegir(List<T> lista) {
            return lista.get(r.nextInt(lista.size()));
        }

        private String texto(String plantilla) {
            return contraer(Plantilla.aplicar(plantilla, v));
        }

        private List<Lugar> conCaso(List<Lugar> l) {
            return l.stream().map(x -> new Lugar(x.id(), pre, x.nombre(), x.descripcion(), x.horasInspeccion())).toList();
        }

        private List<Sospechoso> conCasoS(List<Sospechoso> l) {
            return l.stream().map(s -> new Sospechoso(s.id(), pre, s.nombre(), s.rol(), contraer(s.descripcion()),
                    contraer(s.declaracion()), s.hechosDeclaracion(), s.evidenciaConfrontacionId(),
                    contraer(s.textoConfrontacion()), s.hechosConfrontacion())).toList();
        }

        private List<Evidencia> conCasoE(List<Evidencia> l) {
            return l.stream().map(e -> new Evidencia(e.id(), pre, e.lugarId(), e.nombre(), e.tipo(),
                    contraer(e.descripcion()), e.hechosRecoleccion(), e.requiereAnalisis(), e.horasAnalisis(),
                    e.tecnicaAnalisis(), contraer(e.resultadoAnalisis()), e.hechosAnalisis())).toList();
        }
    }

    // ------------------------------------------------------------------ texto

    private static final Map<String, String> FEMENINOS = Map.ofEntries(
            Map.entry("condenado", "condenada"), Map.entry("detenido", "detenida"),
            Map.entry("identificado", "identificada"), Map.entry("procesado", "procesada"),
            Map.entry("demorado", "demorada"), Map.entry("internado", "internada"),
            Map.entry("sorprendido", "sorprendida"), Map.entry("Nervioso", "Nerviosa"),
            Map.entry("Cansado", "Cansada"), Map.entry("colaborador", "colaboradora"),
            Map.entry("Desconfiado", "Desconfiada"), Map.entry("Tranquilo", "Tranquila"),
            Map.entry("apurado", "apurada"), Map.entry("lo tuve", "la tuve"),
            Map.entry("alojado", "alojada"), Map.entry("el guarda lo recuerda", "el guarda la recuerda"),
            Map.entry("lo había despedido", "la había despedido"), Map.entry("denunciarlo", "denunciarla"),
            Map.entry("lo acusaba", "la acusaba"), Map.entry("lo estaba llevando", "la estaba llevando"),
            Map.entry("reemplazarlo", "reemplazarla"), Map.entry("lo extorsionaba", "la extorsionaba"),
            Map.entry("lo había estafado", "la había estafado"));

    public static String generizar(String texto, boolean femenino) {
        if (!femenino) {
            return texto;
        }
        String resultado = texto;
        for (Map.Entry<String, String> e : FEMENINOS.entrySet()) {
            resultado = resultado.replace(e.getKey(), e.getValue());
        }
        return resultado;
    }

    /** Contracciones del español que surgen al combinar plantillas ("de el" → "del"). */
    private static String contraer(String s) {
        return s.replace(" de el ", " del ").replace(" a el ", " al ")
                .replace("De el ", "Del ").replace(" en en ", " en ");
    }

    private static String capitalizar(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String cadena(String texto) {
        return "\"" + texto.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
