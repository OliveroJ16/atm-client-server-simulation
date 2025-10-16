package Servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BancoGUI extends JFrame implements ActionListener {

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
        txtPuerto = new JTextField("8080", 10);
        panelControl.add(txtPuerto);

        btnIniciar = new JButton("Iniciar Servidor");
        btnIniciar.addActionListener(this);
        panelControl.add(btnIniciar);

        // Panel inferior - Log
        txtLog = new JTextArea(15, 30);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(txtLog);

        panelPrincipal.add(panelControl, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    @Override
    public void actionPerformed(ActionEvent evento) {
        String puertoStr = txtPuerto.getText().trim();
        if (puertoStr.isEmpty()) {
            mostrarError("Debe ingresar un puerto");
            return;
        }

        try {
            int puerto = Integer.parseInt(puertoStr);

            Servidor servidor = new Servidor(this, puerto);
            servidor.start();

            btnIniciar.setEnabled(false);
            txtPuerto.setEnabled(false);

        } catch (NumberFormatException ex) {
            mostrarError("El puerto debe ser un número válido");
        }
    }

    public void mostrarMensaje(String mensaje) {
        txtLog.append(mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
