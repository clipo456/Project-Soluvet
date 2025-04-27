/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMain.java to edit this template
 */

import Controller.Navigation;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author yurim
 */
public class Soluvet extends Application {
 @Override
    public void start(Stage primaryStage) throws Exception {
        // Set the main stage in Navigation
        Navigation.setMainStage(primaryStage);
        
        // Load the initial view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Login.fxml"));
        Parent root = loader.load();
        
        // Set up the primary stage
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Login");
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
