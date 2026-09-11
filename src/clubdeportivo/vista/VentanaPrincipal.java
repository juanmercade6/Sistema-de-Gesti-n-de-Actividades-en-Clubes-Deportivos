package clubdeportivo.vista;

import clubdeportivo.excepciones.CupoExcedidoException;
import clubdeportivo.excepciones.ElementoNoEncontradoException;
import clubdeportivo.controlador.ControladorClub;
import clubdeportivo.modelo.Actividad;
import clubdeportivo.modelo.Deporte;
import clubdeportivo.modelo.Instructor;
import clubdeportivo.modelo.Socio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Interfaz gráfica del sistema (SIA-10), implementada con Swing.
 * Ofrece, en pestañas separadas, la misma funcionalidad completa que la
 * consola: CRUD de Actividades, CRUD de Socios inscritos por actividad,
 * y la funcionalidad propia de filtrado de cupos disponibles.
 */
public class VentanaPrincipal extends JFrame {

    private ControladorClub controlador;

    private DefaultTableModel modeloActividades;
    private JTable tablaActividades;

    private DefaultTableModel modeloSocios;
    private JTable tablaSocios;
    private JTextField campoCodigoActividadSocios;

    private DefaultTableModel modeloFiltro;
    private JTable tablaFiltro;

    public VentanaPrincipal(ControladorClub controlador) {
        super("Sistema de Gestión de Actividades en Clubes Deportivos");
        this.controlador = controlador;
        initComponents();
        refrescarActividades();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Actividades", crearPanelActividades());
        tabs.addTab("Socios", crearPanelSocios());
        tabs.addTab("Cupos disponibles", crearPanelFiltro());
        tabs.addTab("Reporte", crearPanelReporte());

        getContentPane().add(tabs);

        // La ventana es la responsable de guardar los datos al cerrarse,
        // ya que Main no contiene lógica de negocio ni de persistencia.
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                controlador.guardarYSalir();
            }
        });
    }

    // ==================== PANEL REPORTE (SIA-O2) ====================

    private JPanel crearPanelReporte() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel info = new JLabel("Genera un archivo CSV con el detalle de ocupación de cada actividad,"
                + " listo para abrir en una planilla de cálculo.", SwingConstants.CENTER);
        JButton btnGenerar = new JButton("Generar reporte");
        btnGenerar.addActionListener(e -> {
            try {
                controlador.generarReporte();
                JOptionPane.showMessageDialog(this, "Reporte generado: " + controlador.getNombreArchivoReporte());
            } catch (java.io.IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centro.add(btnGenerar);
        panel.add(info, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    // ==================== PANEL ACTIVIDADES ====================

    private JPanel crearPanelActividades() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        modeloActividades = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Deporte", "Horario", "Cupo", "Instructor", "Inscritos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaActividades = new JTable(modeloActividades);
        panel.add(new JScrollPane(tablaActividades), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        botones.add(btnAgregar);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);
        panel.add(botones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> dialogoActividad(null));
        btnEditar.addActionListener(e -> {
            String codigo = obtenerCodigoSeleccionado();
            if (codigo != null) {
                try {
                    dialogoActividad(controlador.buscarActividad(codigo));
                } catch (ElementoNoEncontradoException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnEliminar.addActionListener(e -> {
            String codigo = obtenerCodigoSeleccionado();
            if (codigo != null) {
                try {
                    controlador.eliminarActividad(codigo);
                    refrescarActividades();
                } catch (ElementoNoEncontradoException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        btnBuscar.addActionListener(e -> {
            String codigo = JOptionPane.showInputDialog(this, "Código de la actividad a buscar:");
            if (codigo != null) {
                try {
                    Actividad a = controlador.buscarActividad(codigo);
                    JOptionPane.showMessageDialog(this, a.toString(), "Actividad encontrada", JOptionPane.INFORMATION_MESSAGE);
                } catch (ElementoNoEncontradoException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return panel;
    }

    private String obtenerCodigoSeleccionado() {
        int fila = tablaActividades.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una actividad de la tabla.");
            return null;
        }
        return (String) modeloActividades.getValueAt(fila, 0);
    }

    private void dialogoActividad(Actividad existente) {
        JTextField campoCodigo = new JTextField(existente != null ? existente.getCodigo() : "");
        campoCodigo.setEditable(existente == null);
        JTextField campoNombre = new JTextField(existente != null ? existente.getNombre() : "");
        JComboBox<Deporte> comboDeporte = new JComboBox<>(Deporte.values());
        if (existente != null) comboDeporte.setSelectedItem(existente.getDeporte());
        JTextField campoHorario = new JTextField(existente != null ? existente.getHorario() : "");
        JTextField campoCupo = new JTextField(existente != null ? String.valueOf(existente.getCupoMaximo()) : "");

        Instructor[] instructores = controlador.listarInstructores();
        JComboBox<Instructor> comboInstructor = new JComboBox<>(instructores);
        if (existente != null && existente.getInstructor() != null) comboInstructor.setSelectedItem(existente.getInstructor());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Código:")); panel.add(campoCodigo);
        panel.add(new JLabel("Nombre:")); panel.add(campoNombre);
        panel.add(new JLabel("Deporte:")); panel.add(comboDeporte);
        panel.add(new JLabel("Horario:")); panel.add(campoHorario);
        panel.add(new JLabel("Cupo máximo:")); panel.add(campoCupo);
        panel.add(new JLabel("Instructor:")); panel.add(comboInstructor);

        int resultado = JOptionPane.showConfirmDialog(this, panel,
                existente == null ? "Agregar actividad" : "Editar actividad", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                int cupo = Integer.parseInt(campoCupo.getText().trim());
                Instructor instructor = (Instructor) comboInstructor.getSelectedItem();
                if (existente == null) {
                    controlador.agregarActividad(campoCodigo.getText().trim(), campoNombre.getText().trim(),
                            (Deporte) comboDeporte.getSelectedItem(), campoHorario.getText().trim(), cupo, instructor);
                } else {
                    controlador.editarActividad(existente.getCodigo(), campoNombre.getText().trim(),
                            campoHorario.getText().trim(), cupo, instructor);
                }
                refrescarActividades();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El cupo máximo debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ElementoNoEncontradoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refrescarActividades() {
        modeloActividades.setRowCount(0);
        for (Actividad a : controlador.listarActividades()) {
            String instructor = a.getInstructor() != null
                    ? a.getInstructor().getNombre() + " " + a.getInstructor().getApellido() : "Sin asignar";
            modeloActividades.addRow(new Object[]{a.getCodigo(), a.getNombre(), a.getDeporte(),
                    a.getHorario(), a.getCupoMaximo(), instructor, a.cantidadInscritos()});
        }
    }

    // ==================== PANEL SOCIOS ====================

    private JPanel crearPanelSocios() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Código de actividad:"));
        campoCodigoActividadSocios = new JTextField(10);
        JButton btnCargar = new JButton("Cargar socios");
        topPanel.add(campoCodigoActividadSocios);
        topPanel.add(btnCargar);
        panel.add(topPanel, BorderLayout.NORTH);

        modeloSocios = new DefaultTableModel(
                new Object[]{"N° Socio", "Nombre", "Apellido", "Edad", "Email", "Fecha Inscripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaSocios = new JTable(modeloSocios);
        panel.add(new JScrollPane(tablaSocios), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnInscribir = new JButton("Inscribir socio");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnBuscar = new JButton("Buscar");
        botones.add(btnInscribir);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnBuscar);
        panel.add(botones, BorderLayout.SOUTH);

        btnCargar.addActionListener(e -> refrescarSocios());
        btnInscribir.addActionListener(e -> dialogoInscribirSocio());
        btnEditar.addActionListener(e -> dialogoEditarSocio());
        btnEliminar.addActionListener(e -> eliminarSocioSeleccionado());
        btnBuscar.addActionListener(e -> buscarSocioDialogo());

        return panel;
    }

    private void refrescarSocios() {
        String codigo = campoCodigoActividadSocios.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código de una actividad antes de cargar sus socios.",
                    "Falta el código", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Socio[] socios = controlador.listarSociosDeActividad(codigo);
            modeloSocios.setRowCount(0);
            for (Socio s : socios) {
                modeloSocios.addRow(new Object[]{s.getNumeroSocio(), s.getNombre(), s.getApellido(),
                        s.getEdad(), s.getEmail(), s.getFechaInscripcion()});
            }
            if (socios.length == 0) {
                JOptionPane.showMessageDialog(this, "La actividad '" + codigo + "' no tiene socios inscritos todavía.",
                        "Sin socios", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (ElementoNoEncontradoException ex) {
            modeloSocios.setRowCount(0);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogoInscribirSocio() {
        String codigoActividad = campoCodigoActividadSocios.getText().trim();
        if (codigoActividad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese primero el código de la actividad.");
            return;
        }
        JTextField campoNumero = new JTextField();
        JTextField campoNombre = new JTextField();
        JTextField campoApellido = new JTextField();
        JTextField campoEdad = new JTextField();
        JTextField campoEmail = new JTextField();
        JTextField campoFecha = new JTextField("2026-01-01");

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("N° Socio:")); panel.add(campoNumero);
        panel.add(new JLabel("Nombre:")); panel.add(campoNombre);
        panel.add(new JLabel("Apellido:")); panel.add(campoApellido);
        panel.add(new JLabel("Edad:")); panel.add(campoEdad);
        panel.add(new JLabel("Email:")); panel.add(campoEmail);
        panel.add(new JLabel("Fecha inscripción:")); panel.add(campoFecha);

        int resultado = JOptionPane.showConfirmDialog(this, panel, "Inscribir socio", JOptionPane.OK_CANCEL_OPTION);
        if (resultado == JOptionPane.OK_OPTION) {
            try {
                int edad = Integer.parseInt(campoEdad.getText().trim());
                Socio s = new Socio(campoNumero.getText().trim(), campoNombre.getText().trim(),
                        campoApellido.getText().trim(), edad, campoEmail.getText().trim(), campoFecha.getText().trim());
                controlador.inscribirSocio(codigoActividad, s);
                refrescarSocios();
                refrescarActividades();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "La edad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ElementoNoEncontradoException | CupoExcedidoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private String obtenerNumeroSocioSeleccionado() {
        int fila = tablaSocios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un socio de la tabla.");
            return null;
        }
        return (String) modeloSocios.getValueAt(fila, 0);
    }

    private void dialogoEditarSocio() {
        String codigoActividad = campoCodigoActividadSocios.getText().trim();
        String numeroSocio = obtenerNumeroSocioSeleccionado();
        if (numeroSocio == null) return;
        try {
            Socio actual = controlador.buscarSocio(codigoActividad, numeroSocio);
            JTextField campoNombre = new JTextField(actual.getNombre());
            JTextField campoApellido = new JTextField(actual.getApellido());
            JTextField campoEdad = new JTextField(String.valueOf(actual.getEdad()));
            JTextField campoEmail = new JTextField(actual.getEmail());

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Nombre:")); panel.add(campoNombre);
            panel.add(new JLabel("Apellido:")); panel.add(campoApellido);
            panel.add(new JLabel("Edad:")); panel.add(campoEdad);
            panel.add(new JLabel("Email:")); panel.add(campoEmail);

            int resultado = JOptionPane.showConfirmDialog(this, panel, "Editar socio", JOptionPane.OK_CANCEL_OPTION);
            if (resultado == JOptionPane.OK_OPTION) {
                int edad = Integer.parseInt(campoEdad.getText().trim());
                controlador.editarSocio(codigoActividad, numeroSocio, campoNombre.getText().trim(),
                        campoApellido.getText().trim(), edad, campoEmail.getText().trim());
                refrescarSocios();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ElementoNoEncontradoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSocioSeleccionado() {
        String codigoActividad = campoCodigoActividadSocios.getText().trim();
        String numeroSocio = obtenerNumeroSocioSeleccionado();
        if (numeroSocio == null) return;
        try {
            controlador.eliminarSocio(codigoActividad, numeroSocio);
            refrescarSocios();
            refrescarActividades();
        } catch (ElementoNoEncontradoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarSocioDialogo() {
        String codigoActividad = campoCodigoActividadSocios.getText().trim();
        String numeroSocio = JOptionPane.showInputDialog(this, "Número de socio a buscar:");
        if (numeroSocio == null) return;
        try {
            Socio s = controlador.buscarSocio(codigoActividad, numeroSocio);
            JOptionPane.showMessageDialog(this, s.mostrarInfo(), "Socio encontrado", JOptionPane.INFORMATION_MESSAGE);
        } catch (ElementoNoEncontradoException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== PANEL FILTRO (SIA-9) ====================

    private JPanel crearPanelFiltro() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Filtrar por deporte:"));
        JComboBox<String> comboDeporte = new JComboBox<>();
        comboDeporte.addItem("(Todos)");
        for (Deporte d : Deporte.values()) comboDeporte.addItem(d.name());
        JButton btnFiltrar = new JButton("Buscar cupos disponibles");
        topPanel.add(comboDeporte);
        topPanel.add(btnFiltrar);
        panel.add(topPanel, BorderLayout.NORTH);

        modeloFiltro = new DefaultTableModel(
                new Object[]{"Código", "Nombre", "Deporte", "Horario", "Cupos disponibles"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tablaFiltro = new JTable(modeloFiltro);
        panel.add(new JScrollPane(tablaFiltro), BorderLayout.CENTER);

        btnFiltrar.addActionListener(e -> {
            String seleccion = (String) comboDeporte.getSelectedItem();
            Deporte filtro = (seleccion == null || seleccion.equals("(Todos)")) ? null : Deporte.valueOf(seleccion);
            Actividad[] resultado = controlador.listarActividadesConCupoDisponible(filtro);
            modeloFiltro.setRowCount(0);
            for (Actividad a : resultado) {
                modeloFiltro.addRow(new Object[]{a.getCodigo(), a.getNombre(), a.getDeporte(),
                        a.getHorario(), a.getCupoDisponible()});
            }
        });

        return panel;
    }
}
