package clubdeportivo.modelo;

/**
 * Representa a un instructor del club, responsable de dictar una o más
 * {@link Actividad}.
 */
public class Instructor extends Persona {

    private String especialidad;
    private double sueldoBase;

    public Instructor(String id, String nombre, String apellido, int edad,
                       String email, String especialidad, double sueldoBase) {
        super(id, nombre, apellido, edad, email);
        this.especialidad = especialidad;
        this.sueldoBase = sueldoBase;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public double getSueldoBase() {
        return sueldoBase;
    }

    public void setSueldoBase(double sueldoBase) {
        this.sueldoBase = sueldoBase;
    }

    /**
     * Sobreescritura de mostrarInfo() (SIA-6).
     */
    @Override
    public String mostrarInfo() {
        return "Instructor " + getNombre() + " " + getApellido()
                + " - Especialidad: " + especialidad
                + " - Sueldo base: $" + sueldoBase;
    }

    /**
     * Sobrecarga de método - pago simple, sin bonos (SIA-5).
     */
    public double calcularPago() {
        return sueldoBase;
    }

    /**
     * Sobrecarga de método - pago con bono por cada alumno a cargo (SIA-5).
     */
    public double calcularPago(double bonoPorAlumno, int numeroAlumnos) {
        return sueldoBase + (bonoPorAlumno * numeroAlumnos);
    }
}
