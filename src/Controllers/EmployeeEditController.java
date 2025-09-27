package Controllers;

import GestionMode.Employee;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class EmployeeEditController {

    @FXML
    private TextField EmployeeNE; // Username field
    @FXML
    private TextField PasswordPE; // Password field
    @FXML
    private TextField RoleRE;     // Role field

    private Employee selectedEmployee; // Reference to the selected employee
    private EmployeeManagementController managementController; // Reference to the main controller

    // Method to initialize the form with the selected employee's data
    public void setEmployeeData(Employee employee) {
        this.selectedEmployee = employee;
        if (employee != null) {
            EmployeeNE.setText(employee.getUsername());
            PasswordPE.setText(employee.getPassword());
            RoleRE.setText(employee.getRole());
        }
    }

    // Set the reference to the management controller
    public void setManagementController(EmployeeManagementController controller) {
        this.managementController = controller;
    }

    // Handle the confirm button action
    @FXML
    private void handleConfirm(ActionEvent event) {
        if (selectedEmployee != null) {
            // Update the employee's data
            selectedEmployee.setUsername(EmployeeNE.getText());
            selectedEmployee.setPassword(PasswordPE.getText());
            selectedEmployee.setRole(RoleRE.getText());

            // Update the database and refresh the table
            if (managementController.updateEmployeeInDatabase(selectedEmployee)) {
                managementController.loadEmployeeData(); // Refresh the TableView
                showAlert("Succes", "employee updated succesfuly.");
                closeWindow(); // Close the edit window
            } else {
                showAlert("Error", "Failed to update the employee.");
            }
        } else {
            showAlert("Error", "No employee selected to update.");
        }
    }

    // Handle the cancel button action
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    // Close the current window
    private void closeWindow() {
        Stage stage = (Stage) EmployeeNE.getScene().getWindow();
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
