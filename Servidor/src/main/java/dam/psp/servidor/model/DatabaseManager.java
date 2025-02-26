package dam.psp.servidor.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;



public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://caboose.proxy.rlwy.net:49394/railway";
    private static final String USER = "postgres";
    private static final String PASSWORD = "cINodhnXvleKWLCIBwMlLMLkNnAKBRJY";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión a la base de datos establecida.");
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

    public void insertUser(String username, String password) {
        String hashedPassword = hashPassword(password);
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

    public  boolean logInUser(String username, String password){
        String selectSQL = "SELECT password FROM usuarios WHERE username = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                String hashedPassword = resultSet.getString("password");
                System.out.println("USUARIO Y CONTRASEÑA CORRECTAS");

                return BCrypt.checkpw(password, hashedPassword);
            }
           
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("USUARIO O CONTRASEÑA INCORRECTAS");

            return false;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

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
}
