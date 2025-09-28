package Controllers;

import GestionMode.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EmployeeFormController {

    @FXML
    private TextField EmployeeNF, PasswordPF, RoleRF; // Fields for username, password, role
    @FXML
    private Button confirmFormB, cancelFormB; // Buttons for form actions

    private EmployeeManagementController gestionEmpController;

    // Setter for the main controller
    public void setGestionEmpController(EmployeeManagementController controller) {
        this.gestionEmpController = controller;
    }

@FXML
private void handleConfirm() {
    String username = EmployeeNF.getText().trim();
    String password = PasswordPF.getText().trim();
    String role = RoleRF.getText().trim();

    // Validate fields before proceeding
    if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
        showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
    } else if (!role.equalsIgnoreCase("Employe")) { // Validate that the role is "Employe"
        showAlert(Alert.AlertType.ERROR, "Error", "The role must be 'Employe'.");
    } else if (gestionEmpController == null) {
        showAlert(Alert.AlertType.ERROR, "Error", "Controller not set.");
    } else {
        // Create a new Employee instance without ID
        Employee newEmployee = new Employee(username, password, role);

        // Add the new Employee through the main controller
        gestionEmpController.addEmployee(newEmployee);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Employee added successfully.");
        clearFields();
        closeForm();
    }
}


    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void clearFields() {
        EmployeeNF.clear();
        PasswordPF.clear();
        RoleRF.clear();
    }

    private void closeForm() {
        confirmFormB.getScene().getWindow().hide();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
    alert.setContentText(message);

    // Customize the alert dialog
    if(alertType.equals(Alert.AlertType.ERROR)){
    alert.getDialogPane().setStyle(
        "-fx-background-color: #fef2f2; " +  // Light red background
        "-fx-background-radius: 10px;"
    );

    // Style the OK button
    ((Button) alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0))).setStyle(
        "-fx-background-color: #f44336; " + // Red background
        "-fx-text-fill: white; " +          // White text
        "cursor: pointer;"+
        "-fx-font-weight: bold; " +
        "-fx-background-radius: 5px;"
    );} else {
        alert.getDialogPane().setStyle(
        "-fx-background-color: #f0f0f0; " +  // Light grey background
        "-fx-background-radius: 10px;"
    );

    // Style the OK button
    ((Button) alert.getDialogPane().lookupButton(alert.getButtonTypes().get(0))).setStyle(
        "-fx-background-color: #4CAF50; " + // green background
        "-fx-text-fill: white; " +          // White text
        "-fx-cursor: pointer;"+
        "-fx-font-weight: bold; " +
        "-fx-background-radius: 5px;"
    );
    }

    alert.showAndWait();
    }
}
