package Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BancoGUI extends JFrame {
    
    // Componentes públicos o accesibles
    protected JButton btnIniciar;
    protected JTextField txtPuerto;
    protected JTextArea txtLog;

    public BancoGUI() {
        setTitle("Servidor Banco - Sistema ATM");
        setSize(450, 500);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior - Control del servidor
        JPanel panelControl = new JPanel(new GridBagLayout());
        panelControl.setBorder(BorderFactory.createTitledBorder("Control del Servidor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Etiqueta Puerto
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelControl.add(new JLabel("Puerto:"), gbc);

        // Campo Puerto
        gbc.gridx = 1;
        txtPuerto = new JTextField("5000", 10);
        panelControl.add(txtPuerto, gbc);

        // Botón Iniciar
        gbc.gridx = 2;
        btnIniciar = new JButton("Iniciar Servidor");
        btnIniciar.setBackground(new Color(46, 125, 50));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 12));
        panelControl.add(btnIniciar, gbc);

        // Panel inferior - Log
        JPanel panelLog = new JPanel(new BorderLayout());
        panelLog.setBorder(BorderFactory.createTitledBorder("Registro de Actividad"));

        txtLog = new JTextArea(15, 50);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtLog);
        panelLog.add(scrollPane, BorderLayout.CENTER);

        // Unir todo
        panelPrincipal.add(panelControl, BorderLayout.NORTH);
        panelPrincipal.add(panelLog, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    // Método para agregar texto al área de log
    public void log(String mensaje) {
        txtLog.append(mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void addIniciarListener(ActionListener listener) {
        btnIniciar.addActionListener(listener);
    }
}