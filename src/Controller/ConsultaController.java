package Controller;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class ConsultaController extends Navigation implements Initializable {
    
    @FXML private TextField searchField;
    
    // Tutor Table
    @FXML private TableView<Tutor> tutorTable;
    @FXML private TableColumn<Tutor, Integer> idTCol;
    @FXML private TableColumn<Tutor, String> nomeTCol;
    @FXML private TableColumn<Tutor, String> cpfTCol;
    @FXML private TableColumn<Tutor, String> telTCol;
    
    // Pet Table
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize tables
        initializeTutorTable();
        initializePetTable();
        
        // Load initial data
        loadTutorData();
        
        // Set up selection listener
        tutorTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadPetData(newValue.getId());
                }
            });
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
    
    private void loadTutorData() {
        TutorDAO tutorDAO = new TutorDAO();
        ObservableList<Tutor> tutores = tutorDAO.getTutores();
        tutorTable.setItems(tutores);
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

        // Search across both tables
        ObservableList<Tutor> filteredTutores = FXCollections.observableArrayList();
        ObservableList<PetWithTutor> filteredPets = FXCollections.observableArrayList();

        // Search tutors
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

        // Search pets (including their tutor names)
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

        // Update both tables
        tutorTable.setItems(filteredTutores);
        petTable.setItems(filteredPets);
    }

    // Helper method for case-insensitive contains check
    private boolean containsIgnoreCase(String source, String searchTerm) {
        return source != null && source.toLowerCase().contains(searchTerm);
    }
    
}