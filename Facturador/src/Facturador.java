import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Facturador extends JFrame {
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String usuario = "postgres";
    private final String contraseña = "Daniel2023";
    private Connection conexion;

    private JTextField campoIdentificacion;
    private JTextField campoNombre;
    private JTextField campoDireccion;
    private JTextField campoCelular;
    private JTextField campoEmail;
    private JTextField campoFecha;
    private JTextField campoNumeroFactura; // Nuevo campo para el número de factura

    private JTextField campoCodigoProducto;
    private JTextField campoCantidad;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextArea areaResultado;
    private JTextField campoSubtotal;
    private JTextField campoPrecioFinal;
    private JTextField campoDescuento;
    private JComboBox<String> comboBoxIVA;
    private JComboBox<String> comboBoxMetodoPago;
    private double porcentajeIVA;

    public Facturador() {
        setTitle("Campoverde Soft");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
        conectar();
    }

    private void inicializarComponentes() {
    JPanel panelPrincipal = new JPanel();
    panelPrincipal.setBackground(Color.WHITE);
    panelPrincipal.setLayout(new BorderLayout());

    JLabel tituloEmpresa = new JLabel("Campoverde Soft", JLabel.CENTER);
    tituloEmpresa.setFont(new Font("Arial", Font.BOLD, 36));
    panelPrincipal.add(tituloEmpresa, BorderLayout.NORTH);

    JPanel panelCentral = new JPanel();
    panelCentral.setLayout(new BorderLayout());

    JPanel panelIzquierdo = new JPanel();
    panelIzquierdo.setLayout(new GridLayout(14, 2, 10, 10));

    JLabel etiquetaIdentificacion = new JLabel("Identificación:");
    campoIdentificacion = new JTextField();
    JButton botonBuscar = new JButton("Buscar");
    botonBuscar.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            buscarCliente();
        }
    });

    JLabel etiquetaNombre = new JLabel("Nombre:");
    campoNombre = new JTextField();
    JLabel etiquetaDireccion = new JLabel("Dirección:");
    campoDireccion = new JTextField();
    JLabel etiquetaCelular = new JLabel("Celular:");
    campoCelular = new JTextField();
    JLabel etiquetaEmail = new JLabel("Email:");
    campoEmail = new JTextField();

    campoIdentificacion.setPreferredSize(new Dimension(200, 30));
    campoNombre.setPreferredSize(new Dimension(300, 30));
    campoDireccion.setPreferredSize(new Dimension(300, 30));
    campoCelular.setPreferredSize(new Dimension(150, 30));
    campoEmail.setPreferredSize(new Dimension(300, 30));

    JLabel etiquetaFecha = new JLabel("Fecha:");
    campoFecha = new JTextField();
    campoFecha.setEditable(false);
    campoFecha.setText(java.time.LocalDate.now().toString());

    JLabel etiquetaNumeroFactura = new JLabel("Número de Factura:");
    campoNumeroFactura = new JTextField();
    campoNumeroFactura.setPreferredSize(new Dimension(150, 30));

    areaResultado = new JTextArea(5, 20);
    areaResultado.setEditable(false);

    panelIzquierdo.add(etiquetaIdentificacion);
    JPanel panelIdentificacion = new JPanel();
    panelIdentificacion.setLayout(new BorderLayout());
    panelIdentificacion.add(campoIdentificacion, BorderLayout.CENTER);
    panelIdentificacion.add(botonBuscar, BorderLayout.EAST);

    panelIzquierdo.add(panelIdentificacion);
    panelIzquierdo.add(etiquetaNombre);
    panelIzquierdo.add(campoNombre);
    panelIzquierdo.add(etiquetaDireccion);
    panelIzquierdo.add(campoDireccion);
    panelIzquierdo.add(etiquetaCelular);
    panelIzquierdo.add(campoCelular);
    panelIzquierdo.add(etiquetaEmail);
    panelIzquierdo.add(campoEmail);
    panelIzquierdo.add(etiquetaFecha);
    panelIzquierdo.add(campoFecha);
    panelIzquierdo.add(etiquetaNumeroFactura);
    panelIzquierdo.add(campoNumeroFactura);

    JLabel etiquetaCodigoProducto = new JLabel("Código Producto:");
    campoCodigoProducto = new JTextField();
    JLabel etiquetaCantidad = new JLabel("Cantidad:");
    campoCantidad = new JTextField();
    JButton botonAgregarProducto = new JButton("Agregar Producto");
    botonAgregarProducto.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            agregarProducto();
        }
    });

    JButton botonEliminarProducto = new JButton("Descartar Producto");
    botonEliminarProducto.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            eliminarProducto();
        }
    });

    panelIzquierdo.add(etiquetaCodigoProducto);
    panelIzquierdo.add(campoCodigoProducto);
    panelIzquierdo.add(etiquetaCantidad);
    panelIzquierdo.add(campoCantidad);
    panelIzquierdo.add(botonAgregarProducto);
    panelIzquierdo.add(botonEliminarProducto);

    panelCentral.add(panelIzquierdo, BorderLayout.WEST);

    Object[] columnas = {"Código", "Cantidad", "Descripción", "Precio Unitario", "Precio Total", "Editar"};
    modeloTabla = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 5; // Permitir edición en la columna "Editar"
        }
    };

    tablaProductos = new JTable(modeloTabla);
    tablaProductos.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
    tablaProductos.getColumnModel().getColumn(5).setCellEditor(new ButtonEditorEditar(new JCheckBox()));

    JScrollPane scrollTabla = new JScrollPane(tablaProductos);
    scrollTabla.setPreferredSize(new Dimension(600, 200));

    panelCentral.add(scrollTabla, BorderLayout.CENTER);

    JPanel panelDerecho = new JPanel();
    panelDerecho.setLayout(new GridLayout(6, 2, 10, 10)); // Ajustado para incluir método de pago y descuento

    campoSubtotal = new JTextField();
    campoSubtotal.setEditable(false);
    panelDerecho.add(new JLabel("Subtotal:"));
    panelDerecho.add(campoSubtotal);

    JLabel etiquetaIVA = new JLabel("IVA:");
    panelDerecho.add(etiquetaIVA);

    comboBoxIVA = new JComboBox<>();
    comboBoxIVA.addItem("8%");
    comboBoxIVA.addItem("12%");
    comboBoxIVA.addItem("15%");
    comboBoxIVA.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            actualizarPorcentajeIVA();
        }
    });
    panelDerecho.add(comboBoxIVA);

    JLabel etiquetaMetodoPago = new JLabel("Método de Pago:");
    panelDerecho.add(etiquetaMetodoPago);
    comboBoxMetodoPago = new JComboBox<>();
    comboBoxMetodoPago.addItem("Efectivo");
    comboBoxMetodoPago.addItem("Tarjeta de crédito");
    comboBoxMetodoPago.addItem("Tarjeta de débito");
    comboBoxMetodoPago.addItem("Transferencia");
    comboBoxMetodoPago.addItem("Cheque");
    panelDerecho.add(comboBoxMetodoPago);

    JLabel etiquetaDescuento = new JLabel("Descuento:");
    panelDerecho.add(etiquetaDescuento);

    campoDescuento = new JTextField();
    campoDescuento.setEditable(false);
    panelDerecho.add(campoDescuento);

    campoPrecioFinal = new JTextField();
    campoPrecioFinal.setEditable(false);
    panelDerecho.add(new JLabel("Precio Final:"));
    panelDerecho.add(campoPrecioFinal);
 JButton botonConfirmar = new JButton("Confirmar");
        botonConfirmar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarFactura();
            }
        });
        panelDerecho.add(botonConfirmar);
   JButton botonCancelar = new JButton("Cancelar");
botonCancelar.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        limpiarTodo();
    }
});



panelDerecho.add(botonCancelar);
    panelCentral.add(panelDerecho, BorderLayout.SOUTH);

    panelPrincipal.add(panelCentral, BorderLayout.CENTER);

    add(panelPrincipal);
}


    private void conectar() {
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(url, usuario, contraseña);
        } catch (ClassNotFoundException | SQLException e) {
            areaResultado.append("Error al conectar: " + e.getMessage() + "\n");
        }
    }

    private void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                areaResultado.append("Desconexión exitosa de la base de datos\n");
            }
        } catch (SQLException e) {
            areaResultado.append("Error al desconectar: " + e.getMessage() + "\n");
        }
    }
    private void limpiarTodo() {
    // Limpiar campos de cliente
    limpiarCamposCliente();

    // Limpiar campo de número de factura
    campoNumeroFactura.setText("");

    // Limpiar tabla de productos
    modeloTabla.setRowCount(0); // Elimina todas las filas de la tabla

    // Limpiar campos relacionados a la factura
    limpiarCamposFactura();
}


private void confirmarFactura() {
    // Obtener todos los datos necesarios de la interfaz
    String numeroFactura = campoNumeroFactura.getText().trim();
    String identificacionCliente = campoIdentificacion.getText().trim();
    String fecha = campoFecha.getText().trim();
    String subtotalStr = campoSubtotal.getText().trim();

    // Validar que todos los campos obligatorios estén llenos
    if (numeroFactura.isEmpty() || identificacionCliente.isEmpty() || fecha.isEmpty() || subtotalStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    double subtotal;

    try {
        // Convertir subtotal a double
        subtotalStr = subtotalStr.replace(',', '.'); // Reemplazar ',' por '.' si es necesario
        subtotal = Double.parseDouble(subtotalStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Error en el formato del subtotal. Asegúrate de usar el separador decimal correcto.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Obtener el IVA seleccionado
    double iva;
    String seleccionIVA = (String) this.comboBoxIVA.getSelectedItem();
    switch (seleccionIVA) {
        case "8%":
            iva = 0.08;
            break;
        case "12%":
            iva = 0.12;
            break;
        case "15%":
            iva = 0.15;
            break;
        default:
            iva = 0.0;
            break;
    }

    // Obtener la forma de pago seleccionada
    String formaPago;
    String seleccionMetodoPago = (String) this.comboBoxMetodoPago.getSelectedItem();
    switch (seleccionMetodoPago) {
        case "Efectivo":
            formaPago = "EFECT";
            break;
        case "Tarjeta de crédito":
            formaPago = "TARCR";
            break;
        case "Transferencia":
            formaPago = "TRANS";
            break;
        case "Tarjeta de débito":
            formaPago = "TARDB";
            break;
        case "Cheque":
            formaPago = "CHEQ";
            break;
        default:
            JOptionPane.showMessageDialog(this, "Forma de pago no reconocida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
    }

    // Calcular el descuento
    double descuento = calcularDescuento(subtotal);

    // Obtener el CLICODIGO del cliente basado en la identificación
    String cliCodigo = obtenerCLICODIGO(identificacionCliente);
    if (cliCodigo == null) {
        JOptionPane.showMessageDialog(this, "Cliente no encontrado con la identificación proporcionada.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Preparar la inserción en la base de datos
    String sql = "INSERT INTO FACTURAS (FACNUMERO, CLICODIGO, FACFECHA, FACSUBTOTAL, FACIVA, FACICE, FACDESCUENTO, FACFORMAPAGO, FACSTATUS) " +
                 "VALUES (?, ?, ?, ?, ?, 0, ?, ?, 'ACT')";

    try {
        // Verificar que la conexión no sea null
        if (conexion == null) {
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PreparedStatement pstmt = conexion.prepareStatement(sql);
        pstmt.setString(1, numeroFactura);
        pstmt.setString(2, cliCodigo); // Aquí asignas el CLICODIGO obtenido
        pstmt.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.parse(fecha)));
        pstmt.setDouble(4, subtotal);
        pstmt.setDouble(5, iva * subtotal); // Calcular el IVA sobre el subtotal
        pstmt.setDouble(6, descuento); // Incluir el descuento
        pstmt.setString(7, formaPago);

        int filasInsertadas = pstmt.executeUpdate();

        if (filasInsertadas > 0) {
            areaResultado.append("Factura registrada correctamente.\n");
            limpiarCamposFactura();
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la factura.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al registrar la factura: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private String obtenerCLICODIGO(String identificacionCliente) {
    String cliCodigo = null;

    // Realizar la búsqueda del CLICODIGO en la base de datos según la identificación del cliente
    String sql = "SELECT CLICODIGO FROM CLIENTES WHERE CLIIDENTIFICACION = ?";
    
    try {
        PreparedStatement pstmt = conexion.prepareStatement(sql);
        pstmt.setString(1, identificacionCliente);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            cliCodigo = rs.getString("CLICODIGO");
        }
        
        rs.close();
        pstmt.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al obtener el CLICODIGO del cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    return cliCodigo;
}


private double calcularDescuento(double subtotal) {
    double descuento = 0;

    // Aplicar descuentos según el rango del subtotal
    if (subtotal >= 10 && subtotal <= 20.99) {
        descuento = subtotal * 0.05; // Descuento del 5%
    } else if (subtotal >= 21 && subtotal <= 29.99) {
        descuento = subtotal * 0.10; // Descuento del 10%
    } else if (subtotal > 30) {
        descuento = subtotal * 0.30; // Descuento del 30%
    }

    return descuento;
}





 private void limpiarCamposFactura() {
        // Limpiar campos relacionados a la factura después de insertar
        campoNumeroFactura.setText("");
        // Limpiar otros campos de la interfaz que consideres necesario
    }
    private boolean validarIdentificacion(String identificacion) {
        return identificacion.matches("\\d{10}");
    }

    private void buscarCliente() {
        String identificacion = campoIdentificacion.getText();

        if (identificacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La identificación del cliente es obligatoria.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validarIdentificacion(identificacion)) {
            JOptionPane.showMessageDialog(this, "La identificación debe tener exactamente 10 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT CLINOMBRE, CLIDIRECCION, CLICELULAR, CLIEMAIL " +
                     "FROM CLIENTES " +
                     "WHERE CLIIDENTIFICACION = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, identificacion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                campoNombre.setText(rs.getString("CLINOMBRE"));
                campoDireccion.setText(rs.getString("CLIDIRECCION"));
                campoCelular.setText(rs.getString("CLICELULAR"));
                campoEmail.setText(rs.getString("CLIEMAIL"));
            } else {
                JOptionPane.showMessageDialog(this, "Cliente no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                limpiarCamposCliente();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCamposCliente() {
        campoNombre.setText("");
        campoDireccion.setText("");
        campoCelular.setText("");
        campoEmail.setText("");
    }

    private void agregarProducto() {
        String codigoProducto = campoCodigoProducto.getText();
        String cantidadTexto = campoCantidad.getText();

        if (codigoProducto.isEmpty() || cantidadTexto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El código del producto y la cantidad son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean productoExistente = false;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (codigoProducto.equals(modeloTabla.getValueAt(i, 0))) {
                int cantidadExistente = (int) modeloTabla.getValueAt(i, 1);
                double precioUnitario = (double) modeloTabla.getValueAt(i, 3);
                double precioTotalExistente = (double) modeloTabla.getValueAt(i, 4);

                cantidadExistente += cantidad;
                double nuevoPrecioTotal = precioUnitario * cantidadExistente;

                modeloTabla.setValueAt(cantidadExistente, i, 1);
                modeloTabla.setValueAt(nuevoPrecioTotal, i, 4);

                productoExistente = true;
                break;
            }
        }

        if (!productoExistente) {
            String sql = "SELECT PRODESCRIPCION, PROPRECIOUM FROM PRODUCTOS WHERE PROCODIGO = ?";
            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                pstmt.setString(1, codigoProducto);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String descripcion = rs.getString("PRODESCRIPCION").trim();
                    double precioUnitario = rs.getDouble("PROPRECIOUM");
                    double precioTotal = precioUnitario * cantidad;

                    modeloTabla.addRow(new Object[]{codigoProducto, cantidad, descripcion, precioUnitario, precioTotal, "Editar"});

                    calcularTotales();
                } else {
                    JOptionPane.showMessageDialog(this, "Producto no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al buscar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarProducto() {
        int filaSeleccionada = tablaProductos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una fila para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este producto?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(filaSeleccionada);
            calcularTotales();
        }
    }

  public void calcularTotales() {
    double subtotal = 0;

    for (int i = 0; i < modeloTabla.getRowCount(); i++) {
        subtotal += (double) modeloTabla.getValueAt(i, 4); // Suma de los precios totales
    }

    campoSubtotal.setText(String.format("%.2f", subtotal));

    double descuento = 0;

    // Aplicar descuentos según el rango del subtotal
    if (subtotal >= 10 && subtotal <= 20.99) {
        descuento = subtotal * 0.05; // Descuento del 5%
    } else if (subtotal >= 21 && subtotal <= 29.99) {
        descuento = subtotal * 0.10; // Descuento del 10%
    } else if (subtotal > 30) {
        descuento = subtotal * 0.30; // Descuento del 30%
    }

    campoDescuento.setText(String.format("%.2f", descuento));

    // Calcular el subtotal después del descuento
    double subtotalConDescuento = subtotal - descuento;

    // Calcular el IVA sobre el subtotal con descuento
    double iva = porcentajeIVA * subtotalConDescuento / 100;

    // Calcular el precio final sumando el subtotal con descuento y el IVA
    double precioFinal = subtotalConDescuento + iva;
    
    campoPrecioFinal.setText(String.format("%.2f", precioFinal));
}

    private void actualizarPorcentajeIVA() {
        String seleccion = (String) comboBoxIVA.getSelectedItem();
        switch (seleccion) {
            case "8%":
                porcentajeIVA = 8;
                break;
            case "12%":
                porcentajeIVA = 12;
                break;
            case "15%":
                porcentajeIVA = 15;
                break;
            default:
                porcentajeIVA = 0;
                break;
        }
        calcularTotales();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Facturador facturador = new Facturador();
                facturador.setVisible(true);
            }
        });
    }
}

class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof String && value.equals("Editar")) {
            setText((String) value);
        } else {
            setText("");
        }
        return this;
    }
}

class ButtonEditorEditar extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private JButton button;
    private String label;
    private boolean isPushed;
    private JTable table;

    public ButtonEditorEditar(JCheckBox checkBox) {
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        if (value instanceof String && value.equals("Editar")) {
            label = (String) value;
        } else {
            label = "";
        }
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            int selectedRow = table.getSelectedRow();
            String codigoProducto = (String) table.getValueAt(selectedRow, 0);
            int cantidad = (int) table.getValueAt(selectedRow, 1);
            double precioUnitario = (double) table.getValueAt(selectedRow, 3);

            EditarProductoDialog dialog = new EditarProductoDialog(codigoProducto, cantidad, precioUnitario);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                int nuevaCantidad = dialog.getCantidad();
                double nuevoPrecioUnitario = dialog.getPrecioUnitario();
                double nuevoPrecioTotal = nuevaCantidad * nuevoPrecioUnitario;

                table.setValueAt(nuevaCantidad, selectedRow, 1);
                table.setValueAt(nuevoPrecioUnitario, selectedRow, 3);
                table.setValueAt(nuevoPrecioTotal, selectedRow, 4);

                ((Facturador) SwingUtilities.getWindowAncestor(table)).calcularTotales();
            }
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
    }
}

class EditarProductoDialog extends JDialog {
    private boolean confirmed;
    private JTextField campoCantidad;
    private JTextField campoPrecioUnitario;

    public EditarProductoDialog(String codigoProducto, int cantidad, double precioUnitario) {
        setTitle("Editar Producto");
        setModal(true);
        setLayout(new BorderLayout());
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel panelCampos = new JPanel();
        panelCampos.setLayout(new GridLayout(3, 2, 10, 10));
        panelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelCampos.add(new JLabel("Código Producto:"));
        panelCampos.add(new JLabel(codigoProducto));
        panelCampos.add(new JLabel("Cantidad:"));
        campoCantidad = new JTextField(String.valueOf(cantidad));
        panelCampos.add(campoCantidad);
        panelCampos.add(new JLabel("Precio Unitario:"));
        campoPrecioUnitario = new JTextField(String.valueOf(precioUnitario));
        panelCampos.add(campoPrecioUnitario);

        add(panelCampos, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton botonAceptar = new JButton("Aceptar");
botonAceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = true;
                dispose();
            }
        });
        panelBotones.add(botonAceptar);

        JButton botonCancelar = new JButton("Cancelar");
        botonCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmed = false;
                dispose();
            }
        });
        panelBotones.add(botonCancelar);

        add(panelBotones, BorderLayout.SOUTH);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getCantidad() {
        try {
            return Integer.parseInt(campoCantidad.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double getPrecioUnitario() {
        try {
            return Double.parseDouble(campoPrecioUnitario.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
