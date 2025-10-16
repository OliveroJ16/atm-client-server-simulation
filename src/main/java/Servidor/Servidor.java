package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//Comando para ver IP privada: hostname -I en linux

public class Servidor extends Thread {

    private BancoGUI interfaz;
    private ServerSocket serverSocket;
    private int puerto;

    public Servidor(BancoGUI interfaz, int puerto) {
        this.interfaz = interfaz;
        this.puerto = puerto;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(puerto);
            interfaz.mostrarMensaje("=== SERVIDOR BANCO INICIADO ===");
            interfaz.mostrarMensaje("Escuchando en puerto " + puerto);
            interfaz.mostrarMensaje("Esperando conexiones de ATMs...\n");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clienteSocket = serverSocket.accept();
                    interfaz.mostrarMensaje("Nuevo Cliente ATM conectado: " + clienteSocket.getInetAddress());

                    Cliente cliente = new Cliente(clienteSocket, interfaz);
                    cliente.start();
                } catch (IOException e) {
                    interfaz.mostrarMensaje("Error al aceptar conexión: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            interfaz.mostrarMensaje("Error al iniciar servidor: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
