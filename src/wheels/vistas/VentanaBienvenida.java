package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/** PANTALLA 1 - Bienvenida: presentacion de Wheels con las dos entradas (iniciar sesion / crear cuenta). */
public class VentanaBienvenida extends VentanaBase {

    private JButton entrar;

    public VentanaBienvenida() {
        super("Bienvenida");

        Estilo.PanelMarca fondo = new Estilo.PanelMarca(new BorderLayout(), 0);
        Diseno.Columna c = new Diseno.Columna(520, 32, 40).centrada();

        c.agregar(PanelAcceso.filaIzquierda(new Logo.Marca(48, Estilo.BLANCO, true)));
        c.espacio(48);
        c.agregar(new Estilo.Parrafo("Comparte el camino a la universidad.", Estilo.fuente(32, Estilo.NEGRITA), Estilo.BLANCO));
        c.espacio(14);
        c.agregar(new Estilo.Parrafo("Wheels conecta a estudiantes que van hacia el mismo campus. "
                + "Publica los puestos libres de tu carro o reserva uno en segundos.",
                Estilo.fuente(16, Estilo.REGULAR), new Color(0xC7D2FE)));
        c.espacio(36);
        c.agregar(PanelAcceso.beneficio(Iconos.Tipo.AHORRO, "Ahorra en cada trayecto",
                "Paga solo tu puesto; el conductor define el precio al publicar."));
        c.espacio(18);
        c.agregar(PanelAcceso.beneficio(Iconos.Tipo.UNIVERSIDAD, "Comunidad universitaria",
                "Registro exclusivo con correo institucional (.edu)."));
        c.espacio(18);
        c.agregar(PanelAcceso.beneficio(Iconos.Tipo.ESCUDO, "Viaja tranquilo",
                "Chat, calificaciones y botón SOS durante el recorrido."));
        c.espacio(44);

        JPanel botones = Estilo.panel(new GridLayout(1, 2, 12, 0));
        entrar = Estilo.boton("Iniciar sesión", Estilo.TipoBoton.ACENTO, Estilo.Tamano.GRANDE, null);
        entrar.addActionListener(e -> irA(VentanaLogin::new));
        JButton crear = Estilo.boton("Crear cuenta", Estilo.TipoBoton.CLARO, Estilo.Tamano.GRANDE, null);
        crear.addActionListener(e -> irA(VentanaRegistro::new));
        botones.add(entrar);
        botones.add(crear);
        c.agregar(botones);

        javax.swing.JScrollPane sc = new javax.swing.JScrollPane(c);
        sc.setBorder(null);
        sc.setOpaque(false);
        sc.getViewport().setOpaque(false);
        sc.getVerticalScrollBar().setUnitIncrement(24);
        fondo.add(sc, BorderLayout.CENTER);
        add(fondo, BorderLayout.CENTER);
    }

    @Override public boolean esRaiz() { return true; }

    @Override public void alMostrar() { entrar.requestFocusInWindow(); }
}
