package Controllers;

import java.sql.Connection;
import GestionMode.Truck;
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

public class TrucksMANController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button BackB, EEditTB;

    @FXML
    private TextField ETruckNE;
    @FXML
    private TextField ETruckRIdE;
    @FXML
    private TextField ETruckSE;
    @FXML
    private TableView<Truck> TrucksTable;

    @FXML
    private TableColumn<Truck, Integer> ColTrucksId;

    @FXML
    private TableColumn<Truck, String> ColTrucksN;
    
    @FXML
    private TableColumn<Truck, String> ColTrucksBrand;

    @FXML
    private TableColumn<Truck, String> ColTrucksStatus;
    
    @FXML
    private TableColumn<Truck, String> ColTrucksType;
        
    @FXML
    private TableColumn<Truck, Integer> ColTrucksRespId;

    private ObservableList<Truck> truckList;
    private Connection connection;

    // Initialize database connection
    public void initialize() {
        connectDatabase();
        setupTable();
        loadTrucks();
        setupRowSelectionListener();
    }
    private void setupRowSelectionListener() {
    TrucksTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            populateFields(newValue);
        }
    });
}

private void populateFields(Truck selectedTruck) {
    ETruckNE.setText(selectedTruck.getTruckN());
    ETruckSE.setText(selectedTruck.getTrucksStat());
    ETruckRIdE.setText(String.valueOf(selectedTruck.getRespDriverId()));
}

    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/GdpDb", "root", "");
        } catch (SQLException e) {
            showAlert("Error", "Failed to connect to the database.");
        }
    }

    private void setupTable() {
        ColTrucksId.setCellValueFactory(new PropertyValueFactory<>("TruckId"));
        ColTrucksBrand.setCellValueFactory(new PropertyValueFactory<>("TruckBrand"));
        ColTrucksType.setCellValueFactory(new PropertyValueFactory<>("TruckType"));
        ColTrucksN.setCellValueFactory(new PropertyValueFactory<>("TruckN"));
        ColTrucksStatus.setCellValueFactory(new PropertyValueFactory<>("TrucksStat"));
        ColTrucksRespId.setCellValueFactory(new PropertyValueFactory<>("RespDriverId"));


        truckList = FXCollections.observableArrayList();
        TrucksTable.setItems(truckList);
    }

    private void loadTrucks() {
        truckList.clear();
        try {
            String query = "SELECT * FROM trucks";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int TruckId = resultSet.getInt("TruckId");
                String TruckN = resultSet.getString("TruckN");
                String TruckType = resultSet.getString("TruckType");
                String TruckBrand = resultSet.getString("TruckBrand");
                String TruckStatus = resultSet.getString("TrucksStat");
                int TruckRespId = resultSet.getInt("RespDriverId");
                truckList.add(new Truck(TruckId, TruckBrand, TruckType, TruckN, TruckStatus, TruckRespId));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load products.");
        }
    }

@FXML
private void handleEdittruck(ActionEvent event) {
    Truck selectedProduct = TrucksTable.getSelectionModel().getSelectedItem();
    if (selectedProduct == null) {
        showAlert("Error", "Please select a truck to edit.");
        return;
    }

    String TruckN = ETruckNE.getText();
    String TruckStatus = ETruckSE.getText();
    String TruckType = selectedProduct.getTruckType(); // Assuming TruckType is already in the truck object

    // Validate TruckRespId
    int TruckRespId;
    try {
        TruckRespId = Integer.parseInt(ETruckRIdE.getText());
    } catch (NumberFormatException e) {
        showAlert("Error", "Please enter a valid numeric Driver ID.");
        return;
    }

    if (TruckN.isEmpty() || TruckStatus.isEmpty()) {
        showAlert("Error", "Please fill in all fields.");
        return;
    }

    // Validate TruckStatus
    if (!TruckStatus.equals("On Mission") && !TruckStatus.equals("Inactive") && !TruckStatus.equals("Free")) {
        showAlert("Error", "Invalid Truck Status. Valid values are: On Mission, Inactive, Free.");
        return;
    }

    // Validate TruckType
    if (!TruckType.equals("Large") && !TruckType.equals("Medium") && !TruckType.equals("Small")) {
        showAlert("Error", "Invalid Truck Type. Valid values are: Large, Medium, Small.");
        return;
    }

    // Check if RespDriverId exists in the trucksdrivers table
    try {
        String checkQuery = "SELECT COUNT(*) FROM trucksdrivers WHERE DriverId = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
        checkStatement.setInt(1, TruckRespId);
        ResultSet resultSet = checkStatement.executeQuery();
        if (resultSet.next() && resultSet.getInt(1) == 0) {
            showAlert("Error", "The specified Driver ID does not exist.");
            return;
        }
    } catch (SQLException e) {
        showAlert("Error", "Failed to validate Driver ID.");
        return;
    }

    // Proceed to update the truck if validation passes
    try {
        String query = "UPDATE trucks SET TruckN = ?, TrucksStat = ?, RespDriverId = ? WHERE TruckId = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, TruckN);
        statement.setString(2, TruckStatus);
        statement.setInt(3, TruckRespId);
        statement.setInt(4, selectedProduct.getTruckId());
        statement.executeUpdate();
        loadTrucks();
        clearFields();
        showAlert("Success", "Truck details updated successfully!");
    } catch (SQLException e) {
        showAlert("Error", "Failed to update truck details.");
    }
}




    private void clearFields() {
        ETruckNE.clear();
        ETruckSE.clear();
        ETruckRIdE.clear();
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
