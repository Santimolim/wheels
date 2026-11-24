package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import wheels.datos.BaseDatos;

/** PANTALLA - Notificaciones del usuario (tabla notifications). */
public class VentanaNotificaciones extends VentanaBase {

    public VentanaNotificaciones() {
        super("Notificaciones");
        ArrayList<String[]> datos = BaseDatos.notificaciones();
        int sinLeer = 0;
        for (String[] n : datos) if ("0".equals(n[3])) sinLeer++;

        JButton ir = Estilo.boton("Ir a Mis viajes", Estilo.TipoBoton.SECUNDARIO, Estilo.Tamano.PEQUENO, Iconos.Tipo.RUTA);
        ir.addActionListener(e -> Aplicacion.navegar(VIAJES));
        encabezado("Notificaciones", sinLeer == 0 ? "Estás al día" : sinLeer + (sinLeer == 1 ? " nueva" : " nuevas"), false, ir);
        Diseno.Columna c = crearCuerpo(760);

        if (datos.isEmpty()) {
            Estilo.Tarjeta t = Estilo.Tarjeta.conColumna(0);
            t.columna().agregar(Estilo.vacio(Iconos.Tipo.CAMPANA, "No tienes notificaciones",
                    "Aquí verás cuando acepten tus solicitudes, te escriban o alguien pida un puesto en tus viajes.", null));
            c.agregar(t);
        } else {
            Estilo.Tarjeta lista = Estilo.Tarjeta.conColumna(0);
            lista.conRelleno(6, 6);
            Diseno.Columna l = lista.columna();
            boolean primero = true;
            for (String[] n : datos) {
                if (!primero) l.agregar(Estilo.separador());
                l.agregar(fila(n));
                primero = false;
            }
            c.agregar(lista);
        }
        // al abrir la pantalla quedan leidas (se siguen viendo resaltadas hasta salir)
        if (sinLeer > 0) BaseDatos.marcarNotificacionesLeidas();
    }

    @Override public String seccion() { return AVISOS; }
    @Override public boolean esRaiz() { return true; }

    private JPanel fila(String[] n) {
        boolean nueva = "0".equals(n[3]);
        String tipo = n[4] == null ? "" : n[4];
        Iconos.Tipo ic;
        Color fondo, color;
        if (tipo.startsWith("panic")) { ic = Iconos.Tipo.SOS; fondo = Estilo.ROJO_SUAVE; color = Estilo.ROJO_TEXTO; }
        else if (tipo.equals("request_accepted") || tipo.equals("trip_completed")) { ic = Iconos.Tipo.CHECK; fondo = Estilo.VERDE_SUAVE; color = Estilo.VERDE_TEXTO; }
        else if (tipo.equals("request_rejected") || tipo.contains("cancel")) { ic = Iconos.Tipo.CERRAR; fondo = Estilo.ROJO_SUAVE; color = Estilo.ROJO_TEXTO; }
        else if (tipo.equals("new_message")) { ic = Iconos.Tipo.CHAT; fondo = Estilo.AZUL_SUAVE; color = Estilo.AZUL_TEXTO; }
        else if (tipo.equals("rating")) { ic = Iconos.Tipo.ESTRELLA_LLENA; fondo = Estilo.AMBAR_SUAVE; color = Estilo.AMBAR_TEXTO; }
        else if (tipo.equals("trip_request")) { ic = Iconos.Tipo.PERSONAS; fondo = Estilo.TEAL_SUAVE; color = Estilo.TEAL_OSCURO; }
        else { ic = Iconos.Tipo.CAMPANA; fondo = Estilo.HOVER; color = Estilo.GRIS; }

        Diseno.Columna txt = new Diseno.Columna(100000, 0, 0).conSeparacion(3);
        JPanel titulo = Estilo.panel(new BorderLayout(8, 0));
        JLabel t = Estilo.texto(n[0], nueva ? Estilo.cuerpoSemi() : Estilo.cuerpo(), Estilo.TEXTO);
        titulo.add(t, BorderLayout.CENTER);
        JLabel cuando = Estilo.texto(Utilidades.tiempoRelativo(n[2]), Estilo.fuente(12, Estilo.REGULAR), Estilo.GRIS_CLARO);
        cuando.setToolTipText(n[2]);
        titulo.add(cuando, BorderLayout.EAST);
        txt.agregar(titulo);
        txt.agregar(new Estilo.Parrafo(n[1], Estilo.pequena(), Estilo.GRIS));

        PanelAcceso.FilaIcono f = new PanelAcceso.FilaIcono(new Estilo.IconoCirculo(ic, fondo, color, 40), txt) {
            @Override protected void paintComponent(java.awt.Graphics g) {
                if (nueva) {
                    java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                    Estilo.suavizar(g2);
                    g2.setColor(new Color(0xF0FDFA));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(Estilo.TEAL);
                    g2.fillOval(getWidth() - 12, getHeight() / 2 - 4, 8, 8);
                    g2.dispose();
                }
            }
        };
        f.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 26));
        f.getAccessibleContext().setAccessibleName((nueva ? "Nueva: " : "") + n[0] + ". " + n[1]);
        return new ConMargen(f);
    }

    /** FilaIcono no usa insets; este envoltorio agrega el margen interno y conserva el alto segun ancho. */
    private static final class ConMargen extends JPanel implements Diseno.AltoSegunAncho {
        private final PanelAcceso.FilaIcono fila;
        ConMargen(PanelAcceso.FilaIcono fila) {
            super(new BorderLayout());
            setOpaque(false);
            this.fila = fila;
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            add(fila, BorderLayout.CENTER);
        }
        @Override public int altoPara(int ancho) {
            return 8 + fila.altoPara(ancho - 8);
        }
    }
}
