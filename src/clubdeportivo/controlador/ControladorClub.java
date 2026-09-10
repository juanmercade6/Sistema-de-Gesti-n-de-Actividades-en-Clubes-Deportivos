package clubdeportivo.controlador;

import clubdeportivo.excepciones.CupoExcedidoException;
import clubdeportivo.excepciones.ElementoNoEncontradoException;
import clubdeportivo.gestion.GestorClub;
import clubdeportivo.modelo.Actividad;
import clubdeportivo.modelo.Deporte;
import clubdeportivo.modelo.Instructor;
import clubdeportivo.modelo.Socio;
import clubdeportivo.persistencia.PersistenciaCSV;

import java.io.IOException;

/**
 * Controlador central del sistema (capa intermedia entre las vistas y el
 * modelo). Tanto la consola (MenuConsola) como la interfaz gráfica
 * (VentanaPrincipal) dependen únicamente de esta clase, nunca de
 * GestorClub directamente. Esto permite que:
 *
 *  - Main.java quede reducido a solo elegir el modo de ejecución y
 *    delegar todo lo demás aquí (no hay lógica de negocio en el main).
 *  - Ninguna vista conozca la Persistencia ni el GestorClub, facilitando
 *    mantener el mismo comportamiento sin importar la interfaz usada.
 *  - Igual que GestorClub, ningún método público de esta clase retorna
 *    una colección: siempre se entregan arreglos.
 */
public class ControladorClub {

    private static final String CARPETA_DATOS = "data";
    private static final String ARCHIVO_REPORTE = "reporte_actividades.csv";

    private GestorClub gestor;
    private PersistenciaCSV persistencia;

    public ControladorClub() {
        this.gestor = new GestorClub();
        this.persistencia = new PersistenciaCSV(CARPETA_DATOS);
        cargarDatos();
    }

    /**
     * Carga los datos persistidos previamente; si es la primera vez que
     * se ejecuta el sistema, carga datos de ejemplo (SIA-3).
     */
    private void cargarDatos() {
        if (persistencia.existenDatosPrevios()) {
            persistencia.cargarDatos(gestor);
        } else {
            gestor.cargarDatosIniciales();
        }
    }

    /**
     * Guarda el estado completo del sistema. Se debe invocar al salir de
     * la aplicación, sin importar el modo de interfaz usado.
     */
    public void guardarYSalir() {
        persistencia.guardarDatos(gestor);
    }

    // ==================== ACTIVIDADES ====================

    public void agregarActividad(String codigo, String nombre, Deporte deporte, String horario,
                                  int cupoMaximo, Instructor instructor) {
        gestor.agregarActividad(new Actividad(codigo, nombre, deporte, horario, cupoMaximo, instructor));
    }

    public Actividad[] listarActividades() {
        return gestor.listarActividades();
    }

    public Actividad buscarActividad(String codigo) throws ElementoNoEncontradoException {
        return gestor.buscarActividad(codigo);
    }

    public void editarActividad(String codigo, String nuevoNombre, String nuevoHorario,
                                 int nuevoCupo, Instructor nuevoInstructor) throws ElementoNoEncontradoException {
        gestor.editarActividad(codigo, nuevoNombre, nuevoHorario, nuevoCupo, nuevoInstructor);
    }

    public void eliminarActividad(String codigo) throws ElementoNoEncontradoException {
        gestor.eliminarActividad(codigo);
    }

    // ==================== INSTRUCTORES ====================

    public Instructor[] listarInstructores() {
        return gestor.listarInstructores();
    }

    // ==================== SOCIOS ====================

    public void inscribirSocio(String codigoActividad, Socio socio)
            throws ElementoNoEncontradoException, CupoExcedidoException {
        gestor.inscribirSocio(codigoActividad, socio);
    }

    public Socio[] listarSociosDeActividad(String codigoActividad) throws ElementoNoEncontradoException {
        return gestor.listarSociosDeActividad(codigoActividad);
    }

    public Socio buscarSocio(String codigoActividad, String numeroSocio) throws ElementoNoEncontradoException {
        return gestor.buscarSocio(codigoActividad, numeroSocio);
    }

    public void editarSocio(String codigoActividad, String numeroSocio, String nuevoNombre,
                             String nuevoApellido, int nuevaEdad, String nuevoEmail)
            throws ElementoNoEncontradoException {
        gestor.editarSocio(codigoActividad, numeroSocio, nuevoNombre, nuevoApellido, nuevaEdad, nuevoEmail);
    }

    public void eliminarSocio(String codigoActividad, String numeroSocio) throws ElementoNoEncontradoException {
        gestor.eliminarSocio(codigoActividad, numeroSocio);
    }

    // ==================== FUNCIONALIDAD PROPIA (SIA-9) ====================

    public Actividad[] listarActividadesConCupoDisponible(Deporte filtroDeporte) {
        return gestor.listarActividadesConCupoDisponible(filtroDeporte);
    }

    // ==================== REPORTE (SIA-O2) ====================

    /**
     * Genera un reporte CSV independiente de la persistencia, con el
     * detalle de cada actividad y su ocupación. Es un archivo pensado
     * para ser abierto en una planilla de cálculo (Excel/LibreOffice).
     */
    public void generarReporte() throws IOException {
        persistencia.exportarReporte(gestor, ARCHIVO_REPORTE);
    }

    public String getNombreArchivoReporte() {
        return ARCHIVO_REPORTE;
    }
}
