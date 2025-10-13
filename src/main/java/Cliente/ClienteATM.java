package Cliente;

import java.io.*;
import java.net.Socket;

public class ClienteATM extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String servidor;
    private int puerto;
    private ClienteGUI gui;
    private boolean conectado = false;

    public ClienteATM(ClienteGUI gui, String servidor, int puerto) {
        this.gui = gui;
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
            gui.actualizarEstadoConexion(true);
            gui.agregarLog("✓ Conectado al Banco en " + servidor + ":" + puerto);

            // Iniciar hilo receptor
            ReceptorMensajes receptor = new ReceptorMensajes();
            Thread hiloReceptor = new Thread(receptor);
            hiloReceptor.start();

        } catch (IOException e) {
            gui.mostrarError("Error al conectar: " + e.getMessage());
        }
    }

    public void enviarRetiro(String codigo, double monto) {
        try {
            String solicitud = codigo + ":" + monto;
            out.writeUTF(solicitud);
            out.flush();
            gui.agregarLog("→ Solicitud enviada: Cliente=" + codigo + ", Monto=$" + monto);
        } catch (IOException e) {
            gui.mostrarError("Error al enviar solicitud: " + e.getMessage());
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
            gui.actualizarEstadoConexion(false);
            gui.agregarLog("✓ Desconectado del Banco");

        } catch (IOException e) {
            gui.agregarLog("✗ Error al desconectar: " + e.getMessage());
        }
    }

    public boolean isConectado() {
        return conectado;
    }

    // Clase interna para recibir mensajes del servidor
    private class ReceptorMensajes implements Runnable {
        public void run() {
            try {
                while (conectado) {
                    String respuesta = in.readUTF();

                    if (respuesta.startsWith("EXITO:")) {
                        gui.agregarLog("✓ " + respuesta.substring(7));
                    } else if (respuesta.startsWith("ERROR:")) {
                        gui.agregarLog("✗ " + respuesta.substring(7));
                    } else if (respuesta.startsWith("ADIOS:")) {
                        gui.agregarLog("← " + respuesta.substring(7));
                        break;
                    } else {
                        gui.agregarLog("← " + respuesta);
                    }
                }
            } catch (IOException e) {
                if (conectado) {
                    gui.agregarLog("✗ Conexión perdida con el servidor");
                    gui.actualizarEstadoConexion(false);
                }
            }
        }
    }
}