package clubdeportivo.modelo;

import clubdeportivo.excepciones.CupoExcedidoException;
import clubdeportivo.excepciones.ElementoNoEncontradoException;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una actividad deportiva ofrecida por el club (ej: Fútbol lunes 18:00).
 *
 * Cada actividad mantiene su propia colección de socios inscritos
 * ({@code inscritos}), la cual corresponde a la 2ª colección del sistema,
 * anidada dentro de la 1ª colección (el Map de actividades administrado
 * por GestorClub). Ambas colecciones pertenecen al Java Collections Framework.
 */
public class Actividad {

    private String codigo;
    private String nombre;
    private Deporte deporte;
    private String horario;
    private int cupoMaximo;
    private Instructor instructor;

    // Colección anidada (JCF) - lista de socios inscritos en esta actividad.
    private List<Socio> inscritos;

    public Actividad(String codigo, String nombre, Deporte deporte, String horario,
                      int cupoMaximo, Instructor instructor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.deporte = deporte;
        this.horario = horario;
        this.cupoMaximo = cupoMaximo;
        this.instructor = instructor;
        this.inscritos = new ArrayList<>();
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Deporte getDeporte() {
        return deporte;
    }

    public void setDeporte(Deporte deporte) {
        this.deporte = deporte;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public List<Socio> getInscritos() {
        return inscritos;
    }

    /**
     * Cantidad de cupos aún disponibles en la actividad.
     */
    public int getCupoDisponible() {
        return cupoMaximo - inscritos.size();
    }

    /**
     * Inscribe un socio en la actividad, validando el cupo máximo.
     */
    public void inscribirSocio(Socio socio) throws CupoExcedidoException {
        if (inscritos.size() >= cupoMaximo) {
            throw new CupoExcedidoException("La actividad '" + nombre
                    + "' ya alcanzó su cupo máximo (" + cupoMaximo + ").");
        }
        inscritos.add(socio);
    }

    /**
     * Busca un socio dentro de esta actividad por su número de socio.
     */
    public Socio buscarSocioPorNumero(String numeroSocio) throws ElementoNoEncontradoException {
        for (Socio s : inscritos) {
            if (s.getNumeroSocio().equalsIgnoreCase(numeroSocio)) {
                return s;
            }
        }
        throw new ElementoNoEncontradoException("No se encontró el socio '" + numeroSocio
                + "' en la actividad '" + nombre + "'.");
    }

    /**
     * Elimina un socio de la actividad por su número de socio.
     */
    public void eliminarSocio(String numeroSocio) throws ElementoNoEncontradoException {
        Socio s = buscarSocioPorNumero(numeroSocio);
        inscritos.remove(s);
    }

    @Override
    public String toString() {
        String nombreInstructor = (instructor != null) ? instructor.getNombre() + " " + instructor.getApellido() : "Sin asignar";
        return String.format("[%s] %s (%s) - Horario: %s - Instructor: %s - Cupos: %d/%d",
                codigo, nombre, deporte, horario, nombreInstructor,
                inscritos.size(), cupoMaximo);
    }
}
