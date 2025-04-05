package Model;

import Model.Agendamento;
import Model.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgendamentoDAO {
    private Connection connection;

    public AgendamentoDAO() {
        this.connection = new DBConnection().getConnection();
    }

    // Create
    public void adicionarAgendamento(Agendamento agendamento) throws SQLException {
        // Get the pet's plano ID first
        String petPlanoQuery = "SELECT id_plano FROM cad_animal WHERE id_animal = ?";
        int idPlano = 0;

        try (PreparedStatement petStmt = connection.prepareStatement(petPlanoQuery)) {
            petStmt.setInt(1, agendamento.getIdAnimal());
            try (ResultSet rs = petStmt.executeQuery()) {
                if (rs.next()) {
                    idPlano = rs.getInt("id_plano");
                }
            }
        }

        String sql = "INSERT INTO agendamentos (data_agendamento, hora, id_plano, id_animal, id_tutor) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(agendamento.getDataAgendamento()));
            stmt.setTime(2, Time.valueOf(agendamento.getHora()));
            stmt.setInt(3, idPlano); // Use the pet's plano
            stmt.setInt(4, agendamento.getIdAnimal());
            stmt.setInt(5, agendamento.getIdTutor());

            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao adicionar agendamento: " + e.getMessage());
        }
    }

    // Read all
    public List<Agendamento> listarAgendamentos() throws SQLException {
        System.out.println("Attempting to connect to database...");
        List<Agendamento> agendamentos = new ArrayList<>();

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM agendamentos WHERE isDeleted = 0");
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("Database connection successful");

            while (rs.next()) {
                Agendamento a = new Agendamento();
                a.setIdAgenda(rs.getInt("id_agenda"));
                a.setDataAgendamento(rs.getDate("data_agendamento").toLocalDate());
                a.setHora(rs.getTime("hora").toLocalTime());
                a.setIdPlano(rs.getInt("id_plano"));
                a.setIdAnimal(rs.getInt("id_animal"));
                a.setIdTutor(rs.getInt("id_tutor"));
                a.setIsDeleted(rs.getBoolean("isDeleted"));

                System.out.println("Loaded appointment: " + a.getIdAgenda() + " for date " + a.getDataAgendamento());
                agendamentos.add(a);
            }
        }
        return agendamentos;
    }

    // Update
    public void atualizarAgendamento(Agendamento agendamento) throws SQLException {
        // Get the pet's plano ID first
        String petPlanoQuery = "SELECT id_plano FROM cad_animal WHERE id_animal = ?";
        int idPlano = 0;

        try (PreparedStatement petStmt = connection.prepareStatement(petPlanoQuery)) {
            petStmt.setInt(1, agendamento.getIdAnimal());
            try (ResultSet rs = petStmt.executeQuery()) {
                if (rs.next()) {
                    idPlano = rs.getInt("id_plano");
                }
            }
        }

        String sql = "UPDATE agendamentos SET data_agendamento = ?, hora = ?, " +
                     "id_plano = ?, id_animal = ?, id_tutor = ? WHERE id_agenda = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(agendamento.getDataAgendamento()));
            stmt.setTime(2, Time.valueOf(agendamento.getHora()));
            stmt.setInt(3, idPlano); // Use the pet's plano
            stmt.setInt(4, agendamento.getIdAnimal());
            stmt.setInt(5, agendamento.getIdTutor());
            stmt.setInt(6, agendamento.getIdAgenda());

            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar agendamento: " + e.getMessage());
        }
    }

    // Delete (soft delete)
    public void removerAgendamento(int idAgenda) throws SQLException {
        String sql = "UPDATE agendamentos SET isDeleted = 1 WHERE id_agenda = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idAgenda);
            stmt.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao remover agendamento: " + e.getMessage());
        }
    }

    // Get by ID
    public Agendamento getAgendamentoById(int idAgenda) throws SQLException {
    String sql = "SELECT * FROM agendamentos WHERE id_agenda = ? AND isDeleted = 0";
    Agendamento agendamento = null;
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, idAgenda);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                agendamento = new Agendamento();
                agendamento.setIdAgenda(rs.getInt("id_agenda"));
                agendamento.setDataAgendamento(rs.getDate("data_agendamento").toLocalDate());
                agendamento.setHora(rs.getTime("hora").toLocalTime());
                agendamento.setIdPlano(rs.getInt("id_plano"));
                agendamento.setIdAnimal(rs.getInt("id_animal"));
                agendamento.setIdTutor(rs.getInt("id_tutor"));
                agendamento.setIsDeleted(rs.getBoolean("isDeleted"));
            }
        }
    }
    return agendamento;
}
    
    // Close connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
    
    public List<Agendamento> buscarAgendamentosPorData(LocalDate date) throws SQLException {
        String sql = "SELECT * FROM agendamentos WHERE data_agendamento = ? AND isDeleted = 0";
        List<Agendamento> agendamentos = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Agendamento agendamento = new Agendamento();
                    agendamento.setIdAgenda(rs.getInt("id_agenda"));
                    agendamento.setDataAgendamento(rs.getDate("data_agendamento").toLocalDate());
                    agendamento.setHora(rs.getTime("hora").toLocalTime());
                    agendamento.setIdPlano(rs.getInt("id_plano"));
                    agendamento.setIdAnimal(rs.getInt("id_animal"));
                    agendamento.setIdTutor(rs.getInt("id_tutor"));
                    agendamento.setIsDeleted(rs.getBoolean("isDeleted"));

                    agendamentos.add(agendamento);
                }
            }
        }

        return agendamentos;
    }

    public boolean existeAgendamentoNoMesmoHorario(LocalDate data, LocalTime hora, int idExcluir) throws SQLException {
    String sql = "SELECT COUNT(*) FROM agendamentos WHERE data_agendamento = ? AND hora = ? AND isDeleted = 0";
    
    if (idExcluir > 0) {
        sql += " AND id_agenda != ?";
    }
    
    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setDate(1, java.sql.Date.valueOf(data));
        stmt.setTime(2, Time.valueOf(hora));
        
        if (idExcluir > 0) {
            stmt.setInt(3, idExcluir);
        }
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}
}