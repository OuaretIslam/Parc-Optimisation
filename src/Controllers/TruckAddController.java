package Controllers;

import GestionMode.Truck;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TruckAddController {

    @FXML
    private TextField TruckNF;
    @FXML
    private TextField TruckTF;
    @FXML
    private TextField TruckBF;
    @FXML
    private Button ConfirmFormBT;
    @FXML
    private TextField TruckSF;
    @FXML
    private TextField TruckRespIdF;
    @FXML

    private TrucksManagementController gestionEmpController; // Correct variable name

    // Setter for the main controller
    public void setTruckManagementController(TrucksManagementController controller) {
        this.gestionEmpController = controller; // Assign the correct variable
    }

    @FXML
    private void handleConfirm() {
        String TrucknName = TruckNF.getText().trim();
        String TruckType = TruckTF.getText().trim();
        String TruckBrand = TruckBF.getText().trim();
        String TruckStatus = TruckSF.getText().trim();
        int TruckRespId = Integer.parseInt(TruckRespIdF.getText().trim());
        

        // Validate fields before proceeding
        if (TrucknName.isEmpty() || TruckType.isEmpty() || TruckBrand.isEmpty() || TruckStatus.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        try {

            if (gestionEmpController == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "truck management controller not set.");
                return;
            }

            // Create a new Product instance
            Truck newTruck = new Truck(TrucknName, TruckType, TruckBrand, TruckStatus, TruckRespId);

            // Add the new Product through the product management controller
            gestionEmpController.addTruck(newTruck);

            showAlert(Alert.AlertType.INFORMATION, "Success", "truck added successfully.");
            clearFields();
            closeForm();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric informations.");
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void clearFields() {
        TruckNF.clear();
        TruckTF.clear();
        TruckBF.clear();
        TruckSF.clear();
        TruckRespIdF.clear();
    }

    private void closeForm() {
        ConfirmFormBT.getScene().getWindow().hide();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Customize the alert dialog
        if (alertType.equals(Alert.AlertType.ERROR)) {
            alert.getDialogPane().setStyle(
                "-fx-background-color: #fef2f2; " +  // Light red background
                "-fx-background-radius: 10px;"
            );

            // Style the OK button
            ((Button) alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0))).setStyle(
                "-fx-background-color: #f44336; " + // Red background
                "-fx-text-fill: white; " +          // White text
                "cursor: pointer;" +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px;"
            );
        } else {
            alert.getDialogPane().setStyle(
                "-fx-background-color: #f0f0f0; " +  // Light grey background
                "-fx-background-radius: 10px;"
            );

            // Style the OK button
            ((Button) alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0))).setStyle(
                "-fx-background-color: #4CAF50; " + // Green background
                "-fx-text-fill: white; " +          // White text
                "cursor: pointer;" +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5px;"
            );
        }

        alert.showAndWait();
    }
}
