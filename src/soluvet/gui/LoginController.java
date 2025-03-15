/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package soluvet.gui;

import DB.Authenticator;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
                // Redirecionar para outra tela, se necessário
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


        
     /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
