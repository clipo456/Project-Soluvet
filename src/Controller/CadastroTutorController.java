package Controller;

import Model.Tutor;
import Model.TutorDAO;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class CadastroTutorController extends Navigation implements Initializable {

    // Form fields
    @FXML private TextField nomeTxt;
    @FXML private TextField cpfTxt;
    @FXML private TextField telTxt;
    @FXML private TextField bairroTxt;
    @FXML private DatePicker dataNascPicker;
    @FXML private TextField cepTxt;
    @FXML private TextField ruaTxt;
    @FXML private TextField numeroTxt;
    @FXML private TextField cidadeTxt;
    @FXML private TextField compTxt;
    @FXML private Button btnCadastrar;
    @FXML private Button btnLimpar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize date picker with current date
        dataNascPicker.setValue(LocalDate.now());
        
        // Button actions
        btnCadastrar.setOnAction(e -> cadastrarTutor());
        btnLimpar.setOnAction(e -> limparCampos());
    }


    @FXML
    private void cadastrarTutor() {
        // Validate required fields
        if (!validateFields()) return;
        
        // Get formatted birth date
        String dataNasc = dataNascPicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        // Create Tutor object
        Tutor tutor = new Tutor(
            0, // ID will be auto-generated
            nomeTxt.getText().trim(),
            cpfTxt.getText().replaceAll("[^0-9]", ""),
            telTxt.getText().replaceAll("[^0-9]", ""),
            dataNasc,
            ruaTxt.getText().trim(),
            cidadeTxt.getText().trim(),
            bairroTxt.getText().trim(),
            cepTxt.getText().replaceAll("[^0-9]", ""),
            compTxt.getText().trim(),
            numeroTxt.getText().trim()
        );

        // Save to database
        boolean success = new TutorDAO().insertTutor(tutor);
        
        if (success) {
            showAlert("Sucesso", "Tutor cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            limparCampos();
        } else {
            showAlert("Erro", "Falha ao cadastrar tutor", Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        // Check empty fields
        if (nomeTxt.getText().trim().isEmpty() ||
            cpfTxt.getText().replaceAll("[^0-9]", "").length() != 11 ||
            telTxt.getText().replaceAll("[^0-9]", "").length() < 10 ||
            bairroTxt.getText().trim().isEmpty() ||
            dataNascPicker.getValue() == null ||
            cepTxt.getText().replaceAll("[^0-9]", "").length() != 8 ||
            ruaTxt.getText().trim().isEmpty() ||
            numeroTxt.getText().trim().isEmpty() ||
            cidadeTxt.getText().trim().isEmpty()) {
            
            showAlert("Erro", "Preencha todos os campos corretamente", Alert.AlertType.ERROR);
            return false;
        }
        
        // Validate age (at least 18 years old)
        if (Period.between(dataNascPicker.getValue(), LocalDate.now()).getYears() < 18) {
            showAlert("Erro", "O tutor deve ter pelo menos 18 anos", Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }

    @FXML
    private void limparCampos() {
        nomeTxt.clear();
        cpfTxt.clear();
        telTxt.clear();
        bairroTxt.clear();
        dataNascPicker.setValue(LocalDate.now());
        cepTxt.clear();
        ruaTxt.clear();
        numeroTxt.clear();
        cidadeTxt.clear();
        compTxt.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}