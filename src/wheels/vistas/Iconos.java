package wheels.vistas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.Icon;

/**
 * Iconos vectoriales dibujados con Java2D sobre una cuadricula de 24x24
 * (trazo redondeado de 1.8). Se ven nitidos en cualquier tamano y pantalla.
 */
public final class Iconos {

    private Iconos() { }

    public enum Tipo {
        INICIO, BUSCAR, RUTA, PUBLICAR, PERFIL, CAMPANA, CARRO, UBICACION, ORIGEN, CALENDARIO, RELOJ,
        PERSONAS, DINERO, CHAT, COMPARTIR, MAPA, FLECHA_IZQ, FLECHA_DER, CHEVRON_DER, INTERCAMBIAR,
        ESTRELLA, ESTRELLA_LLENA, CHECK, CERRAR, ALERTA, ESCUDO, SALIR, EDITAR, CANDADO, CORREO,
        TELEFONO, UNIVERSIDAD, INFO, MUSICA, MASCOTA, MALETA, ENVIAR, NAVEGACION, COPIAR, ORDENAR,
        AHORRO, MAS, BASURA, SOS, VOLANTE
    }

    public static Icon de(Tipo tipo, int tam, Color color) {
        return new Vector(tipo, tam, color);
    }

    private static final class Vector implements Icon {
        private final Tipo tipo;
        private final int tam;
        private final Color color;

        Vector(Tipo tipo, int tam, Color color) {
            this.tipo = tipo;
            this.tam = tam;
            this.color = color;
        }

        @Override public int getIconWidth() { return tam; }
        @Override public int getIconHeight() { return tam; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            Estilo.suavizar(g2);
            g2.translate(x, y);
            g2.scale(tam / 24.0, tam / 24.0);
            g2.setColor(color);
            g2.setStroke(Estilo.trazo(1.9f));
            dibujar(g2, tipo);
            g2.dispose();
        }
    }

    private static Path2D p() { return new Path2D.Double(); }

    private static void linea(Graphics2D g, double x1, double y1, double x2, double y2) {
        g.draw(new Line2D.Double(x1, y1, x2, y2));
    }

    private static void circulo(Graphics2D g, double cx, double cy, double r, boolean relleno) {
        Ellipse2D e = new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2);
        if (relleno) g.fill(e); else g.draw(e);
    }

    private static Path2D estrella() {
        Path2D s = p();
        for (int i = 0; i < 10; i++) {
            double ang = Math.toRadians(-90 + i * 36);
            double r = i % 2 == 0 ? 9.5 : 4.2;
            double px = 12 + Math.cos(ang) * r, py = 12.6 + Math.sin(ang) * r;
            if (i == 0) s.moveTo(px, py); else s.lineTo(px, py);
        }
        s.closePath();
        return s;
    }

    static void dibujar(Graphics2D g, Tipo t) {
        Path2D q;
        switch (t) {
            case INICIO:
                q = p(); q.moveTo(3.5, 10.5); q.lineTo(12, 3.8); q.lineTo(20.5, 10.5);
                g.draw(q);
                q = p(); q.moveTo(5.8, 9); q.lineTo(5.8, 19.2); q.quadTo(5.8, 20.2, 6.8, 20.2); q.lineTo(17.2, 20.2);
                q.quadTo(18.2, 20.2, 18.2, 19.2); q.lineTo(18.2, 9);
                g.draw(q);
                q = p(); q.moveTo(9.8, 20.2); q.lineTo(9.8, 14.5); q.lineTo(14.2, 14.5); q.lineTo(14.2, 20.2);
                g.draw(q);
                break;
            case BUSCAR:
                circulo(g, 10.8, 10.8, 6.6, false);
                linea(g, 15.8, 15.8, 20.2, 20.2);
                break;
            case RUTA:
                circulo(g, 6, 18, 2.4, false);
                circulo(g, 18, 6, 2.4, false);
                q = p(); q.moveTo(8.4, 18); q.lineTo(15, 18); q.curveTo(18.6, 18, 18.6, 12, 15, 12);
                q.lineTo(9, 12); q.curveTo(5.4, 12, 5.4, 6, 9, 6); q.lineTo(15.6, 6);
                g.draw(q);
                break;
            case PUBLICAR:
                circulo(g, 12, 12, 9, false);
                linea(g, 12, 8, 12, 16);
                linea(g, 8, 12, 16, 12);
                break;
            case MAS:
                linea(g, 12, 5, 12, 19);
                linea(g, 5, 12, 19, 12);
                break;
            case PERFIL:
                circulo(g, 12, 8.2, 4, false);
                q = p(); q.moveTo(4.5, 20.2); q.curveTo(4.5, 15.8, 8, 13.6, 12, 13.6); q.curveTo(16, 13.6, 19.5, 15.8, 19.5, 20.2);
                g.draw(q);
                break;
            case CAMPANA:
                q = p(); q.moveTo(6, 16.5); q.lineTo(6, 10.5); q.curveTo(6, 7, 8.7, 4.2, 12, 4.2);
                q.curveTo(15.3, 4.2, 18, 7, 18, 10.5); q.lineTo(18, 16.5); q.lineTo(19.6, 18.2); q.lineTo(4.4, 18.2); q.closePath();
                g.draw(q);
                q = p(); q.moveTo(10, 20.6); q.quadTo(12, 22.2, 14, 20.6);
                g.draw(q);
                break;
            case CARRO:
                q = p(); q.moveTo(4, 16.5); q.lineTo(4, 12.6); q.lineTo(6.2, 7.6); q.quadTo(6.7, 6.6, 7.8, 6.6);
                q.lineTo(16.2, 6.6); q.quadTo(17.3, 6.6, 17.8, 7.6); q.lineTo(20, 12.6); q.lineTo(20, 16.5);
                q.quadTo(20, 17.3, 19.2, 17.3); q.lineTo(4.8, 17.3); q.quadTo(4, 17.3, 4, 16.5); q.closePath();
                g.draw(q);
                linea(g, 4.5, 12.4, 19.5, 12.4);
                circulo(g, 7.8, 14.8, 0.6, true);
                circulo(g, 16.2, 14.8, 0.6, true);
                linea(g, 6.5, 17.5, 6.5, 19.6);
                linea(g, 17.5, 17.5, 17.5, 19.6);
                break;
            case VOLANTE:
                circulo(g, 12, 12, 8.6, false);
                circulo(g, 12, 12, 2.2, false);
                linea(g, 3.6, 11, 9.8, 11.6);
                linea(g, 14.2, 11.6, 20.4, 11);
                linea(g, 12, 14.2, 12, 20.4);
                break;
            case UBICACION:
                q = p(); q.moveTo(12, 21); q.curveTo(12, 21, 5, 14.6, 5, 9.8); q.curveTo(5, 5.9, 8.1, 3, 12, 3);
                q.curveTo(15.9, 3, 19, 5.9, 19, 9.8); q.curveTo(19, 14.6, 12, 21, 12, 21); q.closePath();
                g.draw(q);
                circulo(g, 12, 9.8, 2.5, false);
                break;
            case ORIGEN:
                circulo(g, 12, 12, 7.5, false);
                circulo(g, 12, 12, 3, true);
                break;
            case CALENDARIO:
                g.draw(new RoundRectangle2D.Double(3.8, 5.4, 16.4, 15, 3.4, 3.4));
                linea(g, 3.8, 10, 20.2, 10);
                linea(g, 8.2, 3.4, 8.2, 7.2);
                linea(g, 15.8, 3.4, 15.8, 7.2);
                break;
            case RELOJ:
                circulo(g, 12, 12, 8.6, false);
                q = p(); q.moveTo(12, 7.4); q.lineTo(12, 12); q.lineTo(15.2, 14);
                g.draw(q);
                break;
            case PERSONAS:
                circulo(g, 9, 8.4, 3.4, false);
                q = p(); q.moveTo(3, 19.6); q.curveTo(3, 15.8, 5.8, 13.8, 9, 13.8); q.curveTo(12.2, 13.8, 15, 15.8, 15, 19.6);
                g.draw(q);
                q = p(); q.moveTo(15.4, 5.2); q.curveTo(17.6, 5.4, 18.8, 7, 18.8, 8.4); q.curveTo(18.8, 9.8, 17.6, 11.4, 15.4, 11.6);
                g.draw(q);
                q = p(); q.moveTo(17.4, 14.2); q.curveTo(19.6, 14.9, 21, 16.8, 21, 19.6);
                g.draw(q);
                break;
            case DINERO:
                g.draw(new RoundRectangle2D.Double(2.8, 6.4, 18.4, 11.4, 2.8, 2.8));
                circulo(g, 12, 12.1, 2.6, false);
                linea(g, 6, 9.6, 6, 9.6);
                linea(g, 18, 14.6, 18, 14.6);
                break;
            case AHORRO:
                q = p(); q.moveTo(19, 10.5); q.curveTo(19, 6.8, 15.6, 5, 12, 5); q.curveTo(7.2, 5, 4, 8, 4, 11.6);
                q.curveTo(4, 14, 5.4, 15.8, 7, 16.8); q.lineTo(7, 19.6); q.lineTo(10, 19.6); q.lineTo(10.4, 18.2);
                q.lineTo(13.6, 18.2); q.lineTo(14, 19.6); q.lineTo(17, 19.6); q.lineTo(17, 16.6); q.curveTo(18.4, 15.8, 19.4, 14.6, 19.8, 13.2);
                q.lineTo(21, 13.2); q.lineTo(21, 10.5); q.closePath();
                g.draw(q);
                linea(g, 9.5, 7.8, 13.5, 7.8);
                break;
            case CHAT:
                q = p(); q.moveTo(20.4, 11.4); q.curveTo(20.4, 15.8, 16.6, 19, 12, 19); q.curveTo(10.7, 19, 9.5, 18.8, 8.4, 18.3);
                q.lineTo(3.6, 19.8); q.lineTo(5, 15.6); q.curveTo(4.2, 14.4, 3.6, 13, 3.6, 11.4);
                q.curveTo(3.6, 7, 7.4, 3.8, 12, 3.8); q.curveTo(16.6, 3.8, 20.4, 7, 20.4, 11.4); q.closePath();
                g.draw(q);
                break;
            case COMPARTIR:
                circulo(g, 17.6, 5.8, 2.6, false);
                circulo(g, 6.4, 12, 2.6, false);
                circulo(g, 17.6, 18.2, 2.6, false);
                linea(g, 8.7, 10.7, 15.3, 7.1);
                linea(g, 8.7, 13.3, 15.3, 16.9);
                break;
            case MAPA:
                q = p(); q.moveTo(3.5, 6.4); q.lineTo(9, 4); q.lineTo(15, 6.4); q.lineTo(20.5, 4); q.lineTo(20.5, 17.6);
                q.lineTo(15, 20); q.lineTo(9, 17.6); q.lineTo(3.5, 20); q.closePath();
                g.draw(q);
                linea(g, 9, 4, 9, 17.6);
                linea(g, 15, 6.4, 15, 20);
                break;
            case FLECHA_IZQ:
                linea(g, 19, 12, 5, 12);
                q = p(); q.moveTo(11, 6); q.lineTo(5, 12); q.lineTo(11, 18);
                g.draw(q);
                break;
            case FLECHA_DER:
                linea(g, 5, 12, 19, 12);
                q = p(); q.moveTo(13, 6); q.lineTo(19, 12); q.lineTo(13, 18);
                g.draw(q);
                break;
            case CHEVRON_DER:
                q = p(); q.moveTo(9.5, 6); q.lineTo(15.5, 12); q.lineTo(9.5, 18);
                g.draw(q);
                break;
            case INTERCAMBIAR:
                q = p(); q.moveTo(7.5, 4.5); q.lineTo(7.5, 19.5); q.moveTo(4, 8); q.lineTo(7.5, 4.5); q.lineTo(11, 8);
                g.draw(q);
                q = p(); q.moveTo(16.5, 19.5); q.lineTo(16.5, 4.5); q.moveTo(13, 16); q.lineTo(16.5, 19.5); q.lineTo(20, 16);
                g.draw(q);
                break;
            case ORDENAR:
                linea(g, 4, 7, 20, 7);
                linea(g, 7, 12, 17, 12);
                linea(g, 10, 17, 14, 17);
                break;
            case ESTRELLA:
                g.draw(estrella());
                break;
            case ESTRELLA_LLENA:
                g.fill(estrella());
                g.draw(estrella());
                break;
            case CHECK:
                q = p(); q.moveTo(5, 12.5); q.lineTo(10, 17.5); q.lineTo(19.5, 7);
                g.setStroke(Estilo.trazo(2.3f));
                g.draw(q);
                break;
            case CERRAR:
                linea(g, 6.5, 6.5, 17.5, 17.5);
                linea(g, 17.5, 6.5, 6.5, 17.5);
                break;
            case ALERTA:
                q = p(); q.moveTo(12, 3.6); q.lineTo(21.2, 19.6); q.lineTo(2.8, 19.6); q.closePath();
                g.draw(q);
                linea(g, 12, 9.6, 12, 13.6);
                circulo(g, 12, 16.6, 0.9, true);
                break;
            case INFO:
                circulo(g, 12, 12, 8.8, false);
                linea(g, 12, 11, 12, 16.4);
                circulo(g, 12, 7.8, 0.9, true);
                break;
            case ESCUDO:
                q = p(); q.moveTo(12, 3.2); q.lineTo(19.4, 6); q.lineTo(19.4, 11.6); q.curveTo(19.4, 16, 16.2, 19.4, 12, 20.8);
                q.curveTo(7.8, 19.4, 4.6, 16, 4.6, 11.6); q.lineTo(4.6, 6); q.closePath();
                g.draw(q);
                q = p(); q.moveTo(8.8, 12); q.lineTo(11.2, 14.4); q.lineTo(15.4, 9.8);
                g.draw(q);
                break;
            case SOS:
                q = p(); q.moveTo(12, 3.2); q.lineTo(19.4, 6); q.lineTo(19.4, 11.6); q.curveTo(19.4, 16, 16.2, 19.4, 12, 20.8);
                q.curveTo(7.8, 19.4, 4.6, 16, 4.6, 11.6); q.lineTo(4.6, 6); q.closePath();
                g.draw(q);
                linea(g, 12, 8, 12, 12.6);
                circulo(g, 12, 15.6, 0.9, true);
                break;
            case SALIR:
                q = p(); q.moveTo(10, 4); q.lineTo(5.6, 4); q.quadTo(4.4, 4, 4.4, 5.2); q.lineTo(4.4, 18.8); q.quadTo(4.4, 20, 5.6, 20); q.lineTo(10, 20);
                g.draw(q);
                linea(g, 10, 12, 20, 12);
                q = p(); q.moveTo(16, 8); q.lineTo(20, 12); q.lineTo(16, 16);
                g.draw(q);
                break;
            case EDITAR:
                q = p(); q.moveTo(15.6, 4.6); q.lineTo(19.4, 8.4); q.lineTo(8.6, 19.2); q.lineTo(4, 20); q.lineTo(4.8, 15.4); q.closePath();
                g.draw(q);
                linea(g, 13.4, 6.8, 17.2, 10.6);
                break;
            case BASURA:
                linea(g, 4, 6.6, 20, 6.6);
                q = p(); q.moveTo(9, 6.6); q.lineTo(9, 4.4); q.lineTo(15, 4.4); q.lineTo(15, 6.6);
                g.draw(q);
                q = p(); q.moveTo(6.2, 6.6); q.lineTo(7.2, 19.4); q.quadTo(7.3, 20.4, 8.3, 20.4); q.lineTo(15.7, 20.4);
                q.quadTo(16.7, 20.4, 16.8, 19.4); q.lineTo(17.8, 6.6);
                g.draw(q);
                break;
            case CANDADO:
                g.draw(new RoundRectangle2D.Double(5, 10.6, 14, 10, 3, 3));
                q = p(); q.moveTo(8, 10.6); q.lineTo(8, 7.6); q.curveTo(8, 5.2, 9.8, 3.6, 12, 3.6); q.curveTo(14.2, 3.6, 16, 5.2, 16, 7.6); q.lineTo(16, 10.6);
                g.draw(q);
                break;
            case CORREO:
                g.draw(new RoundRectangle2D.Double(3.2, 5.4, 17.6, 13.2, 3, 3));
                q = p(); q.moveTo(3.8, 6.8); q.lineTo(12, 12.8); q.lineTo(20.2, 6.8);
                g.draw(q);
                break;
            case TELEFONO:
                q = p(); q.moveTo(5.2, 3.8); q.lineTo(8.6, 3.8); q.lineTo(10.4, 8.2); q.lineTo(8.2, 9.6);
                q.curveTo(9.2, 11.8, 12.2, 14.8, 14.4, 15.8); q.lineTo(15.8, 13.6); q.lineTo(20.2, 15.4); q.lineTo(20.2, 18.8);
                q.curveTo(20.2, 19.7, 19.5, 20.4, 18.6, 20.3); q.curveTo(10.6, 19.6, 4.4, 13.4, 3.7, 5.4); q.curveTo(3.6, 4.5, 4.3, 3.8, 5.2, 3.8); q.closePath();
                g.draw(q);
                break;
            case UNIVERSIDAD:
                q = p(); q.moveTo(2.6, 9.4); q.lineTo(12, 4.8); q.lineTo(21.4, 9.4); q.lineTo(12, 14); q.closePath();
                g.draw(q);
                q = p(); q.moveTo(6.4, 11.4); q.lineTo(6.4, 16); q.curveTo(8, 18, 10, 18.8, 12, 18.8); q.curveTo(14, 18.8, 16, 18, 17.6, 16); q.lineTo(17.6, 11.4);
                g.draw(q);
                linea(g, 21.4, 9.4, 21.4, 14.6);
                break;
            case MUSICA:
                q = p(); q.moveTo(9, 17.6); q.lineTo(9, 5.6); q.lineTo(19.6, 3.8); q.lineTo(19.6, 15.6);
                g.draw(q);
                circulo(g, 6.6, 17.6, 2.4, false);
                circulo(g, 17.2, 15.6, 2.4, false);
                break;
            case MASCOTA:
                circulo(g, 6.2, 10.4, 1.9, false);
                circulo(g, 9.6, 6.2, 1.9, false);
                circulo(g, 14.4, 6.2, 1.9, false);
                circulo(g, 17.8, 10.4, 1.9, false);
                q = p(); q.moveTo(12, 12); q.curveTo(9.2, 12, 6.8, 15.6, 7.6, 18); q.curveTo(8.4, 20.2, 10.6, 19, 12, 19);
                q.curveTo(13.4, 19, 15.6, 20.2, 16.4, 18); q.curveTo(17.2, 15.6, 14.8, 12, 12, 12); q.closePath();
                g.draw(q);
                break;
            case MALETA:
                g.draw(new RoundRectangle2D.Double(3.4, 7.4, 17.2, 12.2, 3, 3));
                q = p(); q.moveTo(8.6, 7.4); q.lineTo(8.6, 5.4); q.quadTo(8.6, 4.4, 9.6, 4.4); q.lineTo(14.4, 4.4); q.quadTo(15.4, 4.4, 15.4, 5.4); q.lineTo(15.4, 7.4);
                g.draw(q);
                linea(g, 3.6, 12.6, 20.4, 12.6);
                break;
            case ENVIAR:
                q = p(); q.moveTo(20.6, 3.4); q.lineTo(3.4, 10.2); q.lineTo(10.6, 13.4); q.lineTo(13.8, 20.6); q.closePath();
                g.draw(q);
                linea(g, 20.6, 3.4, 10.6, 13.4);
                break;
            case NAVEGACION:
                q = p(); q.moveTo(20, 4); q.lineTo(4, 11); q.lineTo(11.4, 12.6); q.lineTo(13, 20); q.closePath();
                g.draw(q);
                break;
            case COPIAR:
                g.draw(new RoundRectangle2D.Double(8.4, 8.4, 12, 12, 2.6, 2.6));
                q = p(); q.moveTo(15.6, 8.4); q.lineTo(15.6, 5); q.quadTo(15.6, 3.6, 14.2, 3.6); q.lineTo(5, 3.6);
                q.quadTo(3.6, 3.6, 3.6, 5); q.lineTo(3.6, 14.2); q.quadTo(3.6, 15.6, 5, 15.6); q.lineTo(8.4, 15.6);
                g.draw(q);
                break;
            default:
                g.draw(new Arc2D.Double(4, 4, 16, 16, 0, 360, Arc2D.OPEN));
        }
    }
}
