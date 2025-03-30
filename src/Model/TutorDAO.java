package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TutorDAO {
    public ObservableList<Tutor> getTutores() {
        ObservableList<Tutor> tutores = FXCollections.observableArrayList();
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        
        String query = "SELECT id_tutor, nome, cpf, telefone, data_nascimento, rua, cidade, bairro, cep, complemento, numero FROM cad_tutor";
        
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Tutor tutor = new Tutor(
                    resultSet.getInt("id_tutor"),
                    resultSet.getString("nome"),
                    resultSet.getString("cpf"),
                    resultSet.getString("telefone"),
                    resultSet.getString("data_nascimento"),
                    resultSet.getString("rua"),
                    resultSet.getString("cidade"),
                    resultSet.getString("bairro"),
                    resultSet.getString("cep"),
                    resultSet.getString("complemento"),
                    resultSet.getString("numero")
                );
                tutores.add(tutor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tutores;
    }
}