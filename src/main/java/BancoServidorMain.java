import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class BancoServidorMain {
    public static void main(String[] args) {
        Banco banco = new Banco(); // instancia del banco
        int puerto = 5000;

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor Banco escuchando en el puerto " + puerto);

            while (true) {
                Socket clienteSocket = serverSocket.accept(); // espera conexión de un ATM
                System.out.println("Nuevo ATM conectado: " + clienteSocket.getInetAddress());

                // Crear y lanzar un hilo por cliente
                Cliente hilo = new Cliente(clienteSocket, banco);
                hilo.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
