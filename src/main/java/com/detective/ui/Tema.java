package com.detective.ui;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** Paleta y tipografías "noir" centralizadas: un solo lugar para cambiar el aspecto del juego. */
public final class Tema {

    public static final Color FONDO = new Color(0x0F1115);
    public static final Color FONDO_PANEL = new Color(0x171A21);
    public static final Color FONDO_ELEVADO = new Color(0x20242E);
    public static final Color BORDE = new Color(0x323846);
    public static final Color TEXTO = new Color(0xE6E1D6);
    public static final Color TEXTO_SUAVE = new Color(0x9A9488);
    public static final Color AMBAR = new Color(0xD4A24C);
    public static final Color AMBAR_OSCURO = new Color(0x8C6A2E);
    public static final Color ROJO = new Color(0xC0473F);
    public static final Color VERDE = new Color(0x6FA26B);
    public static final Color PAPEL = new Color(0xEFE6D2);
    public static final Color TINTA = new Color(0x2B2620);

    private static final Set<String> FUENTES = Arrays.stream(
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
            .collect(Collectors.toSet());

    private static final String SERIF = elegir("Georgia", "Palatino Linotype", "Book Antiqua", "Serif");
    private static final String MAQUINA = elegir("Courier New", "Courier Prime", "Monospaced");
    private static final String SANS = elegir("Segoe UI", "Helvetica Neue", "Arial", "SansSerif");

    private Tema() {
    }

    public static Font titulo(float tam) {
        return new Font(SERIF, Font.BOLD, Math.round(tam));
    }

    public static Font serif(float tam) {
        return new Font(SERIF, Font.PLAIN, Math.round(tam));
    }

    public static Font maquina(float tam) {
        return new Font(MAQUINA, Font.PLAIN, Math.round(tam));
    }

    public static Font texto(float tam) {
        return new Font(SANS, Font.PLAIN, Math.round(tam));
    }

    public static Font negrita(float tam) {
        return new Font(SANS, Font.BOLD, Math.round(tam));
    }

    public static Border margen(int px) {
        return BorderFactory.createEmptyBorder(px, px, px, px);
    }

    public static Border tarjeta() {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE), margen(14));
    }

    /** Ajusta Nimbus a la paleta oscura para que los componentes estándar acompañen el estilo. */
    public static void instalar() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // si Nimbus no está, seguimos con el look por defecto
        }
        UIManager.put("control", FONDO_PANEL);
        UIManager.put("info", FONDO_ELEVADO);
        UIManager.put("nimbusBase", new Color(0x2A2E38));
        UIManager.put("nimbusBlueGrey", new Color(0x3A3F4B));
        UIManager.put("nimbusLightBackground", FONDO_ELEVADO);
        UIManager.put("nimbusFocus", AMBAR_OSCURO);
        UIManager.put("nimbusSelectionBackground", AMBAR_OSCURO);
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("text", TEXTO);
        UIManager.put("textForeground", TEXTO);
        UIManager.put("Table.alternateRowColor", new Color(0x1C2028));
        UIManager.put("defaultFont", texto(14));
    }

    private static String elegir(String... candidatas) {
        for (String f : candidatas) {
            if (FUENTES.contains(f)) {
                return f;
            }
        }
        return candidatas[candidatas.length - 1];
    }
}
