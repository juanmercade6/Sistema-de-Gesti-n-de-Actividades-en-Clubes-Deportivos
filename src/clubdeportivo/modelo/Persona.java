package clubdeportivo.modelo;

public abstract class Persona {

    private static final int EDAD_MINIMA = 0;
    private static final int EDAD_MAXIMA = 120;

    private String id;
    private String nombre;
    private String apellido;
    private int edad;
    private String email;

    public Persona(String id, String nombre, String apellido, int edad, String email) {
        validarTextoNoVacio(nombre, "El nombre no puede estar vacío.");
        validarTextoNoVacio(apellido, "El apellido no puede estar vacío.");
        validarEdad(edad);
        validarEmail(email);

        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
    }


    public abstract String mostrarInfo();



    private static void validarTextoNoVacio(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private static void validarEdad(int edad) {
        if (edad < EDAD_MINIMA || edad > EDAD_MAXIMA) {
            throw new IllegalArgumentException(
                    "La edad debe estar entre " + EDAD_MINIMA + " y " + EDAD_MAXIMA + " años.");
        }
    }

    private static void validarEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El email no tiene un formato válido.");
        }
    }

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
        validarTextoNoVacio(nombre, "El nombre no puede estar vacío.");
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        validarTextoNoVacio(apellido, "El apellido no puede estar vacío.");
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        validarEdad(edad);
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validarEmail(email);
        this.email = email;
    }

    @Override
    public String toString() {
        return mostrarInfo();
    }
}
