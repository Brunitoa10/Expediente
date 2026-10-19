package com.detective.ui.pantallas.investigacion;

import com.detective.domain.model.Caso;
import com.detective.ui.componentes.Componentes;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public final class PestanaExpediente implements Pestana {

    private final JScrollPane raiz;

    public PestanaExpediente(ContextoPestanas ctx) {
        Caso c = ctx.investigacion().getExpediente().caso();
        String texto = "EXPEDIENTE: " + c.titulo().toUpperCase() + "\n"
                + c.lugarYEpoca() + "\n\n"
                + "HECHO\n" + c.resumen() + "\n\n"
                + "OBJETIVO\n" + c.objetivo() + "\n\n"
                + "TÉCNICA CLAVE DE ESTE CASO\n" + c.procedimientoClave() + "\n\n"
                + "CÓMO TRABAJAR\n"
                + "1. Inspeccioná los lugares (pestaña Lugares). Cada inspección consume horas.\n"
                + "2. Recolectá la evidencia CON PROTOCOLO si querés que sirva en el juicio.\n"
                + "3. Enviá al laboratorio lo que lo requiera (pestaña Evidencias).\n"
                + "4. Tomá declaración a todos (pestaña Sospechosos). Preguntá antes de acusar.\n"
                + "5. Mirá la Pizarra: el motor Prolog cruza todo lo que descubriste.\n"
                + "6. Con una contradicción o una prueba vinculante, confrontá.\n"
                + "7. Acusá y elegí qué pruebas presentar al fiscal.\n\n"
                + "El tiempo no alcanza para hacer todo: elegí como lo haría un investigador real.";
        JTextArea hoja = Componentes.papel(texto);
        raiz = Componentes.desplazable(hoja);
    }

    @Override
    public String titulo() {
        return "Expediente";
    }

    @Override
    public JComponent componente() {
        return raiz;
    }

    @Override
    public void actualizar() {
        // contenido estático
    }
}
