package com.detective.ui.pantallas.investigacion;

import com.detective.razonamiento.prolog.ExcepcionProlog;
import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

/**
 * Consola para hacer consultas Prolog sobre la base de conocimiento de la partida.
 * Pensada para aprender: se ve exactamente qué deduce el motor y por qué.
 */
public final class PestanaConsolaProlog implements Pestana {

    private static final String[] EJEMPLOS = {
            "sospechoso_principal(S)",
            "motivo(S, Motivo)",
            "medio(S, Medio)",
            "oportunidad(S, Lugar, Hora)",
            "contradiccion(S, Evidencia)",
            "descartado(S, PorQue)",
            "vinculo(Evidencia, S)",
            "prueba_admisible(E)",
            "nombre(Id, Nombre)",
            "estuvo_en(S, L, H), H >= 20"
    };

    private final ContextoPestanas ctx;
    private final JPanel raiz = Componentes.panel(new BorderLayout(0, 10));
    private final JTextArea salida = Componentes.texto("");

    public PestanaConsolaProlog(ContextoPestanas ctx) {
        this.ctx = ctx;
        raiz.setBorder(Tema.margen(12));
        salida.setFont(Tema.maquina(14));
        salida.setText("?- Escribí una consulta Prolog y presioná Enter.\n"
                + "   La base contiene las reglas del juego + SOLO los hechos que descubriste.\n\n");

        JTextField consulta = new JTextField("sospechoso_principal(S)");
        consulta.setFont(Tema.maquina(15));
        consulta.addActionListener(e -> ejecutar(consulta.getText()));
        JComboBox<String> ejemplos = new JComboBox<>(EJEMPLOS);
        ejemplos.addActionListener(e -> consulta.setText((String) ejemplos.getSelectedItem()));

        JPanel barra = Componentes.panel(new BorderLayout(8, 0));
        barra.add(Componentes.etiqueta("?-", Tema.maquina(18), Tema.AMBAR), BorderLayout.WEST);
        barra.add(consulta, BorderLayout.CENTER);
        JPanel botones = Componentes.panel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.add(ejemplos);
        BotonNoir correr = new BotonNoir("Consultar", BotonNoir.Estilo.PRIMARIO);
        correr.addActionListener(e -> ejecutar(consulta.getText()));
        botones.add(correr);
        BotonNoir exportar = new BotonNoir("Exportar .pl");
        exportar.setToolTipText("Guarda la base de conocimiento para probarla en SWI-Prolog");
        exportar.addActionListener(e -> exportar());
        botones.add(exportar);
        barra.add(botones, BorderLayout.EAST);

        raiz.add(barra, BorderLayout.NORTH);
        raiz.add(Componentes.desplazable(salida), BorderLayout.CENTER);
    }

    private void ejecutar(String texto) {
        String q = texto.trim();
        if (q.isEmpty()) {
            return;
        }
        salida.append("?- " + q + "\n");
        try {
            List<Map<String, String>> soluciones = ctx.deduccion().consultaLibre(ctx.investigacion(), q);
            if (soluciones.isEmpty()) {
                salida.append("false.\n\n");
            }
            for (Map<String, String> sol : soluciones) {
                salida.append(sol.isEmpty() ? "true." : sol.toString().replace("{", "   ").replace("}", ""));
                salida.append("\n");
            }
            if (!soluciones.isEmpty()) {
                salida.append(soluciones.size() + " solución(es).\n\n");
            }
        } catch (ExcepcionProlog | IllegalArgumentException | StackOverflowError ex) {
            salida.append("ERROR: " + ex.getMessage() + "\n\n");
        }
        salida.setCaretPosition(salida.getDocument().getLength());
    }

    private void exportar() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(ctx.investigacion().getExpediente().caso().id() + ".pl"));
        if (fc.showSaveDialog(raiz) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String programa = ":- set_prolog_flag(unknown, fail).\n"
                + "% Base exportada desde Expedientes: Casos Reales\n\n"
                + ctx.deduccion().constructorPara(ctx.investigacion()).comoTexto();
        try {
            Files.writeString(fc.getSelectedFile().toPath(), programa, StandardCharsets.UTF_8);
            salida.append("% Exportado a " + fc.getSelectedFile().getAbsolutePath() + "\n\n");
        } catch (Exception ex) {
            salida.append("ERROR al exportar: " + ex.getMessage() + "\n\n");
        }
    }

    @Override
    public String titulo() {
        return "Consola Prolog";
    }

    @Override
    public JComponent componente() {
        return raiz;
    }

    @Override
    public void actualizar() {
        // la consola se consulta bajo demanda
    }
}
