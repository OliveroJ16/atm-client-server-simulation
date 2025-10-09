import java.net.Socket;
import java.io.*;

public class Cliente extends Thread {
    private Socket socket;
    private Banco banco; // instancia del banco que maneja retiros

    public Cliente(Socket socket, Banco banco) {
        this.socket = socket;
        this.banco = banco;
    }

    @Override
    public void run() {
        try (
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            // Leer datos enviados por el ATM
            int codigoCliente = in.readInt();
            double monto = in.readDouble();
            System.out.println("Cliente " + codigoCliente + " solicita: $" + monto);

            // Retirar dinero de manera concurrente y segura
            boolean exito = banco.retirarDinero(codigoCliente, monto);
            double saldoRestante = -1;

            if (exito) {
                System.out.println("Bien");
            }

            // Enviar saldo restante al cliente (-1 si hubo error)
            out.writeDouble(saldoRestante);
            out.flush();

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
