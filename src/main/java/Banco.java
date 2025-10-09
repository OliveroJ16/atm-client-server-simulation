import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Banco{

    private final HashMap<Integer, Object> locks = new HashMap<>();

    private Object obtenerLock(int codigoCliente) {
        synchronized (locks) {
            if (!locks.containsKey(codigoCliente)) {
                locks.put(codigoCliente, new Object());
            }
            return locks.get(codigoCliente);
        }
    }

    public boolean retirarDinero(int codigoCliente, double monto) {
        Object lock = obtenerLock(codigoCliente);

        synchronized (lock) {
            try (Connection conexion = ConexionBaseDatos.obtenerConexion()) {
                conexion.setAutoCommit(false);

                String sqlSelect = "SELECT saldo FROM usuarios WHERE id = ?";
                try (PreparedStatement psSelect = conexion.prepareStatement(sqlSelect)) {
                    psSelect.setInt(1, codigoCliente);
                    ResultSet rs = psSelect.executeQuery();

                    if (!rs.next()) {
                        System.out.println("Cliente no existe: " + codigoCliente);
                        return false;
                    }

                    double saldoActual = rs.getDouble("saldo");

                    if (saldoActual < monto) {
                        System.out.println("Fondos insuficientes para cliente " + codigoCliente);
                        return false;
                    }

                    String sqlUpdate = "UPDATE usuarios SET saldo = saldo - ? WHERE id = ?";
                    try (PreparedStatement psUpdate = conexion.prepareStatement(sqlUpdate)) {
                        psUpdate.setDouble(1, monto);
                        psUpdate.setInt(2, codigoCliente);
                        psUpdate.executeUpdate();
                    }

                    conexion.commit();
                    System.out.println("Retiro exitoso de cliente " + codigoCliente + ". Saldo restante: " + (saldoActual - monto));
                    return true;

                } catch (SQLException e) {
                    conexion.rollback();
                    e.printStackTrace();
                    return false;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
