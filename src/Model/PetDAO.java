package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PetDAO {
    private Connection connection;

    public PetDAO() {
        this.connection = new DBConnection().getConnection();
    }
    
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
                     "JOIN planos p ON a.id_plano = p.id_plano " +
                     "WHERE a.isDeleted = 0";

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
                     "WHERE a.id_tutor = ? AND a.isDeleted = 0";

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

    public boolean insertPet(Pet pet) {
        String query = "INSERT INTO cad_animal (nome, data_nascimento, id_tutor, raca, " +
                     "especie, sexo, cor, obs_geral, id_plano) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, " +
                     "(SELECT id_plano FROM planos WHERE nome = ? AND isDeleted = 0))";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, pet.getNome());
            statement.setDate(2, java.sql.Date.valueOf(pet.getDataNasc()));
            statement.setInt(3, pet.getId_tutor());
            statement.setString(4, pet.getRaca());
            statement.setString(5, pet.getEspecie());
            statement.setString(6, String.valueOf(pet.getSexo()));
            statement.setString(7, pet.getCor());
            statement.setString(8, pet.getObs());
            statement.setString(9, pet.getId_plano());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePet(Pet pet) {
        String query = "UPDATE cad_animal SET nome = ?, data_nascimento = ?, id_tutor = ?, " +
                     "raca = ?, especie = ?, sexo = ?, cor = ?, obs_geral = ?, " +
                     "id_plano = (SELECT id_plano FROM planos WHERE nome = ? AND isDeleted = 0) " +
                     "WHERE id_animal = ? AND isDeleted = 0";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setString(1, pet.getNome());
            statement.setDate(2, java.sql.Date.valueOf(pet.getDataNasc()));
            statement.setInt(3, pet.getId_tutor());
            statement.setString(4, pet.getRaca());
            statement.setString(5, pet.getEspecie());
            statement.setString(6, String.valueOf(pet.getSexo()));
            statement.setString(7, pet.getCor());
            statement.setString(8, pet.getObs());
            statement.setString(9, pet.getId_plano());
            statement.setInt(10, pet.getId());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePet(int petId) {
        // Soft delete implementation
        String query = "UPDATE cad_animal SET isDeleted = 1 WHERE id_animal = ?";

        try (Connection connection = new DBConnection().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            statement.setInt(1, petId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Pet getPetById(int petId) {
    String query = "SELECT a.id_animal, a.nome, a.data_nascimento, " +
                 "a.id_tutor, a.raca, a.especie, a.sexo, a.cor, " +
                 "a.obs_geral, p.nome AS plano_nome, " +
                 "TIMESTAMPDIFF(YEAR, a.data_nascimento, CURDATE()) AS idade " +
                 "FROM cad_animal a " +
                 "JOIN planos p ON a.id_plano = p.id_plano " +
                 "WHERE a.id_animal = ? AND a.isDeleted = 0";

    try (Connection connection = new DBConnection().getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
        
        statement.setInt(1, petId);
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return new Pet(
                    resultSet.getInt("id_animal"),
                    resultSet.getString("nome"),
                    resultSet.getDate("data_nascimento").toLocalDate(),
                    resultSet.getInt("id_tutor"),
                    resultSet.getString("raca"),
                    resultSet.getString("especie"),
                    resultSet.getString("sexo").charAt(0),
                    resultSet.getString("cor"),
                    resultSet.getString("obs_geral"),
                    resultSet.getString("plano_nome"),
                    resultSet.getInt("idade")
                );
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
    
    public List<Pet> getPetsByTutorId(int idTutor) throws SQLException {
           List<Pet> pets = new ArrayList<>();
           String sql = "SELECT * FROM cad_animal WHERE id_tutor = ? AND isDeleted = 0";

           try {
               PreparedStatement stmt = connection.prepareStatement(sql);
               stmt.setInt(1, idTutor);

               ResultSet rs = stmt.executeQuery();

               while (rs.next()) {
                   Pet pet = new Pet();
                   pet.setId(rs.getInt("id_animal"));
                   pet.setNome(rs.getString("nome"));
                   pet.setId_tutor(rs.getInt("id_tutor"));
                   // Set other pet properties as needed
                   pets.add(pet);
               }

               rs.close();
               stmt.close();
           } catch (SQLException e) {
               e.printStackTrace();
               throw new SQLException("Error fetching pets by tutor ID: " + e.getMessage());
           }

           return pets;
       }

       public void close() {
           try {
               if (connection != null && !connection.isClosed()) {
                   connection.close();
               }
           } catch (SQLException e) {
               System.err.println("Error closing connection: " + e.getMessage());
           }
       }
}
