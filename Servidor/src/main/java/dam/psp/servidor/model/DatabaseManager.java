package dam.psp.servidor.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Clase que gestiona la conexión a la base de datos y las operaciones de usuario (registro y login).
 */
public class DatabaseManager {

    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("POSTGRES_URL");
    private static final String USER = dotenv.get("POSTGRES_USER");
    private static final String PASSWORD = dotenv.get("POSTGRES_PASSWORD");

    private Connection connection;

    /**
     * Constructor de la clase DatabaseManager. Establece una conexión a la base de datos.
     */
    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión a la base de datos establecida.");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Inserta un nuevo usuario en la base de datos con su nombre de usuario y contraseña.
     * La contraseña es almacenada de forma segura utilizando hash.
     *
     * @param username El nombre de usuario que se va a insertar.
     * @param password La contraseña asociada al usuario.
     */
    public void insertUser(String username, String password) {
        String hashedPassword = hashPassword(password);  // Hashea la contraseña antes de almacenarla
        String insertSQL = "INSERT INTO usuarios (username, password) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Inserción realizada. Filas afectadas: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Error al insertar en la base de datos: " + e.getMessage());
        }
    }

    /**
     * Intenta hacer login con el nombre de usuario y la contraseña proporcionada.
     * Verifica la contraseña ingresada comparándola con la versión hash almacenada en la base de datos.
     *
     * @param username El nombre de usuario con el que se intenta iniciar sesión.
     * @param password La contraseña proporcionada para el login.
     * @return true si las credenciales son correctas, false en caso contrario.
     */
    public boolean logInUser(String username, String password) {
        String selectSQL = "SELECT password FROM usuarios WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");
                System.out.println("USUARIO Y CONTRASEÑA CORRECTAS");

                // Verifica la contraseña con el hash almacenado
                return BCrypt.checkpw(password, hashedPassword);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("USUARIO O CONTRASEÑA INCORRECTAS");

        return false;
    }

    /**
     * Hashea la contraseña utilizando el algoritmo BCrypt.
     *
     * @param password La contraseña a hashear.
     * @return La contraseña hasheada.
     */
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Cierra la conexión con la base de datos, si está abierta.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión a la base de datos cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión a la base de datos: " + e.getMessage());
        }
    }

    /**
     * Método principal para probar la clase DatabaseManager.
     * Inserta un usuario de prueba y luego cierra la conexión.
     *
     * @param args Argumentos de la línea de comandos (no se usan).
     */
    public static void main(String[] args) {

        DatabaseManager dbManager = new DatabaseManager();
        dbManager.insertUser("", "");
        dbManager.closeConnection();
    }
}
