package com.detective.servicio;

/** Las horas del juego son enteros que pueden superar 24 (días siguientes). */
public final class FormatoHora {

    private FormatoHora() {
    }

    public static String formatear(int hora) {
        int dia = hora / 24 + 1;
        String reloj = String.format("%02d:00", hora % 24);
        return dia == 1 ? reloj : "día " + dia + ", " + reloj;
    }

    public static String formatear(String hora) {
        try {
            return formatear(Integer.parseInt(hora));
        } catch (NumberFormatException e) {
            return hora;
        }
    }
}
