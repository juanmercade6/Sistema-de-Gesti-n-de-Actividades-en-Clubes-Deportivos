package clubdeportivo.excepciones;

/**
 * Excepción propia del sistema (SIA-12), lanzada cuando se intenta inscribir
 * un socio en una actividad que ya alcanzó su cupo máximo.
 */
public class CupoExcedidoException extends Exception {

    public CupoExcedidoException(String mensaje) {
        super(mensaje);
    }
}
