package clubdeportivo.vista;

import clubdeportivo.excepciones.CupoExcedidoException;
import clubdeportivo.excepciones.ElementoNoEncontradoException;
import clubdeportivo.controlador.ControladorClub;
import clubdeportivo.modelo.Actividad;
import clubdeportivo.modelo.Deporte;
import clubdeportivo.modelo.Instructor;
import clubdeportivo.modelo.Socio;

import java.util.Scanner;

/**
 * Interfaz de consola del sistema (SIA-10). Ofrece, en menús separados,
 * todas las operaciones sobre Actividades y sobre Socios inscritos.
 */
public class MenuConsola {

    private ControladorClub controlador;
    private Scanner sc;

    public MenuConsola(ControladorClub controlador, Scanner sc) {
        this.controlador = controlador;
        this.sc = sc;
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n===== SISTEMA DE GESTIÓN - CLUB DEPORTIVO =====");
            System.out.println("1. Gestión de Actividades");
            System.out.println("2. Gestión de Socios (inscripciones)");
            System.out.println("3. Actividades con cupos disponibles (filtro por deporte)");
            System.out.println("4. Generar reporte (CSV)");
            System.out.println("0. Guardar y salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: menuActividades(); break;
                case 2: menuSocios(); break;
                case 3: filtrarCupoDisponible(); break;
                case 4: generarReporte(); break;
                case 0: System.out.println("Guardando datos y saliendo..."); break;
                default: System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ==================== MENÚ ACTIVIDADES ====================

    private void menuActividades() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Actividades ---");
            System.out.println("1. Agregar actividad");
            System.out.println("2. Listar actividades");
            System.out.println("3. Editar actividad");
            System.out.println("4. Eliminar actividad");
            System.out.println("5. Buscar actividad");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: agregarActividad(); break;
                case 2: listarActividades(); break;
                case 3: editarActividad(); break;
                case 4: eliminarActividad(); break;
                case 5: buscarActividad(); break;
                case 0: break;
                default: System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void agregarActividad() {
        try {
            System.out.print("Código de la actividad: ");
            String codigo = sc.nextLine();
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            Deporte deporte = seleccionarDeporte();
            System.out.print("Horario (ej: Lunes 18:00-19:00): ");
            String horario = sc.nextLine();
            System.out.print("Cupo máximo: ");
            int cupo = leerEntero();

            Instructor instructor = seleccionarInstructor();

            controlador.agregarActividad(codigo, nombre, deporte, horario, cupo, instructor);
            System.out.println("Actividad agregada correctamente.");
        } catch (Exception e) {
            System.out.println("Error al agregar actividad: " + e.getMessage());
        }
    }

    private void listarActividades() {
        System.out.println("\n--- Listado de Actividades ---");
        for (Actividad a : controlador.listarActividades()) {
            System.out.println(a);
        }
    }

    private void editarActividad() {
        try {
            System.out.print("Código de la actividad a editar: ");
            String codigo = sc.nextLine();
            System.out.print("Nuevo nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Nuevo horario: ");
            String horario = sc.nextLine();
            System.out.print("Nuevo cupo máximo: ");
            int cupo = leerEntero();
            Instructor instructor = seleccionarInstructor();

            controlador.editarActividad(codigo, nombre, horario, cupo, instructor);
            System.out.println("Actividad editada correctamente.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarActividad() {
        try {
            System.out.print("Código de la actividad a eliminar: ");
            String codigo = sc.nextLine();
            controlador.eliminarActividad(codigo);
            System.out.println("Actividad eliminada correctamente.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarActividad() {
        try {
            System.out.print("Código de la actividad a buscar: ");
            String codigo = sc.nextLine();
            Actividad a = controlador.buscarActividad(codigo);
            System.out.println("Encontrada: " + a);
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==================== MENÚ SOCIOS ====================

    private void menuSocios() {
        int opcion;
        do {
            System.out.println("\n--- Gestión de Socios (por actividad) ---");
            System.out.println("1. Inscribir socio en actividad");
            System.out.println("2. Listar socios de una actividad");
            System.out.println("3. Editar datos de un socio");
            System.out.println("4. Eliminar socio de una actividad");
            System.out.println("5. Buscar socio en una actividad");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1: inscribirSocio(); break;
                case 2: listarSocios(); break;
                case 3: editarSocio(); break;
                case 4: eliminarSocio(); break;
                case 5: buscarSocio(); break;
                case 0: break;
                default: System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void inscribirSocio() {
        try {
            System.out.print("Código de la actividad: ");
            String codigoActividad = sc.nextLine();
            System.out.print("Número de socio: ");
            String numeroSocio = sc.nextLine();
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Apellido: ");
            String apellido = sc.nextLine();
            System.out.print("Edad: ");
            int edad = leerEntero();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Fecha de inscripción (yyyy-MM-dd): ");
            String fecha = sc.nextLine();

            Socio s = new Socio(numeroSocio, nombre, apellido, edad, email, fecha);
            controlador.inscribirSocio(codigoActividad, s);

            Actividad a = controlador.buscarActividad(codigoActividad);
            System.out.println("Socio inscrito correctamente.");
            System.out.println(s.generarComprobante(a.getNombre()));
        } catch (ElementoNoEncontradoException | CupoExcedidoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarSocios() {
        try {
            System.out.print("Código de la actividad: ");
            String codigo = sc.nextLine();
            Socio[] socios = controlador.listarSociosDeActividad(codigo);
            System.out.println("\n--- Socios inscritos en " + codigo + " ---");
            for (Socio s : socios) {
                System.out.println(s.mostrarInfo());
            }
            if (socios.length == 0) {
                System.out.println("(sin socios inscritos)");
            }
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editarSocio() {
        try {
            System.out.print("Código de la actividad: ");
            String codigoActividad = sc.nextLine();
            System.out.print("Número de socio a editar: ");
            String numeroSocio = sc.nextLine();
            System.out.print("Nuevo nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Nuevo apellido: ");
            String apellido = sc.nextLine();
            System.out.print("Nueva edad: ");
            int edad = leerEntero();
            System.out.print("Nuevo email: ");
            String email = sc.nextLine();

            controlador.editarSocio(codigoActividad, numeroSocio, nombre, apellido, edad, email);
            System.out.println("Socio editado correctamente.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarSocio() {
        try {
            System.out.print("Código de la actividad: ");
            String codigoActividad = sc.nextLine();
            System.out.print("Número de socio a eliminar: ");
            String numeroSocio = sc.nextLine();
            controlador.eliminarSocio(codigoActividad, numeroSocio);
            System.out.println("Socio eliminado correctamente.");
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarSocio() {
        try {
            System.out.print("Código de la actividad: ");
            String codigoActividad = sc.nextLine();
            System.out.print("Número de socio a buscar: ");
            String numeroSocio = sc.nextLine();
            Socio s = controlador.buscarSocio(codigoActividad, numeroSocio);
            System.out.println("Encontrado: " + s.mostrarInfo());
        } catch (ElementoNoEncontradoException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ==================== FUNCIONALIDAD PROPIA (SIA-9) ====================

    private void filtrarCupoDisponible() {
        System.out.println("¿Filtrar por un deporte específico? (S/N)");
        String resp = sc.nextLine();
        Deporte filtro = null;
        if (resp.trim().equalsIgnoreCase("S")) {
            filtro = seleccionarDeporte();
        }
        Actividad[] resultado = controlador.listarActividadesConCupoDisponible(filtro);
        System.out.println("\n--- Actividades con cupos disponibles ---");
        for (Actividad a : resultado) {
            System.out.println(a + " -> Cupos disponibles: " + a.getCupoDisponible());
        }
        if (resultado.length == 0) {
            System.out.println("No hay actividades con cupos disponibles para ese criterio.");
        }
    }

    // ==================== REPORTE (SIA-O2) ====================

    private void generarReporte() {
        try {
            controlador.generarReporte();
            System.out.println("Reporte generado correctamente: " + controlador.getNombreArchivoReporte());
        } catch (java.io.IOException e) {
            System.out.println("Error al generar el reporte: " + e.getMessage());
        }
    }

    // ==================== UTILITARIOS ====================

    private Deporte seleccionarDeporte() {
        Deporte[] valores = Deporte.values();
        System.out.println("Seleccione un deporte:");
        for (int i = 0; i < valores.length; i++) {
            System.out.println((i + 1) + ". " + valores[i]);
        }
        int opcion = leerEntero();
        if (opcion < 1 || opcion > valores.length) {
            System.out.println("Opción inválida, se asignará FUTBOL por defecto.");
            return Deporte.FUTBOL;
        }
        return valores[opcion - 1];
    }

    private Instructor seleccionarInstructor() {
        Instructor[] lista = controlador.listarInstructores();
        if (lista.length == 0) {
            System.out.println("No hay instructores registrados. La actividad quedará sin instructor.");
            return null;
        }
        System.out.println("Seleccione un instructor:");
        for (int i = 0; i < lista.length; i++) {
            System.out.println((i + 1) + ". " + lista[i].getNombre() + " " + lista[i].getApellido());
        }
        int opcion = leerEntero();
        if (opcion < 1 || opcion > lista.length) {
            System.out.println("Opción inválida, la actividad quedará sin instructor.");
            return null;
        }
        return lista[opcion - 1];
    }

    private int leerEntero() {
        while (true) {
            try {
                String linea = sc.nextLine();
                return Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }
}
