package Controllers;

import GestionMode.Truck;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class TruckEditController {

    @FXML
    private TextField TruckNE; // Username field
    @FXML
    private TextField TruckTE; // Password field
    @FXML
    private TextField TruckBE;// Role field
    @FXML
    private TextField TruckSE;
    @FXML
    private TextField TruckRespIdE;

    private Truck selectedTruck; // Reference to the selected employee
    private TrucksManagementController managementController; // Reference to the main controller

    // Method to initialize the form with the selected employee's data
public void setTruckData(Truck Truck) {
    this.selectedTruck = Truck;
    if (Truck != null) {
        System.out.println("Editing Truck: " + Truck.getTruckId() + ", " + Truck.getTruckN());
        TruckNE.setText(Truck.getTruckN());
        TruckTE.setText(Truck.getTruckType());
        TruckBE.setText(Truck.getTruckBrand());
        TruckSE.setText(Truck.getTrucksStat());
        TruckRespIdE.setText(String.valueOf(Truck.getRespDriverId()));
    } else {
        System.out.println("No truck selected for editing!");
    }
}




    // Set the reference to the management controller
    public void setManagementController(TrucksManagementController controller) {
        this.managementController = controller;
    }

    // Handle the confirm button action
@FXML
private void handleConfirm(ActionEvent event) {
    if (selectedTruck != null) {
        try {
            System.out.println("Before Update: " + selectedTruck);

            selectedTruck.setTruckN(TruckNE.getText());
            selectedTruck.setTruckType(TruckTE.getText());
            selectedTruck.setTruckBrand(TruckBE.getText());
            selectedTruck.setTrucksStat(TruckSE.getText());
            selectedTruck.setRespDriverId(Integer.parseInt(TruckRespIdE.getText()));

            System.out.println("After Update: " + selectedTruck);

            if (managementController.updateTruckInDatabase(selectedTruck)) {
                managementController.loadTruckData(); // Refresh the TableView
                showAlert("Success", "truck updated successfully.");
                closeWindow();
            } else {
                showAlert("Error", "Failed to update the truck.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values for quantity and price.");
        }
    } else {
        showAlert("Error", "No Truck selected to update.");
    }
}



    // Handle the cancel button action
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    // Close the current window
    private void closeWindow() {
        Stage stage = (Stage) TruckNE.getScene().getWindow();
        stage.close();
    }
    private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);

    // Remove the default header text
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Customize the alert dialog
    alert.getDialogPane().setStyle(
        "-fx-background-color: #f0f0f0; " +  // Light gray background
        "-fx-background-radius: 10px;"
    );

    // Style the OK button
    ((Button) alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0))).setStyle(
        "-fx-background-color: #4CAF50; " + // Green background
        "-fx-text-fill: white; " +   // White text
        "-fx-cursor: pointer;"+
        "-fx-font-weight: bold; " +
        "-fx-background-radius: 5px;"
    );

    alert.showAndWait();
}
}
