package clubdeportivo.excepciones;

/**
 * Excepción propia del sistema (SIA-12), lanzada cuando se busca, edita o
 * elimina una Actividad o un Socio que no existe en las colecciones.
 */
public class ElementoNoEncontradoException extends Exception {

    public ElementoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
