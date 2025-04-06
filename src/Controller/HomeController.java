package Controller;

import Model.DBConnection;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.*;
import java.sql.*;
import javafx.animation.*;
import javafx.util.Duration;
import java.time.*;
import java.time.format.*;
import java.util.*;
import javafx.application.Platform;
import javafx.scene.input.MouseEvent;

public class HomeController extends Navigation implements Initializable {
    private final DBConnection dbConnection = new DBConnection();
    private final Connection conn = dbConnection.getConnection();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    @FXML private ListView<String> appointmentListView;
    @FXML private DatePicker datePicker;
    
    private final ObservableList<String> appointments = FXCollections.observableArrayList();
    private final HashMap<String, String> appointmentIdMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupListView();
        initializeDatePicker();
        refreshAppointments(datePicker.getValue());
        startAutoRefresh();
        
        // Subscribe to events
        EventBus.getInstance().subscribe(AppointmentEvents.CalendarRefreshNeeded.class, 
            e -> refreshAppointments(datePicker.getValue()));
    }

    private void initializeDatePicker() {
        datePicker.setValue(LocalDate.now());
        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            refreshAppointments(newDate);
        });
    }

    private void deleteAppointment(String appointmentId) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmar Exclusão");
        confirmation.setHeaderText("Tem certeza que deseja excluir este agendamento?");
        confirmation.setContentText("Esta ação não pode ser desfeita.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String query = "UPDATE agendamentos SET isDeleted = 1 WHERE id_agenda = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, appointmentId);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        // Publish events
                        EventBus.getInstance().publish(
                            new AppointmentEvents.AppointmentDeleted(Integer.parseInt(appointmentId))
                        );
                        EventBus.getInstance().publish(
                            new AppointmentEvents.CalendarRefreshNeeded(datePicker.getValue())
                        );
                        
                        Platform.runLater(() -> {
                            refreshAppointments(datePicker.getValue());
                            showAlert("Sucesso", "Agendamento excluído com sucesso!", Alert.AlertType.INFORMATION);
                        });
                    }
                } catch (SQLException e) {
                    showAlert("Erro", "Falha ao excluir agendamento: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupListView() {
        appointmentListView.setItems(appointments);
        
        appointmentListView.setCellFactory(lv -> new ListCell<String>() {
            private final Label label = new Label();
            
            {
                label.setWrapText(true);
                label.setMaxWidth(appointmentListView.getWidth() - 20);
                label.setStyle("-fx-font-size: 14px;");
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
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(30), e -> refreshAppointments(datePicker.getValue())
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshAppointments(LocalDate date) {
        if (conn == null) {
            showAlert("Erro", "Conexão com o banco de dados não foi estabelecida.", Alert.AlertType.ERROR);
            return;
        }

        String query = "SELECT d.id_agenda, d.hora, d.data_agendamento, p.nome AS nome_plano, " +
                      "a.nome AS nome_animal, t.nome AS nome_tutor " +
                      "FROM agendamentos d " +
                      "JOIN planos p ON d.id_plano = p.id_plano " +
                      "JOIN cad_animal a ON d.id_animal = a.id_animal " +
                      "JOIN cad_tutor t ON d.id_tutor = t.id_tutor " +
                      "WHERE d.isDeleted = 0 AND d.data_agendamento = ? " +
                      "ORDER BY d.hora";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            ObservableList<String> newAppointments = FXCollections.observableArrayList();
            appointmentIdMap.clear();

            while (rs.next()) {
                String appointmentId = rs.getString("id_agenda");  
                LocalTime time = rs.getTime("hora").toLocalTime();
                String plan = rs.getString("nome_plano");
                String animal = rs.getString("nome_animal");
                String tutor = rs.getString("nome_tutor");

                String formattedTime = time.format(timeFormatter);
                String description = String.format("%s - %s (%s) - %s", 
                    formattedTime, animal, plan, tutor);

                newAppointments.add(description);
                appointmentIdMap.put(description, appointmentId);
            }

            Platform.runLater(() -> {
                appointments.setAll(newAppointments);
                if (newAppointments.isEmpty()) {
                    appointments.add("Nenhum agendamento para " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
            });

        } catch (SQLException e) {
            showAlert("Erro no Banco de Dados", "Erro ao carregar agendamentos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void onAppointmentClicked(MouseEvent event) {
        if (event.getClickCount() != 2) return; // Only react to double clicks
        
        String selected = appointmentListView.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.equals("Nenhum agendamento para hoje")) {
            String appointmentId = appointmentIdMap.get(selected);
            if (appointmentId == null) {
                showAlert("Erro", "ID do agendamento não encontrado!", Alert.AlertType.ERROR);
                return;
            }

            showAppointmentOptions(appointmentId);
        }
    }

    private void showAppointmentOptions(String appointmentId) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Opções do Agendamento");
        alert.setHeaderText("Gerenciar Agendamento");
        alert.setContentText("Escolha uma ação para este agendamento:");

        ButtonType editButton = new ButtonType("Editar", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteButton = new ButtonType("Excluir", ButtonBar.ButtonData.OTHER);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(editButton, deleteButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == editButton) {
                editAppointment(appointmentId);
            } else if (response == deleteButton) {
                deleteAppointment(appointmentId);
            }
        });
    }

    private void editAppointment(String appointmentId) {
        // TODO: Implement edit functionality
        System.out.println("Editando agendamento ID: " + appointmentId);
        showAlert("Editar", "Funcionalidade de edição será implementada aqui", Alert.AlertType.INFORMATION);
    }

    

    private void showAlert(String title, String message, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
    
    
}