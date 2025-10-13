package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor extends Thread {

    private Banco banco;
    private BancoGUI gui;
    private ServerSocket serverSocket;
    private int puerto;

    public Servidor(BancoGUI gui, int puerto) {
        this.banco = new Banco();
        this.gui = gui;
        this.puerto = puerto;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(puerto);
            gui.log("SERVIDOR BANCO INICIADO");
            gui.log("Escuchando en puerto " + puerto);
            gui.log("Esperando conexiones de ATMs...\n");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clienteSocket = serverSocket.accept();
                    gui.log("Nuevo Cliente ATM conectado: " + clienteSocket.getInetAddress());

                    Cliente cliente = new Cliente(clienteSocket, banco, gui);
                    cliente.start();
                } catch (IOException e) {
                    gui.log("Error al aceptar conexión: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            gui.log("Error al iniciar servidor: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
