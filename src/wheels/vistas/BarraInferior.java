package wheels.vistas;

import java.awt.Graphics;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Barra de navegacion inferior (ventana angosta, estilo celular):
 * Inicio | Mis viajes | Publicar | Avisos | Perfil.
 */
public class BarraInferior extends JPanel {

    private final ItemNavegacion[] items;

    public BarraInferior() {
        super(new GridLayout(1, 5, 0, 0));
        setBackground(Estilo.BLANCO);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));
        items = new ItemNavegacion[]{
            new ItemNavegacion(VentanaBase.INICIO, "Inicio", Iconos.Tipo.INICIO, true),
            new ItemNavegacion(VentanaBase.VIAJES, "Mis viajes", Iconos.Tipo.RUTA, true),
            new ItemNavegacion(VentanaBase.PUBLICAR, "Publicar", Iconos.Tipo.PUBLICAR, true),
            new ItemNavegacion(VentanaBase.AVISOS, "Avisos", Iconos.Tipo.CAMPANA, true),
            new ItemNavegacion(VentanaBase.PERFIL, "Perfil", Iconos.Tipo.PERFIL, true),
        };
        for (ItemNavegacion i : items) add(i);
        setVisible(false);
    }

    void marcar(String seccion) {
        for (ItemNavegacion i : items) i.setActivo(i.getSeccion().equals(seccion));
    }

    void insignias(int avisos, int solicitudes) {
        items[1].setInsignia(solicitudes);
        items[3].setInsignia(avisos);
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Estilo.LINEA);
        g.fillRect(0, 0, getWidth(), 1);
    }
}
