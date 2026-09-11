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


    private void cargarDatos() {
        if (persistencia.existenDatosPrevios()) {
            persistencia.cargarDatos(gestor);
        } else {
            gestor.cargarDatosIniciales();
        }
    }


    public void guardarYSalir() {
        persistencia.guardarDatos(gestor);
    }



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



    public Instructor[] listarInstructores() {
        return gestor.listarInstructores();
    }



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



    public Actividad[] listarActividadesConCupoDisponible(Deporte filtroDeporte) {
        return gestor.listarActividadesConCupoDisponible(filtroDeporte);
    }


    public void generarReporte() throws IOException {
        persistencia.exportarReporte(gestor, ARCHIVO_REPORTE);
    }

    public String getNombreArchivoReporte() {
        return ARCHIVO_REPORTE;
    }
}
