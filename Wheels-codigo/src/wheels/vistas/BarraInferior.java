package wheels.vistas;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;

/**
 * Barra de navegacion inferior: Home | Trips | Publish | Profile.
 * Se reutiliza en las pantallas Home, Available Trips, My Trips,
 * Publish Trip y Profile.
 */
public class BarraInferior extends JPanel {

    public BarraInferior(final VentanaBase ventana, String activo) {
        setLayout(null);
        setBounds(0, Estilo.ALTO - 58, Estilo.ANCHO, 58);
        setBackground(Estilo.BLANCO);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Estilo.LINEA));

        JButton bHome    = crear("Home",    0,   activo.equals("Home"));
        JButton bTrips   = crear("Trips",   95,  activo.equals("Trips"));
        JButton bPublish = crear("Publish", 190, activo.equals("Publish"));
        JButton bProfile = crear("Profile", 285, activo.equals("Profile"));

        bHome.addActionListener(e -> ventana.irA(new VentanaHome()));
        bTrips.addActionListener(e -> ventana.irA(new VentanaMisViajes()));
        bProfile.addActionListener(e -> ventana.irA(new VentanaPerfil()));

        bPublish.addActionListener(e -> {
            // Si no tiene vehiculo registrado, primero lo mandamos a registrarlo.
            if (BaseDatos.usuarioActual.tieneVehiculo()) {
                ventana.irA(new VentanaPublicarViaje());
            } else {
                javax.swing.JOptionPane.showMessageDialog(ventana,
                        "Primero debes registrar tu vehiculo para poder publicar viajes.",
                        "Wheels", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                ventana.irA(new VentanaRegistrarVehiculo());
            }
        });

        add(bHome);
        add(bTrips);
        add(bPublish);
        add(bProfile);
    }

    private JButton crear(String texto, int x, boolean activo) {
        JButton b = new JButton(texto);
        b.setBounds(x, 8, 95, 42);
        b.setFont(new Font("SansSerif", activo ? Font.BOLD : Font.PLAIN, 12));
        b.setForeground(activo ? Estilo.AZUL : new Color(154, 167, 184));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }
}
