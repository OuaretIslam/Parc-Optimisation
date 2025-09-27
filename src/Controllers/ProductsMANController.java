package Controllers;

import java.sql.Connection;
import GestionMode.Product;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ProductsMANController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button BackB, ECancelBE, EEditPB, EDeletePB;

    @FXML
    private TextField EProductQE;

    @FXML
    private TableView<Product> ProductsTable;

    @FXML
    private TableColumn<Product, Integer> colProductIde;

    @FXML
    private TableColumn<Product, String> colProductNa;

    @FXML
    private TableColumn<Product, Integer> colProductQtty;

    private ObservableList<Product> productList;
    private Connection connection;

    // Initialize database connection
    public void initialize() {
        connectDatabase();
        setupTable();
        loadProducts();
    }

    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/GdpDb", "root", "");
        } catch (SQLException e) {
            showAlert("Error", "Failed to connect to the database.");
        }
    }

    private void setupTable() {
        colProductIde.setCellValueFactory(new PropertyValueFactory<>("ProductId"));
        colProductNa.setCellValueFactory(new PropertyValueFactory<>("ProductN"));
        colProductQtty.setCellValueFactory(new PropertyValueFactory<>("ProductQtt"));

        productList = FXCollections.observableArrayList();
        ProductsTable.setItems(productList);
    }

    private void loadProducts() {
        productList.clear();
        try {
            String query = "SELECT * FROM product";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int ProductId = resultSet.getInt("ProductId");
                String ProductN = resultSet.getString("ProductN");
                int ProductQtt = resultSet.getInt("ProductQtt");
                productList.add(new Product(ProductId, ProductN, ProductQtt));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load products.");
        }
    }

    @FXML
    private void handleEditProduct(ActionEvent event) {
        Product selectedProduct = ProductsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to edit.");
            return;
        }

        String productQtt = EProductQE.getText();

        if (productQtt.isEmpty()) {
            showAlert("Error", "Please fill in the product quantity.");
            return;
        }

        try {
            int ProductQttInt = Integer.parseInt(productQtt);
            String query = "UPDATE product SET ProductQtt = ? WHERE ProductId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, ProductQttInt);
            statement.setInt(2, selectedProduct.getProductId());
            statement.executeUpdate();
            loadProducts();
            clearFields();
            showAlert("Success", "Product quantity updated successfully!");
        } catch (SQLException e) {
            showAlert("Error", "Failed to edit product.");
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid quantity.");
        }
    }

    @FXML
    private void handleDeleteProduct(ActionEvent event) {
        Product selectedProduct = ProductsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Please select a product to delete.");
            return;
        }

        try {
            String query = "DELETE FROM product WHERE ProductId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedProduct.getProductId());
            statement.executeUpdate();
            loadProducts();
            showAlert("Success", "Product deleted successfully!");
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete product.");
        }
    }

    private void clearFields() {
        EProductQE.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackB(ActionEvent event) {
        loadPage("/Views/EmployeeInterface.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent page = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(page));
        } catch (Exception e) {
            showAlert("Error", "Failed to load page.");
        }
    }
}
