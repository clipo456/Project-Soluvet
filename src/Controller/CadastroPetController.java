package Controller;

import Model.Pet;
import Model.PetDAO;
import Model.Plano;
import Model.PlanoDAO;
import Model.Tutor;
import Model.TutorDAO;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class CadastroPetController extends Navigation implements Initializable {

    @FXML private TextField nomeTxt;
    @FXML private TextField racaTxt;
    @FXML private TextField especTxt;
    @FXML private TextField corTxt;
    @FXML private TextField SexoTxt;
    @FXML private TextField obsTxt;
    @FXML private DatePicker dataNascPicker;
    @FXML private MenuButton tutorMenu;
    @FXML private MenuButton planoMenu;
    @FXML private Button btnCadastrar;
    @FXML private Button btnLimpar;
    
    private Pet petForEdit;
    private int tutorId;
    private PlanoDAO planoDAO;
    private TutorDAO tutorDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        planoDAO = new PlanoDAO();
        tutorDAO = new TutorDAO();
        loadTutorMenu();
        loadPlanoMenu();
    }
    
    private void loadTutorMenu() {
        tutorMenu.getItems().clear();
        
        tutorDAO.getTutores().forEach(tutor -> {
            MenuItem item = new MenuItem(tutor.getNome() + " - " + tutor.getCpf());
            item.setOnAction(e -> {
                tutorMenu.setText(tutor.getNome());
                tutorId = tutor.getId();
            });
            tutorMenu.getItems().add(item);
        });
    }
    
    private void loadPlanoMenu() {
        planoMenu.getItems().clear();
        planoMenu.setText("Selecione o Plano");
        
        ObservableList<Plano> planos = planoDAO.getPlanos();
        for (Plano plano : planos) {
            MenuItem item = new MenuItem(plano.getNome());
            item.setOnAction(e -> planoMenu.setText(plano.getNome()));
            planoMenu.getItems().add(item);
        }
    }

    @FXML
    private void cadastrarPet() {
        if (!validateFields()) return;
        
        String planoNome = planoMenu.getText();
        if (planoNome.equals("Selecione o Plano")) {
            showAlert("Erro", "Selecione um plano válido", Alert.AlertType.ERROR);
            return;
        }
        
        String nome = nomeTxt.getText();
        LocalDate dataNasc = dataNascPicker.getValue();
        String raca = racaTxt.getText();
        String especie = especTxt.getText();
        char sexo = SexoTxt.getText().charAt(0);
        String cor = corTxt.getText();
        String obs = obsTxt.getText();
        
        PetDAO petDAO = new PetDAO();
        boolean success;
        
        if (petForEdit == null) {
            Pet newPet = new Pet(0, nome, dataNasc, tutorId, raca, especie, 
                                sexo, cor, obs, planoNome, calculateAge(dataNasc));
            success = petDAO.insertPet(newPet);
        } else {
            petForEdit.setNome(nome);
            petForEdit.setDataNasc(dataNasc);
            petForEdit.setId_tutor(tutorId);
            petForEdit.setRaca(raca);
            petForEdit.setEspecie(especie);
            petForEdit.setSexo(sexo);
            petForEdit.setCor(cor);
            petForEdit.setObs(obs);
            petForEdit.setId_plano(planoNome);
            petForEdit.setIdade(calculateAge(dataNasc));
            
            success = petDAO.updatePet(petForEdit);
        }
        
        if (success) {
            showAlert("Sucesso", petForEdit == null ? "Pet cadastrado com sucesso!" : "Pet atualizado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();
        } else {
            showAlert("Erro", "Falha ao salvar pet", Alert.AlertType.ERROR);
        }
    }
    
    private int calculateAge(LocalDate birthDate) {
        return birthDate == null ? 0 : LocalDate.now().getYear() - birthDate.getYear();
    }
    
    @FXML
    private void limparCampos() {
        nomeTxt.clear();
        racaTxt.clear();
        especTxt.clear();
        corTxt.clear();
        SexoTxt.clear();
        obsTxt.clear();
        dataNascPicker.setValue(null);
        tutorMenu.setText("Selecione o Tutor");
        planoMenu.setText("Selecione o Plano");
        petForEdit = null;
        btnCadastrar.setText("Cadastrar");
    }
    
    private boolean validateFields() {
        if (nomeTxt.getText().isEmpty() || racaTxt.getText().isEmpty() || 
            especTxt.getText().isEmpty() || corTxt.getText().isEmpty() || 
            SexoTxt.getText().isEmpty() || dataNascPicker.getValue() == null || 
            tutorMenu.getText().equals("Selecione o Tutor") || 
            planoMenu.getText().equals("Selecione o Plano")) {
            
            showAlert("Erro", "Preencha todos os campos obrigatórios", Alert.AlertType.WARNING);
            return false;
        }
        
        if (SexoTxt.getText().length() != 1 || !("MmFf".contains(SexoTxt.getText().toUpperCase()))) {
            showAlert("Erro", "Sexo deve ser M ou F", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    public void setPetForEdit(Pet pet) {
        this.petForEdit = pet;
        loadPetData();
        btnCadastrar.setText("Atualizar");
    }
    
    private void loadPetData() {
        if (petForEdit == null) return;
        
        nomeTxt.setText(petForEdit.getNome());
        racaTxt.setText(petForEdit.getRaca());
        especTxt.setText(petForEdit.getEspecie());
        corTxt.setText(petForEdit.getCor());
        SexoTxt.setText(String.valueOf(petForEdit.getSexo()));
        obsTxt.setText(petForEdit.getObs());
        dataNascPicker.setValue(petForEdit.getDataNasc());
        
        Tutor tutor = tutorDAO.getTutorById(petForEdit.getId_tutor());
        if (tutor != null) {
            tutorMenu.setText(tutor.getNome());
            tutorId = tutor.getId();
        }
        
        planoMenu.setText(petForEdit.getId_plano());
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}