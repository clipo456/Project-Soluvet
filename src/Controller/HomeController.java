package Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalDate;

public class HomeController implements Initializable {
    private final Model.DBConnection dbConnection = new Model.DBConnection();
    private final Connection conn = dbConnection.getConnection();

    @FXML
    private ListView<String> appointmentListView;

    private final ObservableList<String> agendamentos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appointmentListView.setItems(agendamentos);
        configureListView();
        atualizarAgendamentos(); // Atualiza ao iniciar
        startAutoRefresh();
    }

    private void configureListView() {
        appointmentListView.setCellFactory(lv -> new ListCell<String>() {
            private final Label label = new Label();

            {
                label.setWrapText(true);
                label.setMaxWidth(appointmentListView.getWidth() - 20);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    label.setText(item);
                    setGraphic(label);
                }
            }
        });
    }

    private void startAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), e -> atualizarAgendamentos()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void atualizarAgendamentos() {
        if (conn == null) {
            System.err.println("Erro: Conexão com o banco de dados não foi estabelecida.");
            return;
        }

        String dataAtual = LocalDate.now().toString();
        String query = "SELECT d.id_agenda, d.data_agendamento, p.nome AS nome_plano, " +
                       "a.nome AS nome_animal, t.nome AS nome_tutor " +
                       "FROM agendamentos d " +
                       "JOIN planos p ON d.id_plano = p.id_plano " +
                       "JOIN cad_animal a ON d.id_animal = a.id_animal " +
                       "JOIN cad_tutor t ON d.id_tutor = t.id_tutor " +
                       "WHERE d.isDeleted = 0 AND d.data_agendamento = ? " +
                       "ORDER BY d.data_agendamento";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, dataAtual);
            ResultSet rs = stmt.executeQuery();

            ObservableList<String> novosAgendamentos = FXCollections.observableArrayList();
            while (rs.next()) {
                String idAgenda = rs.getString("id_agenda");
                String plano = rs.getString("nome_plano");
                String animal = rs.getString("nome_animal");
                String tutor = rs.getString("nome_tutor");
                novosAgendamentos.add(idAgenda + " - " + animal + " - " + tutor + " - " + plano);
            }
            Platform.runLater(() -> agendamentos.setAll(novosAgendamentos));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAppointmentClicked(MouseEvent event) {
        String selectedAppointment = appointmentListView.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Opções do Agendamento");
            alert.setHeaderText("Gerenciar Agendamento");
            alert.setContentText("Deseja editar ou excluir este agendamento?");

            ButtonType editButton = new ButtonType("Editar");
            ButtonType deleteButton = new ButtonType("Excluir");
            ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(editButton, deleteButton, cancelButton);

            alert.showAndWait().ifPresent(response -> {
                String idAgenda = selectedAppointment.split(" - ")[0];
                if (response == editButton) {
                    editarAgendamento(idAgenda);
                } else if (response == deleteButton) {
                    excluirAgendamento(idAgenda);
                }
            });
        }
    }

    private void editarAgendamento(String idAgenda) {
        System.out.println("Editar: " + idAgenda);
        // Implementar lógica para abrir tela de edição
    }

    private void excluirAgendamento(String idAgenda) {
        String query = "Update agendamentos set isDeleted = 1 WHERE id_agenda = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, idAgenda);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                Platform.runLater(this::atualizarAgendamentos);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
