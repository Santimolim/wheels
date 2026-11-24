package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * Dialogos modales con el estilo de Wheels (reemplazan a JOptionPane).
 * Oscurecen la ventana principal mientras estan abiertos.
 * Uso:
 *   boolean ok = Dialogos.confirmar(this, "Cancelar viaje", "Se avisara a los pasajeros.", "Cancelar viaje", true);
 */
public final class Dialogos {

    private Dialogos() { }

    public enum Tono { INFO, EXITO, ALERTA, PELIGRO, NINGUNO }

    public static boolean confirmar(Component padre, String titulo, String mensaje, String textoSi, boolean peligroso) {
        return new Constructor(padre, titulo)
                .tono(peligroso ? Tono.PELIGRO : Tono.INFO)
                .mensaje(mensaje)
                .boton("Volver", Estilo.TipoBoton.SECUNDARIO, null)
                .botonPrincipal(textoSi, peligroso ? Estilo.TipoBoton.PELIGRO : Estilo.TipoBoton.PRIMARIO, null)
                .mostrar() == 1;
    }

    public static void info(Component padre, String titulo, String mensaje, Tono tono) {
        new Constructor(padre, titulo).tono(tono).mensaje(mensaje)
                .botonPrincipal("Entendido", Estilo.TipoBoton.PRIMARIO, null).mostrar();
    }

    public static void error(Component padre, String titulo, String mensaje) {
        info(padre, titulo, mensaje, Tono.PELIGRO);
    }

    /** Construye un dialogo paso a paso. mostrar() devuelve el indice del boton pulsado (-1 si se cerro). */
    public static class Constructor {
        private final Component padre;
        private final String titulo;
        private String mensaje;
        private Tono tono = Tono.NINGUNO;
        private JComponent contenido;
        private int ancho = 440;
        private final List<Object[]> botones = new ArrayList<>();
        private int principal = -1;
        private int resultado = -1;

        public Constructor(Component padre, String titulo) {
            this.padre = padre;
            this.titulo = titulo;
        }

        public Constructor mensaje(String m) { mensaje = m; return this; }
        public Constructor tono(Tono t) { tono = t; return this; }
        public Constructor contenido(JComponent c) { contenido = c; return this; }
        public Constructor ancho(int a) { ancho = a; return this; }

        /** validar: si devuelve false el dialogo no se cierra (errores en el formulario). */
        public Constructor boton(String txt, Estilo.TipoBoton tipo, BooleanSupplier validar) {
            botones.add(new Object[]{txt, tipo, validar});
            return this;
        }

        public Constructor botonPrincipal(String txt, Estilo.TipoBoton tipo, BooleanSupplier validar) {
            principal = botones.size();
            return boton(txt, tipo, validar);
        }

        public int mostrar() {
            Window duena = padre == null ? null : (padre instanceof Window ? (Window) padre : SwingUtilities.getWindowAncestor(padre));
            final JDialog d = new JDialog(duena, titulo, JDialog.ModalityType.APPLICATION_MODAL);
            d.setUndecorated(true);

            JPanel raiz = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    Estilo.suavizar(g2);
                    g2.setColor(Estilo.BLANCO);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                    g2.setColor(Estilo.LINEA_FUERTE);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
                    g2.dispose();
                }
            };
            raiz.setOpaque(false);
            raiz.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

            int anchoReal = ancho;
            if (duena != null) anchoReal = Math.min(ancho, Math.max(300, duena.getWidth() - 32));
            Diseno.Columna col = new Diseno.Columna(anchoReal - 48, 0, 0);

            if (tono != Tono.NINGUNO) {
                Color fondo, color;
                Iconos.Tipo ic;
                switch (tono) {
                    case EXITO:   fondo = Estilo.VERDE_SUAVE; color = Estilo.VERDE_TEXTO; ic = Iconos.Tipo.CHECK; break;
                    case ALERTA:  fondo = Estilo.AMBAR_SUAVE; color = Estilo.AMBAR_TEXTO; ic = Iconos.Tipo.ALERTA; break;
                    case PELIGRO: fondo = Estilo.ROJO_SUAVE; color = Estilo.ROJO_TEXTO; ic = Iconos.Tipo.ALERTA; break;
                    default:      fondo = Estilo.TEAL_SUAVE; color = Estilo.TEAL_OSCURO; ic = Iconos.Tipo.INFO; break;
                }
                JPanel f = Estilo.panel(new BorderLayout());
                f.add(new Estilo.IconoCirculo(ic, fondo, color, 46), BorderLayout.WEST);
                col.agregar(f);
                col.espacio(16);
            }
            col.agregar(Estilo.texto(titulo, Estilo.h2(), Estilo.TEXTO));
            if (mensaje != null) {
                col.espacio(8);
                col.agregar(new Estilo.Parrafo(mensaje, Estilo.cuerpo(), Estilo.GRIS));
            }
            if (contenido != null) {
                col.espacio(18);
                col.agregar(contenido);
            }
            raiz.add(col, BorderLayout.CENTER);

            Diseno.FilaFlexible fila = new Diseno.FilaFlexible(10, 10).aLaDerecha();
            fila.setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
            JButton porDefecto = null;
            for (int i = 0; i < botones.size(); i++) {
                final int idx = i;
                Object[] b = botones.get(i);
                JButton btn = Estilo.boton((String) b[0], (Estilo.TipoBoton) b[1]);
                btn.setPreferredSize(new Dimension(Math.max(110, btn.getPreferredSize().width), btn.getPreferredSize().height));
                final BooleanSupplier validar = (BooleanSupplier) b[2];
                btn.addActionListener(e -> {
                    if (validar != null && !validar.getAsBoolean()) return;
                    resultado = idx;
                    d.dispose();
                });
                fila.add(btn);
                if (i == principal) porDefecto = btn;
            }
            raiz.add(fila, BorderLayout.SOUTH);

            d.setContentPane(raiz);
            if (porDefecto != null) d.getRootPane().setDefaultButton(porDefecto);
            d.getRootPane().registerKeyboardAction(e -> { resultado = -1; d.dispose(); },
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

            // tamano: ancho fijo, alto segun el contenido
            int altoCol = col.altoPara(anchoReal - 48);
            int altoBotones = fila.altoPara(anchoReal - 48) + 22;
            d.setSize(anchoReal, altoCol + altoBotones + 44 + 2);
            if (duena != null && d.getHeight() > duena.getHeight() - 40) {
                d.setSize(anchoReal, Math.max(240, duena.getHeight() - 40));
            }
            d.setShape(new RoundRectangle2D.Double(0, 0, d.getWidth(), d.getHeight(), 22, 22));
            d.setLocationRelativeTo(duena);

            Aplicacion.oscurecer(true);
            try {
                d.setVisible(true);
            } finally {
                Aplicacion.oscurecer(false);
            }
            return resultado;
        }
    }
}
