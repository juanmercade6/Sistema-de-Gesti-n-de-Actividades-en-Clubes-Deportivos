package clubdeportivo.persistencia;

import clubdeportivo.excepciones.CupoExcedidoException;
import clubdeportivo.excepciones.ElementoNoEncontradoException;
import clubdeportivo.gestion.GestorClub;
import clubdeportivo.modelo.Actividad;
import clubdeportivo.modelo.Deporte;
import clubdeportivo.modelo.Instructor;
import clubdeportivo.modelo.Socio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Encargada de la persistencia de datos del sistema (SIA-11) mediante
 * archivos de texto plano en formato CSV, usando un enfoque batch:
 * se cargan todos los datos al iniciar la aplicación y se graban todos
 * los datos al salir de ella.
 *
 * Se usan 3 archivos porque las Actividades (colección 1) contienen,
 * de forma anidada, una lista de Socios inscritos (colección 2); al
 * "aplanar" esa relación en CSV, cada fila de socios.csv representa una
 * inscripción (socio + actividad en la que está inscrito).
 */
public class PersistenciaCSV {

    private static final String SEP = ";";

    private String rutaInstructores;
    private String rutaActividades;
    private String rutaSocios;

    public PersistenciaCSV(String carpetaDatos) {
        this.rutaInstructores = carpetaDatos + File.separator + "instructores.csv";
        this.rutaActividades = carpetaDatos + File.separator + "actividades.csv";
        this.rutaSocios = carpetaDatos + File.separator + "socios.csv";
        new File(carpetaDatos).mkdirs();
    }

    /**
     * Indica si ya existen archivos de datos persistidos previamente.
     */
    public boolean existenDatosPrevios() {
        return new File(rutaInstructores).exists() && new File(rutaActividades).exists();
    }

    /**
     * Carga instructores, actividades y las inscripciones de socios dentro
     * del GestorClub recibido. Todo el manejo de errores se realiza con
     * try-catch (SIA-12).
     */
    public void cargarDatos(GestorClub gestor) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaInstructores))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEP);
                Instructor i = new Instructor(campos[0], campos[1], campos[2],
                        Integer.parseInt(campos[3]), campos[4], campos[5], Double.parseDouble(campos[6]));
                gestor.agregarInstructor(i);
            }
        } catch (IOException e) {
            System.out.println("No se pudieron cargar instructores (" + e.getMessage() + "). Se continúa sin ellos.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaActividades))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEP);
                Instructor instructor = null;
                try {
                    instructor = gestor.buscarInstructor(campos[5]);
                } catch (ElementoNoEncontradoException e) {
                    System.out.println("Aviso: instructor '" + campos[5] + "' no encontrado para actividad " + campos[0]);
                }
                Actividad a = new Actividad(campos[0], campos[1], Deporte.valueOf(campos[2]),
                        campos[3], Integer.parseInt(campos[4]), instructor);
                gestor.agregarActividad(a);
            }
        } catch (IOException e) {
            System.out.println("No se pudieron cargar actividades (" + e.getMessage() + "). Se continúa sin ellas.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(rutaSocios))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] campos = linea.split(SEP);
                Socio s = new Socio(campos[0], campos[1], campos[2],
                        Integer.parseInt(campos[3]), campos[4], campos[5]);
                String codigoActividad = campos[6];
                try {
                    gestor.inscribirSocio(codigoActividad, s);
                } catch (ElementoNoEncontradoException | CupoExcedidoException e) {
                    System.out.println("Aviso: no se pudo inscribir a " + s.getNumeroSocio()
                            + " en " + codigoActividad + " (" + e.getMessage() + ")");
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudieron cargar socios (" + e.getMessage() + "). Se continúa sin ellos.");
        }
    }

    /**
     * Guarda el estado completo del GestorClub (instructores, actividades
     * y todas las inscripciones de socios) en los 3 archivos CSV.
     */
    public void guardarDatos(GestorClub gestor) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaInstructores))) {
            for (Instructor i : gestor.listarInstructores()) {
                pw.println(String.join(SEP,
                        i.getId(), i.getNombre(), i.getApellido(),
                        String.valueOf(i.getEdad()), i.getEmail(),
                        i.getEspecialidad(), String.valueOf(i.getSueldoBase())));
            }
        } catch (IOException e) {
            System.out.println("Error al guardar instructores: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaActividades))) {
            for (Actividad a : gestor.listarActividades()) {
                String idInstructor = (a.getInstructor() != null) ? a.getInstructor().getId() : "";
                pw.println(String.join(SEP,
                        a.getCodigo(), a.getNombre(), a.getDeporte().name(),
                        a.getHorario(), String.valueOf(a.getCupoMaximo()), idInstructor));
            }
        } catch (IOException e) {
            System.out.println("Error al guardar actividades: " + e.getMessage());
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaSocios))) {
            for (Actividad a : gestor.listarActividades()) {
                for (Socio s : a.getInscritos()) {
                    pw.println(String.join(SEP,
                            s.getNumeroSocio(), s.getNombre(), s.getApellido(),
                            String.valueOf(s.getEdad()), s.getEmail(),
                            s.getFechaInscripcion(), a.getCodigo()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al guardar socios: " + e.getMessage());
        }
    }
}
