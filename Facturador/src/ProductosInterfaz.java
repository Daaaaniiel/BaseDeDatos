import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.*;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.EmptyBorder;



public class ProductosInterfaz extends JFrame {

    private JTextField txtCodigo, txtDescripcion, txtUnidadMedida, txtIngresos, txtEgresos, txtCostoUM, txtPrecioUM, txtStatus;
    public JTable tablaProductos;

    private Connection establecerConexion() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String usuario = "postgres";
        String contraseña = "Daniel2023";
        return DriverManager.getConnection(url, usuario, contraseña);
    }

    public ProductosInterfaz() {
        setTitle("Interfaz de Productos");
        setSize(1000, 900); // Cambiar tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana en la pantalla

        // Crear un JPanel personalizado para contener los componentes de la interfaz con imagen de fondo
        JPanel panelConFondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Cargar la imagen de fondo desde un archivo
                ImageIcon imagenFondo = new ImageIcon("C:/Users/USUARIO/Desktop/hola.jpg");
                // Dibujar la imagen en el panel de fondo
                g.drawImage(imagenFondo.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panelConFondo.setLayout(new BorderLayout()); // Establecer el diseño del panel

        // Crear un JPanel adicional para contener el título y la tabla
        JPanel panelConTitulo = new JPanel(new BorderLayout());
        panelConTitulo.setOpaque(false); // Hacer que el panel sea transparente para que se vea el fondo

        // Primer título "CAMPOVERDE SOFT"
JLabel lblTitulo1 = new JLabel("CAMPOVERDE SOFT   "); // Agrega espacios en blanco antes del texto
lblTitulo1.setHorizontalAlignment(SwingConstants.RIGHT); // Alineación a la izquierda
lblTitulo1.setFont(new Font("Times New Roman", Font.BOLD, 36)); // Establecer una fuente grande y negrita
lblTitulo1.setForeground(Color.BLACK);

JLabel lblTitulo2 = new JLabel("Gestión de Productos   ");
lblTitulo2.setHorizontalAlignment(SwingConstants.RIGHT);
lblTitulo2.setFont(new Font("Times New Roman", Font.BOLD, 24)); // Establecer una fuente grande y negrita
lblTitulo2.setForeground(Color.BLACK);

// Crear un JPanel para contener los títulos
JPanel panelTitulos = new JPanel(new GridLayout(2, 1)); // GridLayout de 2 filas y 1 columna
panelTitulos.setOpaque(false); // Hacer que el panel sea transparente para que se vea el fondo

// Agregar los títulos al panel de títulos
panelTitulos.add(lblTitulo1);
panelTitulos.add(lblTitulo2);

// Agregar el panel de títulos al panelConTitulo en la parte superior
panelConTitulo.add(panelTitulos, BorderLayout.NORTH);

        // Crear un JPanel para la tabla con GridBagLayout
        JPanel panelTabla = new JPanel(new GridBagLayout());
        panelTabla.setOpaque(false); // Hacer que el panel sea transparente para que se vea el fondo

        // Configurar las restricciones para la tabla en el GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE; // No permitir que la tabla se expanda en ambas direcciones
        gbc.anchor = GridBagConstraints.CENTER; // Centrar la tabla en el panel

        // Crear la tabla para mostrar los productos
        tablaProductos = new JTable(); // Modifica esta línea

        // Crear JScrollPane para la tabla
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.setPreferredSize(new Dimension(800, 400)); // Establecer tamaño preferido para el JScrollPane

        // Agregar el JScrollPane al panelTabla con las restricciones
        panelTabla.add(scrollPane, gbc);

        // Mostrar la tabla de productos directamente al abrir la interfaz
        mostrarTablaProductos(tablaProductos);

        // Agregar panelTabla al panelConTitulo
        panelConTitulo.add(panelTabla, BorderLayout.CENTER);

        // Agregar panelConTitulo al panel principal con imagen de fondo
        panelConFondo.add(panelConTitulo, BorderLayout.CENTER);

        // Agregar el JPanel con imagen de fondo al contenedor principal de la ventana
        add(panelConFondo);

        // Crear botones para realizar acciones
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.X_AXIS)); // Usar BoxLayout para los botones
        panelBotones.setBorder(new EmptyBorder(10, 10, 10, 10)); // Agregar márgenes
        panelBotones.setBackground(Color.WHITE);

        JButton btnInsertar = new JButton("Insertar Registro");
        JButton btnActualizar = new JButton("Actualizar Registro");
        JButton btnEliminar = new JButton("Eliminar Registro");
        JButton btnBuscarProducto = new JButton("Buscar Producto");

        Color azul = new Color(30, 144, 255); // Color azul (RGB: 30, 144, 255)
        Color blanco = Color.WHITE; // Color blanco

        btnInsertar.setBackground(azul);
        btnInsertar.setForeground(blanco);
        btnActualizar.setBackground(azul);
        btnActualizar.setForeground(blanco);
        btnEliminar.setBackground(azul);
        btnEliminar.setForeground(blanco);
        btnBuscarProducto.setBackground(azul);
        btnBuscarProducto.setForeground(blanco);

    Dimension btnSize = new Dimension(250, 90); // Ajustar el tamaño preferido de los botones
btnInsertar.setPreferredSize(btnSize);
btnActualizar.setPreferredSize(btnSize);
btnEliminar.setPreferredSize(btnSize);
btnBuscarProducto.setPreferredSize(btnSize);

// Ajustar el tamaño de la fuente de los botones
Font btnFont = new Font("Arial", Font.BOLD, 18); // Cambiar la fuente y el tamaño de la letra de los botones
btnInsertar.setFont(btnFont);
btnActualizar.setFont(btnFont);
btnEliminar.setFont(btnFont);
btnBuscarProducto.setFont(btnFont);

        btnInsertar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirVentanaInsertar();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarRegistro();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarRegistro();
            }
        });

        btnBuscarProducto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarProducto();
            }
        });

        // Agregar botones al panel
        panelBotones.add(btnInsertar);
        panelBotones.add(Box.createHorizontalStrut(10)); // Agregar espacio entre los botones
        panelBotones.add(btnActualizar);
        panelBotones.add(Box.createHorizontalStrut(10));
        panelBotones.add(btnEliminar);
        panelBotones.add(Box.createHorizontalStrut(10));
        panelBotones.add(btnBuscarProducto);

        // Agregar el panel de botones en la parte inferior de la ventana
        add(panelBotones, BorderLayout.SOUTH);

        // Configurar el renderizador personalizado para la tabla
        tablaProductos.setDefaultRenderer(Object.class, new BotonOrdenamientoRenderer());
    }

    public void mostrarTablaProductos(JTable tabla) {
        Connection connection = null;
        try {
            connection = establecerConexion();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM Productos");

            // Construir el modelo de tabla a partir del ResultSet
            DefaultTableModel model = buildTableModel(resultSet);

            // Asignar el modelo a la tabla
            tabla.setModel(model);

            // Configurar el ordenamiento de la tabla
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
            tabla.setRowSorter(sorter);

            // Ajustar el tamaño de las columnas
            for (int i = 0; i < tabla.getColumnCount(); i++) {
                tabla.getColumnModel().getColumn(i).setPreferredWidth(150); // Ajusta el ancho de cada columna
            }

            // Ajustar el tamaño de las filas
            tabla.setRowHeight(30); // Ajusta la altura de cada fila

            // Ajustar el tamaño de la fuente
            tabla.setFont(new Font("Arial", Font.PLAIN, 14)); // Cambia la fuente y el tamaño de la letra

            // Establecer el color de fondo de la tabla
            tabla.setBackground(Color.WHITE);

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al mostrar la tabla de productos: " + ex.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static DefaultTableModel buildTableModel(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }
        Vector<Vector<Object>> data = new Vector<>();
        while (resultSet.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(resultSet.getObject(columnIndex));
            }
            data.add(vector);
        }
        return new DefaultTableModel(data, columnNames);
    }

    private void abrirVentanaInsertar() {
        ProductosInterfaz interfaz = new ProductosInterfaz();
        InsertarRegistroVentana ventanaInsertar = new InsertarRegistroVentana(interfaz, this);
        ventanaInsertar.setVisible(true);
    }


   private void actualizarRegistro() {
    String codigoProducto = JOptionPane.showInputDialog(this, "Ingrese el código del producto a actualizar:");
    if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
        return; // Si el usuario cancela o no ingresa un código, salir del método
    }

    Connection connection = null;
    try {
        connection = establecerConexion();

        // Consulta SQL para obtener la información del producto con el código proporcionado
        String sql = "SELECT * FROM Productos WHERE PROCODIGO = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, codigoProducto);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            // Si se encuentra el producto, llenar los campos de texto con su información
            JTextField txtCodigo = new JTextField(resultSet.getString("PROCODIGO"));
            JTextField txtDescripcion = new JTextField(resultSet.getString("PRODESCRIPCION"));
            JTextField txtUnidadMedida = new JTextField(resultSet.getString("PROUNIDADMEDIDA"));
            JTextField txtIngresos = new JTextField(resultSet.getBigDecimal("PROINGRESOS").toString());
            JTextField txtEgresos = new JTextField(resultSet.getBigDecimal("PROEGRESOS").toString());
            JTextField txtCostoUM = new JTextField(resultSet.getBigDecimal("PROCOSTOUM").toString());
            JTextField txtPrecioUM = new JTextField(resultSet.getBigDecimal("PROPRECIOUM").toString());
            JTextField txtStatus = new JTextField(resultSet.getString("PROSTATUS"));

            JPanel panel = new JPanel(new GridLayout(8, 2));
            panel.add(new JLabel("Código:"));
            panel.add(txtCodigo);
            panel.add(new JLabel("Descripción:"));
            panel.add(txtDescripcion);
            panel.add(new JLabel("Unidad de Medida:"));
            panel.add(txtUnidadMedida);
            panel.add(new JLabel("Ingresos:"));
            panel.add(txtIngresos);
            panel.add(new JLabel("Egresos:"));
            panel.add(txtEgresos);
            panel.add(new JLabel("Costo UM:"));
            panel.add(txtCostoUM);
            panel.add(new JLabel("Precio UM:"));
            panel.add(txtPrecioUM);
            panel.add(new JLabel("Status:"));
            panel.add(txtStatus);

            int confirmacion = JOptionPane.showConfirmDialog(this, panel, "Actualizar Producto", JOptionPane.OK_CANCEL_OPTION);
            if (confirmacion == JOptionPane.OK_OPTION) {
                // Realizar la actualización de la información
                String updateSql = "UPDATE Productos SET PRODESCRIPCION = ?, PROUNIDADMEDIDA = ?, PROINGRESOS = ?, PROEGRESOS = ?, PROCOSTOUM = ?, PROPRECIOUM = ?, PROSTATUS = ?, PROSALDOINICIAL = ?, PROSALDOFINAL = ? WHERE PROCODIGO = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                updateStatement.setString(1, txtDescripcion.getText());
                updateStatement.setString(2, txtUnidadMedida.getText());
                updateStatement.setBigDecimal(3, new BigDecimal(txtIngresos.getText()));
                updateStatement.setBigDecimal(4, new BigDecimal(txtEgresos.getText()));
                updateStatement.setBigDecimal(5, new BigDecimal(txtCostoUM.getText()));
                updateStatement.setBigDecimal(6, new BigDecimal(txtPrecioUM.getText()));
                updateStatement.setString(7, txtStatus.getText());
                updateStatement.setInt(8, 0); // Saldo inicial en 0
                updateStatement.setInt(9, 0); // Saldo final en 0
                updateStatement.setString(10, codigoProducto);
                int filasActualizadas = updateStatement.executeUpdate();
                if (filasActualizadas > 0) {
                    JOptionPane.showMessageDialog(this, "Información del producto actualizada correctamente.");
                    // Actualizar la tabla de productos después de la actualización
                    mostrarTablaProductos(tablaProductos);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo actualizar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // Si no se encuentra el producto, mostrar un mensaje de error
            JOptionPane.showMessageDialog(this, "No se encontró ningún producto con el código especificado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    } catch (ClassNotFoundException | SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al buscar el producto: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}





  private void eliminarRegistro() {
    String codigoProducto = JOptionPane.showInputDialog(this, "Ingrese el código del producto a eliminar:");
    if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
        return; // Si el usuario cancela o no ingresa un código, salir del método
    }

    int confirmacion = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar el producto?", "Confirmar eliminación", JOptionPane.OK_CANCEL_OPTION);
    if (confirmacion == JOptionPane.OK_OPTION) {
        Connection connection = null;
        try {
            connection = establecerConexion();

            // Preparar la consulta SQL para actualizar el estado del registro en la tabla Productos
            String sql = "UPDATE Productos SET PROSTATUS = 'INA' WHERE PROCODIGO = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, codigoProducto);

            // Ejecutar la consulta
            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(this, "Registro marcado como inactivo correctamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo marcar como inactivo el registro.");
            }

            // Después de eliminar el registro, actualizamos la tabla de productos
            mostrarTablaProductos(tablaProductos);

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar el estado del registro: " + ex.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}


 private void buscarProducto() {
    String codigoProducto = JOptionPane.showInputDialog(this, "Ingrese el código del producto a buscar:");
    if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
        return; // Si el usuario cancela o no ingresa un código, salir del método
    }

    Connection connection = null;
    try {
        connection = establecerConexion();

        // Consulta SQL para obtener la información del producto con el código proporcionado
        String sql = "SELECT * FROM Productos WHERE PROCODIGO = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, codigoProducto);
        ResultSet resultSet = statement.executeQuery();

        // Mostrar la información del producto si se encuentra
        if (resultSet.next()) {
            StringBuilder productoInfo = new StringBuilder();
            productoInfo.append("Código: ").append(resultSet.getString("PROCODIGO")).append("\n");
            productoInfo.append("Descripción: ").append(resultSet.getString("PRODESCRIPCION")).append("\n");
            productoInfo.append("Unidad de Medida: ").append(resultSet.getString("PROUNIDADMEDIDA")).append("\n");
            productoInfo.append("Ingresos: ").append(resultSet.getBigDecimal("PROINGRESOS")).append("\n");
            productoInfo.append("Egresos: ").append(resultSet.getBigDecimal("PROEGRESOS")).append("\n");
            productoInfo.append("Costo UM: ").append(resultSet.getBigDecimal("PROCOSTOUM")).append("\n");
            productoInfo.append("Precio UM: ").append(resultSet.getBigDecimal("PROPRECIOUM")).append("\n");
            productoInfo.append("Estado: ").append(resultSet.getString("PROSTATUS")).append("\n");

            // Crear un panel para mostrar la información del producto y la imagen
            JPanel panel = new JPanel(new BorderLayout());

            // Crear un área de texto para la información del producto
            JTextArea textArea = new JTextArea(productoInfo.toString());
            textArea.setEditable(false);
            panel.add(new JScrollPane(textArea), BorderLayout.CENTER);

            // Recuperar la imagen del producto y mostrarla si existe
            byte[] imagenBytes = resultSet.getBytes("PROIMAGEN");
            if (imagenBytes != null && imagenBytes.length > 0) {
                ImageIcon originalIcon = new ImageIcon(imagenBytes);
                Image originalImage = originalIcon.getImage();
                // Escalar la imagen a un tamaño más pequeño (por ejemplo, 100x100 píxeles)
                Image scaledImage = originalImage.getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                JLabel imageLabel = new JLabel(scaledIcon);
                panel.add(imageLabel, BorderLayout.EAST);
            }

            // Mostrar el panel en un cuadro de diálogo
            JOptionPane.showMessageDialog(this, panel, "Información del Producto", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Producto no encontrado.");
        }

    } catch (ClassNotFoundException | SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al buscar el producto: " + ex.getMessage());
    } finally {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

    public static void main(String[] args) {
        ProductosInterfaz interfaz = new ProductosInterfaz();
        interfaz.setVisible(true);
    }
}
class BotonOrdenamientoRenderer implements TableCellRenderer {

    private static final ImageIcon ICONO_ASCENDENTE = new ImageIcon("ascendente.png");
    private static final ImageIcon ICONO_DESCENDENTE = new ImageIcon("descendente.png");

    private final JButton boton;

    public BotonOrdenamientoRenderer() {
        this.boton = new JButton();
        this.boton.setMargin(new Insets(0, 0, 0, 0));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof JButton) {
            return (JButton) value;
        } else {
            if (isSelected) {
                boton.setForeground(table.getSelectionForeground());
                boton.setBackground(table.getSelectionBackground());
            } else {
                boton.setForeground(table.getForeground());
                boton.setBackground(UIManager.getColor("Button.background"));
            }

            boton.setText(value == null ? "" : value.toString());
            return boton;
        }
    }
}
   

    class InsertarRegistroVentana extends JFrame {
private ProductosInterfaz interfazProductos;
    private JTextField txtCodigo, txtDescripcion, txtUnidadMedida, txtIngresos, txtEgresos, txtCostoUM, txtPrecioUM;
    private Connection establecerConexion() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String usuario = "postgres";
        String contraseña = "Daniel2023";
        return DriverManager.getConnection(url, usuario, contraseña);
    }

    public InsertarRegistroVentana(JFrame parent,ProductosInterfaz interfazProductos) {
        this.interfazProductos = interfazProductos;
        setTitle("Insertar Registro");
        setSize(400, 300);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayout(9, 2)); // Aumentamos el número de filas para incluir el botón de cargar imagen

        txtCodigo = new JTextField();
        txtDescripcion = new JTextField();
        txtUnidadMedida = new JTextField();
        txtIngresos = new JTextField();
        txtEgresos = new JTextField();
        txtCostoUM = new JTextField();
        txtPrecioUM = new JTextField();

        panelPrincipal.add(new JLabel("Código:"));
        panelPrincipal.add(txtCodigo);
        panelPrincipal.add(new JLabel("Descripción:"));
        panelPrincipal.add(txtDescripcion);
        panelPrincipal.add(new JLabel("Unidad de Medida:"));
        panelPrincipal.add(txtUnidadMedida);
        panelPrincipal.add(new JLabel("Ingresos:"));
        panelPrincipal.add(txtIngresos);
        panelPrincipal.add(new JLabel("Egresos:"));
        panelPrincipal.add(txtEgresos);
        panelPrincipal.add(new JLabel("Costo UM:"));
        panelPrincipal.add(txtCostoUM);
        panelPrincipal.add(new JLabel("Precio UM:"));
        panelPrincipal.add(txtPrecioUM);

        // Botón para cargar imagen
        JButton btnCargarImagen = new JButton("Cargar Imagen");
        btnCargarImagen.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            cargarImagen(txtCodigo.getText()); // Pasamos el código del producto desde el campo correspondiente
        } catch (IOException ex) {
            Logger.getLogger(InsertarRegistroVentana.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
});        panelPrincipal.add(btnCargarImagen);

        JButton btnInsertar = new JButton("Insertar");
        btnInsertar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertarRegistro();
            }
        });
        panelPrincipal.add(btnInsertar);

        add(panelPrincipal);
    }
private void insertarRegistro() {
    Connection connection = null;
    try {
        connection = establecerConexion();

        // Validar formato de código (P-0001)
        if (!txtCodigo.getText().matches("^P-\\d{4}$")) {
            JOptionPane.showMessageDialog(this, "El código debe estar en el formato P-0001.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar el campo Descripción
        String descripcion = txtDescripcion.getText();
        if (descripcion.isEmpty() || !descripcion.matches("^[A-Za-zÑñÁÉÍÓÚáéíóú ]+$")) {
            JOptionPane.showMessageDialog(this, "La descripción debe contener solo letras y espacios.");
            return;
        }

        // Validar el campo Unidad de Medida
        String unidadMedida = txtUnidadMedida.getText();
        if (unidadMedida.isEmpty() || !unidadMedida.matches("^(QQ|CAJ|KG|PAQ|LIT|UNI)$")) {
            JOptionPane.showMessageDialog(this, "La unidad de medida debe ser una de las siguientes: QQ, CAJ, KG, PAQ, LIT, UNI.");
            return;
        }

        // Validar que los egresos sean menores o iguales que los ingresos
        BigDecimal ingresos = new BigDecimal(txtIngresos.getText());
        BigDecimal egresos = new BigDecimal(txtEgresos.getText());
        if (egresos.compareTo(ingresos) > 0) {
            JOptionPane.showMessageDialog(this, "Los egresos deben ser menores o iguales que los ingresos.");
            return;
        }

        // Validar que el costo de la unidad de medida sea menor que el precio por unidad de medida
        BigDecimal costoUM = new BigDecimal(txtCostoUM.getText());
        BigDecimal precioUM = new BigDecimal(txtPrecioUM.getText());
        if (costoUM.compareTo(precioUM) >= 0) {
            JOptionPane.showMessageDialog(this, "El costo de la unidad de medida debe ser menor que el precio por unidad de medida.");
            return;
        }

        // Validar los campos numéricos
        try {
            if (ingresos.compareTo(BigDecimal.ZERO) < 0 || egresos.compareTo(BigDecimal.ZERO) < 0 ||
                    costoUM.compareTo(BigDecimal.ZERO) < 0 || precioUM.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Los valores numéricos deben ser mayores o iguales a cero.");
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Los valores numéricos ingresados no son válidos.");
            return;
        }

        // Preparar la consulta SQL para insertar un nuevo registro en la tabla Productos
        String sql = "INSERT INTO Productos (PROCODIGO, PRODESCRIPCION, PROUNIDADMEDIDA, PROINGRESOS, PROEGRESOS, PROCOSTOUM, PROPRECIOUM, PROAJUSTES, PROSALDOINICIAL, PROSALDOFINAL, PROSTATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0, 0, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, txtCodigo.getText());
        statement.setString(2, txtDescripcion.getText());
        statement.setString(3, txtUnidadMedida.getText());
        statement.setBigDecimal(4, ingresos);
        statement.setBigDecimal(5, egresos);
        statement.setBigDecimal(6, costoUM);
        statement.setBigDecimal(7, precioUM);
        statement.setBigDecimal(8, BigDecimal.ZERO); // PROAJUSTES
        statement.setString(9, "ACT"); // Estado por defecto: activo

        // Ejecutar la consulta
        int filasInsertadas = statement.executeUpdate();
        if (filasInsertadas > 0) {
            JOptionPane.showMessageDialog(this, "Registro insertado correctamente.");
            interfazProductos.mostrarTablaProductos(interfazProductos.tablaProductos);
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo insertar el registro.");
        }

    } catch (ClassNotFoundException | SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al insertar el registro: " + ex.getMessage());
    } finally {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
 private void cargarImagen(String codigoProducto) throws IOException {
    if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Debe ingresar un código de producto válido.");
        return;
    }

    // Abrir el selector de archivos para cargar la imagen
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    int result = fileChooser.showOpenDialog(this);
    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        try {
            // Convertir la imagen en un array de bytes
            byte[] imageData = Files.readAllBytes(selectedFile.toPath());
            
            // Actualizar la base de datos con el array de bytes de la imagen
            Connection connection = establecerConexion();
            String sql = "UPDATE Productos SET proimagen = ? WHERE PROCODIGO = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBytes(1, imageData);
            statement.setString(2, codigoProducto);
            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas > 0) {
                JOptionPane.showMessageDialog(this, "Imagen cargada correctamente para el producto con código: " + codigoProducto);
                interfazProductos.mostrarTablaProductos(interfazProductos.tablaProductos);
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo cargar la imagen para el producto con código: " + codigoProducto);
            }
        } catch (ClassNotFoundException | SQLException  ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar la imagen: " + ex.getMessage());
        }
    }
}
}
    