package Cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClienteGUI extends JFrame implements ActionListener {
    private ClienteATM cliente;

    protected JTextField txtServidor;
    protected JTextField txtPuerto;
    private JTextField txtCodigo;
    private JTextField txtMonto;
    protected JButton btnConectar;
    protected JButton btnDesconectar;
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelConexion.add(new JLabel("Servidor:"), gbc);

        txtServidor = new JTextField("localhost");
        txtServidor.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelConexion.add(txtServidor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panelConexion.add(new JLabel("Puerto:"), gbc);

        txtPuerto = new JTextField("8080");
        txtPuerto.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelConexion.add(txtPuerto, gbc);

        JPanel panelBotonesConexion = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnConectar = new JButton("Conectar");
        btnConectar.addActionListener(this);
        btnDesconectar = new JButton("Desconectar");
        btnDesconectar.addActionListener(this);
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
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelTransaccion.add(new JLabel("Código Cliente:"), gbc);

        txtCodigo = new JTextField();
        txtCodigo.setPreferredSize(new Dimension(150, 25));
        txtCodigo.setEnabled(false);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTransaccion.add(txtCodigo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panelTransaccion.add(new JLabel("Monto a Retirar:"), gbc);

        txtMonto = new JTextField();
        txtMonto.setPreferredSize(new Dimension(150, 25));
        txtMonto.setEnabled(false);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelTransaccion.add(txtMonto, gbc);

        btnRetirar = new JButton("Realizar Retiro");
        btnRetirar.addActionListener(this);
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
        txtAreaLog = new JTextArea(10, 40);
        txtAreaLog.setEditable(false);
        txtAreaLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        panelLog.add(scrollPane, BorderLayout.CENTER);

        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelConexion, BorderLayout.NORTH);
        panelSuperior.add(panelTransaccion, BorderLayout.CENTER);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelLog, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        Object source = evento.getSource();

        if (source == btnConectar) {
            String servidor = txtServidor.getText().trim();
            String puertoStr = txtPuerto.getText().trim();

            if (servidor.isEmpty() || puertoStr.isEmpty()) {
                mostrarError("Debe ingresar servidor y puerto");
                return;
            }

            try {
                int puerto = Integer.parseInt(puertoStr);
                cliente = new ClienteATM(this, servidor, puerto);
                cliente.start();
                setCliente(cliente);

            } catch (NumberFormatException ex) {
                mostrarError("El puerto debe ser un número válido");
            }
        }

        else if (source == btnDesconectar) {
            if (cliente != null) {
                cliente.desconectar();
                cliente = null;
            }
        }

 
        else if (source == btnRetirar) {
            String codigo = getCodigoCliente();
            String montoStr = getMontoTexto();

            if (codigo.isEmpty() || montoStr.isEmpty()) {
                mostrarError("Debe ingresar código y monto");
                return;
            }

            try {
                double monto = Double.parseDouble(montoStr);

                if (monto <= 0) {
                    mostrarError("El monto debe ser mayor a cero");
                    return;
                }

                if (cliente == null || !cliente.isConectado()) {
                    mostrarError("No hay conexión activa");
                    return;
                }

                cliente.enviarRetiro(codigo, monto);
                limpiarCampos();

            } catch (NumberFormatException ex) {
                mostrarError("El monto debe ser un número válido");
            }
        }
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
        btnConectar.setEnabled(!conectado);
        btnDesconectar.setEnabled(conectado);
        txtServidor.setEnabled(!conectado);
        txtPuerto.setEnabled(!conectado);
        txtCodigo.setEnabled(conectado);
        txtMonto.setEnabled(conectado);
        btnRetirar.setEnabled(conectado);
    }

    public void agregarLog(String mensaje) {
        txtAreaLog.append(mensaje + "\n");
        txtAreaLog.setCaretPosition(txtAreaLog.getDocument().getLength());
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
