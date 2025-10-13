import javax.swing.*;
import java.awt.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketTimeoutException;

public class BancoServidorGUI extends JFrame {
    private Banco banco;
    private ServerSocket serverSocket;
    private JTextArea txtAreaLog;
    private JButton btnIniciar;
    private JTextField txtPuerto;
    private Thread hiloServidor;

    public BancoServidorGUI() {
        setTitle("Servidor Banco - Sistema ATM");
        setSize(450, 500);
        setResizable(false); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        banco = new Banco();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior
        JPanel panelControl = new JPanel(new GridBagLayout());
        panelControl.setBorder(BorderFactory.createTitledBorder("Control del Servidor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelControl.add(new JLabel("Puerto:"), gbc);
        
        gbc.gridx = 1;
        txtPuerto = new JTextField("5000", 10);
        panelControl.add(txtPuerto, gbc);

        gbc.gridx = 2;
        btnIniciar = new JButton("Iniciar Servidor");
        btnIniciar.setBackground(new Color(46, 125, 50));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 12));
        btnIniciar.addActionListener(e -> iniciarServidor());
        panelControl.add(btnIniciar, gbc);

        // Log
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Registro de Actividad"));
        
        txtAreaLog = new JTextArea(15, 50);
        txtAreaLog.setEditable(false);
        txtAreaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        panelLog.add(scrollPane, BorderLayout.CENTER);

        // Ensamblar
        panelPrincipal.add(panelControl, BorderLayout.NORTH);
        panelPrincipal.add(panelLog, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    private void iniciarServidor() {
        String puertoStr = txtPuerto.getText().trim();
        if (puertoStr.isEmpty()) {
            mostrarError("Debe ingresar un puerto");
            return;
        }

        try {
            int puerto = Integer.parseInt(puertoStr);
            
            hiloServidor = new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(puerto);
                    serverSocket.setSoTimeout(1000); // evita bloqueo indefinido
                    
                    SwingUtilities.invokeLater(() -> {
                        agregarLog("===========================================");
                        agregarLog("  SERVIDOR BANCO INICIADO");
                        agregarLog("===========================================");
                        agregarLog("✓ Escuchando en puerto " + puerto);
                        agregarLog("✓ Esperando conexiones de ATMs...\n");
                        btnIniciar.setEnabled(false);
                        txtPuerto.setEnabled(false);
                    });

                    while (!Thread.currentThread().isInterrupted()) {
                        try {
                            Socket clienteSocket = serverSocket.accept();
                            SwingUtilities.invokeLater(() -> 
                                agregarLog("→ Nuevo ATM conectado: " + clienteSocket.getInetAddress())
                            );
                            ClienteHandler hiloCliente = new ClienteHandler(clienteSocket, banco, this);
                            hiloCliente.start();
                        } catch (SocketTimeoutException e) {
                            // Permite revisar interrupciones cada segundo
                        }
                    }

                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> agregarLog("✗ Error en el servidor: " + e.getMessage()));
                }
            });
            
            hiloServidor.start();

        } catch (NumberFormatException e) {
            mostrarError("El puerto debe ser un número válido");
        }
    }

    public void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtAreaLog.append(mensaje + "\n");
            txtAreaLog.setCaretPosition(txtAreaLog.getDocument().getLength());
        });
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BancoServidorGUI servidor = new BancoServidorGUI();
            servidor.setVisible(true);
        });
    }
}
