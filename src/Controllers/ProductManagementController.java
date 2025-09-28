package Controllers;

import GestionMode.Product;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ProductManagementController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button BackB, addButton, editButton, deleteButton;
    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, Integer> colProductId;
    @FXML
    private TableColumn<Product, String> colProductN;
    @FXML
    private TableColumn<Product, Integer> colProductQtt;
    @FXML
    private TableColumn<Product, Integer> colProductPrice;

    private ObservableList<Product> productList;

    // Database connection credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GdpDb"; // Adjust URL and DB name
    private static final String DB_USER = "root"; // Database username
    private static final String DB_PASSWORD = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        productList = FXCollections.observableArrayList();
        productTable.setItems(productList); // Bind TableView to productList
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductN.setCellValueFactory(new PropertyValueFactory<>("ProductN"));
        colProductQtt.setCellValueFactory(new PropertyValueFactory<>("ProductQtt"));
        colProductPrice.setCellValueFactory(new PropertyValueFactory<>("productPrice"));
        loadProductData(); // Load initial data
    }

    public void loadProductData() {
        productList.clear(); // Clear the list before reloading
        productList.addAll(getProductData());
    }

    private ObservableList<Product> getProductData() {
        ObservableList<Product> data = FXCollections.observableArrayList();
        String query = "SELECT ProductId, ProductN, ProductQtt, ProductPrice FROM product";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                data.add(new Product(
                        rs.getInt("ProductId"),
                        rs.getString("ProductN"),
                        rs.getInt("ProductQtt"),
                        rs.getInt("ProductPrice")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to fetch data from the database.");
        }

        return data;
    }

    @FXML
    private void handleBackButton(ActionEvent event) {
        loadPage("/Views/AdminInterface.fxml");
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        openFormWindow();
    }

    @FXML
    private void handleEditButton(ActionEvent event) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductEdit.fxml"));
                Parent root = loader.load();

                ProductEditController editController = loader.getController();
                editController.setProductData(selectedProduct); // Pass the selected product data
                editController.setManagementController(this); // Pass the main controller reference

                Stage stage = new Stage();
                stage.setTitle("Edit Product");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to open the edit window.");
            }
        } else {
            showAlert("Error", "No product selected to edit.");
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            if (deleteProductFromDatabase(selectedProduct.getProductId())) {
                loadProductData(); // Reload data
                showAlert("Success", "Product deleted successfully!");
            } else {
                showAlert("Error", "Failed to delete product.");
            }
        } else {
            showAlert("Error", "No product selected!");
        }
    }

    public void addProduct(Product newProduct) {
        if (insertProductIntoDatabase(newProduct)) {
            loadProductData(); // Reload data
            showAlert("Success", "Product added successfully!");
        } else {
            showAlert("Error", "Failed to add product.");
        }
    }

    private boolean insertProductIntoDatabase(Product product) {
        String query = "INSERT INTO product (ProductN, ProductQtt, ProductPrice) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getProductN());
            stmt.setInt(2, product.getProductQtt());
            stmt.setDouble(3, product.getProductPrice());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProductInDatabase(Product product) {
        String query = "UPDATE product SET ProductN = ?, ProductQtt = ?, ProductPrice = ? WHERE ProductId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, product.getProductN());
            stmt.setInt(2, product.getProductQtt());
            stmt.setDouble(3, product.getProductPrice());
            stmt.setInt(4, product.getProductId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteProductFromDatabase(int productId) {
        String query = "DELETE FROM product WHERE ProductId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void loadPage(String fxmlPath) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent page = loader.load();
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setScene(new Scene(page));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Failed to load the page. Please try again.");
    }
}


    private void openFormWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ProductAdd.fxml"));
            Parent root = loader.load();

            ProductAddController formController = loader.getController();
            formController.setProductManagementController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Product");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the form.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
