package Controllers;

import GestionMode.Truck;
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

public class TrucksManagementController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button BackB, TaddButton, TeditButton, TdeleteButton;
    @FXML
    private TableView<Truck> TrucksTable;
    @FXML
    private TableColumn<Truck, Integer> colTruckId;
    @FXML
    private TableColumn<Truck, String> colTruckB;
    @FXML
    private TableColumn<Truck, String> colTruckT;
    @FXML
    private TableColumn<Truck, String> colTruckN;
    @FXML
    private TableColumn<Truck, String> colTrucksStat;
    @FXML
    private TableColumn<Truck, Integer> colTrucksResp;

    private ObservableList<Truck> TruckList;

    // Database connection credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GdpDb"; // Adjust URL and DB name
    private static final String DB_USER = "root"; // Database username
    private static final String DB_PASSWORD = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TruckList = FXCollections.observableArrayList();
        TrucksTable.setItems(TruckList); // Bind TableView to productList
        colTruckId.setCellValueFactory(new PropertyValueFactory<>("TruckId"));
        colTruckB.setCellValueFactory(new PropertyValueFactory<>("TruckBrand"));
        colTruckT.setCellValueFactory(new PropertyValueFactory<>("TruckType"));
        colTruckN.setCellValueFactory(new PropertyValueFactory<>("TruckN"));
        colTrucksStat.setCellValueFactory(new PropertyValueFactory<>("TrucksStat"));
        colTrucksResp.setCellValueFactory(new PropertyValueFactory<>("RespDriverId"));
        loadTruckData(); // Load initial data
    }

    public void loadTruckData() {
        TruckList.clear(); // Clear the list before reloading
        TruckList.addAll(getTruckData());
    }

    private ObservableList<Truck> getTruckData() {
        ObservableList<Truck> data = FXCollections.observableArrayList();
        String query = "SELECT TruckId, TruckBrand, TruckType, TruckN, TrucksStat, RespDriverId FROM trucks";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                data.add(new Truck(
                        rs.getInt("TruckId"),
                        rs.getString("TruckBrand"),
                        rs.getString("TruckType"),
                        rs.getString("TruckN"),
                        rs.getString("TrucksStat"),
                        rs.getInt("RespDriverId")
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
        Truck selectedTruck = TrucksTable.getSelectionModel().getSelectedItem();
        if (selectedTruck != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/TruckEdit.fxml"));
                Parent root = loader.load();

                TruckEditController editController = loader.getController();
                editController.setTruckData(selectedTruck); // Pass the selected product data
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
            showAlert("Error", "No Truck selected to edit.");
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Truck selectedProduct = TrucksTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            if (deleteTruckFromDatabase(selectedProduct.getTruckId())) {
                loadTruckData(); // Reload data
                showAlert("Success", "Truck deleted successfully!");
            } else {
                showAlert("Error", "Failed to delete product.");
            }
        } else {
            showAlert("Error", "No Truck selected!");
        }
    }

    public void addTruck(Truck newTruck) {
        if (insertTruckIntoDatabase(newTruck)) {
            loadTruckData(); // Reload data
            showAlert("Success", "Truck added successfully!");
        } else {
            showAlert("Error", "Failed to add Truck.");
        }
    }

    private boolean insertTruckIntoDatabase(Truck Truck) {
        String query = "INSERT INTO trucks (TruckBrand, TruckType, TruckN, TrucksStat, RespDriverId) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, Truck.getTruckBrand());
            stmt.setString(2, Truck.getTruckType());
            stmt.setString(3, Truck.getTruckN());
            stmt.setString(4, Truck.getTrucksStat());
            stmt.setInt(5, Truck.getRespDriverId());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        Truck.setTruckId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateTruckInDatabase(Truck Truck) {
        String query = "UPDATE trucks SET TruckBrand = ?, TruckType = ?, TruckN = ?, TrucksStat = ?, RespDriverId = ? WHERE TruckId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, Truck.getTruckBrand());
            stmt.setString(2, Truck.getTruckType());
            stmt.setString(3, Truck.getTruckN());
            stmt.setString(4, Truck.getTrucksStat());
            stmt.setInt(5, Truck.getRespDriverId());
            stmt.setInt(6, Truck.getTruckId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteTruckFromDatabase(int TruckId) {
        String query = "DELETE FROM trucks WHERE TruckId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, TruckId);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/TruckAdd.fxml"));
            Parent root = loader.load();

            TruckAddController formController = loader.getController();
            formController.setTruckManagementController(this);

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
