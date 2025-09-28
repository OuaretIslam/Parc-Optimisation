package Controllers;

import GestionMode.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class ProductEditController {

    @FXML
    private TextField ProductNE; // Username field
    @FXML
    private TextField ProductQE; // Password field
    @FXML
    private TextField ProductPE;     // Role field

    private Product selectedProduct; // Reference to the selected employee
    private ProductManagementController managementController; // Reference to the main controller

    // Method to initialize the form with the selected employee's data
public void setProductData(Product product) {
    this.selectedProduct = product;
    if (product != null) {
        System.out.println("Editing Product: " + product.getProductId() + ", " + product.getProductN());
        ProductNE.setText(product.getProductN());
        ProductQE.setText(String.valueOf(product.getProductQtt()));
        ProductPE.setText(String.valueOf(product.getProductPrice()));
    } else {
        System.out.println("No product selected for editing!");
    }
}




    // Set the reference to the management controller
    public void setManagementController(ProductManagementController controller) {
        this.managementController = controller;
    }

    // Handle the confirm button action
@FXML
private void handleConfirm(ActionEvent event) {
    if (selectedProduct != null) {
        try {
            System.out.println("Before Update: " + selectedProduct);

            selectedProduct.setProductN(ProductNE.getText());
            selectedProduct.setProductQtt(Integer.parseInt(ProductQE.getText()));
            selectedProduct.setProductPrice(Integer.parseInt(ProductPE.getText()));

            System.out.println("After Update: " + selectedProduct);

            if (managementController.updateProductInDatabase(selectedProduct)) {
                managementController.loadProductData(); // Refresh the TableView
                showAlert("Success", "Product updated successfully.");
                closeWindow();
            } else {
                showAlert("Error", "Failed to update the product.");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numeric values for quantity and price.");
        }
    } else {
        showAlert("Error", "No product selected to update.");
    }
}



    // Handle the cancel button action
    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow();
    }

    // Close the current window
    private void closeWindow() {
        Stage stage = (Stage) ProductNE.getScene().getWindow();
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
