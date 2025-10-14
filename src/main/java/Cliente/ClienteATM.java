package Cliente;

import java.io.*;
import java.net.Socket;

public class ClienteATM extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String servidor;
    private int puerto;
    private ClienteGUI interfaz;
    private boolean conectado = false;

    public ClienteATM(ClienteGUI interfaz, String servidor, int puerto) {
        this.interfaz = interfaz;
        this.servidor = servidor;
        this.puerto = puerto;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(servidor, puerto);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            conectado = true;
            interfaz.actualizarEstadoConexion(true);
            interfaz.agregarLog("CONECTADO AL BANCO: " + servidor + ":" + puerto);

            // Recibir mensajes del servidor
            while (conectado) {
                String respuesta = in.readUTF();

                if (respuesta.startsWith("EXITO:")) {
                    interfaz.agregarLog("--" + respuesta.substring(7));
                } else if (respuesta.startsWith("ERROR:")) {
                    interfaz.agregarLog("" + respuesta.substring(7));
                } else if (respuesta.startsWith("ADIOS:")) {
                    interfaz.agregarLog("--" + respuesta.substring(7));
                    break;
                } else {
                    interfaz.agregarLog("--" + respuesta);
                }
            }

        } catch (IOException e) {
            if (conectado) {
                interfaz.agregarLog("**CONEXION PERDIDA CON EL SERVIDOR**");
                interfaz.actualizarEstadoConexion(false);
            } else {
                interfaz.mostrarError("Error al conectar: " + e.getMessage());
            }
        }
    }

    public void enviarRetiro(String codigo, double monto) {
        try {
            String solicitud = codigo + ":" + monto;
            out.writeUTF(solicitud);
            out.flush();
            interfaz.agregarLog("--Solicitud enviada: Cliente=" + codigo + ", Monto=$" + monto);
        } catch (IOException e) {
            interfaz.mostrarError("Error al enviar solicitud: " + e.getMessage());
            desconectar();
        }
    }

    public void desconectar() {
        try {
            if (conectado && out != null) {
                out.writeUTF("salir");
                out.flush();
            }

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            conectado = false;
            interfaz.actualizarEstadoConexion(false);
            interfaz.agregarLog("DESCONECTADO DEL BANCO");

        } catch (IOException e) {
            interfaz.agregarLog("--Error al desconectar: " + e.getMessage());
        }
    }

    public boolean isConectado() {
        return conectado;
    }
}