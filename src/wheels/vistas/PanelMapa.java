package wheels.vistas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/**
 * Mapa de la ruta de un viaje: origen -> puntos de recogida -> destino.
 * Se dibuja con Java2D (no necesita internet). Las coordenadas de los
 * lugares conocidos de Bogota y la Sabana son reales (latitud/longitud).
 * Puede mostrar un carro que avanza por la ruta (viaje en curso).
 */
public class PanelMapa extends JPanel {

    /** Lugares conocidos: {nombre en minuscula, latitud, longitud}. */
    private static final Object[][] LUGARES = {
        {"universidad de la sabana", 4.8614, -74.0336},
        {"sabana", 4.8614, -74.0336},
        {"universidad de los andes", 4.6014, -74.0661},
        {"andes", 4.6014, -74.0661},
        {"universidad nacional", 4.6381, -74.0840},
        {"javeriana", 4.6280, -74.0646},
        {"rosario", 4.6005, -74.0730},
        {"externado", 4.5960, -74.0685},
        {"centro chia", 4.8829, -74.0373},
        {"chia", 4.8616, -74.0592},
        {"cajica", 4.9186, -74.0277},
        {"portal del norte", 4.7546, -74.0460},
        {"portal norte", 4.7546, -74.0460},
        {"calle 170", 4.7518, -74.0412},
        {"calle 100", 4.6863, -74.0560},
        {"zona t", 4.6670, -74.0530},
        {"portal suba", 4.7460, -74.0960},
        {"calle 72", 4.6580, -74.0600},
        {"chapinero", 4.6480, -74.0630},
        {"usaquen", 4.6950, -74.0310},
        {"suba", 4.7410, -74.0840},
        {"zipaquira", 5.0221, -74.0048},
        {"bogota", 4.7110, -74.0721},
    };

    private final ArrayList<double[]> puntos = new ArrayList<>();   // {lat, lon}
    private final ArrayList<String> nombres = new ArrayList<>();
    private double progreso = -1;   // -1 = sin carro; 0..1 = posicion del carro
    private boolean alerta = false;

    public PanelMapa(Viaje viaje) {
        setLayout(null);
        setOpaque(false);
        agregar(viaje.getOrigen(), null);
        for (String[] p : BaseDatos.puntosRecogida(viaje.getId())) {
            agregar(p[0], p[1]);
        }
        agregar(viaje.getDestino(), null);
    }

    private void agregar(String nombre, String direccion) {
        double[] c = ubicar(nombre);
        if (c == null && direccion != null) c = ubicar(direccion);
        if (c == null) {
            // Lugar desconocido: posicion fija (siempre la misma) cerca de Bogota
            Random r = new Random(nombre.toLowerCase().hashCode());
            c = new double[]{4.65 + r.nextDouble() * 0.12, -74.12 + r.nextDouble() * 0.08};
        }
        puntos.add(c);
        nombres.add(nombre);
    }

    private static double[] ubicar(String texto) {
        String t = texto.toLowerCase();
        for (Object[] l : LUGARES) {
            if (t.contains((String) l[0])) {
                return new double[]{(Double) l[1], (Double) l[2]};
            }
        }
        return null;
    }

    // ------------------------------------------------------------------
    //  API publica
    // ------------------------------------------------------------------

    public void setProgreso(double p) { this.progreso = p; repaint(); }
    public void setAlerta(boolean a) { this.alerta = a; repaint(); }

    /** Distancia total aproximada de la ruta en km. */
    public double distanciaKm() {
        double d = 0;
        for (int i = 1; i < puntos.size(); i++) d += km(puntos.get(i - 1), puntos.get(i));
        return d * 1.3;   // factor por calles reales (no es linea recta)
    }

    /** Latitud/longitud del carro segun el progreso. */
    public double[] posicionActual() {
        double t = Math.max(0, progreso);
        double total = 0;
        for (int i = 1; i < puntos.size(); i++) total += km(puntos.get(i - 1), puntos.get(i));
        double objetivo = total * t, acum = 0;
        for (int i = 1; i < puntos.size(); i++) {
            double tramo = km(puntos.get(i - 1), puntos.get(i));
            if (acum + tramo >= objetivo || i == puntos.size() - 1) {
                double f = tramo == 0 ? 0 : Math.min(1, (objetivo - acum) / tramo);
                double[] a = puntos.get(i - 1), b = puntos.get(i);
                return new double[]{a[0] + (b[0] - a[0]) * f, a[1] + (b[1] - a[1]) * f};
            }
            acum += tramo;
        }
        return puntos.get(0);
    }

    /** Enlace de Google Maps con la ruta completa. */
    public String enlaceGoogleMaps() {
        StringBuilder sb = new StringBuilder("https://www.google.com/maps/dir/?api=1");
        sb.append("&origin=").append(coord(puntos.get(0)));
        sb.append("&destination=").append(coord(puntos.get(puntos.size() - 1)));
        if (puntos.size() > 2) {
            sb.append("&waypoints=");
            for (int i = 1; i < puntos.size() - 1; i++) {
                if (i > 1) sb.append("%7C");
                sb.append(coord(puntos.get(i)));
            }
        }
        return sb.toString();
    }

    public static String coord(double[] c) {
        return String.format(java.util.Locale.US, "%.5f,%.5f", c[0], c[1]);
    }

    private static double km(double[] a, double[] b) {
        double dLat = (b[0] - a[0]) * 111.0;
        double dLon = (b[1] - a[1]) * 111.0 * Math.cos(Math.toRadians(a[0]));
        return Math.sqrt(dLat * dLat + dLon * dLon);
    }

    // ------------------------------------------------------------------
    //  Dibujo
    // ------------------------------------------------------------------

    private double minLat, maxLat, minLon, maxLon;

    private void calcularLimites() {
        minLat = 99; maxLat = -99; minLon = 999; maxLon = -999;
        for (double[] p : puntos) {
            minLat = Math.min(minLat, p[0]); maxLat = Math.max(maxLat, p[0]);
            minLon = Math.min(minLon, p[1]); maxLon = Math.max(maxLon, p[1]);
        }
        double h = Math.max(maxLat - minLat, 0.02), w = Math.max(maxLon - minLon, 0.02);
        // mantener proporcion del panel
        double prop = (double) getWidth() / Math.max(1, getHeight());
        if (w / h < prop) w = h * prop; else h = w / prop;
        double cLat = (minLat + maxLat) / 2, cLon = (minLon + maxLon) / 2;
        h *= 1.45; w *= 1.45;
        minLat = cLat - h / 2; maxLat = cLat + h / 2;
        minLon = cLon - w / 2; maxLon = cLon + w / 2;
    }

    private int px(double lon) { return (int) ((lon - minLon) / (maxLon - minLon) * getWidth()); }
    private int py(double lat) { return (int) ((maxLat - lat) / (maxLat - minLat) * getHeight()); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Estilo.suavizar(g2);
        int w = getWidth(), h = getHeight();
        calcularLimites();

        // Fondo del mapa con esquinas redondeadas
        g2.setClip(new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 18, 18));
        g2.setColor(new Color(237, 241, 236));
        g2.fillRect(0, 0, w, h);

        // Parques y agua (decorativos, siempre iguales)
        Random r = new Random(42);
        g2.setColor(new Color(205, 230, 200));
        for (int i = 0; i < 5; i++) {
            g2.fillRoundRect(r.nextInt(w), r.nextInt(h), 40 + r.nextInt(60), 30 + r.nextInt(50), 20, 20);
        }
        g2.setColor(new Color(190, 218, 240));
        g2.setStroke(Estilo.trazo(10));
        g2.drawArc(-w / 2, h / 3, w * 2, h, 20, 60);

        // Calles
        g2.setStroke(Estilo.trazo(1.5f));
        g2.setColor(Color.WHITE);
        for (int x = 14; x < w; x += 26) g2.drawLine(x, 0, x - 18, h);
        for (int y = 10; y < h; y += 26) g2.drawLine(0, y, w, y + 8);
        // Avenidas principales
        g2.setStroke(Estilo.trazo(5));
        g2.setColor(new Color(252, 226, 160));
        g2.drawLine(w / 3, 0, w / 2, h);
        g2.drawLine(0, h * 2 / 3, w, h / 2);

        // Ruta
        Path2D ruta = new Path2D.Double();
        for (int i = 0; i < puntos.size(); i++) {
            int x = px(puntos.get(i)[1]), y = py(puntos.get(i)[0]);
            if (i == 0) ruta.moveTo(x, y); else ruta.lineTo(x, y);
        }
        g2.setStroke(Estilo.trazo(9));
        g2.setColor(Color.WHITE);
        g2.draw(ruta);
        g2.setStroke(Estilo.trazo(5));
        g2.setColor(alerta ? Estilo.ROJO : Estilo.AZUL);
        g2.draw(ruta);

        // Marcadores
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        for (int i = 0; i < puntos.size(); i++) {
            int x = px(puntos.get(i)[1]), y = py(puntos.get(i)[0]);
            boolean primero = i == 0, ultimo = i == puntos.size() - 1;
            Color c = primero ? Estilo.VERDE : ultimo ? Estilo.ROJO : Estilo.AMBAR;
            if (ultimo) {
                // pin de destino
                g2.setColor(c);
                g2.fillOval(x - 9, y - 24, 18, 18);
                g2.fillPolygon(new int[]{x - 7, x + 7, x}, new int[]{y - 11, y - 11, y}, 3);
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 4, y - 19, 8, 8);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillOval(x - 9, y - 9, 18, 18);
                g2.setColor(c);
                g2.fillOval(x - 7, y - 7, 14, 14);
                g2.setColor(Color.WHITE);
                String n = primero ? "A" : String.valueOf(i);
                g2.drawString(n, x - g2.getFontMetrics().stringWidth(n) / 2, y + 4);
            }
            // nombre del lugar
            String nom = nombres.get(i);
            if (nom.length() > 22) nom = nom.substring(0, 21) + ".";
            int tw = g2.getFontMetrics().stringWidth(nom);
            int lx = Math.max(4, Math.min(w - tw - 10, x - tw / 2 - 4));
            int ly = ultimo ? y - 44 : y + 12;
            if (ly < 2) ly = y + 6;
            g2.setColor(new Color(255, 255, 255, 225));
            g2.fillRoundRect(lx, ly, tw + 8, 16, 8, 8);
            g2.setColor(Estilo.TEXTO);
            g2.drawString(nom, lx + 4, ly + 12);
        }

        // Carro
        if (progreso >= 0) {
            double[] c = posicionActual();
            int x = px(c[1]), y = py(c[0]);
            g2.setColor(new Color(alerta ? 220 : 30, alerta ? 38 : 123, alerta ? 38 : 224, 50));
            g2.fillOval(x - 22, y - 22, 44, 44);
            g2.setColor(Color.WHITE);
            g2.fillOval(x - 12, y - 12, 24, 24);
            g2.setColor(alerta ? Estilo.ROJO : Estilo.NAVY);
            g2.fillOval(x - 10, y - 10, 20, 20);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x - 6, y - 3, 12, 6, 3, 3);
            g2.fillRoundRect(x - 4, y - 6, 8, 4, 2, 2);
        }

        // Escala / leyenda
        g2.setClip(null);
        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRoundRect(8, h - 26, 118, 18, 9, 9);
        g2.setColor(Estilo.GRIS);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        g2.drawString(String.format("Ruta: %.1f km aprox.", distanciaKm()), 14, h - 13);

        g2.setColor(Estilo.LINEA);
        g2.setStroke(Estilo.trazo(1));
        g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
        g2.dispose();
    }
}
