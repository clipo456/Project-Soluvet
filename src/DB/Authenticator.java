
package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Authenticator {
     public static boolean login(String username, String password) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        
        String query = "SELECT * FROM cad_usuarios WHERE usuario = ? AND password = ?";
        
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password); // Idealmente, use hash de senha
            
            ResultSet resultSet = statement.executeQuery();
            
            return resultSet.next(); // Retorna true se encontrou um usuário
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

