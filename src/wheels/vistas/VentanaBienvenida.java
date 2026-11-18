package wheels.vistas;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** PANTALLA 1 - Welcome / Splash. */
public class VentanaBienvenida extends VentanaBase {

    public VentanaBienvenida() {
        super("Bienvenida");
        lienzo.setBackground(Estilo.NAVY);

        JLabel logo = new JLabel("W", SwingConstants.CENTER);
        logo.setBounds(140, 180, 100, 100);
        logo.setOpaque(true);
        logo.setBackground(Estilo.TEAL);
        logo.setForeground(Estilo.NAVY);
        logo.setFont(new Font("SansSerif", Font.BOLD, 56));
        lienzo.add(logo);

        JLabel nombre = new JLabel("Wheels", SwingConstants.CENTER);
        nombre.setBounds(0, 300, Estilo.ANCHO, 46);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 40));
        nombre.setForeground(Estilo.BLANCO);
        lienzo.add(nombre);

        JLabel frase = new JLabel(
                "<html><div style='text-align:center;'>Comparte el camino a la universidad<br>con tus companeros.</div></html>",
                SwingConstants.CENTER);
        frase.setBounds(40, 352, Estilo.ANCHO - 80, 50);
        frase.setFont(Estilo.NORMAL);
        frase.setForeground(new java.awt.Color(159, 178, 204));
        lienzo.add(frase);

        JButton comenzar = Estilo.botonPrimario("Comenzar", 90, 440, 200);
        comenzar.setBackground(Estilo.TEAL);
        comenzar.setForeground(Estilo.NAVY);
        comenzar.addActionListener(e -> irA(new VentanaLogin()));
        lienzo.add(comenzar);
    }
}
