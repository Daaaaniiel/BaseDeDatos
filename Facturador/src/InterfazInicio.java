import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InterfazInicio extends JFrame {
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String usuario = "postgres";
    private final String contraseña = "Daniel2023";
    private Connection conexion;
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private JTextField campoIdentificacionCliente;
    private JTextField campoNumeroFactura;
    private JButton botonEliminarFactura;

    public InterfazInicio() {
        setTitle("CAMPOVERDE SOFT - Generar Factura");
        setSize(800, 500); // Ajuste de tamaño
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        JLabel tituloPrincipal = new JLabel("Facturas", SwingConstants.CENTER);
        tituloPrincipal.setFont(new Font("Arial", Font.BOLD, 24));
        panelPrincipal.add(tituloPrincipal, BorderLayout.NORTH);

        // Panel para los campos de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel labelIdentificacionCliente = new JLabel("Identificación Cliente:");
        campoIdentificacionCliente = new JTextField(10);
        JLabel labelNumeroFactura = new JLabel("Número Factura:");
        campoNumeroFactura = new JTextField(10);

        panelBusqueda.add(labelIdentificacionCliente);
        panelBusqueda.add(campoIdentificacionCliente);
        panelBusqueda.add(labelNumeroFactura);
        panelBusqueda.add(campoNumeroFactura);

        JButton botonBuscar = new JButton("Buscar");
        botonBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarFacturas();
            }
        });
        panelBusqueda.add(botonBuscar);

        // Crear la tabla de facturas
        Object[] columnas = {"Número", "Código Cliente", "Fecha", "Subtotal", "Descuento", "IVA", "ICE", "Forma de Pago", "Status"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaFacturas = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaFacturas);

        // Ajuste de tamaño de la tabla
        scrollTabla.setPreferredSize(new Dimension(780, 300));

        panelPrincipal.add(panelBusqueda, BorderLayout.SOUTH);
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        // Panel para los botones "Generar Factura" y "Eliminar Factura"
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton botonGenerarFactura = new JButton("Generar Factura");
        botonGenerarFactura.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirFacturador();
            }
        });
        panelBotones.add(botonGenerarFactura);

        botonEliminarFactura = new JButton("Eliminar Factura");
        botonEliminarFactura.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarFactura();
            }
        });
        panelBotones.add(botonEliminarFactura);

        JButton botonInterfazProductos = new JButton("Interfaz Productos");
        botonInterfazProductos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirInterfazProductos();
            }
        });
        panelBotones.add(botonInterfazProductos);

        // Botón para abrir la interfaz de clientes
        JButton botonInterfazClientes = new JButton("Interfaz Clientes");
        botonInterfazClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirInterfazClientes();
            }
        });
        panelBotones.add(botonInterfazClientes);

        panelPrincipal.add(panelBotones, BorderLayout.NORTH);

        // Panel para el título "CAMPOVERDE SOFT"
        JPanel panelTitulo = new JPanel();
        JLabel tituloCampoVerdeSoft = new JLabel("CAMPOVERDE SOFT");
        tituloCampoVerdeSoft.setFont(new Font("Arial", Font.BOLD, 18));
        panelTitulo.add(tituloCampoVerdeSoft);

        // Añadir el panel del título principal y el panel del título "CAMPOVERDE SOFT" al marco
        JPanel panelFinal = new JPanel(new BorderLayout());
        panelFinal.add(panelTitulo, BorderLayout.NORTH);
        panelFinal.add(panelPrincipal, BorderLayout.CENTER);

        add(panelFinal);

        conectar();
        cargarFacturas();
    }

    private void conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(url, usuario, contraseña);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarFacturas() {
        String sql = "SELECT f.FACNUMERO, c.CLICODIGO, f.FACFECHA, f.FACSUBTOTAL, f.FACDESCUENTO, f.FACIVA, f.FACICE, f.FACFORMAPAGO, f.FACSTATUS " +
                "FROM FACTURAS f INNER JOIN CLIENTES c ON f.CLICODIGO = c.CLICODIGO";
        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String numero = rs.getString("FACNUMERO");
                String codigoCliente = rs.getString("CLICODIGO");
                Date fecha = rs.getDate("FACFECHA");
                double subtotal = rs.getDouble("FACSUBTOTAL");
                double descuento = rs.getDouble("FACDESCUENTO");
                double iva = rs.getDouble("FACIVA");
                double ice = rs.getDouble("FACICE");
                String formaPago = rs.getString("FACFORMAPAGO");
                String status = rs.getString("FACSTATUS");

                modeloTabla.addRow(new Object[]{numero, codigoCliente, fecha, subtotal, descuento, iva, ice, formaPago, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar facturas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarFacturas() {
        String identificacionCliente = campoIdentificacionCliente.getText();
        String numeroFactura = campoNumeroFactura.getText();

        StringBuilder sqlBuilder = new StringBuilder("SELECT f.FACNUMERO, c.CLICODIGO, f.FACFECHA, f.FACSUBTOTAL, f.FACDESCUENTO, f.FACIVA, f.FACICE, f.FACFORMAPAGO, f.FACSTATUS ")
                .append("FROM FACTURAS f INNER JOIN CLIENTES c ON f.CLICODIGO = c.CLICODIGO ");

        if (!identificacionCliente.isEmpty()) {
            sqlBuilder.append("WHERE c.CLIIDENTIFICACION = '").append(identificacionCliente).append("'");
        } else if (!numeroFactura.isEmpty()) {
            sqlBuilder.append("WHERE f.FACNUMERO = '").append(numeroFactura).append("'");
        }

        String sql = sqlBuilder.toString();

        try (Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Limpiar tabla antes de cargar nuevos datos
            modeloTabla.setRowCount(0);

            while (rs.next()) {
                String numero = rs.getString("FACNUMERO");
                String codigoCliente = rs.getString("CLICODIGO");
                Date fecha = rs.getDate("FACFECHA");
                double subtotal = rs.getDouble("FACSUBTOTAL");
                double descuento = rs.getDouble("FACDESCUENTO");
                double iva = rs.getDouble("FACIVA");
                double ice = rs.getDouble("FACICE");
                String formaPago = rs.getString("FACFORMAPAGO");
                String status = rs.getString("FACSTATUS");

                modeloTabla.addRow(new Object[]{numero, codigoCliente, fecha, subtotal, descuento, iva, ice, formaPago, status});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar facturas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFacturador() {
        Facturador facturador = new Facturador();
        facturador.setVisible(true);
        this.dispose(); // Cierra la interfaz de inicio
    }

    private void eliminarFactura() {
        String codigoFactura = JOptionPane.showInputDialog(this, "Ingrese el código de la factura a eliminar:");

        if (codigoFactura == null || codigoFactura.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el código de la factura.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la factura?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            String sql = "UPDATE FACTURAS SET FACSTATUS = 'INA' WHERE FACNUMERO = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                               pstmt.setString(1, codigoFactura);
                int filasActualizadas = pstmt.executeUpdate();
                if (filasActualizadas > 0) {
                    JOptionPane.showMessageDialog(this, "Factura eliminada correctamente.");
                    // Limpiar la tabla y cargar nuevamente las facturas
                    modeloTabla.setRowCount(0); // Limpiar la tabla
                    cargarFacturas(); // Cargar las facturas actualizadas
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró una factura con el código especificado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirInterfazProductos() {
        ProductosInterfaz productos = new ProductosInterfaz();
        productos.setVisible(true);
    }

    private void abrirInterfazClientes() {
        ClientesInterfaz clientes = new ClientesInterfaz();
        clientes.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                InterfazInicio inicio = new InterfazInicio();
                inicio.setVisible(true);
            }
        });
    }
}








