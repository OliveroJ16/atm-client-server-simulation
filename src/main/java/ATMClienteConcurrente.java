import java.net.Socket;
import java.io.*;

public class ATMClienteConcurrente {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int puerto = 5000;
        int numeroHilos = 100; // 100 clientes simultáneos
        int codigoCliente = 2;  // mismo cliente para probar concurrencia
        double montoSolicitado = 1.00; // $1 por retiro

        for (int i = 0; i < numeroHilos; i++) {
            Thread t = new Thread(() -> {
                try (Socket socket = new Socket(host, puerto);
                     DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                     DataInputStream in = new DataInputStream(socket.getInputStream())) {

                    // Enviar datos
                    out.writeInt(codigoCliente);
                    out.writeDouble(montoSolicitado);
                    out.flush();

                    // Recibir saldo restante
                    double saldoRestante = in.readDouble();
                    if (saldoRestante >= 0) {
                        System.out.println(Thread.currentThread().getName() + " Saldo restante: $" + saldoRestante);
                    } else {
                        System.out.println(Thread.currentThread().getName() + " Error en la transacción.");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "Hilo-" + i);
            t.start();
        }
    }
}
