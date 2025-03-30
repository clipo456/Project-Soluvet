package Controller;

import Model.Pet;
import Model.PetDAO;
import Model.PetDAO.PetWithTutor;
import Model.Tutor;
import Model.TutorDAO;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConsultaController extends Navigation implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private TableView<Tutor> tutorTable;
    @FXML private TableColumn<Tutor, Integer> idTCol;
    @FXML private TableColumn<Tutor, String> nomeTCol;
    @FXML private TableColumn<Tutor, String> cpfTCol;
    @FXML private TableColumn<Tutor, String> telTCol;
    @FXML private TableView<PetWithTutor> petTable;
    @FXML private TableColumn<PetWithTutor, Integer> idPCol;
    @FXML private TableColumn<PetWithTutor, String> nomePCol;
    @FXML private TableColumn<PetWithTutor, Integer> tidPCol;
    @FXML private TableColumn<PetWithTutor, String> tnomePCol;
    @FXML private TableColumn<PetWithTutor, String> especPCol;
    @FXML private TableColumn<PetWithTutor, String> racaPCol;
    @FXML private TableColumn<PetWithTutor, String> sexoPCol;
    @FXML private TableColumn<PetWithTutor, String> corPCol;
    @FXML private TableColumn<PetWithTutor, String> planoPCol;
    @FXML private TableColumn<PetWithTutor, String> obsPCol;
    @FXML private TableColumn<PetWithTutor, String> idadePCol;

    private TableView<?> lastFocusedTable;
    private Stage currentStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTutorTable();
        initializePetTable();
        loadTutorData();
        
        tutorTable.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) lastFocusedTable = tutorTable;
        });
        
        petTable.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) lastFocusedTable = petTable;
        });
        
        tutorTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadPetData(newValue.getId());
                }
            });
    }
    
    public void setStage(Stage stage) {
        this.currentStage = stage;
    }

    private void initializeTutorTable() {
        idTCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomeTCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        cpfTCol.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        telTCol.setCellValueFactory(new PropertyValueFactory<>("telefone"));
    }
    
    private void initializePetTable() {
        idPCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomePCol.setCellValueFactory(new PropertyValueFactory<>("nome"));
        tidPCol.setCellValueFactory(new PropertyValueFactory<>("id_tutor"));
        tnomePCol.setCellValueFactory(new PropertyValueFactory<>("tutorNome"));
        especPCol.setCellValueFactory(new PropertyValueFactory<>("especie"));
        racaPCol.setCellValueFactory(new PropertyValueFactory<>("raca"));
        sexoPCol.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        corPCol.setCellValueFactory(new PropertyValueFactory<>("cor"));
        planoPCol.setCellValueFactory(new PropertyValueFactory<>("id_plano"));
        obsPCol.setCellValueFactory(new PropertyValueFactory<>("obs"));
        idadePCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(cellData.getValue().getIdade())));
    }
    
    private void loadPetData(int tutorId) {
        PetDAO petDAO = new PetDAO();
        ObservableList<PetWithTutor> pets = petDAO.getPetsByTutorWithName(tutorId);
        petTable.setItems(pets);
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            loadTutorData();
            return;
        }

        PetDAO petDAO = new PetDAO();
        TutorDAO tutorDAO = new TutorDAO();

        ObservableList<Tutor> filteredTutores = FXCollections.observableArrayList();
        ObservableList<PetWithTutor> filteredPets = FXCollections.observableArrayList();

        for (Tutor tutor : tutorDAO.getTutores()) {
            if (containsIgnoreCase(tutor.getNome(), searchTerm) ||
                containsIgnoreCase(tutor.getCpf(), searchTerm) ||
                containsIgnoreCase(tutor.getTelefone(), searchTerm) ||
                containsIgnoreCase(tutor.getRua(), searchTerm) ||
                containsIgnoreCase(tutor.getCidade(), searchTerm) ||
                containsIgnoreCase(tutor.getBairro(), searchTerm) ||
                containsIgnoreCase(tutor.getCep(), searchTerm)) {
                filteredTutores.add(tutor);
            }
        }

        for (PetWithTutor pet : petDAO.getPetsWithTutors()) {
            if (containsIgnoreCase(pet.getNome(), searchTerm) ||
                containsIgnoreCase(pet.getTutorNome(), searchTerm) ||
                containsIgnoreCase(pet.getRaca(), searchTerm) ||
                containsIgnoreCase(pet.getEspecie(), searchTerm) ||
                containsIgnoreCase(pet.getCor(), searchTerm) ||
                containsIgnoreCase(pet.getObs(), searchTerm) ||
                containsIgnoreCase(pet.getId_plano(), searchTerm) ||
                containsIgnoreCase(String.valueOf(pet.getIdade()), searchTerm) ||
                containsIgnoreCase(String.valueOf(pet.getSexo()), searchTerm)) {
                filteredPets.add(pet);
            }
        }

        tutorTable.setItems(filteredTutores);
        petTable.setItems(filteredPets);
    }

    private boolean containsIgnoreCase(String source, String searchTerm) {
        return source != null && source.toLowerCase().contains(searchTerm);
    }
    
    @FXML
    private void handleEdit() {
        if (lastFocusedTable == null) {
            showAlert("Erro", "Selecione um tutor ou pet para editar", Alert.AlertType.WARNING);
            return;
        }
        
        if (lastFocusedTable == tutorTable) {
            handleEditTutor();
        } else if (lastFocusedTable == petTable) {
            handleEditPet();
        }
    }
    
    private void handleEditTutor() {
        Tutor selectedTutor = tutorTable.getSelectionModel().getSelectedItem();
        
        if (selectedTutor == null) {
            showAlert("Erro", "Selecione um tutor para editar", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CadastroTutor.fxml"));
            Parent root = loader.load();
            
            CadastroTutorController controller = loader.getController();
            controller.setTutorForEdit(selectedTutor);
            
            Stage editStage = new Stage();
            editStage.setScene(new Scene(root));
            editStage.setTitle("Editar Tutor");
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.initOwner(currentStage);
            
            currentStage.hide();
            
            editStage.setOnHidden(e -> {
                currentStage.show();
                refreshTables();
            });
            
            editStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleEditPet() {
        PetWithTutor selectedPet = petTable.getSelectionModel().getSelectedItem();
        
        if (selectedPet == null) {
            showAlert("Erro", "Selecione um pet para editar", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CadastroPet.fxml"));
            Parent root = loader.load();
            
            CadastroPetController controller = loader.getController();
            Pet pet = new Pet(
                selectedPet.getId(),
                selectedPet.getNome(),
                selectedPet.getDataNasc(),
                selectedPet.getId_tutor(),
                selectedPet.getRaca(),
                selectedPet.getEspecie(),
                selectedPet.getSexo(),
                selectedPet.getCor(),
                selectedPet.getObs(),
                selectedPet.getId_plano(),
                selectedPet.getIdade()
            );
            controller.setPetForEdit(pet);
            
            Stage editStage = new Stage();
            editStage.setScene(new Scene(root));
            editStage.setTitle("Editar Pet");
            editStage.initModality(Modality.APPLICATION_MODAL);
            editStage.initOwner(currentStage);
            
            currentStage.hide();
            
            editStage.setOnHidden(e -> {
                currentStage.show();
                refreshTables();
            });
            
            editStage.showAndWait();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDelete() {
        if (lastFocusedTable == null) {
            showAlert("Erro", "Selecione um tutor ou pet para excluir", Alert.AlertType.WARNING);
            return;
        }
        
        if (lastFocusedTable == tutorTable) {
            handleDeleteTutor();
        } else if (lastFocusedTable == petTable) {
            handleDeletePet();
        }
    }
    
    private void handleDeleteTutor() {
        Tutor selectedTutor = tutorTable.getSelectionModel().getSelectedItem();
        
        if (selectedTutor == null) {
            showAlert("Erro", "Selecione um tutor para excluir", Alert.AlertType.WARNING);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Excluir Tutor");
        alert.setContentText("Tem certeza que deseja excluir o tutor " + selectedTutor.getNome() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = new TutorDAO().softDeleteTutor(selectedTutor.getId());
                
                if (success) {
                    showAlert("Sucesso", "Tutor excluído com sucesso", Alert.AlertType.INFORMATION);
                    refreshTables();
                } else {
                    showAlert("Erro", "Falha ao excluir tutor", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void handleDeletePet() {
        PetWithTutor selectedPet = petTable.getSelectionModel().getSelectedItem();
        
        if (selectedPet == null) {
            showAlert("Erro", "Selecione um pet para excluir", Alert.AlertType.WARNING);
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Excluir Pet");
        alert.setContentText("Tem certeza que deseja excluir o pet " + selectedPet.getNome() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = new PetDAO().deletePet(selectedPet.getId());
                
                if (success) {
                    showAlert("Sucesso", "Pet excluído com sucesso", Alert.AlertType.INFORMATION);
                    refreshTables();
                } else {
                    showAlert("Erro", "Falha ao excluir pet", Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void refreshTables() {
        loadTutorData();
        petTable.setItems(FXCollections.observableArrayList());
    }
    
    public void loadTutorData() {
        TutorDAO tutorDAO = new TutorDAO();
        ObservableList<Tutor> tutores = tutorDAO.getTutores();
        tutorTable.setItems(tutores);
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}