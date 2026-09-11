package clubdeportivo.gestion;

import clubdeportivo.excepciones.CupoExcedidoException;
import clubdeportivo.excepciones.ElementoNoEncontradoException;
import clubdeportivo.modelo.Actividad;
import clubdeportivo.modelo.Deporte;
import clubdeportivo.modelo.Instructor;
import clubdeportivo.modelo.Socio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase central del sistema (capa de modelo/negocio). Administra la
 * colección principal de Actividades (Map, JCF) y, a través de cada
 * Actividad, la colección anidada de Socios inscritos (List, JCF).
 *
 * IMPORTANTE: por buenas prácticas de encapsulamiento, ningún método
 * público de esta clase retorna una colección (List/Map/Collection);
 * las colecciones internas jamás se exponen hacia afuera. Cuando se
 * necesita entregar varios elementos, se retorna un arreglo construido
 * a partir de la colección interna.
 *
 * Todos los atributos son privados con sus respectivos accesos (SIA-3).
 */
public class GestorClub {

    // Colección 1 (JCF - Map): clave = código de actividad.
    private Map<String, Actividad> actividades;

    // Repositorio auxiliar de instructores (no es una de las 2 colecciones
    // exigidas por el enunciado, pero facilita administrarlos de forma
    // independiente; tampoco se expone directamente hacia afuera).
    private Map<String, Instructor> instructores;

    public GestorClub() {
        this.actividades = new LinkedHashMap<>();
        this.instructores = new LinkedHashMap<>();
    }

    // ==================== ACTIVIDADES (Colección 1) ====================

    /**
     * Normaliza un código de actividad (sin espacios extra y en mayúsculas)
     * para que la búsqueda no dependa de que el usuario escriba
     * exactamente "A001" y no "a001" o " a001 ".
     */
    private String normalizarCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase();
    }

    public void agregarActividad(Actividad actividad) {
        actividades.put(normalizarCodigo(actividad.getCodigo()), actividad);
    }

    /**
     * Devuelve todas las actividades como arreglo (no se retorna la
     * colección interna).
     */
    public Actividad[] listarActividades() {
        return actividades.values().toArray(new Actividad[0]);
    }

    public int cantidadActividades() {
        return actividades.size();
    }

    public Actividad buscarActividad(String codigo) throws ElementoNoEncontradoException {
        Actividad a = actividades.get(normalizarCodigo(codigo));
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
        actividades.remove(normalizarCodigo(codigo));
    }

    // ==================== INSTRUCTORES (repositorio auxiliar) ====================

    public void agregarInstructor(Instructor instructor) {
        instructores.put(instructor.getId(), instructor);
    }

    /**
     * Devuelve todos los instructores como arreglo (no se retorna la
     * colección interna).
     */
    public Instructor[] listarInstructores() {
        return instructores.values().toArray(new Instructor[0]);
    }

    public Instructor buscarInstructor(String id) throws ElementoNoEncontradoException {
        Instructor i = instructores.get(id);
        if (i == null) {
            throw new ElementoNoEncontradoException("No existe un instructor con id '" + id + "'.");
        }
        return i;
    }

    // ==================== SOCIOS (Colección 2, anidada) ====================

    public void inscribirSocio(String codigoActividad, Socio socio)
            throws ElementoNoEncontradoException, CupoExcedidoException {
        Actividad a = buscarActividad(codigoActividad);
        a.inscribirSocio(socio);
    }

    /**
     * Devuelve los socios inscritos en una actividad como arreglo
     * (la Actividad ya se encarga de no exponer su List interna).
     */
    public Socio[] listarSociosDeActividad(String codigoActividad) throws ElementoNoEncontradoException {
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
     * El filtrado se arma internamente con una List auxiliar (para no
     * depender de un arreglo de tamaño fijo mientras se recorre), pero
     * el método retorna el resultado ya convertido a arreglo.
     *
     * @param filtroDeporte si es null, no filtra por deporte.
     */
    public Actividad[] listarActividadesConCupoDisponible(Deporte filtroDeporte) {
        List<Actividad> resultado = new ArrayList<>();
        for (Actividad a : actividades.values()) {
            boolean tieneCupo = a.getCupoDisponible() > 0;
            boolean coincideDeporte = (filtroDeporte == null) || (a.getDeporte() == filtroDeporte);
            if (tieneCupo && coincideDeporte) {
                resultado.add(a);
            }
        }
        return resultado.toArray(new Actividad[0]);
    }

    // ==================== DATOS INICIALES (SIA-3) ====================

    /**
     * Carga datos de ejemplo que permiten ejecutar cualquiera de las
     * funcionalidades del sistema sin depender de archivos externos.
     * Se invoca únicamente si no existen datos persistidos previamente.
     *
     * Se cargan bastantes datos de ejemplo (9 instructores, 10 actividades
     * cubriendo todos los deportes —incluyendo Powerlifting y Boxeo— y
     * cerca de 30 socios distribuidos entre ellas) para que el sistema
     * tenga, desde el primer arranque, un conjunto de datos representativo,
     * similar a una tabla de Excel/CSV ya poblada.
     */
    public void cargarDatosIniciales() {
        Instructor i1 = new Instructor("I001", "Marcela", "Rojas", 34, "marcela.rojas@club.cl", "Fútbol", 650000);
        Instructor i2 = new Instructor("I002", "Pedro", "Salinas", 41, "pedro.salinas@club.cl", "Natación", 700000);
        Instructor i3 = new Instructor("I003", "Valentina", "Ibáñez", 29, "valentina.ibanez@club.cl", "Crossfit", 600000);
        Instructor i4 = new Instructor("I004", "Rodrigo", "Contreras", 37, "rodrigo.contreras@club.cl", "Básquetbol", 620000);
        Instructor i5 = new Instructor("I005", "Javiera", "Soto", 26, "javiera.soto@club.cl", "Vóleibol", 580000);
        Instructor i6 = new Instructor("I006", "Andrés", "Herrera", 45, "andres.herrera@club.cl", "Tenis", 690000);
        Instructor i7 = new Instructor("I007", "Constanza", "Reyes", 31, "constanza.reyes@club.cl", "Atletismo", 610000);
        Instructor i8 = new Instructor("I008", "Maximiliano", "Vega", 33, "maximiliano.vega@club.cl", "Powerlifting", 640000);
        Instructor i9 = new Instructor("I009", "Camila", "Godoy", 28, "camila.godoy@club.cl", "Boxeo", 630000);
        agregarInstructor(i1);
        agregarInstructor(i2);
        agregarInstructor(i3);
        agregarInstructor(i4);
        agregarInstructor(i5);
        agregarInstructor(i6);
        agregarInstructor(i7);
        agregarInstructor(i8);
        agregarInstructor(i9);

        Actividad act1 = new Actividad("A001", "Fútbol Formativo", Deporte.FUTBOL, "Lunes 18:00-19:30", 6, i1);
        Actividad act2 = new Actividad("A002", "Natación Adultos", Deporte.NATACION, "Martes 07:00-08:00", 5, i2);
        Actividad act3 = new Actividad("A003", "Crossfit Intensivo", Deporte.CROSSFIT, "Miércoles 19:00-20:00", 4, i3);
        Actividad act4 = new Actividad("A004", "Básquetbol Damas", Deporte.BASQUETBOL, "Jueves 18:30-20:00", 5, i4);
        Actividad act5 = new Actividad("A005", "Vóleibol Mixto", Deporte.VOLEIBOL, "Viernes 19:00-20:30", 6, i5);
        Actividad act6 = new Actividad("A006", "Tenis Principiantes", Deporte.TENIS, "Sábado 09:00-10:30", 3, i6);
        Actividad act7 = new Actividad("A007", "Atletismo Fondo", Deporte.ATLETISMO, "Lunes 07:00-08:00", 5, i7);
        Actividad act8 = new Actividad("A008", "Fútbol Senior", Deporte.FUTBOL, "Miércoles 20:00-21:30", 4, i1);
        Actividad act9 = new Actividad("A009", "Powerlifting Básico", Deporte.POWERLIFTING, "Martes 19:00-20:30", 5, i8);
        Actividad act10 = new Actividad("A010", "Boxeo Recreativo", Deporte.BOXEO, "Jueves 20:00-21:00", 6, i9);
        agregarActividad(act1);
        agregarActividad(act2);
        agregarActividad(act3);
        agregarActividad(act4);
        agregarActividad(act5);
        agregarActividad(act6);
        agregarActividad(act7);
        agregarActividad(act8);
        agregarActividad(act9);
        agregarActividad(act10);

        try {
            inscribirSocio("A001", new Socio("S001", "Camila", "Fuentes", 22, "camila.fuentes@mail.cl", "2026-03-01"));
            inscribirSocio("A001", new Socio("S002", "Diego", "Vera", 27, "diego.vera@mail.cl", "2026-03-02"));
            inscribirSocio("A001", new Socio("S003", "Matías", "Cerda", 19, "matias.cerda@mail.cl", "2026-03-04"));
            inscribirSocio("A001", new Socio("S004", "Ignacio", "Bravo", 24, "ignacio.bravo@mail.cl", "2026-03-05"));

            inscribirSocio("A002", new Socio("S005", "Fernanda", "Muñoz", 31, "fernanda.munoz@mail.cl", "2026-03-03"));
            inscribirSocio("A002", new Socio("S006", "Pablo", "Araya", 45, "pablo.araya@mail.cl", "2026-03-06"));
            inscribirSocio("A002", new Socio("S007", "Sofía", "Navarro", 28, "sofia.navarro@mail.cl", "2026-03-07"));

            inscribirSocio("A003", new Socio("S008", "Tomás", "Gómez", 33, "tomas.gomez@mail.cl", "2026-03-08"));
            inscribirSocio("A003", new Socio("S009", "Antonia", "Silva", 26, "antonia.silva@mail.cl", "2026-03-09"));

            inscribirSocio("A004", new Socio("S010", "Valeria", "Torres", 20, "valeria.torres@mail.cl", "2026-03-10"));
            inscribirSocio("A004", new Socio("S011", "Josefa", "Riquelme", 23, "josefa.riquelme@mail.cl", "2026-03-11"));
            inscribirSocio("A004", new Socio("S012", "Camila", "Vidal", 25, "camila.vidal@mail.cl", "2026-03-12"));

            inscribirSocio("A005", new Socio("S013", "Benjamín", "Rojas", 21, "benjamin.rojas@mail.cl", "2026-03-13"));
            inscribirSocio("A005", new Socio("S014", "Martina", "Flores", 24, "martina.flores@mail.cl", "2026-03-14"));
            inscribirSocio("A005", new Socio("S015", "Cristóbal", "Paredes", 29, "cristobal.paredes@mail.cl", "2026-03-15"));
            inscribirSocio("A005", new Socio("S016", "Isidora", "Campos", 18, "isidora.campos@mail.cl", "2026-03-16"));

            inscribirSocio("A006", new Socio("S017", "Felipe", "Zúñiga", 40, "felipe.zuniga@mail.cl", "2026-03-17"));
            inscribirSocio("A006", new Socio("S018", "Daniela", "Espinoza", 35, "daniela.espinoza@mail.cl", "2026-03-18"));

            inscribirSocio("A007", new Socio("S019", "Gabriel", "Morales", 30, "gabriel.morales@mail.cl", "2026-03-19"));
            inscribirSocio("A007", new Socio("S020", "Amanda", "Castro", 27, "amanda.castro@mail.cl", "2026-03-20"));
            inscribirSocio("A007", new Socio("S021", "Nicolás", "Vergara", 22, "nicolas.vergara@mail.cl", "2026-03-21"));

            inscribirSocio("A008", new Socio("S022", "Rocío", "Sepúlveda", 38, "rocio.sepulveda@mail.cl", "2026-03-22"));
            inscribirSocio("A008", new Socio("S023", "Hernán", "Toro", 42, "hernan.toro@mail.cl", "2026-03-23"));

            inscribirSocio("A009", new Socio("S024", "Ricardo", "Aguilera", 29, "ricardo.aguilera@mail.cl", "2026-03-24"));
            inscribirSocio("A009", new Socio("S025", "Paula", "Cornejo", 26, "paula.cornejo@mail.cl", "2026-03-25"));
            inscribirSocio("A009", new Socio("S026", "Esteban", "Miranda", 31, "esteban.miranda@mail.cl", "2026-03-26"));

            inscribirSocio("A010", new Socio("S027", "Francisca", "Leiva", 24, "francisca.leiva@mail.cl", "2026-03-27"));
            inscribirSocio("A010", new Socio("S028", "Joaquín", "Pizarro", 27, "joaquin.pizarro@mail.cl", "2026-03-28"));
            inscribirSocio("A010", new Socio("S029", "Trinidad", "Guzmán", 21, "trinidad.guzman@mail.cl", "2026-03-29"));
        } catch (ElementoNoEncontradoException | CupoExcedidoException e) {
            // No debería ocurrir con los datos de ejemplo controlados.
            System.out.println("Error inesperado al cargar datos iniciales: " + e.getMessage());
        }
    }
}
