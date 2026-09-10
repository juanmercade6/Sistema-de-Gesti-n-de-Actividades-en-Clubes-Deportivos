package clubdeportivo.modelo;

/**
 * Clase abstracta que representa a cualquier persona relacionada con el club.
 * Concentra los datos comunes a {@link Socio} e {@link Instructor}.
 *
 * Todos los atributos son privados y cuentan con getters/setters (SIA-3).
 */
public abstract class Persona {

    private String id;
    private String nombre;
    private String apellido;
    private int edad;
    private String email;

    public Persona(String id, String nombre, String apellido, int edad, String email) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
    }

    /**
     * Cada subclase debe definir cómo se presenta su información detallada.
     * Este método es sobreescrito por Socio e Instructor (SIA-6).
     */
    public abstract String mostrarInfo();

    // ----- Getters y Setters -----
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return mostrarInfo();
    }
}
