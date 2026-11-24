package wheels.vistas;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.JComponent;

/**
 * Base para los componentes dibujados a mano (logo, avatares, iconos, menu):
 * les da un contexto de accesibilidad para que los lectores de pantalla
 * puedan anunciarlos con su nombre y su rol.
 */
public abstract class ComponenteAccesible extends JComponent {

    private final AccessibleRole rol;

    ComponenteAccesible(AccessibleRole rol) {
        this.rol = rol;
    }

    ComponenteAccesible() {
        this(AccessibleRole.LABEL);
    }

    @Override
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJComponent() {
                @Override public AccessibleRole getAccessibleRole() { return rol; }
            };
        }
        return accessibleContext;
    }
}
