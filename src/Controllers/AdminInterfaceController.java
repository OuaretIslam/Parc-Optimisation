package Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


public class AdminInterfaceController{

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button EmpM;
    @FXML
    private Button ProductM;
    @FXML 
    private Button BackB;

    @FXML
    private void handleBackB(ActionEvent event) {
        loadPage("/Views/Login.fxml");
    }
    @FXML
    private void handleEmpMan(ActionEvent event) {
        loadPage("/Views/EmployeeManagement.fxml");
    }
    @FXML
    private void handleProductMan(ActionEvent event) {
        loadPage("/Views/ProductManagement.fxml");
    }
    @FXML
    private void handleTruckMan(ActionEvent event) {
        loadPage("/Views/TrucksManagement.fxml");
    }
    
    private void loadPage(String fxmlPath) {
        try {
            // Load the appropriate FXML page based on user role
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Scene scene = new Scene(page);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the page.");
        }
    }
private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);

    // Remove the default header text
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Customize the alert dialog
    alert.getDialogPane().setStyle(
        "-fx-background-color: #fef2f2; " +  // Light red background
        "-fx-background-radius: 10px;"
    );

    // Style the OK button
    ((Button) alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0))).setStyle(
        "-fx-background-color: #f44336; " + // Red background
        "-fx-text-fill: white; " +          // White text
        "-fx-font-weight: bold; " +
        "-fx-background-radius: 5px;"
    );

    alert.showAndWait();
}
    
}
