package wheels.vistas;

import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** PANTALLA 12 - Trip Published (confirmacion). */
public class VentanaViajePublicado extends VentanaBase {

    public VentanaViajePublicado() {
        super("Viaje publicado");

        Viaje v = BaseDatos.viajeSeleccionado;

        JLabel icono = new JLabel("OK", SwingConstants.CENTER);
        icono.setBounds(150, 120, 80, 80);
        icono.setOpaque(true);
        icono.setBackground(Estilo.TEAL);
        icono.setForeground(Estilo.NAVY);
        icono.setFont(new Font("SansSerif", Font.BOLD, 26));
        lienzo.add(icono);

        JLabel t = new JLabel("Viaje publicado", SwingConstants.CENTER);
        t.setBounds(0, 222, Estilo.ANCHO, 30);
        t.setFont(Estilo.TITULO);
        t.setForeground(Estilo.TEXTO);
        lienzo.add(t);

        JLabel s = new JLabel("<html><div style='text-align:center;'>Tu viaje ya es visible para los "
                + "estudiantes.<br>Recibiras sus solicitudes.</div></html>", SwingConstants.CENTER);
        s.setBounds(30, 256, 320, 46);
        s.setFont(Estilo.NORMAL);
        s.setForeground(Estilo.GRIS);
        lienzo.add(s);

        JPanel resumen = Estilo.tarjeta(20, 320, 340, 130);
        resumen.add(Estilo.texto("Origen:  " + v.getOrigen(), 14, 12, 300, Estilo.NORMAL, Estilo.TEXTO));
        resumen.add(Estilo.texto("Destino: " + v.getDestino(), 14, 32, 300, Estilo.NORMAL, Estilo.TEXTO));
        resumen.add(Estilo.separador(14, 60, 312));
        resumen.add(Estilo.texto(v.getFecha() + "  -  " + v.getHora() + "  -  "
                + v.getPuestosDisponibles() + " puestos", 14, 70, 300, Estilo.PEQUENA, Estilo.GRIS));
        JLabel precio = Estilo.texto(v.getPrecioTexto(), 14, 92, 312, Estilo.TITULO, Estilo.AZUL);
        precio.setHorizontalAlignment(SwingConstants.RIGHT);
        resumen.add(precio);
        lienzo.add(resumen);

        JButton inicio = Estilo.botonPrimario("Volver al inicio", 20, 480, 340);
        inicio.addActionListener(e -> irA(new VentanaHome()));
        lienzo.add(inicio);
    }
}
