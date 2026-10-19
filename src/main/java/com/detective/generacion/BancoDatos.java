package com.detective.generacion;

import java.util.List;

/** Material narrativo combinable para generar personajes, lugares y declaraciones. */
final class BancoDatos {

    private BancoDatos() {
    }

    record Nombre(String nombre, boolean femenino) {
    }

    /** Relación con la víctima y el motivo que esa relación suele implicar. */
    record Rol(String masculino, String femenino, String motivo, String admision) {
        String para(boolean femenino) {
            return femenino ? this.femenino : masculino;
        }
    }

    /** Coartada verificable por un registro independiente (desde cierto año). */
    record Coartada(int desde, String lugar, String descripcionLugar, String evidencia, String tipo, String detalle) {
    }

    static final List<Nombre> NOMBRES = List.of(
            new Nombre("Ramón", false), new Nombre("Esteban", false), new Nombre("Julián", false),
            new Nombre("Benito", false), new Nombre("Héctor", false), new Nombre("Daniel", false),
            new Nombre("Tomás", false), new Nombre("Ricardo", false), new Nombre("Ignacio", false),
            new Nombre("Dionisio", false), new Nombre("Ernesto", false), new Nombre("Federico", false),
            new Nombre("Gustavo", false), new Nombre("Horacio", false), new Nombre("Joaquín", false),
            new Nombre("Leandro", false), new Nombre("Martín", false), new Nombre("Nicolás", false),
            new Nombre("Óscar", false), new Nombre("Pablo", false), new Nombre("Rodrigo", false),
            new Nombre("Sergio", false), new Nombre("Ulises", false), new Nombre("Víctor", false),
            new Nombre("Walter", false), new Nombre("Agustín", false), new Nombre("Bruno", false),
            new Nombre("Ciro", false), new Nombre("Emilio", false), new Nombre("Fermín", false),
            new Nombre("Gregorio", false), new Nombre("Lisandro", false), new Nombre("Matías", false),
            new Nombre("Octavio", false), new Nombre("Ramiro", false), new Nombre("Simón", false),
            new Nombre("Rosa", true), new Nombre("Amalia", true), new Nombre("Lucía", true),
            new Nombre("María", true), new Nombre("Clementina", true), new Nombre("Ana", true),
            new Nombre("Valeria", true), new Nombre("Beatriz", true), new Nombre("Carolina", true),
            new Nombre("Delfina", true), new Nombre("Elena", true), new Nombre("Florencia", true),
            new Nombre("Graciela", true), new Nombre("Inés", true), new Nombre("Julia", true),
            new Nombre("Laura", true), new Nombre("Mercedes", true), new Nombre("Natalia", true),
            new Nombre("Olga", true), new Nombre("Paula", true), new Nombre("Renata", true),
            new Nombre("Silvia", true), new Nombre("Teresa", true), new Nombre("Victoria", true),
            new Nombre("Aurora", true), new Nombre("Celeste", true), new Nombre("Eugenia", true),
            new Nombre("Josefina", true), new Nombre("Martina", true), new Nombre("Pilar", true),
            new Nombre("Soledad", true), new Nombre("Irene", true));

    static final List<String> APELLIDOS = List.of(
            "Cáceres", "Ibarra", "Ortega", "Velázquez", "Varela", "Quiroga", "Benítez", "Ferraro",
            "Lafuente", "Barbier", "Kessler", "Ruiz", "Pons", "Acosta", "Aguirre", "Albornoz", "Arce",
            "Bazán", "Bustos", "Cabral", "Campos", "Castro", "Cejas", "Correa", "Domínguez", "Echeverría",
            "Escobar", "Farías", "Figueroa", "Funes", "Gallardo", "Giménez", "Godoy", "Herrera", "Juárez",
            "Ledesma", "Lucero", "Maidana", "Medina", "Miranda", "Molina", "Montenegro", "Navarro",
            "Ocampo", "Olmos", "Paz", "Peralta", "Ponce", "Quinteros", "Ríos", "Rivero", "Robledo",
            "Salinas", "Sosa", "Suárez", "Tapia", "Toledo", "Urquiza", "Vera", "Villalba", "Zalazar",
            "Zabala", "Mansilla", "Arrieta", "Belgrano", "Castelli", "Irigoyen", "Lugones", "Moreno",
            "Oliveira", "Pereyra", "Rossi", "Bianchi", "Schulz", "O'Neill", "Duarte", "Iriarte", "Luna");

    static final List<String> CIUDADES = List.of(
            "Necochea, Buenos Aires", "Tandil, Buenos Aires", "Bahía Blanca, Buenos Aires",
            "Mar del Plata, Buenos Aires", "San Isidro, Buenos Aires", "La Plata, Buenos Aires",
            "Chivilcoy, Buenos Aires", "Azul, Buenos Aires", "Rosario, Santa Fe", "Rafaela, Santa Fe",
            "Venado Tuerto, Santa Fe", "Córdoba, Córdoba", "Río Cuarto, Córdoba", "Villa General Belgrano, Córdoba",
            "Mendoza, Mendoza", "San Rafael, Mendoza", "Uspallata, Mendoza", "San Juan, San Juan",
            "Salta, Salta", "Cafayate, Salta", "San Salvador de Jujuy, Jujuy", "Tilcara, Jujuy",
            "San Miguel de Tucumán, Tucumán", "Santiago del Estero, Santiago del Estero",
            "Posadas, Misiones", "Oberá, Misiones", "Corrientes, Corrientes", "Goya, Corrientes",
            "Paraná, Entre Ríos", "Gualeguaychú, Entre Ríos", "Concepción del Uruguay, Entre Ríos",
            "Santa Rosa, La Pampa", "Neuquén, Neuquén", "San Martín de los Andes, Neuquén",
            "Bariloche, Río Negro", "Viedma, Río Negro", "Puerto Madryn, Chubut", "Trelew, Chubut",
            "Comodoro Rivadavia, Chubut", "Río Gallegos, Santa Cruz", "Ushuaia, Tierra del Fuego",
            "Catamarca, Catamarca", "La Rioja, La Rioja", "San Luis, San Luis", "Resistencia, Chaco",
            "Formosa, Formosa", "Buenos Aires, CABA", "Montevideo, Uruguay", "Colonia, Uruguay");

    static final List<String> EMPRESAS = List.of(
            "Metalúrgica del Sur", "Bodegas La Encina", "Transportes Andinos", "Molinos San Ignacio",
            "Frigorífico El Ombú", "Textil Las Acacias", "Aserradero Los Pinos", "Curtiembre La Unión",
            "Yerbatera Ñandutí", "Constructora Horizonte", "Laboratorios Delta", "Editorial Faro",
            "Cooperativa La Esperanza", "Sistemas Australes", "Hotel Gran Continental", "Joyería Imperio",
            "Banco Regional del Plata", "Ingenio Santa Clara", "Olivícola Los Algarrobos", "Lanera Patagónica");

    static final List<Rol> ROLES = List.of(
            new Rol("socio comercial", "socia comercial", "Quería quedarse con la parte del negocio de {V}",
                    "Teníamos diferencias por el negocio, es cierto"),
            new Rol("sobrino", "sobrina", "Heredaría una parte importante de los bienes de {V}",
                    "Sé que heredo, y sé cómo suena eso"),
            new Rol("contador", "contadora", "{V} había descubierto faltantes en las cuentas que manejaba",
                    "Las cuentas tenían desórdenes, lo admito"),
            new Rol("ex pareja", "ex pareja", "No aceptaba la separación y había amenazado a {V}",
                    "Nos separamos mal y dije cosas que no debía"),
            new Rol("empleado despedido", "empleada despedida", "{V} lo había despedido sin indemnización",
                    "Me echaron de mala manera, sí"),
            new Rol("vecino", "vecina", "Arrastraba un pleito judicial con {V} por un terreno",
                    "Con {V} teníamos un juicio por el terreno"),
            new Rol("capataz", "capataza", "{V} iba a denunciarlo por robo de mercadería",
                    "Hubo problemas con mercadería que faltaba"),
            new Rol("hermano", "hermana", "Disputaba con {V} la herencia de sus padres",
                    "Peleábamos por la herencia, como tantas familias"),
            new Rol("prestamista", "prestamista", "{V} le debía una suma grande y se negaba a pagar",
                    "Me debía plata, y mucha"),
            new Rol("competidor", "competidora", "La competencia con {V} lo estaba llevando a la quiebra",
                    "Me estaba arruinando el negocio"),
            new Rol("yerno", "nuera", "{V} se oponía a su matrimonio y amenazaba con desheredar a su pareja",
                    "{V} nunca me aceptó en la familia"),
            new Rol("gerente", "gerenta", "{V} estaba por reemplazarlo en el cargo",
                    "Iba a perder el puesto, lo sabía"),
            new Rol("chofer", "chofer", "{V} lo acusaba de haberle robado dinero",
                    "Me acusó de robarle, y era mentira"),
            new Rol("inquilino", "inquilina", "{V} había iniciado su desalojo",
                    "Me estaba desalojando"),
            new Rol("ex socio", "ex socia", "{V} lo había estafado en un negocio anterior",
                    "Ese hombre me estafó una vez"),
            new Rol("asistente personal", "asistente personal", "{V} lo extorsionaba con un secreto del pasado",
                    "{V} sabía cosas mías que nadie más sabía"));

    static final List<Rol> TESTIGOS = List.of(
            new Rol("sereno del lugar", "serena del lugar", "", ""),
            new Rol("encargado de limpieza", "encargada de limpieza", "", ""),
            new Rol("vecino de enfrente", "vecina de enfrente", "", ""),
            new Rol("cadete", "cadeta", "", ""),
            new Rol("portero", "portera", "", ""),
            new Rol("cocinero", "cocinera", "", ""),
            new Rol("mozo del bar de la esquina", "moza del bar de la esquina", "", ""),
            new Rol("canillita de la cuadra", "canillita de la cuadra", "", ""));

    /** Lugares donde el responsable dice haber estado (coartada falsa, sin registro que la respalde). */
    static final List<String> COARTADAS_FALSAS = List.of(
            "su casa, durmiendo", "la casa de su madre", "el club social", "un bar del centro",
            "la iglesia del barrio", "la biblioteca", "el campo de un amigo", "su taller",
            "la plaza principal", "el cine", "la estación de trenes", "la casa de un primo");

    /** Coartadas verificables del sospechoso señalado: siempre hay un registro independiente. */
    static final List<Coartada> COARTADAS_VERIFICABLES = List.of(
            new Coartada(1800, "Comisaría de la sección", "Un edificio frío con un libro de guardia prolijo.",
                    "Libro de guardia de la comisaría", "Documento",
                    "{H} estuvo demorado en la comisaría por una pelea callejera desde antes del hecho hasta mucho después: figura en el libro de guardia."),
            new Coartada(1800, "Hospital regional", "Pasillos blancos y un registro de guardia.",
                    "Registro de la guardia del hospital", "Documento",
                    "{H} ingresó a la guardia por una herida en la mano antes del hecho y estuvo internado bajo observación durante todo ese horario."),
            new Coartada(1880, "Estación de ferrocarril", "Andenes, boleterías y un jefe de estación que anota todo.",
                    "Talonario de pasajes y testimonio del guarda", "Documento",
                    "{H} viajaba en tren hacia otra ciudad durante todo el horario del hecho: figura en el talonario y el guarda lo recuerda."),
            new Coartada(1900, "Hotel de la ciudad vecina", "Recepción con libro de pasajeros.",
                    "Libro de pasajeros del hotel", "Documento",
                    "{H} estaba alojado en el hotel de la ciudad vecina y, según el conserje y el libro de pasajeros, no salió en todo el horario del hecho."),
            new Coartada(1960, "Peaje de la autopista", "Cabinas, barreras y cámaras.",
                    "Ticket y cámara del peaje", "Digital",
                    "La cámara del peaje registró el auto de {H}, con {H} al volante, a 200 km del lugar durante el hecho."),
            new Coartada(1990, "Fábrica donde trabaja", "Portón, reloj fichador y planillas de turno.",
                    "Fichadas del reloj y planilla de turno", "Documento",
                    "{H} fichó entrada y salida en un turno que cubre todo el horario del hecho, y sus compañeros confirman que no se movió de la línea."),
            new Coartada(2000, "Aeropuerto", "Mostradores, controles y cámaras.",
                    "Registro migratorio y de embarque", "Documento",
                    "{H} embarcó en un vuelo antes del hecho y aterrizó en otro país; figura en el registro migratorio."),
            new Coartada(1920, "Estancia vecina", "Un casco de estancia con un capataz memorioso.",
                    "Libro de jornales de la estancia", "Documento",
                    "{H} trabajó en la yerra con otros seis peones, de sol a sol, sin ausentarse."));

    static final List<String> DEFENSAS = List.of(
            "No tengo nada que ver con esto.",
            "Pregunten a quien quieran.",
            "Es absurdo que me pregunten a mí.",
            "Yo quería a {V}, a mi manera.",
            "Estoy tan sorprendido como todos.",
            "Si sospechan de mí, están perdiendo el tiempo.");

    static final List<String> CONFRONTACIONES = List.of(
            "Ponés sobre la mesa el informe: {EVID}. {C} lo lee dos veces. El silencio se hace largo hasta que, en voz baja, admite que estuvo en {ESC} a las {HC}.",
            "Ante {EVID}, {C} intenta sostener su versión, pero se contradice. Termina reconociendo que esa noche fue a {ESC}, cerca de las {HC}.",
            "{C} mira {EVID} y se derrumba. \"No era mi intención que terminara así\", dice. Admite haber estado en {ESC} a las {HC}.",
            "Le mostrás {EVID}. {C} pide un abogado, pero antes de que llegue ya admitió que pasó por {ESC} a las {HC}.");
}
