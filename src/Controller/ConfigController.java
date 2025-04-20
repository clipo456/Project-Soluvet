package Controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConfigController extends Navigation implements Initializable {

    @FXML
    private AnchorPane configPane;
    private Stage configStage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code if needed
    }

    public void showConfigWindow() {
        
        if (configStage == null) {
            configStage = new Stage();
            configStage.initModality(Modality.WINDOW_MODAL);
            configStage.initStyle(StageStyle.UTILITY);
            configStage.setTitle("Configurações");
            
            // Create scene with the configPane
            Scene scene = new Scene(configPane);
            configStage.setScene(scene);
            configStage.setResizable(false);
            
            configStage.setOnCloseRequest(e -> {
                configStage = null;
            });
        }
        
        if (!configStage.isShowing()) {
            // Set owner if possible
            Stage mainStage = Navigation.getMainStage();
            if (mainStage != null) {
                configStage.initOwner(mainStage);
            }
            configStage.show();
        } else {
            configStage.toFront();
        }
    }
    
    public void setConfigPane(AnchorPane pane) {
        this.configPane = pane;
    }
    
    
    
}