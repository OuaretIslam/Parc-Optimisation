package Controllers;

import GestionMode.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ProductAddController {

    @FXML
    private TextField ProductNF;

    @FXML
    private TextField ProductQF, ProductPF; // Fields for product name, quantity, price
    @FXML
    private Button ConfirmFormBP; // Buttons for form actions
    @FXML
    private ProductManagementController gestionEmpController; // Correct variable name

    // Setter for the main controller
    public void setProductManagementController(ProductManagementController controller) {
        this.gestionEmpController = controller; // Assign the correct variable
    }

    @FXML
    private void handleConfirm() {
        String productName = ProductNF.getText().trim();
        String productQuantityStr = ProductQF.getText().trim();
        String productPriceStr = ProductPF.getText().trim();

        // Validate fields before proceeding
        if (productName.isEmpty() || productQuantityStr.isEmpty() || productPriceStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        try {
            // Convert String inputs to correct types
            int productQuantity = Integer.parseInt(productQuantityStr);
            int productPrice = Integer.parseInt(productPriceStr);

            if (gestionEmpController == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Product management controller not set.");
                return;
            }

            // Create a new Product instance
            Product newProduct = new Product(productName, productQuantity, productPrice);

            // Add the new Product through the product management controller
            gestionEmpController.addProduct(newProduct);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Product added successfully.");
            clearFields();
            closeForm();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numeric values for quantity and price.");
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private void clearFields() {
        ProductNF.clear();
        ProductQF.clear();
        ProductPF.clear();
    }

    private void closeForm() {
        ConfirmFormBP.getScene().getWindow().hide();
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
