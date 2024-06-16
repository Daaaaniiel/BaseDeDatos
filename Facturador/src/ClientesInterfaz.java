import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ClientesInterfaz extends JFrame {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtIdentificacion;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtCelular;
    private JTextField txtEmail;
    private JTextField txtTipo;

    public ClientesInterfaz() {
        setTitle("Agregar Cliente");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new GridLayout(9, 2, 10, 10));

        JLabel lblCodigo = new JLabel("Código:");
        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblIdentificacion = new JLabel("Identificación:");
        JLabel lblDireccion = new JLabel("Dirección:");
        JLabel lblTelefono = new JLabel("Teléfono:");
        JLabel lblCelular = new JLabel("Celular:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblTipo = new JLabel("Tipo:");

        txtCodigo = new JTextField();
        txtNombre = new JTextField();
        txtIdentificacion = new JTextField();
        txtDireccion = new JTextField();
        txtTelefono = new JTextField();
        txtCelular = new JTextField();
        txtEmail = new JTextField();
        txtTipo = new JTextField();

        panelPrincipal.add(lblCodigo);
        panelPrincipal.add(txtCodigo);
        panelPrincipal.add(lblNombre);
        panelPrincipal.add(txtNombre);
        panelPrincipal.add(lblIdentificacion);
        panelPrincipal.add(txtIdentificacion);
        panelPrincipal.add(lblDireccion);
        panelPrincipal.add(txtDireccion);
        panelPrincipal.add(lblTelefono);
        panelPrincipal.add(txtTelefono);
        panelPrincipal.add(lblCelular);
        panelPrincipal.add(txtCelular);
        panelPrincipal.add(lblEmail);
        panelPrincipal.add(txtEmail);
        panelPrincipal.add(lblTipo);
        panelPrincipal.add(txtTipo);

        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        // Acción del botón Guardar
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String codigo = txtCodigo.getText();
                String nombre = txtNombre.getText();
                String identificacion = txtIdentificacion.getText();
                String direccion = txtDireccion.getText();
                String telefono = txtTelefono.getText();
                String celular = txtCelular.getText();
                String email = txtEmail.getText();
                String tipo = txtTipo.getText();
                String status = "ACT";  // Establece el status por defecto como ACT

                if (codigo.isEmpty() || nombre.isEmpty() || identificacion.isEmpty() || direccion.isEmpty()
                        || telefono.isEmpty() || celular.isEmpty() || tipo.isEmpty()) {
                    JOptionPane.showMessageDialog(ClientesInterfaz.this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection connection = null;
                try {
                    connection = establecerConexion(); // Método para establecer la conexión con la base de datos
                    String sql = "INSERT INTO CLIENTES (CLICODIGO, CLINOMBRE, CLIIDENTIFICACION, CLIDIRECCION, CLITELEFONO, CLICELULAR, CLIEMAIL, CLITIPO, CLISTATUS) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, codigo);
                    statement.setString(2, nombre);
                    statement.setString(3, identificacion);
                    statement.setString(4, direccion);
                    statement.setString(5, telefono);
                    statement.setString(6, celular);
                    statement.setString(7, email);
                    statement.setString(8, tipo);
                    statement.setString(9, status);

                    int filasInsertadas = statement.executeUpdate();
                    if (filasInsertadas > 0) {
                        JOptionPane.showMessageDialog(ClientesInterfaz.this, "Cliente agregado correctamente.");
                        limpiarCampos();
                    } else {
                        JOptionPane.showMessageDialog(ClientesInterfaz.this, "No se pudo agregar el cliente.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ClientesInterfaz.this, "Error al agregar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        });

        // Acción del botón Cancelar
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra la ventana sin realizar ninguna acción
            }
        });

        getContentPane().add(panelPrincipal, BorderLayout.CENTER);
        getContentPane().add(panelBotones, BorderLayout.SOUTH);
    }

    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtIdentificacion.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtCelular.setText("");
        txtEmail.setText("");
        txtTipo.setText("");
    }

    private Connection establecerConexion() throws ClassNotFoundException, SQLException {
        // Método para establecer la conexión con la base de datos
        // Cambia estos detalles según tu configuración de base de datos
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String usuario = "postgres";
        String contrasena = "Daniel2023";
        
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClientesInterfaz ventana = new ClientesInterfaz();
                ventana.setVisible(true);
            }
        });
    }
}
