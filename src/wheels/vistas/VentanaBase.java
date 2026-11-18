package wheels.vistas;

import java.awt.Dimension;
import java.util.ArrayDeque;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Clase padre de todas las ventanas de Wheels.
 * Aqui se configura una sola vez el tamano, el fondo y la posicion,
 * para no repetir ese codigo en todas las pantallas (herencia).
 * Tambien guarda un HISTORIAL de pantallas para que "Volver" regrese
 * siempre a la pantalla anterior.
 */
public class VentanaBase extends JFrame {

    /** Pantallas visitadas (la ultima es la anterior a la actual). */
    private static final ArrayDeque<Class<? extends VentanaBase>> historial = new ArrayDeque<>();

    /** Panel donde se colocan todos los componentes de la pantalla. */
    protected JPanel lienzo;

    public VentanaBase(String tituloVentana) {
        setTitle("Wheels - " + tituloVentana);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lienzo = new JPanel(null);                 // null = posiciono todo con setBounds
        lienzo.setPreferredSize(new Dimension(Estilo.ANCHO, Estilo.ALTO));
        lienzo.setBackground(Estilo.FONDO);
        setContentPane(lienzo);

        pack();
        setResizable(false);
        setLocationRelativeTo(null);               // centra la ventana en la pantalla
    }

    /** Cierra esta ventana, la guarda en el historial y abre la siguiente. */
    protected void irA(JFrame siguiente) {
        if (esPrincipal(siguiente)) {
            historial.clear();                     // las pestanas principales reinician el historial
        } else {
            historial.push(getClass());
        }
        mostrar(siguiente);
    }

    /** Regresa a la pantalla anterior. Si no hay, va al Inicio. */
    protected void volver() {
        JFrame anterior = null;
        while (anterior == null && !historial.isEmpty()) {
            try {
                anterior = historial.pop().getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                System.out.println("No se pudo volver: " + e.getMessage());
            }
        }
        mostrar(anterior != null ? anterior : new VentanaHome());
    }

    /** Limpia el historial (por ejemplo al cerrar sesion). */
    public static void limpiarHistorial() {
        historial.clear();
    }

    private void mostrar(JFrame siguiente) {
        siguiente.setLocation(getLocation());     // la nueva ventana aparece en el mismo lugar
        siguiente.setVisible(true);
        this.dispose();
    }

    /** Pantallas raiz: Inicio, pestanas de la barra inferior, login y bienvenida. */
    private static boolean esPrincipal(JFrame f) {
        return f instanceof VentanaHome || f instanceof VentanaMisViajes
                || f instanceof VentanaPerfil || f instanceof VentanaPublicarViaje
                || f instanceof VentanaLogin || f instanceof VentanaBienvenida;
    }

    /** Agrega la barra de navegacion inferior. El parametro dice cual boton se ve activo. */
    protected void agregarBarraInferior(String activo) {
        lienzo.add(new BarraInferior(this, activo));
    }

    /** Agrega el enlace "< Volver" blanco en la esquina del encabezado azul. */
    protected void agregarVolverEncabezado() {
        javax.swing.JButton v = Estilo.enlace("< Volver", 280, 20, 80);
        v.setForeground(Estilo.BLANCO);
        v.addActionListener(e -> volver());
        lienzo.add(v);
        lienzo.setComponentZOrder(v, 0);
    }
}
