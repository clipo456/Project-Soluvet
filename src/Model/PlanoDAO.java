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

    public boolean inserirPlano(Plano plano) {
        // Verifica se existe plano ATIVO com mesmo nome
        String verificaQuery = "SELECT COUNT(*) FROM planos WHERE nome = ? AND isDeleted = 0";
        String insereQuery = "INSERT INTO planos (nome, valor, corte_unhas, limpeza_dentes, " +
                           "banho_mes, tosa_mes, banhos_realizados, tosas_realizadas, isDeleted) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement verificaStmt = connection.prepareStatement(verificaQuery);
             PreparedStatement insereStmt = connection.prepareStatement(insereQuery)) {

            // Verifica se existe plano ativo com mesmo nome
            verificaStmt.setString(1, plano.getNome());
            ResultSet rs = verificaStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // Já existe plano ativo com este nome
                return false;
            }

            // Não existe plano ativo, pode cadastrar
            insereStmt.setString(1, plano.getNome());
            insereStmt.setDouble(2, plano.getValor());
            insereStmt.setBoolean(3, plano.isCorte_unhas());
            insereStmt.setBoolean(4, plano.isLimpeza_dentes());
            insereStmt.setInt(5, plano.getBanho_mes());
            insereStmt.setInt(6, plano.getTosa_mes());
            insereStmt.setInt(7, plano.getBanhos_realizados());
            insereStmt.setInt(8, plano.getTosas_realizadas());
            insereStmt.setBoolean(9, plano.isDeleted());

            return insereStmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarPlano(Plano plano) {
        String query = "UPDATE planos SET nome = ?, valor = ?, corte_unhas = ?, limpeza_dentes = ?, " +
                      "banho_mes = ?, tosa_mes = ? WHERE id_plano = ? AND isDeleted = 0";
        
        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, plano.getNome());
            statement.setDouble(2, plano.getValor());
            statement.setBoolean(3, plano.isCorte_unhas());
            statement.setBoolean(4, plano.isLimpeza_dentes());
            statement.setInt(5, plano.getBanho_mes());
            statement.setInt(6, plano.getTosa_mes());
            statement.setInt(7, plano.getId_plano());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean excluirPlano(int id) {
        // Verifica se o plano está sendo usado por algum animal
        if (isPlanoEmUso(id)) {
            return false;
        }
        
        String query = "UPDATE planos SET isDeleted = 1 WHERE id_plano = ?";
        
        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isPlanoEmUso(int idPlano) {
        String query = "SELECT COUNT(*) FROM cad_animal WHERE id_plano = ? AND isDeleted = 0";
        
        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, idPlano);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}