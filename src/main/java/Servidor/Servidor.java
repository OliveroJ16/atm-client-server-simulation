package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//ip privada: 192.168.0.11
//Comando para ver IP privada: hostname -I

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
            interfaz.log("SERVIDOR BANCO INICIADO");
            interfaz.log("Escuchando en puerto " + puerto);
            interfaz.log("Esperando conexiones de ATMs...\n");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clienteSocket = serverSocket.accept();
                    interfaz.log("Nuevo Cliente ATM conectado: " + clienteSocket.getInetAddress());

                    Cliente cliente = new Cliente(clienteSocket, interfaz);
                    cliente.start();
                } catch (IOException e) {
                    interfaz.log("Error al aceptar conexión: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            interfaz.log("Error al iniciar servidor: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
