package Controller;

import Model.AgendamentoDAO;
import Model.Agendamento;
import Model.PetDAO;
import Model.PlanoDAO;
import Model.TutorDAO;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class CadastroAgendamentoController extends Navigation implements Initializable {

    // FXML injections
    @FXML private Button btnHome;
    @FXML private Button btnCadastrar;
    @FXML private Button btnExcluir;
    @FXML private Button btnLimpar;
    @FXML private Button btnAlterar;
    @FXML private DatePicker dataPicker;
    @FXML private MenuButton horarioMenu;
    @FXML private MenuButton petMenu;
    @FXML private MenuButton tutorMenu;
    @FXML private TableView<Agendamento> tableView;
    @FXML private TableColumn<Agendamento, String> horaAgendamento;
    @FXML private TableColumn<Agendamento, String> dataAgendamento;
    @FXML private TableColumn<Agendamento, String> petAgendamento;
    @FXML private TableColumn<Agendamento, String> tutorAgendamento;
    @FXML private TableColumn<Agendamento, String> planoAgendamento;

    private AgendamentoDAO agendamentoDAO;
    private PetDAO petDAO;
    private PlanoDAO planoDAO;
    private TutorDAO tutorDAO;
    private ObservableList<Agendamento> agendamentosList;
    private Agendamento agendamentoSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Initialize with empty list first
            agendamentosList = FXCollections.observableArrayList();
            tableView.setItems(agendamentosList);
            
            // Configure table columns
            configureTableColumns();
            
            // Initialize DAOs first
            agendamentoDAO = new AgendamentoDAO();
            petDAO = new PetDAO();
            planoDAO = new PlanoDAO();
            tutorDAO = new TutorDAO();

            // Initialize components
            configurarMenus();
            configurarHorarios();
            configurarDatePicker();

            // Load appointments
            carregarAgendamentos();

            // Set up table selection listener LAST
            configureTableSelection();

        } catch (Exception e) {
            showErrorAlert("Erro de Inicialização", "Ocorreu um erro ao inicializar a tela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureTableColumns() {
        horaAgendamento.setCellValueFactory(cellData -> {
            LocalTime time = cellData.getValue().getHora();
            return new SimpleStringProperty(time != null ? time.toString() : "");
        });

        dataAgendamento.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDataAgendamento();
            return new SimpleStringProperty(date != null ? date.toString() : "");
        });

        petAgendamento.setCellValueFactory(cellData -> {
            try {
                int idAnimal = cellData.getValue().getIdAnimal();
                Model.Pet pet = petDAO.getPetById(idAnimal);
                return new SimpleStringProperty(pet != null ? pet.getNome() : "");
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        tutorAgendamento.setCellValueFactory(cellData -> {
            try {
                int idTutor = cellData.getValue().getIdTutor();
                Model.Tutor tutor = tutorDAO.getTutorById(idTutor);
                return new SimpleStringProperty(tutor != null ? tutor.getNome() : "");
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });

        planoAgendamento.setCellValueFactory(cellData -> {
            try {
                int idAnimal = cellData.getValue().getIdAnimal();
                Model.Pet pet = petDAO.getPetById(idAnimal);
                return new SimpleStringProperty(pet != null ? pet.getId_plano() : "");
            } catch (Exception e) {
                return new SimpleStringProperty("");
            }
        });
    }

    private void configureTableSelection() {
        // Single selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    Platform.runLater(() -> selecionarAgendamento(newSelection));
                }
            });

        // More robust row factory
        tableView.setRowFactory(tv -> {
            TableRow<Agendamento> row = new TableRow<>() {
                @Override
                protected void updateItem(Agendamento item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setOnMouseClicked(null);
                    } else {
                        setOnMouseClicked(event -> {
                            if (event.getClickCount() == 1 && !isEmpty()) {
                                tableView.getSelectionModel().select(getIndex());
                            }
                        });
                    }
                }
            };
            return row;
        });
    }

    private void configurarMenus() {
        // Configure tutor menu
        tutorMenu.getItems().clear();
        tutorMenu.setText("Tutor");
        tutorDAO.getTutores().forEach(tutor -> {
            MenuItem item = new MenuItem(tutor.getNome());
            item.setOnAction(e -> {
                tutorMenu.setText(tutor.getNome());
                atualizarPetsPorTutor(tutor.getId());
                if (agendamentoSelecionado != null) {
                    agendamentoSelecionado.setIdTutor(tutor.getId());
                }
            });
            tutorMenu.getItems().add(item);
        });

        // Initialize pet menu
        petMenu.getItems().clear();
        petMenu.setText("PET");
        petMenu.setDisable(true);

    }

    private void atualizarPetsPorTutor(int idTutor) {
        petMenu.getItems().clear();
        petMenu.setText("PET");
        
        try {
            List<Model.Pet> petsDoTutor = petDAO.getPetsByTutorId(idTutor);
            petsDoTutor.forEach(pet -> {
                MenuItem item = new MenuItem(pet.getNome());
                item.setOnAction(e -> {
                    petMenu.setText(pet.getNome());
                    if (agendamentoSelecionado != null) {
                        agendamentoSelecionado.setIdAnimal(pet.getId());
                        agendamentoSelecionado.setIdTutor(idTutor);
                    }
                });
                petMenu.getItems().add(item);
            });
            
            if (petsDoTutor.isEmpty()) {
                MenuItem emptyItem = new MenuItem("Nenhum pet cadastrado");
                emptyItem.setDisable(true);
                petMenu.getItems().add(emptyItem);
            }
            petMenu.setDisable(false);
        } catch (SQLException e) {
            showErrorAlert("Erro", "Não foi possível carregar os pets deste tutor");
        }
    }

    private void configurarHorarios() {
        horarioMenu.getItems().clear();
        
        // Add time slots (every 15 minutes from 08:00 to 18:00)
        for (int hour = 8; hour < 18; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                String time = String.format("%02d:%02d", hour, minute);
                MenuItem item = new MenuItem(time);
                item.setOnAction(e -> horarioMenu.setText(time));
                horarioMenu.getItems().add(item);
            }
        }
    }

    private void configurarDatePicker() {
        dataPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                try {
                    List<Agendamento> agendamentosFiltrados = agendamentoDAO.buscarAgendamentosPorData(newDate);
                    agendamentosList.setAll(agendamentosFiltrados);
                } catch (SQLException e) {
                    showErrorAlert("Erro ao Filtrar", "Não foi possível filtrar os agendamentos: " + e.getMessage());
                }
            } else {
                carregarAgendamentos();
            }
        });
    }

    private void carregarAgendamentos() {
        try {
            List<Agendamento> agendamentos = agendamentoDAO.listarAgendamentos();
            Platform.runLater(() -> {
                // Clear selection first
                tableView.getSelectionModel().clearSelection();

                // Update items
                agendamentosList.setAll(agendamentos);

                if (agendamentos.isEmpty()) {
                    limparCampos();
                }
            });
        } catch (SQLException e) {
            Platform.runLater(() -> {
                showErrorAlert("Erro ao Carregar Dados", "Não foi possível carregar os agendamentos: " + e.getMessage());
                limparCampos();
            });
        }
    }

    private void selecionarAgendamento(Agendamento agendamento) {
        try {
            if (agendamento == null || agendamentosList == null || agendamentosList.isEmpty()) {
                limparCampos();
                return;
            }

            agendamentoSelecionado = agendamento;
            dataPicker.setValue(agendamento.getDataAgendamento());
            horarioMenu.setText(agendamento.getHora().toString());

            // Set tutor first to load their pets
            Model.Tutor tutor = tutorDAO.getTutorById(agendamento.getIdTutor());
            if (tutor != null) {
                tutorMenu.setText(tutor.getNome());
                atualizarPetsPorTutor(tutor.getId());
            }

            // Then set pet
            Model.Pet pet = petDAO.getPetById(agendamento.getIdAnimal());
            if (pet != null) {
                petMenu.setText(pet.getNome());
            }

        } catch (Exception e) {
            showErrorAlert("Erro", "Não foi possível carregar os dados do agendamento");
            limparCampos();
        }
    }

    @FXML
    private void handleCadastrar() {
        if (validarCampos()) {
            Agendamento agendamento = new Agendamento();
            agendamento.setDataAgendamento(dataPicker.getValue());
            agendamento.setHora(LocalTime.parse(horarioMenu.getText()));

            // Get selected pet (which also sets the tutor)
            String petSelecionado = petMenu.getText();
            if (!petSelecionado.equals("PET")) {
                try {
                    List<Model.Pet> pets = petDAO.getPetsByTutorId(getTutorIdFromMenu());
                    Model.Pet pet = pets.stream()
                        .filter(p -> p.getNome().equals(petSelecionado))
                        .findFirst()
                        .orElse(null);
                    if (pet != null) {
                        agendamento.setIdAnimal(pet.getId());
                        agendamento.setIdTutor(pet.getId_tutor());
                    }
                } catch (SQLException e) {
                    showErrorAlert("Erro", "Não foi possível verificar o pet selecionado");
                    return;
                }
            }


            try {
                if (agendamentoDAO.existeAgendamentoNoMesmoHorario(
                    agendamento.getDataAgendamento(), 
                    agendamento.getHora(), 
                    -1)) {
                    showWarningAlert("Conflito de Horário", "Já existe um agendamento para este horário");
                    return;
                }

                agendamentoDAO.adicionarAgendamento(agendamento);
                carregarAgendamentos();
                limparCampos();
                showSuccessAlert("Sucesso", "Agendamento cadastrado com sucesso!");
            } catch (SQLException e) {
                showErrorAlert("Erro ao Cadastrar", "Não foi possível cadastrar o agendamento: " + e.getMessage());
            }
        }
    }

    private int getTutorIdFromMenu() throws SQLException {
        String tutorSelecionado = tutorMenu.getText();
        if (!tutorSelecionado.equals("Tutor")) {
            return tutorDAO.getTutores().stream()
                .filter(t -> t.getNome().equals(tutorSelecionado))
                .findFirst()
                .map(t -> t.getId())
                .orElseThrow(() -> new SQLException("Tutor não encontrado"));
        }
        throw new SQLException("Tutor não selecionado");
    }

    @FXML
    private void handleAlterar() {
        if (agendamentoSelecionado != null && validarCampos()) {
            agendamentoSelecionado.setDataAgendamento(dataPicker.getValue());
            agendamentoSelecionado.setHora(LocalTime.parse(horarioMenu.getText()));

            // Get selected pet (which also sets the tutor)
            String petSelecionado = petMenu.getText();
            if (!petSelecionado.equals("PET")) {
                try {
                    List<Model.Pet> pets = petDAO.getPetsByTutorId(agendamentoSelecionado.getIdTutor());
                    Model.Pet pet = pets.stream()
                        .filter(p -> p.getNome().equals(petSelecionado))
                        .findFirst()
                        .orElse(null);
                    if (pet != null) {
                        agendamentoSelecionado.setIdAnimal(pet.getId());
                        agendamentoSelecionado.setIdTutor(pet.getId_tutor());
                    }
                } catch (SQLException e) {
                    showErrorAlert("Erro", "Não foi possível verificar o pet selecionado");
                    return;
                }
            }


            try {
                if (agendamentoDAO.existeAgendamentoNoMesmoHorario(
                    agendamentoSelecionado.getDataAgendamento(), 
                    agendamentoSelecionado.getHora(),
                    agendamentoSelecionado.getIdAgenda())) {
                    showWarningAlert("Conflito de Horário", "Já existe um agendamento para este horário");
                    return;
                }

                agendamentoDAO.atualizarAgendamento(agendamentoSelecionado);
                carregarAgendamentos();
                limparCampos();
                showSuccessAlert("Sucesso", "Agendamento atualizado com sucesso!");
            } catch (SQLException e) {
                showErrorAlert("Erro ao Atualizar", "Não foi possível atualizar o agendamento: " + e.getMessage());
            }
        } else {
            showWarningAlert("Aviso", "Selecione um agendamento para alterar");
        }
    }

    @FXML
    private void handleExcluir() {
        Agendamento agendamentoSelecionado = tableView.getSelectionModel().getSelectedItem();
        if (agendamentoSelecionado != null) {
            try {
                agendamentoDAO.removerAgendamento(agendamentoSelecionado.getIdAgenda());
                carregarAgendamentos();
                limparCampos();
                showSuccessAlert("Sucesso", "Agendamento removido com sucesso!");
            } catch (SQLException e) {
                showErrorAlert("Erro ao Excluir", "Não foi possível remover o agendamento: " + e.getMessage());
            }
        } else {
            showWarningAlert("Aviso", "Selecione um agendamento para excluir");
        }
    }

    @FXML
    private void handleLimpar() {
        limparCampos();
    }

    private void limparCampos() {
        dataPicker.setValue(null);
        horarioMenu.setText("00:00");
        petMenu.setText("PET");
        petMenu.setDisable(true);
        tutorMenu.setText("Tutor");
        agendamentoSelecionado = null;
        if (tableView != null) {
            tableView.getSelectionModel().clearSelection();
        }
    }

    private boolean validarCampos() {
        if (dataPicker.getValue() == null) {
            showWarningAlert("Validação", "Selecione uma data");
            return false;
        }
        
        if (horarioMenu.getText().equals("00:00")) {
            showWarningAlert("Validação", "Selecione um horário");
            return false;
        }
        
        if (tutorMenu.getText().equals("Tutor")) {
            showWarningAlert("Validação", "Selecione um tutor");
            return false;
        }
        
        if (petMenu.getText().equals("PET")) {
            showWarningAlert("Validação", "Selecione um PET");
            return false;
        }
       
        
        return true;
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
}