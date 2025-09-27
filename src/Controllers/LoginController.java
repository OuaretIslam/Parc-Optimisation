package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;
import Database.Database;  // Ensure this import is correct
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class LoginController {

    @FXML
    private TextField UserNField;  // Username field
    @FXML
    private PasswordField MDPfield;   // Password field
    @FXML
    private Button LoginBut;      // Login button
    @FXML
    private AnchorPane rootPane;  // Root pane for scene transitions
    @FXML
    private Label label;          // Label for error messages

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = UserNField.getText();
        String password = MDPfield.getText();

        try {
            // Get the role from the database based on the username and password
            String role = Database.getUserRole(username, password);

            // Check the role and redirect to the appropriate page
            if (role != null) {
                if (role.equals("Admin")) {
                    loadPage("/Views/AdminInterface.fxml");
                } else if (role.equals("Employe")) {
                    loadPage("/Views/EmployeeInterface.fxml");
                } else {
                    showAlert("Login Failed", "Invalid role assigned. Please contact administrator.");
                }
            } else {
                showAlert("Login Failed", "Invalid username or password. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database connection failed!");
        }
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
        "-fx-cursor: pointer;"+
        "-fx-font-weight: bold; " +
        "-fx-background-radius: 5px;"
    );

    alert.showAndWait();
}



}
