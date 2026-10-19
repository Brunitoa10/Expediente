package com.detective.ui.pantallas;

import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.model.Evidencia;
import com.detective.domain.model.Sospechoso;
import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Formulario de acusación: a quién se acusa y qué pruebas se elevan al fiscal. */
final class DialogoAcusacion {

    record Acusacion(String sospechosoId, Set<String> pruebas) {
    }

    private DialogoAcusacion() {
    }

    static Optional<Acusacion> pedir(JComponent padre, Investigacion inv) {
        Window ventana = SwingUtilities.getWindowAncestor(padre);
        JDialog d = new JDialog(ventana, "Elevar acusación al fiscal", JDialog.ModalityType.APPLICATION_MODAL);
        JPanel raiz = Componentes.panel(new BorderLayout(0, 14));
        raiz.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 0, 0, 0, Tema.ROJO), Tema.margen(20)));
        raiz.add(Componentes.titulo("Acusación formal", 24), BorderLayout.NORTH);

        JComboBox<Sospechoso> acusado = new JComboBox<>(inv.getExpediente().sospechosos().toArray(new Sospechoso[0]));
        acusado.setRenderer((list, value, index, sel, foc) ->
                Componentes.etiqueta(value == null ? "" : value.nombre() + " — " + value.rol(), Tema.texto(14),
                        sel ? Tema.AMBAR : Tema.TEXTO));

        JPanel centro = Componentes.panel(null);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(izquierda(Componentes.etiqueta("ACUSADO", Tema.negrita(12), Tema.TEXTO_SUAVE)));
        centro.add(izquierda(acusado));
        centro.add(javax.swing.Box.createVerticalStrut(14));
        centro.add(izquierda(Componentes.etiqueta("PRUEBAS QUE PRESENTÁS", Tema.negrita(12), Tema.TEXTO_SUAVE)));
        Map<String, JCheckBox> casillas = new LinkedHashMap<>();
        for (Evidencia e : inv.evidenciasRecolectadas()) {
            String marca = inv.custodiaRota(e.id()) ? "  (custodia rota)" : "";
            JCheckBox cb = new JCheckBox(e.nombre() + marca);
            cb.setOpaque(false);
            cb.setForeground(inv.custodiaRota(e.id()) ? Tema.ROJO : Tema.TEXTO);
            cb.setFont(Tema.texto(14));
            casillas.put(e.id(), cb);
            centro.add(izquierda(cb));
        }
        if (casillas.isEmpty()) {
            centro.add(izquierda(Componentes.suave("No recolectaste ninguna evidencia. El fiscal no tendrá con qué trabajar.")));
        }
        centro.add(javax.swing.Box.createVerticalStrut(12));
        centro.add(izquierda(Componentes.suave("La acusación es definitiva: cierra el caso.")));
        javax.swing.JScrollPane sp = Componentes.desplazable(centro);
        sp.setBorder(null);
        sp.setPreferredSize(new Dimension(560, Math.min(460, 180 + casillas.size() * 34)));
        raiz.add(sp, BorderLayout.CENTER);

        Acusacion[] resultado = new Acusacion[1];
        BotonNoir cancelar = new BotonNoir("Seguir investigando");
        cancelar.addActionListener(e -> d.dispose());
        BotonNoir confirmar = new BotonNoir("Acusar", BotonNoir.Estilo.PELIGRO);
        confirmar.addActionListener(e -> {
            Sospechoso s = (Sospechoso) acusado.getSelectedItem();
            if (s != null) {
                Set<String> pruebas = casillas.entrySet().stream().filter(en -> en.getValue().isSelected())
                        .map(Map.Entry::getKey).collect(Collectors.toCollection(java.util.LinkedHashSet::new));
                resultado[0] = new Acusacion(s.id(), pruebas);
            }
            d.dispose();
        });
        JPanel sur = Componentes.panel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        sur.add(cancelar);
        sur.add(confirmar);
        raiz.add(sur, BorderLayout.SOUTH);

        d.setContentPane(raiz);
        d.pack();
        d.setLocationRelativeTo(ventana);
        d.setVisible(true);
        return Optional.ofNullable(resultado[0]);
    }

    private static JComponent izquierda(JComponent c) {
        c.setAlignmentX(0f);
        return c;
    }
}
