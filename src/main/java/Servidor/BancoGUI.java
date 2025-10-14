package Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BancoGUI extends JFrame {

    protected JButton btnIniciar;
    protected JTextField txtPuerto;
    protected JTextArea txtLog;

    public BancoGUI() {
        setTitle("Servidor Banco - Sistema ATM");
        setSize(400, 400);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior - Control del servidor
        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        panelControl.add(new JLabel("Puerto:"));
        txtPuerto = new JTextField("5000", 10);
        panelControl.add(txtPuerto);

        btnIniciar = new JButton("Iniciar Servidor");
        panelControl.add(btnIniciar);

        // Panel inferior - Log
        txtLog = new JTextArea(15, 30);
        txtLog.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtLog);

        panelPrincipal.add(panelControl, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    public void log(String mensaje) {
        txtLog.append(mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void registrarEventoIniciar(ActionListener listener) {
        btnIniciar.addActionListener(listener);
    }
}
