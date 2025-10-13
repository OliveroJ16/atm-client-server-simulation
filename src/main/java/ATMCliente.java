import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ATMCliente extends JFrame {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String servidor;
    private int puerto;

    // Componentes de la interfaz
    private JTextField txtServidor;
    private JTextField txtPuerto;
    private JButton btnConectar;
    private JButton btnDesconectar;
    private JTextField txtCodigo;
    private JTextField txtMonto;
    private JButton btnRetirar;
    private JTextArea txtAreaLog;
    private JLabel lblEstado;
    private boolean conectado = false;

    public ATMCliente() {
        setTitle("Cajero Automático (ATM)");
        setSize(450, 500);
        setResizable(false); // ventana fija
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // -------- Panel de conexión --------
        JPanel panelConexion = new JPanel(new GridBagLayout());
        panelConexion.setBorder(BorderFactory.createTitledBorder("Conexión al Banco"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Etiqueta Servidor
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelConexion.add(new JLabel("Servidor:"), gbc);

        // Campo Servidor
        txtServidor = new JTextField("localhost");
        txtServidor.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelConexion.add(txtServidor, gbc);

        // Etiqueta Puerto
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        panelConexion.add(new JLabel("Puerto:"), gbc);

        // Campo Puerto
        txtPuerto = new JTextField("5000");
        txtPuerto.setPreferredSize(new Dimension(80, 25));
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelConexion.add(txtPuerto, gbc);

        // Botones Conectar/Desconectar centrados en fila nueva
        JPanel panelBotonesConexion = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnConectar = new JButton("Conectar");
        btnConectar.setPreferredSize(new Dimension(115, 25));
        btnConectar.setBackground(new Color(46, 125, 50));
        btnConectar.setForeground(Color.WHITE);
        btnConectar.addActionListener(e -> conectar());

        btnDesconectar = new JButton("Desconectar");
        btnDesconectar.setPreferredSize(new Dimension(115, 25));
        btnDesconectar.setBackground(new Color(211, 47, 47));
        btnDesconectar.setForeground(Color.WHITE);
        btnDesconectar.setEnabled(false);
        btnDesconectar.addActionListener(e -> desconectar());

        panelBotonesConexion.add(btnConectar);
        panelBotonesConexion.add(btnDesconectar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        panelConexion.add(panelBotonesConexion, gbc);

        // -------- Panel de transacción --------
        JPanel panelTransaccion = new JPanel(new GridBagLayout());
        panelTransaccion.setBorder(BorderFactory.createTitledBorder("Realizar Retiro"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Código Cliente
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

        // Monto
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

        // Botón Retiro centrado
        // Botón Retiro centrado
        btnRetirar = new JButton("Realizar Retiro");
        btnRetirar.setPreferredSize(new Dimension(150, 20));
        btnRetirar.setBackground(new Color(25, 118, 210));
        btnRetirar.setForeground(Color.WHITE);
        btnRetirar.setFont(new Font("Arial", Font.BOLD, 14));
        btnRetirar.setEnabled(false);
        btnRetirar.addActionListener(e -> realizarRetiro());

        // Panel para centrar el botón
        JPanel panelBotonRetiro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotonRetiro.add(btnRetirar);

        // Ajustes de GridBag para centrarlo
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // ocupa las dos columnas
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER; // centrado
        panelTransaccion.add(panelBotonRetiro, gbc);

        // -------- Panel de log --------
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Registro de Transacciones"));
        txtAreaLog = new JTextArea(10, 40);
        txtAreaLog.setEditable(false);
        txtAreaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        panelLog.add(scrollPane, BorderLayout.CENTER);

        // -------- Panel de estado --------
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblEstado = new JLabel("Estado: Desconectado");
        lblEstado.setForeground(Color.RED);
        lblEstado.setFont(new Font("Arial", Font.BOLD, 12));
        panelEstado.add(lblEstado);

        // -------- Panel principal --------
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(panelConexion, BorderLayout.NORTH);
        panelSuperior.add(panelTransaccion, BorderLayout.CENTER);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelLog, BorderLayout.CENTER);
        panelPrincipal.add(panelEstado, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void conectar() {
        servidor = txtServidor.getText().trim();
        String puertoStr = txtPuerto.getText().trim();

        if (servidor.isEmpty() || puertoStr.isEmpty()) {
            mostrarError("Debe ingresar servidor y puerto");
            return;
        }

        try {
            puerto = Integer.parseInt(puertoStr);
            socket = new Socket(servidor, puerto);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            conectado = true;
            actualizarEstadoConexion(true);
            agregarLog("✓ Conectado al Banco en " + servidor + ":" + puerto);

            // Iniciar hilo receptor
            Thread receptor = new Thread(new ReceptorMensajes());
            receptor.start();

        } catch (NumberFormatException e) {
            mostrarError("El puerto debe ser un número válido");
        } catch (IOException e) {
            mostrarError("Error al conectar: " + e.getMessage());
        }
    }

    private void desconectar() {
        try {
            if (conectado && out != null) {
                out.writeUTF("salir");
                out.flush();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            conectado = false;
            actualizarEstadoConexion(false);
            agregarLog("✓ Desconectado del Banco");

        } catch (IOException e) {
            agregarLog("✗ Error al desconectar: " + e.getMessage());
        }
    }

    private void realizarRetiro() {
        String codigo = txtCodigo.getText().trim();
        String montoStr = txtMonto.getText().trim();

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

            String solicitud = codigo + ":" + monto;
            out.writeUTF(solicitud);
            out.flush();

            agregarLog("→ Solicitud enviada: Cliente=" + codigo + ", Monto=$" + monto);

            // Limpiar campos
            txtCodigo.setText("");
            txtMonto.setText("");

        } catch (NumberFormatException e) {
            mostrarError("El monto debe ser un número válido");
        } catch (IOException e) {
            mostrarError("Error al enviar solicitud: " + e.getMessage());
            desconectar();
        }
    }

    private void actualizarEstadoConexion(boolean conectado) {
        this.conectado = conectado;
        btnConectar.setEnabled(!conectado);
        btnDesconectar.setEnabled(conectado);
        txtServidor.setEnabled(!conectado);
        txtPuerto.setEnabled(!conectado);
        txtCodigo.setEnabled(conectado);
        txtMonto.setEnabled(conectado);
        btnRetirar.setEnabled(conectado);

        if (conectado) {
            lblEstado.setText("Estado: Conectado");
            lblEstado.setForeground(new Color(46, 125, 50));
        } else {
            lblEstado.setText("Estado: Desconectado");
            lblEstado.setForeground(Color.RED);
        }
    }

    private void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtAreaLog.append(mensaje + "\n");
            txtAreaLog.setCaretPosition(txtAreaLog.getDocument().getLength());
        });
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Clase interna para recibir mensajes del servidor
    private class ReceptorMensajes implements Runnable {
        @Override
        public void run() {
            try {
                while (conectado) {
                    String respuesta = in.readUTF();

                    if (respuesta.startsWith("EXITO:")) {
                        agregarLog("✓ " + respuesta.substring(7));
                    } else if (respuesta.startsWith("ERROR:")) {
                        agregarLog("✗ " + respuesta.substring(7));
                    } else if (respuesta.startsWith("ADIOS:")) {
                        agregarLog("← " + respuesta.substring(7));
                        break;
                    } else {
                        agregarLog("← " + respuesta);
                    }
                }
            } catch (IOException e) {
                if (conectado) {
                    agregarLog("✗ Conexión perdida con el servidor");
                    SwingUtilities.invokeLater(() -> actualizarEstadoConexion(false));
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ATMCliente cliente = new ATMCliente();
            cliente.setVisible(true);
        });
    }
}