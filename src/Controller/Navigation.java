package Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigation {
    
    private static Stage mainStage; // Keep track of the main stage
    
    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }
    
    public void abrirHome(ActionEvent event) throws IOException {
        loadView("/View/Home.fxml", "Home", event);
    }
    
    public void abrirCadastroTutor(ActionEvent event) throws IOException {
        loadView("/View/CadastroTutor.fxml", "Cadastro Tutor", event);
    }
    
    public void abrirCadastroPet(ActionEvent event) throws IOException {
        loadView("/View/CadastroPet.fxml", "Cadastro Pet", event);
    }
    
    public void abrirConsulta(ActionEvent event) throws IOException {
        loadView("/View/Consulta.fxml", "Consulta", event);
    }
    
    public void abrirCadastroAgendamento(ActionEvent event) throws IOException {
        loadView("/View/CadastroAgendamento.fxml", "Cadastro Agendamento", event);
    }
    
    private void loadView(String fxmlPath, String title, ActionEvent event) throws IOException {
        // Get the current stage from the event source
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        // Load the new FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        // Set the new scene on the current stage
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle(title);
        currentStage.show();
        
        // If we're loading Consulta, set its stage reference
        if (loader.getController() instanceof ConsultaController) {
            ((ConsultaController) loader.getController()).setStage(currentStage);
        }
    }
}