package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * Pantalla de confirmacion (solicitud enviada, viaje publicado...):
 * un check animado, el mensaje y dos acciones.
 */
public class VentanaExito extends VentanaBase {

    private final CheckAnimado check = new CheckAnimado();
    private final JButton principal;

    public VentanaExito(String titulo, String mensaje, String textoPrincipal, Runnable accionPrincipal,
                        String textoSecundario, Runnable accionSecundaria) {
        this(titulo, mensaje, null, textoPrincipal, accionPrincipal, textoSecundario, accionSecundaria);
    }

    public VentanaExito(String titulo, String mensaje, JComponent detalle, String textoPrincipal, Runnable accionPrincipal,
                        String textoSecundario, Runnable accionSecundaria) {
        super(titulo);
        Diseno.Columna c = new Diseno.Columna(480, 24, 40).centrada();

        JPanel ic = Estilo.panel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        ic.add(check);
        c.agregar(ic);
        c.espacio(24);
        JLabel t = Estilo.texto(titulo, Estilo.fuente(26, Estilo.NEGRITA), Estilo.TEXTO);
        t.setHorizontalAlignment(SwingConstants.CENTER);
        c.agregar(t);
        c.espacio(10);
        Estilo.Parrafo p = new Estilo.Parrafo(mensaje, Estilo.fuente(15, Estilo.REGULAR), Estilo.GRIS);
        p.centrar();
        c.agregar(p);
        if (detalle != null) {
            c.espacio(24);
            c.agregar(detalle);
        }
        c.espacio(32);
        JPanel botones = Estilo.panel(new GridLayout(1, 2, 12, 0));
        JButton sec = Estilo.boton(textoSecundario, Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.GRANDE, null);
        sec.addActionListener(e -> accionSecundaria.run());
        principal = Estilo.boton(textoPrincipal, Estilo.TipoBoton.PRIMARIO, Estilo.Tamano.GRANDE, null);
        principal.addActionListener(e -> accionPrincipal.run());
        botones.add(sec);
        botones.add(principal);
        c.agregar(botones);

        javax.swing.JScrollPane sc = new javax.swing.JScrollPane(c);
        sc.setBorder(null);
        sc.getViewport().setBackground(Estilo.FONDO);
        add(sc, BorderLayout.CENTER);
    }

    @Override public String seccion() { return INICIO; }
    @Override public boolean esRaiz() { return true; }

    @Override public void alMostrar() {
        principal.requestFocusInWindow();
        check.iniciar();
    }

    @Override public void alSalir() { check.detener(); }

    /** Circulo que crece y check que se dibuja (animacion de ~0.7 s). */
    private static final class CheckAnimado extends ComponenteAccesible {
        private float t = 0f;
        private Timer timer;

        CheckAnimado() {
            setPreferredSize(new Dimension(96, 96));
            getAccessibleContext().setAccessibleName("Completado");
        }

        void iniciar() {
            final long inicio = System.currentTimeMillis();
            timer = new Timer(15, e -> {
                t = Math.min(1f, (System.currentTimeMillis() - inicio) / 700f);
                repaint();
                if (t >= 1f) ((Timer) e.getSource()).stop();
            });
            timer.start();
        }

        void detener() { if (timer != null) timer.stop(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Estilo.suavizar(g2);
            float c = Math.min(1f, t / 0.45f);
            float escala = 1f - (float) Math.pow(1 - c, 3);            // ease-out
            float halo = 96 * escala, cuerpo = 72 * escala;
            g2.setColor(new Color(20, 184, 166, 40));
            g2.fill(new Ellipse2D.Float(48 - halo / 2, 48 - halo / 2, halo, halo));
            g2.setColor(Estilo.TEAL_OSCURO);
            g2.fill(new Ellipse2D.Float(48 - cuerpo / 2, 48 - cuerpo / 2, cuerpo, cuerpo));
            float k = Math.max(0f, (t - 0.4f) / 0.6f);
            if (k > 0) {
                Path2D p = new Path2D.Float();
                float[][] pts = {{34, 49}, {44, 59}, {63, 39}};
                float l1 = (float) Math.hypot(10, 10), l2 = (float) Math.hypot(19, 20), total = l1 + l2;
                float d = total * k;
                p.moveTo(pts[0][0], pts[0][1]);
                if (d <= l1) {
                    p.lineTo(pts[0][0] + 10 * d / l1, pts[0][1] + 10 * d / l1);
                } else {
                    p.lineTo(pts[1][0], pts[1][1]);
                    float r = (d - l1) / l2;
                    p.lineTo(pts[1][0] + 19 * r, pts[1][1] - 20 * r);
                }
                g2.setColor(Color.WHITE);
                g2.setStroke(Estilo.trazo(6f));
                g2.draw(p);
            }
            g2.dispose();
        }
    }
}
