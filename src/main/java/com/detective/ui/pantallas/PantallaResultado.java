package com.detective.ui.pantallas;

import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.model.Caso;
import com.detective.domain.model.Veredicto;
import com.detective.servicio.DesglosePuntaje;
import com.detective.servicio.ResultadoCierre;
import com.detective.ui.Navegador;
import com.detective.ui.Tema;
import com.detective.ui.componentes.BotonNoir;
import com.detective.ui.componentes.Componentes;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public final class PantallaResultado extends JPanel {

    public PantallaResultado(Navegador navegador, Investigacion inv, ResultadoCierre cierre) {
        super(new BorderLayout());
        setBackground(Tema.FONDO);
        Caso caso = inv.getExpediente().caso();
        Veredicto v = cierre.juicio().veredicto();
        Color color = switch (v) {
            case CONDENA -> Tema.VERDE;
            case ABSOLUCION_FALTA_PRUEBAS -> Tema.AMBAR;
            case ERROR_JUDICIAL -> Tema.ROJO;
        };

        JPanel cabecera = Componentes.panel(new BorderLayout(0, 6));
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(6, 0, 1, 0, color), Tema.margen(22)));
        JLabel titulo = Componentes.titulo(v.getTitulo(), 34);
        titulo.setForeground(color);
        cabecera.add(titulo, BorderLayout.NORTH);
        String acusado = inv.getExpediente().sospechoso(cierre.juicio().acusadoId()).nombre();
        JTextArea descripcion = Componentes.texto("Acusado: " + acusado + "  ·  " + v.getDescripcion());
        descripcion.setFont(Tema.serif(16));
        descripcion.setBackground(Tema.FONDO);
        cabecera.add(descripcion, BorderLayout.CENTER);
        cabecera.add(Componentes.etiqueta("PUNTAJE: " + cierre.puntaje().total(), Tema.titulo(22), Tema.AMBAR),
                BorderLayout.SOUTH);
        add(cabecera, BorderLayout.NORTH);

        StringBuilder fiscal = new StringBuilder("DICTAMEN DEL FISCAL\n\n");
        cierre.juicio().fundamentos().forEach(f -> fiscal.append(f).append('\n'));
        fiscal.append("\nPUNTAJE\n\n");
        for (DesglosePuntaje.Concepto c : cierre.puntaje().conceptos()) {
            fiscal.append(String.format("%-46s %6d%n", c.descripcion(), c.puntos()));
        }
        fiscal.append(String.format("%-46s %6d%n", "TOTAL", cierre.puntaje().total()));

        String culpable = inv.getExpediente().sospechoso(caso.culpableId()).nombre();
        String historia = "RESOLUCIÓN\n\n" + (v == Veredicto.CONDENA ? caso.epilogo()
                : "El responsable era " + culpable + ". Así se cerró el expediente en la versión resuelta: "
                + caso.epilogo())
                + "\n\nEL CASO REAL QUE LO INSPIRÓ\n\n" + caso.inspiracionReal()
                + "\n\nTÉCNICA CLAVE\n\n" + caso.procedimientoClave();

        JPanel centro = Componentes.panel(new GridLayout(1, 2, 16, 0));
        centro.setBorder(Tema.margen(18));
        JTextArea izq = Componentes.papel(fiscal.toString());
        JTextArea der = Componentes.papel(historia);
        centro.add(Componentes.desplazable(izq));
        centro.add(Componentes.desplazable(der));
        add(centro, BorderLayout.CENTER);

        JPanel sur = Componentes.panel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        BotonNoir otro = new BotonNoir("Otro expediente", BotonNoir.Estilo.PRIMARIO);
        otro.addActionListener(e -> navegador.irASeleccionDeCaso());
        BotonNoir ranking = new BotonNoir("Ver ranking");
        ranking.addActionListener(e -> navegador.irARanking());
        BotonNoir portada = new BotonNoir("Portada");
        portada.addActionListener(e -> navegador.irAPortada());
        sur.add(portada);
        sur.add(ranking);
        sur.add(otro);
        add(sur, BorderLayout.SOUTH);
    }
}
