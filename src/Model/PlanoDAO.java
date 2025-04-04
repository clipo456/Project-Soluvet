package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlanoDAO {
    
    public ObservableList<Plano> getPlanos() {
        ObservableList<Plano> planos = FXCollections.observableArrayList();
        String query = "SELECT * FROM planos WHERE isDeleted = 0";
        
        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                Plano plano = new Plano(
                    resultSet.getInt("id_plano"),
                    resultSet.getString("nome"),
                    resultSet.getDouble("valor"),
                    resultSet.getBoolean("corte_unhas"),
                    resultSet.getBoolean("limpeza_dentes"),
                    resultSet.getInt("banho_mes"),
                    resultSet.getInt("tosa_mes"),
                    resultSet.getInt("banhos_realizados"),
                    resultSet.getInt("tosas_realizadas"),
                    resultSet.getBoolean("isDeleted")
                );
                planos.add(plano);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return planos;
    }
    
    public Plano getPlanoByName(String name) {
        String query = "SELECT * FROM planos WHERE nome = ? AND isDeleted = 0";
        
        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Plano(
                        resultSet.getInt("id_plano"),
                        resultSet.getString("nome"),
                        resultSet.getDouble("valor"),
                        resultSet.getBoolean("corte_unhas"),
                        resultSet.getBoolean("limpeza_dentes"),
                        resultSet.getInt("banho_mes"),
                        resultSet.getInt("tosa_mes"),
                        resultSet.getInt("banhos_realizados"),
                        resultSet.getInt("tosas_realizadas"),
                        resultSet.getBoolean("isDeleted")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Plano getPlanoById(int id) {
    String query = "SELECT * FROM planos WHERE id_plano = ? AND isDeleted = 0";
    
    try (Connection connection = new DBConnection().getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        
        statement.setInt(1, id);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return new Plano(
                    resultSet.getInt("id_plano"),
                    resultSet.getString("nome"),
                    resultSet.getDouble("valor"),
                    resultSet.getBoolean("corte_unhas"),
                    resultSet.getBoolean("limpeza_dentes"),
                    resultSet.getInt("banho_mes"),
                    resultSet.getInt("tosa_mes"),
                    resultSet.getInt("banhos_realizados"),
                    resultSet.getInt("tosas_realizadas"),
                    resultSet.getBoolean("isDeleted")
                );
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
}