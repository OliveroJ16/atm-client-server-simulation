package Servidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Banco {

    private final HashMap<Integer, Object> locks = new HashMap<>();

    private Object obtenerLock(int codigoCliente) {
        synchronized (locks) {
            if (!locks.containsKey(codigoCliente)) {
                locks.put(codigoCliente, new Object());
            }
            return locks.get(codigoCliente);
        }
    }

    public String retirarDinero(int codigoCliente, double monto) {

        Object lock = obtenerLock(codigoCliente);

        synchronized (lock) {
            try (Connection conexion = ConexionBaseDatos.obtenerConexion()) {

                //Para el control manual de la transaccion
                conexion.setAutoCommit(false);

                // Consultar saldo
                String sql = "SELECT saldo FROM clientes WHERE id = ?";
                try (PreparedStatement psSelect = conexion.prepareStatement(sql)) {
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

                    // Actualizar saldo
                    String sqlUpdate = "UPDATE clientes SET saldo = saldo - ? WHERE id = ?";
                    try (PreparedStatement psUpdate = conexion.prepareStatement(sqlUpdate)) {
                        psUpdate.setDouble(1, monto);
                        psUpdate.setInt(2, codigoCliente);
                        psUpdate.executeUpdate();
                    }

                    //Confirmar transaccion
                    conexion.commit();

                    return "EXITO: Retiro completado. Saldo restante: $" + String.format("%.2f", saldoActual - monto);

                } catch (SQLException e) {
                    conexion.rollback();
                    return "ERROR: Ocurrió un problema durante la transacción.";
                }

            } catch (SQLException e) {
                return "ERROR: No se pudo conectar con el banco.";
            }
        }
    }
}
