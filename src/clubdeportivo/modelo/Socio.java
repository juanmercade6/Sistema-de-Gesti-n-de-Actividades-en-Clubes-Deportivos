package clubdeportivo.modelo;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Socio extends Persona {

    private String numeroSocio;
    private String fechaInscripcion; // formato yyyy-MM-dd

    public Socio(String numeroSocio, String nombre, String apellido, int edad,
                 String email, String fechaInscripcion) {
        super(numeroSocio, nombre, apellido, edad, email);
        if (numeroSocio == null || numeroSocio.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de socio no puede estar vacío.");
        }
        if (fechaInscripcion == null || fechaInscripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de inscripción no puede estar vacía.");
        }
        this.numeroSocio = numeroSocio;
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getNumeroSocio() {
        return numeroSocio;
    }

    public void setNumeroSocio(String numeroSocio) {
        if (numeroSocio == null || numeroSocio.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de socio no puede estar vacío.");
        }
        this.numeroSocio = numeroSocio;
        setId(numeroSocio);
    }

    public String getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(String fechaInscripcion) {
        if (fechaInscripcion == null || fechaInscripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de inscripción no puede estar vacía.");
        }
        try {
            LocalDate.parse(fechaInscripcion);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener el formato válido (yyyy-MM-dd).");
        }
        this.fechaInscripcion = fechaInscripcion;
    }


    @Override
    public String mostrarInfo() {
        return "Socio #" + numeroSocio + " - " + getNombre() + " " + getApellido()
                + " (" + getEdad() + " años) - " + getEmail()
                + " - Inscrito desde: " + fechaInscripcion;
    }


    public String generarComprobante(Actividad actividad) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== COMPROBANTE DE INSCRIPCIÓN =====\n");
        sb.append("Socio: ").append(getNombre()).append(" ").append(getApellido())
                .append(" (#").append(numeroSocio).append(")\n");
        sb.append("Actividad: ").append(actividad.getNombre())
                .append(" - ").append(actividad.getDeporte()).append("\n");
        sb.append("Horario: ").append(actividad.getHorario()).append("\n");
        sb.append("Fecha de inscripción: ").append(fechaInscripcion);
        return sb.toString();
    }


    public String generarComprobante(Actividad actividad, boolean incluirInstructor) {
        String base = generarComprobante(actividad);
        if (incluirInstructor && actividad.getInstructor() != null) {
            base += "\nInstructor a cargo: " + actividad.getInstructor().getNombre()
                    + " " + actividad.getInstructor().getApellido();
        }
        return base;
    }
}

