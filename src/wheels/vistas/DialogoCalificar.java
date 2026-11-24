package wheels.vistas;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import wheels.datos.BaseDatos;
import wheels.modelo.Viaje;

/** Dialogo para calificar al conductor (1 a 5 estrellas + comentario opcional). */
final class DialogoCalificar {

    private static final String[] TEXTOS = {"", "Muy malo", "Malo", "Regular", "Bueno", "¡Excelente!"};

    private DialogoCalificar() { }

    /** Devuelve true si se guardo la calificacion. */
    static boolean mostrar(Component padre, Viaje viaje) {
        final int[] puntaje = {0};
        final Estrella[] estrellas = new Estrella[5];
        final JLabel leyenda = Estilo.texto("Toca una estrella", Estilo.pequenaSemi(), Estilo.GRIS);

        JPanel filaEstrellas = Estilo.panel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        filaEstrellas.setBorder(BorderFactory.createEmptyBorder(0, -4, 0, 0));
        for (int i = 0; i < 5; i++) {
            final int valor = i + 1;
            Estrella e = new Estrella(valor);
            e.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent ev) { pintar(estrellas, valor); leyenda.setText(TEXTOS[valor]); }
                @Override public void mouseExited(MouseEvent ev) {
                    pintar(estrellas, puntaje[0]);
                    leyenda.setText(puntaje[0] == 0 ? "Toca una estrella" : TEXTOS[puntaje[0]]);
                }
                @Override public void mouseClicked(MouseEvent ev) {
                    puntaje[0] = valor;
                    pintar(estrellas, valor);
                    leyenda.setText(TEXTOS[valor]);
                    leyenda.setForeground(Estilo.AMBAR_TEXTO);
                }
            });
            estrellas[i] = e;
            filaEstrellas.add(e);
        }

        JTextArea comentario = new JTextArea(3, 20);
        comentario.setLineWrap(true);
        comentario.setWrapStyleWord(true);
        comentario.setFont(Estilo.cuerpo());
        comentario.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Cuéntanos cómo te fue (opcional)");
        comentario.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane sc = new JScrollPane(comentario);
        sc.putClientProperty(FlatClientProperties.STYLE, "arc:12;borderColor:#d3dae5;focusedBorderColor:#0f766e");
        sc.setPreferredSize(new Dimension(300, 92));

        Diseno.Columna contenido = new Diseno.Columna(400, 0, 0);
        contenido.agregar(filaEstrellas);
        contenido.espacio(6);
        contenido.agregar(leyenda);
        contenido.espacio(14);
        contenido.agregar(sc);
        Estilo.Aviso aviso = new Estilo.Aviso();
        contenido.espacio(8);
        contenido.agregar(aviso);

        int r = new Dialogos.Constructor(padre, "¿Cómo estuvo tu viaje con " + viaje.getConductor().getPrimerNombre() + "?")
                .mensaje("Tu calificación ayuda a que la comunidad viaje más segura.")
                .contenido(contenido)
                .boton("Ahora no", Estilo.TipoBoton.SECUNDARIO, null)
                .botonPrincipal("Enviar calificación", Estilo.TipoBoton.PRIMARIO, () -> {
                    if (puntaje[0] == 0) {
                        aviso.mostrar("Elige de 1 a 5 estrellas.", "alerta");
                        contenido.revalidate();
                        return false;
                    }
                    return true;
                })
                .mostrar();
        if (r != 1) return false;
        String c = comentario.getText().trim();
        if (c.length() > 500) c = c.substring(0, 500);
        return BaseDatos.calificar(viaje, viaje.getConductor().getId(), puntaje[0], c);
    }

    private static void pintar(Estrella[] es, int hasta) {
        for (Estrella e : es) e.setLlena(e.valor <= hasta);
    }

    private static final class Estrella extends ComponenteAccesible {
        final int valor;
        private boolean llena = false;
        Estrella(int valor) {
            super(javax.accessibility.AccessibleRole.PUSH_BUTTON);
            this.valor = valor;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setToolTipText(valor + (valor == 1 ? " estrella" : " estrellas"));
            getAccessibleContext().setAccessibleName(valor + " estrellas");
        }
        void setLlena(boolean b) { llena = b; repaint(); }
        @Override public Dimension getPreferredSize() { return new Dimension(44, 44); }
        @Override protected void paintComponent(Graphics g) {
            Iconos.de(llena ? Iconos.Tipo.ESTRELLA_LLENA : Iconos.Tipo.ESTRELLA, 38, llena ? Estilo.AMBAR : Estilo.LINEA_FUERTE)
                    .paintIcon(this, g, 3, 3);
        }
    }
}
