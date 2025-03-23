/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controller;

import Model.Authenticator;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
      public void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        
        if (Authenticator.login(username, password)) {
                showAlert("Login bem-sucedido!", "Bem-vindo, " + username);
                abrirOutraTela(event);
            } else {
                showAlert("Erro de Login", "Usuário ou senha inválidos.");
            }
        }
        private void showAlert(String title, String message) {
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
        }

        public void abrirOutraTela(ActionEvent event) {
        try {
            // Carregar o novo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Home.fxml"));
            Parent root = loader.load();

            // Criar nova janela
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();

            //Fechar a janela atual
            Stage janelaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janelaAtual.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
     /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
