package Servidor;

import java.net.Socket;
import java.io.*;

public class Cliente extends Thread {
    private Socket socket;
    private Banco banco;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private BancoGUI interfaz;

    public Cliente(Socket socket, Banco banco, BancoGUI interfaz) {
        this.socket = socket;
        this.banco = banco;
        this.interfaz = interfaz;
    }

    @Override
    public void run() {
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());

            // Log conexión inicial
            if (interfaz != null) {
                interfaz.log("ATM conectado: " + socket.getInetAddress());
            }

            while (true) {
                String solicitud = entrada.readUTF().trim();

                if (solicitud.equalsIgnoreCase("salir")) {
                    if (interfaz != null) {
                        interfaz.log("ATM desconectado: " + socket.getInetAddress());
                    }
                    salida.writeUTF("CONEXIÓN TERMINADA");
                    break;
                }

                String[] partes = solicitud.split(":");

                // Validar código cliente
                int codigoCliente;
                try {
                    codigoCliente = Integer.parseInt(partes[0].trim());
                    if (codigoCliente <= 0) {
                        salida.writeUTF("ERROR: Código de cliente inválido");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    salida.writeUTF("ERROR: Código de cliente inválido");
                    continue;
                }

                // Validar monto
                double monto;
                try {
                    monto = Double.parseDouble(partes[1].trim());
                    if (monto <= 0) {
                        salida.writeUTF("ERROR: Monto inválido");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    salida.writeUTF("ERROR: Monto inválido");
                    continue;
                }

                interfaz.log("Procesando: Cliente =" + codigoCliente + ", Monto = $" + monto);

                // Procesar retiro
                String respuesta = banco.retirarDinero(codigoCliente, monto);

                salida.writeUTF(respuesta);
                salida.flush();
            }

        } catch (IOException e) {
            if (interfaz != null) {
                interfaz.log("Error con ATM: " + e.getMessage());
            }
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                    if (interfaz != null) {
                        interfaz.log("Conexión cerrada: " + socket.getInetAddress());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
