package Servidor;

import java.net.Socket;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Cliente extends Thread {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private BancoGUI interfaz;

    // Locks internos para control de concurrencia
    private final HashMap<Integer, Object> locks = new HashMap<>();

    public Cliente(Socket socket, BancoGUI interfaz) {
        this.socket = socket;
        this.interfaz = interfaz;
    }

    @Override
    public void run() {
        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());

            if (interfaz != null) {
                interfaz.log("ATM conectado: " + socket.getInetAddress());
            }

            while (true) {
                String solicitud = entrada.readUTF().trim();
                if (solicitud.equalsIgnoreCase("salir")) {
                    salida.writeUTF("CONEXIÓN TERMINADA");
                    break;
                }

                String[] partes = solicitud.split(":");
                int codigoCliente = Integer.parseInt(partes[0].trim());
                double monto = Double.parseDouble(partes[1].trim());

                if (interfaz != null) {
                    interfaz.log("Procesando: Cliente =" + codigoCliente + ", Monto = $" + monto);
                }

                String respuesta = retirarDinero(codigoCliente, monto);
                salida.writeUTF(respuesta);
                salida.flush();
            }

        } catch (Exception e) {
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

    // ------------------- Métodos de "Banco" incorporados -------------------

    private Object obtenerLock(int codigoCliente) {
        synchronized (locks) {
            locks.putIfAbsent(codigoCliente, new Object());
            return locks.get(codigoCliente);
        }
    }

    public String retirarDinero(int codigoCliente, double monto) {
        synchronized (obtenerLock(codigoCliente)) {
            try (Connection conexion = ConexionBaseDatos.obtenerConexion()) {

                conexion.setAutoCommit(false);

                PreparedStatement psSelect = conexion.prepareStatement(
                        "SELECT saldo FROM clientes WHERE id = ?");
                psSelect.setInt(1, codigoCliente);
                ResultSet rs = psSelect.executeQuery();

                if (!rs.next()) {
                    conexion.rollback();
                    return "ERROR: Cliente no encontrado.";
                }

                double saldoActual = rs.getDouble("saldo");

                if (saldoActual < monto) {
                    conexion.rollback();
                    return "ERROR: Fondos insuficientes.";
                }

                PreparedStatement psUpdate = conexion.prepareStatement(
                        "UPDATE clientes SET saldo = saldo - ? WHERE id = ?");
                psUpdate.setDouble(1, monto);
                psUpdate.setInt(2, codigoCliente);
                psUpdate.executeUpdate();

                conexion.commit();
                return "EXITO: Retiro completado. Saldo restante: $" + String.format("%.2f", saldoActual - monto);

            } catch (SQLException e) {
                return "ERROR: Problema durante la transacción.";
            }
        }
    }
}
