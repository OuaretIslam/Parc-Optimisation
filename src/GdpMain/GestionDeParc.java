package GdpMain;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class GestionDeParc extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        
        Scene scene = SceneLoader.loadScene("/Views/Login.fxml");
        
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
