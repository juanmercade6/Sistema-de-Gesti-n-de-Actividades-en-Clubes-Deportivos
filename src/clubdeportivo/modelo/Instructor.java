package clubdeportivo.modelo;


public class Instructor extends Persona {

    private String especialidad;
    private double sueldoBase;

    public Instructor(String id, String nombre, String apellido, int edad,
                       String email, String especialidad, double sueldoBase) {
        super(id, nombre, apellido, edad, email);
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad no puede estar vacía.");
        }
        if (sueldoBase < 0) {
            throw new IllegalArgumentException("El sueldo base no puede ser negativo.");
        }
        this.especialidad = especialidad;
        this.sueldoBase = sueldoBase;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad no puede estar vacía.");
        }
        this.especialidad = especialidad;
    }

    public double getSueldoBase() {
        return sueldoBase;
    }

    public void setSueldoBase(double sueldoBase) {
        if (sueldoBase < 0) {
            throw new IllegalArgumentException("El sueldo base no puede ser negativo.");
        }
        this.sueldoBase = sueldoBase;
    }


    @Override
    public String mostrarInfo() {
        return "Instructor " + getNombre() + " " + getApellido()
                + " - Especialidad: " + especialidad
                + " - Sueldo base: $" + sueldoBase;
    }


    public double calcularPago(int diasTrabajados) {
        if (diasTrabajados < 0 || diasTrabajados > 31) {
            throw new IllegalArgumentException("Los días trabajados deben estar entre 0 y 31.");
        }
        return (sueldoBase / 30.0) * diasTrabajados;
    }


    public double calcularPago(int diasTrabajados, double bonoPorAlumno, int numeroAlumnos) {
        return calcularPago(diasTrabajados) + (bonoPorAlumno * numeroAlumnos);
    }
}
