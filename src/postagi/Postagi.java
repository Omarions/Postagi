/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postagi;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Omar
 */
public class Postagi extends Application {
    @FXML
    public static Stage mainStage;
    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/view/PostagiLayout.fxml"));
        Scene scene = null;
        
        String osName = System.getProperty("os.name");
        if(osName != null && osName.startsWith("Windows")){
            scene = (new WindowsHack()).getShadowScene(root);
            stage.initStyle(StageStyle.TRANSPARENT);
        }else{
            scene = new Scene(root);
            stage.initStyle(StageStyle.UNDECORATED);
        }
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        
        stage.setTitle("Postagi");
        stage.setScene(scene);
        stage.setMinHeight(500.0d);
        stage.setMinWidth(620.0d);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
