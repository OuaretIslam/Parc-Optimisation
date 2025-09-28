package Controllers;

import GestionMode.Itinerary;
import java.net.URL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ItineraryMANController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Itinerary> ItineraryTable;
    @FXML
    private TableColumn<Itinerary, Integer> InteraryIdCol;
    @FXML
    private TableColumn<Itinerary, Integer> DeliveryIdCol;
    @FXML
    private TableColumn<Itinerary, String> DeliveryAdresseCol;
    @FXML
    private ComboBox<Integer> EDelValIdF;
    @FXML
    private TextField EDelAdrF;
    @FXML
    private Button BackB;

    private ObservableList<Itinerary> itineraries = FXCollections.observableArrayList();
    private ObservableList<Integer> validDeliveryIds = FXCollections.observableArrayList();
    private Connection connection; // Assumes you have a database connection utility

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connectDatabase();
        setupTable();
        loadItineraries();
        loadValidDeliveries();
    }
    
    private void connectDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/GdpDb", "root", "");
        } catch (SQLException e) {
            showAlert("Error", "Failed to connect to the database.");
        }
    }
    
    private void setupTable() {
            InteraryIdCol.setCellValueFactory(new PropertyValueFactory<>("itineraryId"));
            DeliveryIdCol.setCellValueFactory(new PropertyValueFactory<>("deliveryId"));
            DeliveryAdresseCol.setCellValueFactory(new PropertyValueFactory<>("adresse"));
            ItineraryTable.setItems(itineraries);
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
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the page.");
        }
    }

@FXML
private void handleAddItinerary(ActionEvent event) {
    try {
        // Get selected delivery ID
        Integer deliveryId = EDelValIdF.getValue();
        if (deliveryId == null) {
            showAlert("Validation Error", "Please select a valid Delivery ID.");
            return;
        }

        // Fetch address automatically from the orders table
        String query = "SELECT o.OrderLocation " +
                       "FROM delivery d " +
                       "INNER JOIN orders o ON d.OrderID = o.OrderID " +
                       "WHERE d.DeliveryID = ?";
        
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, deliveryId);

        ResultSet resultSet = preparedStatement.executeQuery();

        // Check if the address is found
        String address = null;
        if (resultSet.next()) {
            address = resultSet.getString("OrderLocation");
            System.out.println("Address found: " + address);
        } else {
            showAlert("Error", "No address found for the selected Delivery ID: " + deliveryId);
            return; // Stop execution if no address is found
        }

        // Insert the new itinerary into the database
        String queryInsert = "INSERT INTO itinerary (DeliveryId, Adresse) VALUES (?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(queryInsert);
        insertStatement.setInt(1, deliveryId);
        insertStatement.setString(2, address);
        insertStatement.executeUpdate();

        // Notify the user of success
        showAlert("Success", "Itinerary added successfully!");

        // Clear fields and refresh data
        clearFields();
        loadItineraries();
        loadValidDeliveries();
    } catch (Exception e) {
        // Print the stack trace for debugging
        e.printStackTrace();

        // Notify the user of failure
        showAlert("Error", "Failed to add the itinerary. Please check the details and try again.");
    }
}

@FXML
private void handleDeleteItinerary(ActionEvent event) {
    // Get the selected itinerary from the TableView
    Itinerary selectedItinerary = ItineraryTable.getSelectionModel().getSelectedItem();

    if (selectedItinerary == null) {
        // Show an alert if no row is selected
        showAlert("Validation Error", "Please select an itinerary to delete.");
        return;
    }

    try {
        // Delete the selected itinerary from the database
        String query = "DELETE FROM itinerary WHERE ItineraryId = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, selectedItinerary.getItineraryId());
        int rowsAffected = statement.executeUpdate();

        if (rowsAffected > 0) {
            // Remove the selected itinerary from the TableView
            itineraries.remove(selectedItinerary);
            showAlert("Success", "Itinerary deleted successfully!");
        } else {
            showAlert("Error", "Failed to delete the itinerary. Please try again.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "An error occurred while deleting the itinerary.");
    }
}




    private void loadItineraries() {
        itineraries.clear();
        try {
            String query = "SELECT * FROM itinerary";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                itineraries.add(new Itinerary(
                        rs.getInt("ItineraryId"),
                        rs.getInt("DeliveryId"),
                        rs.getString("Adresse")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadValidDeliveries() {
        validDeliveryIds.clear();
        try {
            String query = "SELECT DeliveryId FROM delivery WHERE DeliveryStatus = 'Pending'";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                validDeliveryIds.add(rs.getInt("DeliveryId"));
            }

            EDelValIdF.setItems(validDeliveryIds);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        EDelValIdF.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
