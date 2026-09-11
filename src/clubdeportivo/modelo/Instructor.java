package clubdeportivo.modelo;


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


    @Override
    public String mostrarInfo() {
        return "Instructor " + getNombre() + " " + getApellido()
                + " - Especialidad: " + especialidad
                + " - Sueldo base: $" + sueldoBase;
    }


    public double calcularPago() {
        return sueldoBase;
    }


    public double calcularPago(double bonoPorAlumno, int numeroAlumnos) {
        return sueldoBase + (bonoPorAlumno * numeroAlumnos);
    }
}
