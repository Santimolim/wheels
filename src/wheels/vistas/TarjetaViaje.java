package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/**
 * Tarjeta que resume un viaje: fecha y hora, precio, ruta, conductor y puestos.
 * Toda la tarjeta es clicable y abre el detalle del viaje.
 */
public class TarjetaViaje extends Estilo.Tarjeta {

    public TarjetaViaje(final Viaje viaje) {
        this(viaje, true);
    }

    /** clicable = false para vistas previas (no abre el detalle). */
    public TarjetaViaje(final Viaje viaje, boolean clicable) {
        super(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        getAccessibleContext().setAccessibleName("Viaje " + viaje.getRuta() + ", " + viaje.getFechaAmigable()
                + " " + viaje.getHora() + ", " + viaje.getPrecioTexto());

        // ---- arriba: fecha/hora y precio ----
        JPanel arriba = Estilo.panel(new BorderLayout(8, 0));
        Estilo.Pastilla cuando = new Estilo.Pastilla(viaje.getFechaAmigable() + " · " + viaje.getHora(),
                Estilo.TEAL_SUAVE, Estilo.TEAL_OSCURO, Iconos.Tipo.CALENDARIO);
        JPanel izq = Estilo.panel(new BorderLayout());
        izq.add(cuando, BorderLayout.WEST);
        arriba.add(izq, BorderLayout.CENTER);
        JLabel precio = Estilo.texto(viaje.getPrecioTexto(), Estilo.fuente(19, Estilo.NEGRITA), Estilo.NAVY);
        arriba.add(precio, BorderLayout.EAST);
        add(arriba, BorderLayout.NORTH);

        // ---- centro: ruta ----
        add(new RutaMini(viaje.getOrigen(), viaje.getDestino()), BorderLayout.CENTER);

        // ---- abajo: conductor y puestos ----
        JPanel abajo = Estilo.panel(new BorderLayout(10, 0));
        abajo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.LINEA),
                BorderFactory.createEmptyBorder(12, 0, 0, 0)));
        abajo.add(new Estilo.Avatar(viaje.getConductor().getIniciales(), viaje.getConductor().getEmail(), 34), BorderLayout.WEST);
        JPanel quien = Estilo.panel(new java.awt.GridLayout(2, 1));
        quien.add(Estilo.texto(viaje.getConductor().getNombre(), Estilo.pequenaSemi(), Estilo.TEXTO));
        JLabel cal = Estilo.texto(viaje.getConductor().getCalificacion() + "  ·  " + viaje.getVehiculo().getMarca()
                + " " + viaje.getVehiculo().getModelo(), Estilo.fuente(12, Estilo.REGULAR), Estilo.GRIS);
        cal.setIcon(Iconos.de(Iconos.Tipo.ESTRELLA_LLENA, 12, Estilo.AMBAR));
        cal.setIconTextGap(4);
        quien.add(cal);
        abajo.add(quien, BorderLayout.CENTER);
        int p = viaje.getPuestosDisponibles();
        JLabel puestos = Estilo.texto(p + (p == 1 ? " puesto" : " puestos"), Estilo.pequenaSemi(),
                p <= 1 ? Estilo.AMBAR_TEXTO : Estilo.TEXTO2);
        puestos.setIcon(Iconos.de(Iconos.Tipo.PERSONAS, 16, p <= 1 ? Estilo.AMBAR_TEXTO : Estilo.GRIS));
        puestos.setIconTextGap(6);
        abajo.add(puestos, BorderLayout.EAST);
        add(abajo, BorderLayout.SOUTH);

        if (clicable) {
            alHacerClic(() -> {
                BaseDatos.viajeSeleccionado = viaje;
                Aplicacion.ir(VentanaDetalleViaje::new);
            });
        }
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(300, 196);
    }

    /** Linea de tiempo compacta: origen arriba, destino abajo, unidos por una linea punteada. */
    static class RutaMini extends ComponenteAccesible {
        private final String origen, destino;

        RutaMini(String origen, String destino) {
            this.origen = origen;
            this.destino = destino;
            setToolTipText(origen + " → " + destino);
        }

        @Override public Dimension getPreferredSize() { return new Dimension(200, 58); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Estilo.suavizar(g2);
            int y1 = 10, y2 = 46;
            g2.setColor(Estilo.LINEA_FUERTE);
            g2.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND,
                    1f, new float[]{1f, 5f}, 0f));
            g2.drawLine(7, y1 + 8, 7, y2 - 8);
            g2.setStroke(Estilo.trazo(2.4f));
            g2.setColor(Estilo.TEAL_OSCURO);
            g2.draw(new Ellipse2D.Float(2.5f, y1 - 4.5f, 9, 9));
            g2.setColor(Estilo.NAVY);
            g2.fill(new Ellipse2D.Float(2, y2 - 5, 10, 10));
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(5, y2 - 2, 4, 4));

            int x = 24, ancho = getWidth() - x;
            g2.setFont(Estilo.cuerpoSemi());
            g2.setColor(Estilo.TEXTO);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(recortar(origen, fm, ancho), x, y1 - fm.getHeight() / 2 + fm.getAscent());
            g2.drawString(recortar(destino, fm, ancho), x, y2 - fm.getHeight() / 2 + fm.getAscent());
            g2.dispose();
        }
    }

    /** Recorta el texto con "…" para que quepa en el ancho dado. */
    static String recortar(String txt, FontMetrics fm, int ancho) {
        if (fm.stringWidth(txt) <= ancho) return txt;
        String t = txt;
        while (t.length() > 1 && fm.stringWidth(t + "…") > ancho) t = t.substring(0, t.length() - 1);
        return t.trim() + "…";
    }
}
