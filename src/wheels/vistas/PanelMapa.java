package wheels.vistas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/**
 * Mapa esquematico de la ruta de un viaje: origen -> puntos de recogida -> destino.
 * Se dibuja con Java2D (no necesita internet) usando coordenadas reales de
 * lugares conocidos de Bogota y la Sabana. Si un lugar no se reconoce NO se
 * inventa su posicion: se avisa y se ofrece abrir la ruta en Google Maps.
 */
public class PanelMapa extends JPanel {

    /** Lugares conocidos: {nombre sin tildes en minuscula, latitud, longitud}. Los mas especificos primero. */
    private static final Object[][] LUGARES = {
        {"universidad de la sabana", 4.8614, -74.0336},
        {"unisabana", 4.8614, -74.0336},
        {"universidad de los andes", 4.6014, -74.0661},
        {"uniandes", 4.6014, -74.0661},
        {"universidad nacional", 4.6381, -74.0840},
        {"javeriana", 4.6280, -74.0646},
        {"universidad del rosario", 4.6005, -74.0730},
        {"urosario", 4.6005, -74.0730},
        {"externado", 4.5960, -74.0685},
        {"centro chia", 4.8829, -74.0373},
        {"portal del norte", 4.7546, -74.0460},
        {"portal norte", 4.7546, -74.0460},
        {"portal de suba", 4.7460, -74.0960},
        {"portal suba", 4.7460, -74.0960},
        {"calle 170", 4.7518, -74.0412},
        {"calle 100", 4.6863, -74.0560},
        {"calle 72", 4.6580, -74.0600},
        {"zona t", 4.6670, -74.0530},
        {"unicentro", 4.7023, -74.0414},
        {"andino", 4.6667, -74.0527},
        {"chapinero", 4.6480, -74.0630},
        {"usaquen", 4.6950, -74.0310},
        {"suba", 4.7410, -74.0840},
        {"chia", 4.8616, -74.0592},
        {"cajica", 4.9186, -74.0277},
        {"zipaquira", 5.0221, -74.0048},
        {"cota", 4.8094, -74.1033},
        {"sopo", 4.9078, -73.9381},
        {"tocancipa", 4.9653, -73.9128},
        {"la calera", 4.7206, -73.9686},
        {"funza", 4.7166, -74.2116},
        {"mosquera", 4.7059, -74.2302},
        {"soacha", 4.5794, -74.2168},
        {"bogota", 4.7110, -74.0721},
    };

    private final List<double[]> puntos = new ArrayList<>();   // {lat, lon} solo de lugares reconocidos
    private final List<String> nombres = new ArrayList<>();
    private final List<Integer> tipos = new ArrayList<>();     // 0 origen, 1 parada, 2 destino
    private final List<String> consultas = new ArrayList<>();  // texto para Google Maps (todos los lugares)
    private final List<String> desconocidos = new ArrayList<>();
    private double progreso = -1;   // -1 = sin carro; 0..1 = posicion del carro
    private boolean alerta = false;

    public PanelMapa(Viaje viaje) {
        setOpaque(false);
        ArrayList<String[]> paradas = BaseDatos.puntosRecogida(viaje.getId());
        agregar(viaje.getOrigen(), null, 0);
        for (String[] p : paradas) agregar(p[0], p[1], 1);
        agregar(viaje.getDestino(), null, 2);
        getAccessibleContext().setAccessibleName("Mapa de la ruta " + viaje.getRuta());
    }

    private void agregar(String nombre, String direccion, int tipo) {
        double[] c = ubicar(nombre);
        if (c == null && direccion != null) c = ubicar(direccion);
        String consulta = (direccion != null && !direccion.isEmpty() ? direccion : nombre);
        if (!normalizar(consulta).contains("colombia")) consulta += ", Colombia";
        consultas.add(consulta);
        if (c == null) {
            desconocidos.add(nombre);
            return;
        }
        puntos.add(c);
        nombres.add(nombre);
        tipos.add(tipo);
    }

    static String normalizar(String s) {
        String n = Normalizer.normalize(s == null ? "" : s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase(Locale.ROOT);
    }

    private static double[] ubicar(String texto) {
        String t = normalizar(texto);
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

    /** true si todos los lugares de la ruta se pudieron ubicar. */
    public boolean rutaCompleta() { return desconocidos.isEmpty() && puntos.size() >= 2; }

    public List<String> getDesconocidos() { return desconocidos; }

    /** Distancia aproximada de la ruta en km, o -1 si algun lugar no se reconocio. */
    public double distanciaKm() {
        if (!rutaCompleta()) return -1;
        double d = 0;
        for (int i = 1; i < puntos.size(); i++) d += km(puntos.get(i - 1), puntos.get(i));
        return d * 1.3;   // factor por calles reales (no es linea recta)
    }

    /** Latitud/longitud estimada del carro segun el progreso (solo si la ruta es completa). */
    public double[] posicionActual() {
        if (!rutaCompleta()) return null;
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

    /** Enlace de Google Maps con la ruta completa (por nombre de los lugares). */
    public String enlaceGoogleMaps() {
        StringBuilder sb = new StringBuilder("https://www.google.com/maps/dir/?api=1&travelmode=driving");
        sb.append("&origin=").append(url(consultas.get(0)));
        sb.append("&destination=").append(url(consultas.get(consultas.size() - 1)));
        if (consultas.size() > 2) {
            sb.append("&waypoints=");
            for (int i = 1; i < consultas.size() - 1; i++) {
                if (i > 1) sb.append("%7C");
                sb.append(url(consultas.get(i)));
            }
        }
        return sb.toString();
    }

    private static String url(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static String coord(double[] c) {
        return String.format(Locale.US, "%.5f,%.5f", c[0], c[1]);
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
        double prop = (double) getWidth() / Math.max(1, getHeight());
        if (w / h < prop) w = h * prop; else h = w / prop;
        double cLat = (minLat + maxLat) / 2, cLon = (minLon + maxLon) / 2;
        h *= 1.5; w *= 1.5;
        minLat = cLat - h / 2; maxLat = cLat + h / 2;
        minLon = cLon - w / 2; maxLon = cLon + w / 2;
    }

    private float px(double lon) { return (float) ((lon - minLon) / (maxLon - minLon) * getWidth()); }
    private float py(double lat) { return (float) ((maxLat - lat) / (maxLat - minLat) * getHeight()); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Estilo.suavizar(g2);
        int w = getWidth(), h = getHeight();
        RoundRectangle2D marco = new RoundRectangle2D.Float(0, 0, w, h, 20, 20);
        g2.setClip(marco);

        // Fondo con cuadricula suave (estilo plano)
        g2.setPaint(new GradientPaint(0, 0, new Color(0xEEF2F7), w, h, new Color(0xE6EDF4)));
        g2.fillRect(0, 0, w, h);
        g2.setColor(new Color(255, 255, 255, 150));
        g2.setStroke(new BasicStroke(1f));
        for (int x = 0; x < w; x += 32) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 32) g2.drawLine(0, y, w, y);

        if (puntos.size() < 2) {
            pintarSinRuta(g2, w, h);
            g2.dispose();
            return;
        }

        calcularLimites();
        Path2D ruta = new Path2D.Float();
        for (int i = 0; i < puntos.size(); i++) {
            float x = px(puntos.get(i)[1]), y = py(puntos.get(i)[0]);
            if (i == 0) ruta.moveTo(x, y); else ruta.lineTo(x, y);
        }
        Color colorRuta = alerta ? Estilo.ROJO : Estilo.TEAL_OSCURO;
        g2.setStroke(Estilo.trazo(10));
        g2.setColor(new Color(255, 255, 255, 230));
        g2.draw(ruta);
        g2.setStroke(Estilo.trazo(5));
        g2.setColor(colorRuta);
        g2.draw(ruta);

        // Tramo ya recorrido (mas oscuro)
        if (progreso > 0 && rutaCompleta()) {
            double[] pos = posicionActual();
            Path2D hecho = new Path2D.Float();
            hecho.moveTo(px(puntos.get(0)[1]), py(puntos.get(0)[0]));
            double total = 0;
            for (int i = 1; i < puntos.size(); i++) total += km(puntos.get(i - 1), puntos.get(i));
            double acum = 0, objetivo = total * progreso;
            for (int i = 1; i < puntos.size(); i++) {
                double tramo = km(puntos.get(i - 1), puntos.get(i));
                if (acum + tramo >= objetivo) break;
                hecho.lineTo(px(puntos.get(i)[1]), py(puntos.get(i)[0]));
                acum += tramo;
            }
            hecho.lineTo(px(pos[1]), py(pos[0]));
            g2.setColor(alerta ? new Color(0x7F1D1D) : Estilo.NAVY);
            g2.draw(hecho);
        }

        // Marcadores
        int parada = 1;
        for (int i = 0; i < puntos.size(); i++) {
            float x = px(puntos.get(i)[1]), y = py(puntos.get(i)[0]);
            int tipo = tipos.get(i);
            if (tipo == 2) {
                // pin de destino
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new Ellipse2D.Float(x - 6, y - 2, 12, 5));
                Path2D pin = new Path2D.Float();
                pin.moveTo(x, y);
                pin.curveTo(x - 4, y - 8, x - 11, y - 13, x - 11, y - 21);
                pin.curveTo(x - 11, y - 28, x - 6, y - 33, x, y - 33);
                pin.curveTo(x + 6, y - 33, x + 11, y - 28, x + 11, y - 21);
                pin.curveTo(x + 11, y - 13, x + 4, y - 8, x, y);
                g2.setColor(Estilo.NAVY);
                g2.fill(pin);
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(x - 4, y - 25, 8, 8));
            } else {
                Color c = tipo == 0 ? Estilo.TEAL : Estilo.AMBAR;
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(x - 10, y - 10, 20, 20));
                g2.setColor(c);
                g2.fill(new Ellipse2D.Float(x - 7.5f, y - 7.5f, 15, 15));
                if (tipo == 1) {
                    g2.setColor(Color.WHITE);
                    g2.setFont(Estilo.fuente(10, Estilo.NEGRITA));
                    String n = String.valueOf(parada++);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(n, x - fm.stringWidth(n) / 2f, y + fm.getAscent() / 2f - 1);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fill(new Ellipse2D.Float(x - 3, y - 3, 6, 6));
                }
            }
        }
        pintarEtiquetas(g2, w, h);

        // Carro
        if (progreso >= 0 && rutaCompleta()) {
            double[] c = posicionActual();
            float x = px(c[1]), y = py(c[0]);
            Color base = alerta ? Estilo.ROJO : Estilo.AZUL;
            g2.setColor(Estilo.alfa(base, 45));
            g2.fill(new Ellipse2D.Float(x - 22, y - 22, 44, 44));
            g2.setColor(Color.WHITE);
            g2.fill(new Ellipse2D.Float(x - 13, y - 13, 26, 26));
            g2.setColor(base);
            g2.fill(new Ellipse2D.Float(x - 10.5f, y - 10.5f, 21, 21));
            javax.swing.Icon carro = Iconos.de(Iconos.Tipo.CARRO, 14, Color.WHITE);
            carro.paintIcon(this, g2, Math.round(x - 7), Math.round(y - 7));
        }

        // Leyenda de distancia
        g2.setClip(null);
        double km = distanciaKm();
        String txt = km > 0 ? String.format(Locale.US, "Ruta aprox. %.1f km · mapa esquemático", km)
                : "Mapa esquemático · algunos lugares sin ubicar";
        g2.setFont(Estilo.fuente(11.5f, Estilo.SEMI));
        FontMetrics fm = g2.getFontMetrics();
        int lw = fm.stringWidth(txt) + 20;
        g2.setColor(new Color(255, 255, 255, 235));
        g2.fillRoundRect(10, h - 34, lw, 24, 24, 24);
        g2.setColor(Estilo.TEXTO2);
        g2.drawString(txt, 20, h - 34 + (24 - fm.getHeight()) / 2 + fm.getAscent());

        g2.setColor(Estilo.LINEA_FUERTE);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 20, 20));
        g2.dispose();
    }

    /**
     * Etiquetas con el nombre de cada lugar. Para cada una se prueba abajo, a la
     * derecha, a la izquierda y arriba del marcador; se usa la primera posicion que
     * no tape otra etiqueta ni otro marcador. Si ninguna sirve, se omite.
     */
    private void pintarEtiquetas(Graphics2D g2, int w, int h) {
        g2.setFont(Estilo.fuente(11.5f, Estilo.SEMI));
        FontMetrics fm = g2.getFontMetrics();
        List<java.awt.geom.Rectangle2D> ocupados = new ArrayList<>();
        for (int i = 0; i < puntos.size(); i++) {
            float x = px(puntos.get(i)[1]), y = py(puntos.get(i)[0]);
            float arriba = tipos.get(i) == 2 ? 36 : 12;   // el pin de destino es mas alto
            ocupados.add(new java.awt.geom.Rectangle2D.Float(x - 12, y - arriba, 24, arriba + 12));
        }
        // destino y origen primero: son los mas importantes
        List<Integer> orden = new ArrayList<>();
        for (int i = puntos.size() - 1; i >= 0; i--) if (tipos.get(i) != 1) orden.add(i);
        for (int i = 0; i < puntos.size(); i++) if (tipos.get(i) == 1) orden.add(i);

        for (int i : orden) {
            float x = px(puntos.get(i)[1]), y = py(puntos.get(i)[0]);
            String nom = TarjetaViaje.recortar(nombres.get(i), fm, Math.min(170, w - 40));
            float ew = fm.stringWidth(nom) + 16, eh = 22;
            float arriba = tipos.get(i) == 2 ? 36 : 12;
            float[][] opciones = {
                {x - ew / 2, y + 14},            // abajo
                {x + 16, y - eh / 2},            // derecha
                {x - 16 - ew, y - eh / 2},       // izquierda
                {x - ew / 2, y - arriba - eh - 4} // arriba
            };
            for (float[] o : opciones) {
                float lx = Math.max(6, Math.min(w - ew - 6, o[0]));
                float ly = Math.max(6, Math.min(h - eh - 40, o[1]));
                java.awt.geom.Rectangle2D r = new java.awt.geom.Rectangle2D.Float(lx - 2, ly - 2, ew + 4, eh + 4);
                boolean choca = false;
                for (java.awt.geom.Rectangle2D oc : ocupados) {
                    if (oc.intersects(r)) { choca = true; break; }
                }
                if (choca) continue;
                ocupados.add(r);
                g2.setColor(new Color(15, 23, 42, 22));
                g2.fill(new RoundRectangle2D.Float(lx, ly + 1, ew, eh, eh, eh));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Float(lx, ly, ew, eh, eh, eh));
                g2.setColor(Estilo.TEXTO);
                g2.drawString(nom, lx + 8, ly + (eh - fm.getHeight()) / 2f + fm.getAscent());
                break;
            }
        }
    }

    private void pintarSinRuta(Graphics2D g2, int w, int h) {
        javax.swing.Icon ic = Iconos.de(Iconos.Tipo.MAPA, 40, Estilo.GRIS_CLARO);
        ic.paintIcon(this, g2, (w - 40) / 2, h / 2 - 56);
        g2.setFont(Estilo.cuerpoSemi());
        g2.setColor(Estilo.TEXTO2);
        String t1 = "No podemos dibujar esta ruta sin conexión";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(t1, (w - fm.stringWidth(t1)) / 2, h / 2 + 6);
        g2.setFont(Estilo.pequena());
        g2.setColor(Estilo.GRIS);
        String t2 = "Ábrela en Google Maps para ver el recorrido exacto.";
        fm = g2.getFontMetrics();
        g2.drawString(t2, (w - fm.stringWidth(t2)) / 2, h / 2 + 28);
        g2.setColor(Estilo.LINEA_FUERTE);
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 20, 20));
    }
}
