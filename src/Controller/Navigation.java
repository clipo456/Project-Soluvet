/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Navigation {
    
    public void abrirHome(ActionEvent event) {
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
    
    public void abrirConsulta(ActionEvent event) {
        try {
            // Carregar o novo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Consulta.fxml"));
            Parent root = loader.load();

            // Criar nova janela
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Consulta");
            stage.show();

            //Fechar a janela atual
            Stage janelaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janelaAtual.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void abrirCadastroTutor(ActionEvent event) {
        try {
            // Carregar o novo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CadastroTutor.fxml"));
            Parent root = loader.load();

            // Criar nova janela
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("CadastroTutor");
            stage.show();

            //Fechar a janela atual
            Stage janelaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janelaAtual.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void abrirCadastroPet(ActionEvent event) {
        try {
            // Carregar o novo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/CadastroPet.fxml"));
            Parent root = loader.load();

            // Criar nova janela
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("CadastroPet");
            stage.show();

            //Fechar a janela atual
            Stage janelaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            janelaAtual.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
