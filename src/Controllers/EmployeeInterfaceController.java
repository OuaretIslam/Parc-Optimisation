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


public class EmployeeInterfaceController{

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button DriversM;
    @FXML
    private Button ProductsM;
    @FXML 
    private Button DeliveriesM;
    @FXML 
    private Button OrdersM;
    @FXML 
    private Button IrineraryN;
    @FXML 
    private Button TruckSM;
    @FXML 
    private Button BackB;

    @FXML
    private void handleBackB(ActionEvent event) {
        loadPage("/Views/Login.fxml");
    }
    @FXML
    private void handleDriversMB(ActionEvent event) {
        loadPage("/Views/DriversMAN.fxml");
    }
    @FXML
    private void handleProductsMB(ActionEvent event) {
        loadPage("/Views/ProductsMAN.fxml");
    }
    @FXML
    private void handleDeliveriesMB(ActionEvent event) {
        loadPage("/Views/DeliveryMAN.fxml");
    }
    @FXML
    private void handleOrdersMB(ActionEvent event) {
        loadPage("/Views/OrdersMAN.fxml");
    }
    @FXML
    private void handleIrineraryNB(ActionEvent event) {
        loadPage("/Views/ItineraryMAN.fxml");
    }
    @FXML
    private void handleTruckSMB(ActionEvent event) {
        loadPage("/Views/TrucksMAN.fxml");
    }
    @FXML
    private void handleCloseB(ActionEvent event){
    Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
    stage.close();
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
