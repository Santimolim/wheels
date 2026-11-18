package wheels.vistas;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Clase padre de todas las ventanas de Wheels.
 * Aqui se configura una sola vez el tamano, el fondo y la posicion,
 * para no repetir ese codigo en las 13 pantallas (herencia).
 */
public class VentanaBase extends JFrame {

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

    /** Cierra esta ventana y abre la siguiente. */
    protected void irA(JFrame siguiente) {
        siguiente.setVisible(true);
        this.dispose();
    }

    /** Agrega la barra de navegacion inferior. El parametro dice cual boton se ve activo. */
    protected void agregarBarraInferior(String activo) {
        lienzo.add(new BarraInferior(this, activo));
    }
}
