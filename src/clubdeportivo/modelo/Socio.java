package clubdeportivo.modelo;

/**
 * Representa a un socio del club deportivo. Un socio puede inscribirse
 * en distintas {@link Actividad}, las cuales lo almacenan en su colección
 * anidada de inscritos.
 */
public class Socio extends Persona {

    private String numeroSocio;
    private String fechaInscripcion; // formato yyyy-MM-dd

    public Socio(String numeroSocio, String nombre, String apellido, int edad,
                 String email, String fechaInscripcion) {
        super(numeroSocio, nombre, apellido, edad, email);
        this.numeroSocio = numeroSocio;
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getNumeroSocio() {
        return numeroSocio;
    }

    public void setNumeroSocio(String numeroSocio) {
        this.numeroSocio = numeroSocio;
        setId(numeroSocio);
    }

    public String getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(String fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    /**
     * Sobreescritura de mostrarInfo() (SIA-6).
     */
    @Override
    public String mostrarInfo() {
        return "Socio #" + numeroSocio + " - " + getNombre() + " " + getApellido()
                + " (" + getEdad() + " años) - " + getEmail()
                + " - Inscrito desde: " + fechaInscripcion;
    }

    /**
     * Sobrecarga de método (no constructor) - versión sin actividad (SIA-5).
     */
    public String generarComprobante() {
        return "Comprobante de socio: " + mostrarInfo();
    }

    /**
     * Sobrecarga de método - versión que incluye el nombre de la actividad (SIA-5).
     */
    public String generarComprobante(String nombreActividad) {
        return generarComprobante() + " | Actividad inscrita: " + nombreActividad;
    }
}
