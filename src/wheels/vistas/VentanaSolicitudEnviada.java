package wheels.vistas;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/** PANTALLA 8 - Solicitud enviada (confirmacion). */
public class VentanaSolicitudEnviada extends VentanaBase {

    public VentanaSolicitudEnviada() {
        super("Solicitud enviada");

        JLabel icono = new JLabel("OK", SwingConstants.CENTER);
        icono.setBounds(150, 180, 80, 80);
        icono.setOpaque(true);
        icono.setBackground(Estilo.TEAL);
        icono.setForeground(Estilo.NAVY);
        icono.setFont(new Font("SansSerif", Font.BOLD, 26));
        lienzo.add(icono);

        JLabel t = new JLabel("Solicitud enviada", SwingConstants.CENTER);
        t.setBounds(0, 285, Estilo.ANCHO, 30);
        t.setFont(Estilo.TITULO);
        t.setForeground(Estilo.TEXTO);
        lienzo.add(t);

        JLabel s = new JLabel("<html><div style='text-align:center;'>El conductor recibira tu solicitud."
                + "<br>Puedes seguir su estado en My Trips.</div></html>", SwingConstants.CENTER);
        s.setBounds(40, 320, 300, 50);
        s.setFont(Estilo.NORMAL);
        s.setForeground(Estilo.GRIS);
        lienzo.add(s);

        JButton misViajes = Estilo.botonPrimario("Ver mis viajes", 30, 400, 320);
        misViajes.addActionListener(e -> irA(new VentanaMisViajes()));
        lienzo.add(misViajes);

        JButton inicio = Estilo.botonBlanco("Volver al inicio", 30, 452, 320);
        inicio.addActionListener(e -> irA(new VentanaHome()));
        lienzo.add(inicio);
    }
}
