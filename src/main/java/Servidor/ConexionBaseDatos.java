package Servidor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos{


    //sudo systemctl start postgresql           iniciar el servidor
    //psql -h localhost -U postgres -d banco    acceder a la base de datos

    private static final String URL = "jdbc:postgresql://localhost:5432/banco";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin123";

    private static Connection conexion;

    public static Connection obtenerConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                conexion = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (SQLException e) {
                System.err.println("Error al conectar a la base de datos: " + e.getMessage());
                throw e;
            }
        }
        return conexion;
    }
}
