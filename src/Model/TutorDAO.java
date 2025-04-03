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
        
        String query = "SELECT id_tutor, nome, cpf, telefone, data_nascimento, rua, cidade, bairro, cep, complemento, numero " +
                      "FROM cad_tutor WHERE isDeleted = 0";
        
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
    
    public boolean insertTutor(Tutor tutor) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        
        String query = "INSERT INTO cad_tutor (nome, cpf, data_nascimento, telefone, rua, cidade, bairro, cep, complemento, numero) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tutor.getNome());
            statement.setString(2, tutor.getCpf());
            statement.setString(3, tutor.getDataNasc());
            statement.setString(4, tutor.getTelefone());
            statement.setString(5, tutor.getRua());
            statement.setString(6, tutor.getCidade());
            statement.setString(7, tutor.getBairro());
            statement.setString(8, tutor.getCep());
            statement.setString(9, tutor.getComplemento());
            statement.setString(10, tutor.getNumero());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTutor(Tutor tutor) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        
        String query = "UPDATE cad_tutor SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, " +
                      "rua = ?, cidade = ?, bairro = ?, cep = ?, complemento = ?, numero = ? " +
                      "WHERE id_tutor = ?";
        
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, tutor.getNome());
            statement.setString(2, tutor.getCpf());
            statement.setString(3, tutor.getDataNasc());
            statement.setString(4, tutor.getTelefone());
            statement.setString(5, tutor.getRua());
            statement.setString(6, tutor.getCidade());
            statement.setString(7, tutor.getBairro());
            statement.setString(8, tutor.getCep());
            statement.setString(9, tutor.getComplemento());
            statement.setString(10, tutor.getNumero());
            statement.setInt(11, tutor.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDeleteTutor(int id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        
        String query = "UPDATE cad_tutor SET isDeleted = 1 WHERE id_tutor = ?";
        
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Tutor getTutorById(int id) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        
        String query = "SELECT id_tutor, nome, cpf, telefone, data_nascimento, rua, cidade, bairro, cep, complemento, numero " +
                      "FROM cad_tutor WHERE id_tutor = ? AND isDeleted = 0";
        
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return new Tutor(
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}