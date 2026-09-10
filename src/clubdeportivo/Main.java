package clubdeportivo;

import clubdeportivo.gestion.GestorClub;
import clubdeportivo.persistencia.PersistenciaCSV;
import clubdeportivo.vista.MenuConsola;
import clubdeportivo.vista.VentanaPrincipal;

import javax.swing.*;
import java.util.Scanner;

/**
 * Punto de entrada del Sistema de Gestión de Actividades en Clubes Deportivos.
 *
 * Al iniciar, pregunta al usuario si desea usar consola o ventana (SIA-10),
 * carga los datos persistidos en batch (SIA-11) y, al finalizar, guarda
 * el estado completo del sistema.
 */
public class Main {

    private static final String CARPETA_DATOS = "data";

    public static void main(String[] args) {
        GestorClub gestor = new GestorClub();
        PersistenciaCSV persistencia = new PersistenciaCSV(CARPETA_DATOS);

        // Carga batch: si ya existen datos guardados se cargan desde CSV,
        // si es la primera ejecución se cargan datos de ejemplo (SIA-3).
        if (persistencia.existenDatosPrevios()) {
            persistencia.cargarDatos(gestor);
            System.out.println("Datos cargados desde archivos CSV.");
        } else {
            gestor.cargarDatosIniciales();
            System.out.println("No se encontraron datos previos. Se cargaron datos de ejemplo.");
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("=== Sistema de Gestión de Actividades en Clubes Deportivos ===");
        System.out.println("¿Cómo desea utilizar el sistema?");
        System.out.println("1. Consola");
        System.out.println("2. Ventana (interfaz gráfica)");
        System.out.print("Seleccione una opción: ");

        int modo = 1;
        try {
            modo = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida, se usará el modo consola por defecto.");
        }

        if (modo == 2) {
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(gestor);
                ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        persistencia.guardarDatos(gestor);
                        System.out.println("Datos guardados correctamente. Hasta pronto.");
                    }
                });
                ventana.setVisible(true);
            });
        } else {
            MenuConsola menu = new MenuConsola(gestor, sc);
            menu.iniciar();
            persistencia.guardarDatos(gestor);
            System.out.println("Datos guardados correctamente. Hasta pronto.");
        }
    }
}
