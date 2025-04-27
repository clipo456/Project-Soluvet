package Controller;

import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.scene.control.Alert;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConfigController extends Navigation implements Initializable {
    
    @FXML
    private AnchorPane configPane;
    private Stage configStage;
    private Stage parentStage; // Guarda a tela que abriu a configuração (ex.: Home.fxml)

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // Método atualizado para receber o Stage pai (tela que chamou a configuração)
    public void showConfigWindow(Stage parentStage) {
        this.parentStage = parentStage;

        if (configStage == null) {
            configStage = new Stage();
            configStage.initModality(Modality.WINDOW_MODAL);
            configStage.initStyle(StageStyle.UTILITY);
            configStage.setTitle("Configurações");
            configStage.initOwner(parentStage);

            Scene scene = new Scene(configPane);
            configStage.setScene(scene);
            configStage.setResizable(false);

            configStage.setOnCloseRequest(e -> {
                configStage = null;
            });
        }

        configStage.show();
    }
    
    public void setConfigPane(AnchorPane pane) {
        this.configPane = pane;
    }
    
    @FXML
    public void abrirUsuarios(ActionEvent event) throws IOException {
        if(sessao.isAdmin()){
            if (configStage != null) {
                configStage.close();
                configStage = null;
            }
            // Obtém o mainStage global
            Stage mainStage = Navigation.getMainStage();
            if (mainStage != null) {
                // Fecha a tela atual (que abriu a configuração)
                if (parentStage != null && parentStage != mainStage) {
                    parentStage.close();
                }
                loadView("/View/CadastroUsuario.fxml", "Gerenciamento de Usuários", mainStage);
            }
        }else{
            showAlert("Erro!", "O usuário logado não tem privilégios de administrador.");
        }
    }

    @FXML
    public void logout(ActionEvent event) throws IOException {
        if (configStage != null) {
            configStage.close();
            configStage = null;
        }

        Stage mainStage = Navigation.getMainStage();
        if (mainStage != null) {
            if (parentStage != null && parentStage != mainStage) {
                parentStage.close();
            }
            
            loadView("/View/Login.fxml", "Login", mainStage);
            
        }
    }
    
    private void loadView(String fxmlPath, String title, Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }
    
    private void showAlert(String title, String message) {
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
        }
}