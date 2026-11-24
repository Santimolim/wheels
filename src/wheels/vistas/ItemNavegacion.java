package wheels.vistas;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JComponent;

/**
 * Boton de navegacion (menu lateral u horizontal en la barra inferior):
 * icono, texto, estado activo y numerito de pendientes.
 */
class ItemNavegacion extends ComponenteAccesible {

    private final String seccion;
    private final String texto;
    private final Iconos.Tipo tipo;
    private final boolean vertical;   // true = icono arriba y texto abajo (barra inferior)
    private boolean activo = false;
    private boolean encima = false;
    private boolean foco = false;
    private int insignia = 0;

    ItemNavegacion(String seccion, String texto, Iconos.Tipo tipo, boolean vertical) {
        super(javax.accessibility.AccessibleRole.PUSH_BUTTON);
        this.seccion = seccion;
        this.texto = texto;
        this.tipo = tipo;
        this.vertical = vertical;
        setOpaque(false);
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(vertical ? null : texto);
        getAccessibleContext().setAccessibleName(texto);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { Aplicacion.navegar(ItemNavegacion.this.seccion); }
        });
        addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    Aplicacion.navegar(ItemNavegacion.this.seccion);
                }
            }
        });
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { foco = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { foco = false; repaint(); }
        });
    }

    String getSeccion() { return seccion; }

    void setActivo(boolean a) { activo = a; repaint(); }

    void setInsignia(int n) {
        if (n != insignia) {
            insignia = n;
            getAccessibleContext().setAccessibleDescription(n > 0 ? n + " pendientes" : null);
            repaint();
        }
    }

    @Override public Dimension getPreferredSize() {
        return vertical ? new Dimension(64, 60) : new Dimension(200, 44);
    }

    @Override public Dimension getMaximumSize() {
        return vertical ? new Dimension(Integer.MAX_VALUE, 60) : new Dimension(Integer.MAX_VALUE, 44);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        Estilo.suavizar(g2);
        int w = getWidth(), h = getHeight();
        Color color = activo ? Estilo.TEAL_OSCURO : (encima ? Estilo.TEXTO : Estilo.GRIS);

        if (vertical) {
            if (activo) {
                g2.setColor(Estilo.TEAL_SUAVE);
                g2.fillRoundRect(w / 2 - 26, 6, 52, 30, 30, 30);
            } else if (encima || foco) {
                g2.setColor(Estilo.HOVER);
                g2.fillRoundRect(w / 2 - 26, 6, 52, 30, 30, 30);
            }
            Icon ic = Iconos.de(tipo, 22, color);
            ic.paintIcon(this, g2, (w - 22) / 2, 10);
            g2.setFont(activo ? Estilo.fuente(11.5f, Estilo.SEMI) : Estilo.fuente(11.5f, Estilo.REGULAR));
            g2.setColor(color);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, (w - fm.stringWidth(texto)) / 2, 52);
            pintarInsignia(g2, w / 2 + 8, 6);
        } else {
            if (activo || encima || foco) {
                g2.setColor(activo ? Estilo.TEAL_SUAVE : Estilo.HOVER);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
            }
            if (activo) {
                g2.setColor(Estilo.TEAL_OSCURO);
                g2.fillRoundRect(0, 12, 4, h - 24, 4, 4);
            }
            Icon ic = Iconos.de(tipo, 20, color);
            ic.paintIcon(this, g2, 16, (h - 20) / 2);
            g2.setFont(activo ? Estilo.cuerpoSemi() : Estilo.cuerpo());
            g2.setColor(activo ? Estilo.TEAL_OSCURO : Estilo.TEXTO2);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, 48, (h - fm.getHeight()) / 2 + fm.getAscent());
            if (insignia > 0) {
                String n = insignia > 99 ? "99+" : String.valueOf(insignia);
                g2.setFont(Estilo.fuente(11, Estilo.SEMI));
                FontMetrics f2 = g2.getFontMetrics();
                int bw = Math.max(22, f2.stringWidth(n) + 12);
                g2.setColor(Estilo.ROJO);
                g2.fillRoundRect(w - bw - 12, (h - 20) / 2, bw, 20, 20, 20);
                g2.setColor(Color.WHITE);
                g2.drawString(n, w - bw - 12 + (bw - f2.stringWidth(n)) / 2, (h - 20) / 2 + (20 - f2.getHeight()) / 2 + f2.getAscent());
            }
        }
        g2.dispose();
    }

    private void pintarInsignia(Graphics2D g2, int x, int y) {
        if (insignia <= 0) return;
        String n = insignia > 9 ? "9+" : String.valueOf(insignia);
        g2.setFont(Estilo.fuente(10, Estilo.SEMI));
        FontMetrics fm = g2.getFontMetrics();
        int bw = Math.max(18, fm.stringWidth(n) + 10);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x - 2, y - 2, bw + 4, 22, 22, 22);
        g2.setColor(Estilo.ROJO);
        g2.fillRoundRect(x, y, bw, 18, 18, 18);
        g2.setColor(Color.WHITE);
        g2.drawString(n, x + (bw - fm.stringWidth(n)) / 2, y + (18 - fm.getHeight()) / 2 + fm.getAscent());
    }
}
