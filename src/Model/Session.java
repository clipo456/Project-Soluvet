
package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Session {
    private static Session instance;
    private String usuarioLogado;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setUsuario(String usuario) {
        this.usuarioLogado = usuario;
    }

    public String getUsuario() {
        return usuarioLogado;
    }

    public boolean isAdmin() {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();

        String query = "SELECT * from cad_usuarios where usuario = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, usuarioLogado);

            ResultSet resultSet = statement.executeQuery();

            // Verifica se há resultados e move o cursor para a primeira linha
            if (resultSet.next()) {
                Usuario usuario = new Usuario(
                    resultSet.getInt("id_usuario"),
                    resultSet.getString("nome"),
                    resultSet.getString("usuario"),
                    resultSet.getString("cpf"),
                    resultSet.getString("senha"),
                    resultSet.getBoolean("permissao_admin")
                );
                return usuario.isAdmin();
            } else {
                // Nenhum usuário encontrado
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;     
        } finally {
            // Fechar a conexão (importante!)
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void logout() {
        usuarioLogado = null;
    }
}
