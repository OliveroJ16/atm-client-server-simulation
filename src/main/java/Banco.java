import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Banco {

    // Evita condiciones de carrera al manejar múltiples clientes
    private final HashMap<Integer, Object> locks = new HashMap<>();

    private Object obtenerLock(int codigoCliente) {
        synchronized (locks) {
            return locks.computeIfAbsent(codigoCliente, k -> new Object());
        }
    }

    public String retirarDinero(String codigoClienteStr, double monto) {
        int codigoCliente;

        // Validar que el código sea un número entero
        try {
            codigoCliente = Integer.parseInt(codigoClienteStr);
        } catch (NumberFormatException e) {
            return "ERROR: El código de cliente debe ser numérico.";
        }

        Object lock = obtenerLock(codigoCliente);

        synchronized (lock) {
            try (Connection conexion = ConexionBaseDatos.obtenerConexion()) {

                conexion.setAutoCommit(false);

                // Consulta del saldo actual
                String sqlSelect = "SELECT saldo FROM clientes WHERE id = ?";
                try (PreparedStatement psSelect = conexion.prepareStatement(sqlSelect)) {
                    psSelect.setInt(1, codigoCliente);
                    ResultSet rs = psSelect.executeQuery();

                    if (!rs.next()) {
                        conexion.rollback();
                        return "ERROR: El código de cliente no existe en el banco.";
                    }

                    double saldoActual = rs.getDouble("saldo");

                    if (saldoActual < monto) {
                        conexion.rollback();
                        return "ERROR: Fondos insuficientes. Saldo actual: $" + String.format("%.2f", saldoActual);
                    }

                    // Actualizar el saldo
                    String sqlUpdate = "UPDATE clientes SET saldo = saldo - ? WHERE id = ?";
                    try (PreparedStatement psUpdate = conexion.prepareStatement(sqlUpdate)) {
                        psUpdate.setDouble(1, monto);
                        psUpdate.setInt(2, codigoCliente);
                        psUpdate.executeUpdate();
                    }

                    conexion.commit();

                    double nuevoSaldo = saldoActual - monto;
                    return "EXITO: Retiro exitoso. Saldo restante: $" + String.format("%.2f", nuevoSaldo);

                } catch (SQLException e) {
                    conexion.rollback();
                    System.err.println("❌ Error SQL en la transacción: " + e.getMessage());
                    return "ERROR: Fallo en la transacción.";
                }

            } catch (SQLException e) {
                System.err.println("❌ Error al conectar con la base de datos: " + e.getMessage());
                return "ERROR: No se pudo conectar con la base de datos.";
            }
        }
    }
}
