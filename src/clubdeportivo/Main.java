package clubdeportivo;

import clubdeportivo.controlador.ControladorClub;
import clubdeportivo.vista.MenuConsola;
import clubdeportivo.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Punto de entrada del Sistema de Gestión de Actividades en Clubes
 * Deportivos.
 *
 * Esta clase se mantiene deliberadamente mínima: solo pregunta el modo
 * de ejecución (consola o ventana, SIA-10) y delega todo el resto —
 * carga de datos, lógica de negocio y persistencia— al ControladorClub
 * y a las vistas correspondientes. No contiene reglas de negocio.
 */
public class Main {

    public static void main(String[] args) {
        ControladorClub controlador = new ControladorClub();
        Scanner sc = new Scanner(System.in);

        int modo = leerModo(sc);

        if (modo == 2) {
            SwingUtilities.invokeLater(() -> new VentanaPrincipal(controlador).setVisible(true));
        } else {
            new MenuConsola(controlador, sc).iniciar();
            controlador.guardarYSalir();
            System.out.println("Datos guardados correctamente. Hasta pronto.");
        }
    }

    private static int leerModo(Scanner sc) {
        System.out.println("=== Sistema de Gestión de Actividades en Clubes Deportivos ===");
        System.out.println("¿Cómo desea utilizar el sistema?");
        System.out.println("1. Consola");
        System.out.println("2. Ventana (interfaz gráfica)");
        System.out.print("Seleccione una opción: ");
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida, se usará el modo consola por defecto.");
            return 1;
        }
    }
}
