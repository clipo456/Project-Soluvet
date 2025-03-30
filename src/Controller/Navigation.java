package Controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Navigation {
    
    public void abrirHome(ActionEvent event) throws IOException {
        carregarJanela("/View/Home.fxml", "Home", event);
    }
    
    public void abrirCadastroTutor(ActionEvent event) throws IOException {
        carregarJanela("/View/CadastroTutor.fxml", "Cadastro Tutor", event);
    }
    
    public void abrirCadastroPet(ActionEvent event) throws IOException {
        carregarJanela("/View/CadastroPet.fxml", "Cadastro Pet", event);
    }
    
    public void abrirConsulta(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/Consulta.fxml"));
        Parent root = loader.load();
        
        ConsultaController controller = loader.getController();
        Stage stage = new Stage();
        controller.setStage(stage);
        
        stage.setScene(new Scene(root));
        stage.setTitle("Consulta");
        stage.show();
        
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
    
    private void carregarJanela(String fxml, String title, ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
        
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }
}