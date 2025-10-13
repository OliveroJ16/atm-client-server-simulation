package Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ClienteGUI extends JFrame {
    private ClienteATM cliente;

    // Componentes de la interfaz
    public JTextField txtServidor;
    public JTextField txtPuerto;
    public JButton btnConectar;
    public JButton btnDesconectar;
    private JTextField txtCodigo;
    private JTextField txtMonto;
    private JButton btnRetirar;
    private JTextArea txtAreaLog;

    public ClienteGUI() {
        setTitle("Cajero Automático (ATM)");
        setSize(450, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel de conexión
        JPanel panelConexion = new JPanel(new GridBagLayout());
        panelConexion.setBorder(BorderFactory.createTitledBorder("Conexión al Banco"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelConexion.add(new JLabel("Servidor:"), gbc);

        txtServidor = new JTextField("localhost");
        txtServidor.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelConexion.add(txtServidor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelConexion.add(new JLabel("Puerto:"), gbc);

        txtPuerto = new JTextField("5000");
        txtPuerto.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelConexion.add(txtPuerto, gbc);

        JPanel panelBotonesConexion = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnConectar = new JButton("Conectar");
        btnConectar.setPreferredSize(new Dimension(115, 25));
        btnConectar.setBackground(new Color(46, 125, 50));
        btnConectar.setForeground(Color.WHITE);

        btnDesconectar = new JButton("Desconectar");
        btnDesconectar.setPreferredSize(new Dimension(115, 25));
        btnDesconectar.setBackground(new Color(211, 47, 47));
        btnDesconectar.setForeground(Color.WHITE);
        btnDesconectar.setEnabled(false);

        panelBotonesConexion.add(btnConectar);
        panelBotonesConexion.add(btnDesconectar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panelConexion.add(panelBotonesConexion, gbc);

        // Panel de transacción
        JPanel panelTransaccion = new JPanel(new GridBagLayout());
        panelTransaccion.setBorder(BorderFactory.createTitledBorder("Realizar Retiro"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelTransaccion.add(new JLabel("Código Cliente:"), gbc);

        txtCodigo = new JTextField();
        txtCodigo.setPreferredSize(new Dimension(150, 25));
        txtCodigo.setEnabled(false);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTransaccion.add(txtCodigo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelTransaccion.add(new JLabel("Monto a Retirar:"), gbc);

        txtMonto = new JTextField();
        txtMonto.setPreferredSize(new Dimension(150, 25));
        txtMonto.setEnabled(false);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTransaccion.add(txtMonto, gbc);

        btnRetirar = new JButton("Realizar Retiro");
        btnRetirar.setPreferredSize(new Dimension(150, 20));
        btnRetirar.setBackground(new Color(25, 118, 210));
        btnRetirar.setForeground(Color.WHITE);
        btnRetirar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRetirar.setEnabled(false);

        JPanel panelBotonRetiro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotonRetiro.add(btnRetirar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelTransaccion.add(panelBotonRetiro, gbc);

        // Panel de log
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Registro de Transacciones"));
        txtAreaLog = new JTextArea(10, 40);
        txtAreaLog.setEditable(false);
        txtAreaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        panelLog.add(scrollPane, BorderLayout.CENTER);

        // Panel de estado
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelConexion, BorderLayout.NORTH);
        panelSuperior.add(panelTransaccion, BorderLayout.CENTER);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelLog, BorderLayout.CENTER);
        panelPrincipal.add(panelEstado, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    public void addConectarListener(ActionListener listener) {
        btnConectar.addActionListener(listener);
    }

    public void addDesconectarListener(ActionListener listener) {
        btnDesconectar.addActionListener(listener);
    }

    public void addRetirarListener(ActionListener listener) {
        btnRetirar.addActionListener(listener);
    }

    public void setCliente(ClienteATM cliente) {
        this.cliente = cliente;
    }

    public String getCodigoCliente() {
        return txtCodigo.getText().trim();
    }

    public String getMontoTexto() {
        return txtMonto.getText().trim();
    }

    public void limpiarCampos() {
        txtCodigo.setText("");
        txtMonto.setText("");
    }

    public void actualizarEstadoConexion(boolean conectado) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                btnConectar.setEnabled(!conectado);
                btnDesconectar.setEnabled(conectado);
                txtServidor.setEnabled(!conectado);
                txtPuerto.setEnabled(!conectado);
                txtCodigo.setEnabled(conectado);
                txtMonto.setEnabled(conectado);
                btnRetirar.setEnabled(conectado);
            }
        });
    }

    public void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                txtAreaLog.append(mensaje + "\n");
                txtAreaLog.setCaretPosition(txtAreaLog.getDocument().getLength());
            }
        });
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}