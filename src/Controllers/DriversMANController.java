package Controllers;

import java.sql.Connection;
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
import GestionMode.Drivers;

public class DriversMANController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button BackB, EDAddBUT, EDEdBUT1, EDDelBUT;

    @FXML
    private TextField EDNameF, EDStatusF;

    @FXML
    private TableView<Drivers> DriversTable;

    @FXML
    private TableColumn<Drivers, Integer> DriverIdCol;

    @FXML
    private TableColumn<Drivers, String> DriverNCol;

    @FXML
    private TableColumn<Drivers, String> DriverStatusCol;

    private ObservableList<Drivers> DriversList;
    private Connection connection;

    // Initialize database connection
    public void initialize() {
        connectDatabase();
        setupTable();
        loadDrivers();
        setupSelectionListener();
    }
    
    private void setupSelectionListener() {
    DriversTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            EDNameF.setText(newValue.getDriverN());
            EDStatusF.setText(newValue.getDriverStatus());
        } else {
            clearFields();
        }
    });
}


    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/GdpDb", "root", "");
        } catch (SQLException e) {
            showAlert("Error", "Failed to connect to the database.");
        }
    }

    private void setupTable() {
        DriverIdCol.setCellValueFactory(new PropertyValueFactory<>("DriverId"));
        DriverNCol.setCellValueFactory(new PropertyValueFactory<>("DriverN"));
        DriverStatusCol.setCellValueFactory(new PropertyValueFactory<>("DriverStatus"));

        DriversList = FXCollections.observableArrayList();
        DriversTable.setItems(DriversList);
    }

    private void loadDrivers() {
        DriversList.clear();
        try {
            String query = "SELECT * FROM trucksdrivers";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int DriverId = resultSet.getInt("DriverId");
                String DriverN = resultSet.getString("DriverN");
                String DriverStatus = resultSet.getString("DriverStatus");
                DriversList.add(new Drivers(DriverId, DriverN, DriverStatus));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load drivers.");
        }
    }
@FXML
private void handleAddDriver(ActionEvent event) {
    String driverName = EDNameF.getText();
    String driverStatus = EDStatusF.getText();

    // Valid status values
    String[] validStatuses = {"onMission", "inActive", "free"};

    // Check if the status is valid
    if (driverName.isEmpty() || driverStatus.isEmpty()) {
        showAlert("Error", "Please fill in all fields.");
        return;
    }

    // Check if the driver status is valid
    boolean isValidStatus = false;
    for (String status : validStatuses) {
        if (driverStatus.equalsIgnoreCase(status)) {
            isValidStatus = true;
            break;
        }
    }

    if (!isValidStatus) {
        showAlert("Error", "Invalid status. Please enter one of the following: onMission, inactive, or free.");
        return;
    }

    try {
        // Insert into the database
        String query = "INSERT INTO trucksdrivers (DriverN, DriverStatus) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, driverName);
        statement.setString(2, driverStatus);
        statement.executeUpdate();

        // Refresh the table and clear fields
        loadDrivers();
        clearFields();
        showAlert("Success", "Driver added successfully!");
    } catch (SQLException e) {
        showAlert("Error", "Failed to add driver.");
    }
}


@FXML
private void handleEditDrivers(ActionEvent event) {
    Drivers selectedDriver = DriversTable.getSelectionModel().getSelectedItem();
    if (selectedDriver == null) {
        showAlert("Error", "Please select a driver to edit.");
        return;
    }

    String driverName = EDNameF.getText();
    String driverStatus = EDStatusF.getText();

    // Valid status values
    String[] validStatuses = {"onMission", "inActive", "free"};

    // Check if the status is valid
    boolean isValidStatus = false;
    for (String status : validStatuses) {
        if (driverStatus.equalsIgnoreCase(status)) {
            isValidStatus = true;
            break;
        }
    }

    if (driverName.isEmpty() || driverStatus.isEmpty()) {
        showAlert("Error", "Please fill in all fields.");
        return;
    }

    if (!isValidStatus) {
        showAlert("Error", "Invalid status. Please enter one of the following: onMission, inactive, or free.");
        return;
    }

    try {
        String query = "UPDATE trucksdrivers SET DriverN = ?, DriverStatus = ? WHERE DriverId = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, driverName);
        statement.setString(2, driverStatus);
        statement.setInt(3, selectedDriver.getDriverId());
        statement.executeUpdate();
        loadDrivers();
        clearFields();
        showAlert("Success", "Driver details updated successfully!");
    } catch (SQLException e) {
        showAlert("Error", "Failed to update driver details.");
    }
}

    @FXML
    private void handleDeleteDriver(ActionEvent event) {
        Drivers selectedDriver = DriversTable.getSelectionModel().getSelectedItem();
        if (selectedDriver == null) {
            showAlert("Error", "Please select a driver to delete.");
            return;
        }

        try {
            String query = "DELETE FROM trucksdrivers WHERE DriverId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedDriver.getDriverId());
            statement.executeUpdate();
            loadDrivers();
            showAlert("Success", "Driver deleted successfully!");
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete driver.");
        }
    }

    private void clearFields() {
        EDNameF.clear();
        EDStatusF.clear();
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