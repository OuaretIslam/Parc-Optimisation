package GdpMain;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class SceneLoader {

    public static Scene loadScene(String fxmlFile) throws IOException {
        // Using the correct relative path for FXML resources
        FXMLLoader loader = new FXMLLoader();
        
        // Ensure the path is correct from the root of the classpath
        loader.setLocation(SceneLoader.class.getResource(fxmlFile)); 
        
        Parent root = loader.load();
        
        return new Scene(root);
    }
}
