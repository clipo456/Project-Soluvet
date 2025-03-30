package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PetDAO {
    
    public static class PetWithTutor extends Pet {
        private String tutorNome;

        public PetWithTutor(int id, String nome, LocalDate dataNasc, int id_tutor, 
                          String tutorNome, String raca, String especie, 
                          char sexo, String cor, String obs, String id_plano, int idade) {
            super(id, nome, dataNasc, id_tutor, raca, especie, sexo, cor, obs, id_plano, idade);
            this.tutorNome = tutorNome;
        }

        public String getTutorNome() {
            return tutorNome;
        }
    }

    public ObservableList<PetWithTutor> getPetsWithTutors() {
        ObservableList<PetWithTutor> pets = FXCollections.observableArrayList();
        String query = "SELECT a.id_animal, a.nome, a.data_nascimento, " +
                     "a.id_tutor, t.nome AS tutor_nome, a.raca, " +
                     "a.especie, a.sexo, a.cor, a.obs_geral, p.nome AS plano_nome, " +
                     "TIMESTAMPDIFF(YEAR, a.data_nascimento, CURDATE()) AS idade " +
                     "FROM cad_animal a " +
                     "JOIN cad_tutor t ON a.id_tutor = t.id_tutor " +
                     "JOIN planos p ON a.id_plano = p.id_plano";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                PetWithTutor pet = new PetWithTutor(
                    resultSet.getInt("id_animal"),
                    resultSet.getString("nome"),
                    resultSet.getDate("data_nascimento").toLocalDate(),
                    resultSet.getInt("id_tutor"),
                    resultSet.getString("tutor_nome"),
                    resultSet.getString("raca"),
                    resultSet.getString("especie"),
                    resultSet.getString("sexo").charAt(0),
                    resultSet.getString("cor"),
                    resultSet.getString("obs_geral"),
                    resultSet.getString("plano_nome"),
                    resultSet.getInt("idade")
                );
                pets.add(pet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }

    public ObservableList<PetWithTutor> getPetsByTutorWithName(int tutorId) {
        ObservableList<PetWithTutor> pets = FXCollections.observableArrayList();
        String query = "SELECT a.id_animal, a.nome, a.data_nascimento, " +
                     "a.id_tutor, t.nome AS tutor_nome, a.raca, " +
                     "a.especie, a.sexo, a.cor, a.obs_geral, p.nome AS plano_nome, " +
                     "TIMESTAMPDIFF(YEAR, a.data_nascimento, CURDATE()) AS idade " +
                     "FROM cad_animal a " +
                     "JOIN cad_tutor t ON a.id_tutor = t.id_tutor " +
                     "JOIN planos p ON a.id_plano = p.id_plano " +
                     "WHERE a.id_tutor = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, tutorId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    PetWithTutor pet = new PetWithTutor(
                        resultSet.getInt("id_animal"),
                        resultSet.getString("nome"),
                        resultSet.getDate("data_nascimento").toLocalDate(),
                        resultSet.getInt("id_tutor"),
                        resultSet.getString("tutor_nome"),
                        resultSet.getString("raca"),
                        resultSet.getString("especie"),
                        resultSet.getString("sexo").charAt(0),
                        resultSet.getString("cor"),
                        resultSet.getString("obs_geral"),
                        resultSet.getString("plano_nome"),
                        resultSet.getInt("idade")
                    );
                    pets.add(pet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pets;
    }
}