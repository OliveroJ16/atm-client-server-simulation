import java.net.Socket;
import java.io.*;

public class ClienteHandler extends Thread {
    private Socket socket;
    private Banco banco;
    private DataInputStream in;
    private DataOutputStream out;
    private BancoServidorGUI gui;

    public ClienteHandler(Socket socket, Banco banco) {
        this(socket, banco, null);
    }

    public ClienteHandler(Socket socket, Banco banco, BancoServidorGUI gui) {
        this.socket = socket;
        this.banco = banco;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            
            log("ATM conectado desde: " + socket.getInetAddress());

            boolean continuar = true;
            while (continuar) {
                String solicitud = in.readUTF();

                if (solicitud.equalsIgnoreCase("salir")) {
                    log("← ATM se desconecta: " + socket.getInetAddress());
                    out.writeUTF("ADIOS:Conexión terminada");
                    break;
                }

                String[] partes = solicitud.split(":");
                if (partes.length != 2) {
                    out.writeUTF("ERROR:Formato incorrecto. Use Codigo:Monto");
                    continue;
                }

                String codigoCliente = partes[0].trim();
                double monto;

                try {
                    monto = Double.parseDouble(partes[1].trim());
                    if (monto <= 0) {
                        out.writeUTF("ERROR:El monto debe ser mayor a cero");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    out.writeUTF("ERROR:Monto inválido");
                    continue;
                }

                log("→ Procesando: Cliente=" + codigoCliente + ", Monto=$" + monto);

                // Procesar retiro
                String respuesta = banco.retirarDinero(codigoCliente, monto);

                out.writeUTF(respuesta);
                out.flush();
            }

        } catch (IOException e) {
            log("✗ Error con ATM: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
                log("Conexión cerrada con ATM: " + socket.getInetAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Método auxiliar para loguear tanto en GUI como en consola
    private void log(String mensaje) {
        if (gui != null) {
            gui.agregarLog(mensaje);
        } else {
            System.out.println(mensaje);
        }
    }
}
