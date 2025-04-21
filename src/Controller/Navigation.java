package Controller;

import Model.Session;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Navigation{
    
    Session sessao = Session.getInstance();
    
    private static Stage mainStage; // Keep track of the main stage
    private Stage configStage;

    public static void setMainStage(Stage stage) {
        mainStage = stage;
    }

    public static Stage getMainStage() {
        return mainStage;
    }
    
    public void abrirConfiguracoes(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Config.fxml"));
            AnchorPane root = loader.load();

            ConfigController configController = loader.getController();
            configController.setConfigPane(root);

            // Obter o Stage atual a partir do evento
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            configController.showConfigWindow(currentStage); // Passar o Stage atual
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void abrirHome(ActionEvent event) throws IOException {
        loadView("/View/Home.fxml", "Home", event);
    }
    
    public void abrirCadastroTutor(ActionEvent event) throws IOException {
        loadView("/View/CadastroTutor.fxml", "Gerenciamento de Tutores", event);
    }
    
    public void abrirCadastroPet(ActionEvent event) throws IOException {
        loadView("/View/CadastroPet.fxml", "Gerenciamento de Pets", event);
    }
    
    public void abrirConsulta(ActionEvent event) throws IOException {
        loadView("/View/Consulta.fxml", "Consultas", event);
    }
    
    public void abrirCadastroAgendamento(ActionEvent event) throws IOException {
        loadView("/View/CadastroAgendamento.fxml", "Gerenciamento de Agendamentos", event);
    }
    
    public void abrirCadastroPlanos(ActionEvent event) throws IOException {
        loadView("/View/CadastroPlanos.fxml", "Gerenciamento de Planos", event);
    }
    
    public void abrirCadastroUsuarios(ActionEvent event) throws IOException {
        loadView("/View/CadastroUsuario.fxml", "Gerenciamento de Usuários", event);
    }
    
    public void loadView(String fxmlPath, String title, ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle(title);
        currentStage.show();
        
        if (loader.getController() instanceof ConsultaController) {
            ((ConsultaController) loader.getController()).setStage(currentStage);
        }
    }
    
}
