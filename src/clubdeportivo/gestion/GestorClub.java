package clubdeportivo.gestion;

import clubdeportivo.excepciones.CupoExcedidoException;
import clubdeportivo.excepciones.ElementoNoEncontradoException;
import clubdeportivo.modelo.Actividad;
import clubdeportivo.modelo.Deporte;
import clubdeportivo.modelo.Instructor;
import clubdeportivo.modelo.Socio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase central del sistema. Administra la colección principal de
 * Actividades (Map, JCF) y, a través de cada Actividad, la colección
 * anidada de Socios inscritos (List, JCF).
 *
 * Todos los atributos son privados con sus respectivos accesos (SIA-3).
 */
public class GestorClub {

    // Colección 1 (JCF - Map): clave = código de actividad.
    private Map<String, Actividad> actividades;

    // Repositorio auxiliar de instructores (no es una de las 2 colecciones
    // exigidas, pero facilita administrarlos independientemente).
    private Map<String, Instructor> instructores;

    public GestorClub() {
        this.actividades = new LinkedHashMap<>();
        this.instructores = new LinkedHashMap<>();
    }

    // ==================== ACTIVIDADES (Colección 1) ====================

    public void agregarActividad(Actividad actividad) {
        actividades.put(actividad.getCodigo(), actividad);
    }

    public Collection<Actividad> listarActividades() {
        return actividades.values();
    }

    public Actividad buscarActividad(String codigo) throws ElementoNoEncontradoException {
        Actividad a = actividades.get(codigo);
        if (a == null) {
            throw new ElementoNoEncontradoException("No existe una actividad con código '" + codigo + "'.");
        }
        return a;
    }

    public void editarActividad(String codigo, String nuevoNombre, String nuevoHorario,
                                 int nuevoCupo, Instructor nuevoInstructor) throws ElementoNoEncontradoException {
        Actividad a = buscarActividad(codigo);
        a.setNombre(nuevoNombre);
        a.setHorario(nuevoHorario);
        a.setCupoMaximo(nuevoCupo);
        a.setInstructor(nuevoInstructor);
    }

    public void eliminarActividad(String codigo) throws ElementoNoEncontradoException {
        buscarActividad(codigo); // valida existencia
        actividades.remove(codigo);
    }

    public Map<String, Actividad> getActividades() {
        return actividades;
    }

    // ==================== INSTRUCTORES (repositorio auxiliar) ====================

    public void agregarInstructor(Instructor instructor) {
        instructores.put(instructor.getId(), instructor);
    }

    public Collection<Instructor> listarInstructores() {
        return instructores.values();
    }

    public Instructor buscarInstructor(String id) throws ElementoNoEncontradoException {
        Instructor i = instructores.get(id);
        if (i == null) {
            throw new ElementoNoEncontradoException("No existe un instructor con id '" + id + "'.");
        }
        return i;
    }

    public Map<String, Instructor> getInstructores() {
        return instructores;
    }

    // ==================== SOCIOS (Colección 2, anidada) ====================

    public void inscribirSocio(String codigoActividad, Socio socio)
            throws ElementoNoEncontradoException, CupoExcedidoException {
        Actividad a = buscarActividad(codigoActividad);
        a.inscribirSocio(socio);
    }

    public List<Socio> listarSociosDeActividad(String codigoActividad) throws ElementoNoEncontradoException {
        Actividad a = buscarActividad(codigoActividad);
        return a.getInscritos();
    }

    public Socio buscarSocio(String codigoActividad, String numeroSocio) throws ElementoNoEncontradoException {
        Actividad a = buscarActividad(codigoActividad);
        return a.buscarSocioPorNumero(numeroSocio);
    }

    public void editarSocio(String codigoActividad, String numeroSocio, String nuevoNombre,
                             String nuevoApellido, int nuevaEdad, String nuevoEmail)
            throws ElementoNoEncontradoException {
        Socio s = buscarSocio(codigoActividad, numeroSocio);
        s.setNombre(nuevoNombre);
        s.setApellido(nuevoApellido);
        s.setEdad(nuevaEdad);
        s.setEmail(nuevoEmail);
    }

    public void eliminarSocio(String codigoActividad, String numeroSocio) throws ElementoNoEncontradoException {
        Actividad a = buscarActividad(codigoActividad);
        a.eliminarSocio(numeroSocio);
    }

    // ==================== FUNCIONALIDAD PROPIA (SIA-9) ====================

    /**
     * Filtra el subconjunto de actividades que tienen cupos disponibles,
     * opcionalmente acotado a un deporte específico. Es la funcionalidad
     * propia de utilidad para el negocio exigida en SIA-9 (distinta de
     * inserción/edición/eliminación/reportes), pensada para que el club
     * pueda promocionar rápidamente los cupos abiertos.
     *
     * @param filtroDeporte si es null, no filtra por deporte.
     */
    public List<Actividad> listarActividadesConCupoDisponible(Deporte filtroDeporte) {
        List<Actividad> resultado = new ArrayList<>();
        for (Actividad a : actividades.values()) {
            boolean tieneCupo = a.getCupoDisponible() > 0;
            boolean coincideDeporte = (filtroDeporte == null) || (a.getDeporte() == filtroDeporte);
            if (tieneCupo && coincideDeporte) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    // ==================== DATOS INICIALES (SIA-3) ====================

    /**
     * Carga datos de ejemplo que permiten ejecutar cualquiera de las
     * funcionalidades del sistema sin depender de archivos externos.
     * Se invoca únicamente si no existen datos persistidos previamente.
     */
    public void cargarDatosIniciales() {
        Instructor i1 = new Instructor("I001", "Marcela", "Rojas", 34, "marcela.rojas@club.cl", "Fútbol", 650000);
        Instructor i2 = new Instructor("I002", "Pedro", "Salinas", 41, "pedro.salinas@club.cl", "Natación", 700000);
        Instructor i3 = new Instructor("I003", "Valentina", "Ibáñez", 29, "valentina.ibanez@club.cl", "Crossfit", 600000);
        agregarInstructor(i1);
        agregarInstructor(i2);
        agregarInstructor(i3);

        Actividad act1 = new Actividad("A001", "Fútbol Formativo", Deporte.FUTBOL, "Lunes 18:00-19:30", 4, i1);
        Actividad act2 = new Actividad("A002", "Natación Adultos", Deporte.NATACION, "Martes 07:00-08:00", 3, i2);
        Actividad act3 = new Actividad("A003", "Crossfit Intensivo", Deporte.CROSSFIT, "Miércoles 19:00-20:00", 2, i3);
        agregarActividad(act1);
        agregarActividad(act2);
        agregarActividad(act3);

        Socio s1 = new Socio("S001", "Camila", "Fuentes", 22, "camila.fuentes@mail.cl", "2026-03-01");
        Socio s2 = new Socio("S002", "Diego", "Vera", 27, "diego.vera@mail.cl", "2026-03-02");
        Socio s3 = new Socio("S003", "Fernanda", "Muñoz", 31, "fernanda.munoz@mail.cl", "2026-03-03");

        try {
            inscribirSocio("A001", s1);
            inscribirSocio("A001", s2);
            inscribirSocio("A002", s3);
        } catch (ElementoNoEncontradoException | CupoExcedidoException e) {
            // No debería ocurrir con los datos de ejemplo controlados.
            System.out.println("Error inesperado al cargar datos iniciales: " + e.getMessage());
        }
    }
}
