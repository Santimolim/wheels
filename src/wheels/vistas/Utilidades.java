package wheels.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Funciones de apoyo para las pantallas: enlaces, portapapeles, fechas y pequenos componentes. */
final class Utilidades {

    private Utilidades() { }

    /** Abre un enlace en el navegador; si no se puede, lo copia y lo avisa. */
    static void abrirNavegador(Component padre, String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                Aplicacion.toast("Abriendo Google Maps en tu navegador…", Dialogos.Tono.INFO);
                return;
            }
        } catch (Exception ignorado) {
            // se copia el enlace como alternativa
        }
        copiar(url);
        Dialogos.info(padre, "No se pudo abrir el navegador",
                "Copiamos el enlace al portapapeles para que lo pegues en tu navegador.", Dialogos.Tono.ALERTA);
    }

    static void copiar(String texto) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(texto), null);
    }

    /** Fila "Etiqueta ........ Valor" para resumenes. */
    static JPanel filaDato(String etiqueta, String valor) {
        JPanel p = Estilo.panel(new BorderLayout(16, 0));
        p.add(Estilo.texto(etiqueta, Estilo.cuerpo(), Estilo.GRIS), BorderLayout.WEST);
        JLabel v = Estilo.texto(valor, Estilo.cuerpoSemi(), Estilo.TEXTO);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        v.setToolTipText(valor);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    /** Placa con el estilo de las placas colombianas (amarilla, letras negras). */
    static JLabel placa(String placa) {
        Estilo.Pastilla p = new Estilo.Pastilla(placa.toUpperCase(), new Color(0xFACC15), Estilo.TEXTO, null);
        p.setFont(Estilo.fuente(13, Estilo.NEGRITA));
        p.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        p.getAccessibleContext().setAccessibleName("Placa " + placa);
        return p;
    }

    private static final DateTimeFormatter BD = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** "hace 5 min", "hace 3 h", "ayer", "12 nov". */
    static String tiempoRelativo(String fechaBD) {
        if (fechaBD == null || fechaBD.length() < 19) return fechaBD == null ? "" : fechaBD;
        try {
            LocalDateTime f = LocalDateTime.parse(fechaBD, BD);
            Duration d = Duration.between(f, LocalDateTime.now());
            long min = d.toMinutes();
            if (min < 1) return "ahora";
            if (min < 60) return "hace " + min + " min";
            if (min < 24 * 60) return "hace " + (min / 60) + " h";
            if (min < 48 * 60) return "ayer";
            if (min < 7 * 24 * 60) return "hace " + (min / (24 * 60)) + " días";
            Locale es = Locale.forLanguageTag("es-CO");
            return f.format(DateTimeFormatter.ofPattern(f.getYear() == LocalDateTime.now().getYear() ? "d MMM" : "d MMM yyyy", es))
                    .replace(".", "");
        } catch (RuntimeException e) {
            return fechaBD;
        }
    }
}
