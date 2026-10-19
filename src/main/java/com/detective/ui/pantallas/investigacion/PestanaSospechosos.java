package com.detective.ui.pantallas.investigacion;

import com.detective.domain.investigacion.ConfrontarSospechoso;
import com.detective.domain.investigacion.InterrogarSospechoso;
import com.detective.domain.investigacion.Investigacion;
import com.detective.domain.model.Deduccion;
import com.detective.domain.model.Sospechoso;
import com.detective.ui.componentes.BotonNoir;

import javax.swing.JComponent;
import java.util.List;

public final class PestanaSospechosos extends PestanaListaDetalle<Sospechoso> {

    public PestanaSospechosos(ContextoPestanas ctx) {
        super(ctx);
    }

    @Override
    public String titulo() {
        return "Sospechosos";
    }

    @Override
    protected List<Sospechoso> elementos() {
        return ctx.investigacion().getExpediente().sospechosos();
    }

    @Override
    protected Object clave(Sospechoso s) {
        return s.id();
    }

    @Override
    protected String rotulo(Sospechoso s) {
        Investigacion inv = ctx.investigacion();
        String estado = inv.fueConfrontado(s.id()) ? "§  " : inv.fueInterrogado(s.id()) ? "√  " : "□  ";
        return estado + s.nombre() + " — " + s.rol();
    }

    @Override
    protected JComponent detalleDe(Sospechoso s) {
        Investigacion inv = ctx.investigacion();
        StringBuilder sb = new StringBuilder(s.descripcion());
        if (inv.fueInterrogado(s.id())) {
            sb.append(Fichas.separador()).append("DECLARACIÓN\n\n«").append(s.declaracion()).append("»");
        }
        if (inv.fueConfrontado(s.id())) {
            sb.append(Fichas.separador()).append("CONFRONTACIÓN\n\n").append(s.textoConfrontacion());
        }
        List<Deduccion> propias = ctx.deduccion().deducciones(inv).stream()
                .filter(d -> s.id().equals(d.sospechosoId())).toList();
        if (!propias.isEmpty()) {
            sb.append(Fichas.separador()).append("LO QUE SABEMOS (motor Prolog)\n\n");
            propias.forEach(d -> sb.append("• ").append(d.tipo().getEtiqueta()).append(": ").append(d.detalle()).append('\n'));
        }
        // Se habilita para todos por igual: deshabilitarlo sólo para algunos delataría al culpable.
        boolean puedeConfrontar = inv.fueInterrogado(s.id()) && !inv.fueConfrontado(s.id());
        return Fichas.ficha(s.nombre(), s.rol(), sb.toString(),
                Fichas.boton("Tomar declaración (" + InterrogarSospechoso.HORAS + " h)", BotonNoir.Estilo.PRIMARIO,
                        !inv.fueInterrogado(s.id()), "Entrevista con preguntas abiertas (modelo PEACE)",
                        () -> ctx.ejecutor().ejecutar(new InterrogarSospechoso(s.id()))),
                Fichas.boton("Confrontar con evidencia (" + ConfrontarSospechoso.HORAS + " h)", BotonNoir.Estilo.SECUNDARIO,
                        puedeConfrontar, "Requiere una contradicción o una prueba vinculante",
                        () -> ctx.ejecutor().ejecutar(new ConfrontarSospechoso(s.id()))));
    }
}
