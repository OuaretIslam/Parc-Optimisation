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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import GestionMode.Deliveries;
import java.time.LocalDate;
import java.util.Date;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;

public class DeliveryMANController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button BackB, EDeAddBUT, EDeEditBUT, EDeDelBUT;

    @FXML
    private TextField EDeliStatF;
    
    @FXML
    private DatePicker EDeliDateF;


    @FXML
    private ComboBox<Integer> OrderIdComboBox, TruckIdComboBox;

    @FXML
    private TableView<Deliveries> DeliveryTable;

    @FXML
    private TableColumn<Deliveries, Integer> DeliveryIdCol;

    @FXML
    private TableColumn<Deliveries, Integer> OrderIdCol;

    @FXML
    private TableColumn<Deliveries, Integer> TruckIdCol;

    @FXML
    private TableColumn<Deliveries, String> DeliveryDateCol;

    @FXML
    private TableColumn<Deliveries, String> DeliveryStatusCol;

    private ObservableList<Deliveries> DeliveriesList;
    private Connection connection;

@FXML
public void initialize() {
    connectDatabase();  // Ensure database connection is established
    setupTable();       // Set up the delivery table if applicable
    loadDeliveries();   // Load delivery data (if this part remains unchanged)
    loadValidOrders();  // Load valid orders into the OrderIdComboBox

    // Initially clear the TruckIdComboBox as no order is selected
    TruckIdComboBox.getItems().clear();

    // Add a listener to update valid trucks whenever a new order is selected
    OrderIdComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue != null) {
            loadValidTrucks(newValue); // Load trucks valid for the selected order
        } else {
            TruckIdComboBox.getItems().clear(); // Clear trucks if no order is selected
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
        DeliveryIdCol.setCellValueFactory(new PropertyValueFactory<>("DeliveryId"));
        OrderIdCol.setCellValueFactory(new PropertyValueFactory<>("OrderId"));
        TruckIdCol.setCellValueFactory(new PropertyValueFactory<>("TruckId"));
        DeliveryDateCol.setCellValueFactory(new PropertyValueFactory<>("DeliveryD"));
        DeliveryStatusCol.setCellValueFactory(new PropertyValueFactory<>("DeliveryStatus"));
    }

    private void loadDeliveries() {
        DeliveriesList = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM delivery";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Deliveries delivery = new Deliveries(
                        resultSet.getInt("DeliveryId"),
                        resultSet.getInt("OrderId"),
                        resultSet.getInt("TruckId"),
                        resultSet.getDate("DeliveryD"),
                        resultSet.getString("DeliveryStatus")
                );
                DeliveriesList.add(delivery);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load deliveries.");
        }
        DeliveryTable.setItems(DeliveriesList);
    }

private void loadValidOrders() {
    ObservableList<Integer> validOrders = FXCollections.observableArrayList();

    if (connection == null) {
        showAlert("Error", "Database connection is not established.");
        return;
    }

    String query = "SELECT OrderId FROM orders WHERE OrderStatus = 'Shipped'";
    try (PreparedStatement statement = connection.prepareStatement(query);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {
            validOrders.add(resultSet.getInt("OrderId"));
        }

        OrderIdComboBox.setItems(validOrders);

        // Listen for order selection
        OrderIdComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadValidTrucks(newVal); // Load valid trucks for the selected order
            }
        });

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "Failed to load valid orders. Please try again.");
    }
}

private void loadValidTrucks(int selectedOrderId) {
    ObservableList<Integer> validTrucks = FXCollections.observableArrayList();
    
    if (connection == null) {
        showAlert("Error", "Database connection is not established.");
        return;
    }

    // Retrieve the weight of the selected order
    String queryOrderWeight = "SELECT TotelWeight FROM orders WHERE OrderId = ?";
    // Query for trucks with no capacity filter
    String queryValidTrucks = "SELECT TruckId FROM trucks WHERE TrucksStat = 'Free' AND RespDriverId IS NOT NULL";
    // Query to get the TruckType
    String queryTruckType = "SELECT TruckType FROM trucks WHERE TruckId = ?";

    try (PreparedStatement orderWeightStatement = connection.prepareStatement(queryOrderWeight);
         PreparedStatement validTrucksStatement = connection.prepareStatement(queryValidTrucks)) {

        // Get the total weight of the selected order
        orderWeightStatement.setInt(1, selectedOrderId);

        try (ResultSet orderWeightResult = orderWeightStatement.executeQuery()) {
            if (orderWeightResult.next()) {
                int totalWeight = orderWeightResult.getInt("TotelWeight");
                System.out.println("Total weight of selected order: " + totalWeight);

                // Retrieve all available trucks
                try (ResultSet validTrucksResult = validTrucksStatement.executeQuery()) {
                    validTrucks.clear();  // Clear existing items in the ComboBox
                    while (validTrucksResult.next()) {
                        int truckId = validTrucksResult.getInt("TruckId");

                        // Fetch the truck type from the database
                        try (PreparedStatement truckTypeStatement = connection.prepareStatement(queryTruckType)) {
                            truckTypeStatement.setInt(1, truckId);
                            try (ResultSet truckTypeResult = truckTypeStatement.executeQuery()) {
                                if (truckTypeResult.next()) {
                                    String truckType = truckTypeResult.getString("TruckType");

                                    // Apply the weight condition to filter trucks
                                    if (isValidTruckForOrder(totalWeight).equals(truckType)) {
                                        validTrucks.add(truckId);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                showAlert("Warning", "No weight information found for the selected order.");
            }
        }

        // Update the TruckIdComboBox with valid trucks and refresh UI immediately
        TruckIdComboBox.setItems(validTrucks);

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "Failed to load valid trucks. Please try again.");
    }
}

// Simplified truck validation based on order weight
private String isValidTruckForOrder(int orderWeight) {
    // Apply the weight categories and truck conditions
    if (orderWeight <= 5000) {
        return "Small";  // Valid for small trucks (0 - 5000kg)
    } else if (orderWeight <= 15000) {
        return "Medium";  // Valid for medium trucks (5001 - 15000kg)
    } else {
        return "Large";  // Valid for large trucks (15001kg and above)
    }
}











    @FXML
    private void handleEditDelivery(ActionEvent event) {
        Deliveries selectedDelivery = DeliveryTable.getSelectionModel().getSelectedItem();
        if (selectedDelivery == null) {
            showAlert("Error", "Please select a delivery to edit.");
            return;
        }

        try {
            int orderId = OrderIdComboBox.getValue();
            int truckId = TruckIdComboBox.getValue();
            LocalDate date = EDeliDateF.getValue();
            String deliveryStatus = EDeliStatF.getText();

            String query = "UPDATE delivery SET OrderId = ?, TruckId = ?, DeliveryD = ?, DeliveryStatus = ? WHERE DeliveryId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, orderId);
            statement.setInt(2, truckId);
            statement.setDate(3, java.sql.Date.valueOf(date));
            statement.setString(4, deliveryStatus);
            statement.setInt(5, selectedDelivery.getDeliveryId());
            statement.executeUpdate();
            
            
                    // If delivery status is "Delivered", update the OrderStatus to "Affected"
            if ("Delivered".equalsIgnoreCase(deliveryStatus)) {
                String updateOrderStatusQuery = "UPDATE orders SET OrderStatus = 'Affected' WHERE OrderId = ?";
                PreparedStatement updateOrderStatement = connection.prepareStatement(updateOrderStatusQuery);
                updateOrderStatement.setInt(1, orderId);
                updateOrderStatement.executeUpdate();
            }

            showAlert("Success", "Delivery updated successfully!");
            loadDeliveries();
            loadValidOrders();
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Failed to edit delivery.");
        }
    }

private boolean showConfirmation(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
}

@FXML
private void handleDeleteDelivery(ActionEvent event) {
    Deliveries selectedDelivery = DeliveryTable.getSelectionModel().getSelectedItem();
    if (selectedDelivery == null) {
        showAlert("Error", "Please select a delivery to delete.");
        return;
    }

    if (!showConfirmation("Confirm Deletion", "Are you sure you want to delete this delivery?")) {
        return;
    }

    try {
        String query = "DELETE FROM delivery WHERE DeliveryId = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, selectedDelivery.getDeliveryId());
        statement.executeUpdate();

        showAlert("Success", "Delivery deleted successfully!");
        loadDeliveries();
    } catch (SQLException e) {
        showAlert("Error", "Failed to delete delivery. Please try again later.");
        e.printStackTrace();
    }
}

@FXML
private void handleAddDelivery(ActionEvent event) {
    try {
        // Validate input data
        if (OrderIdComboBox.getValue() == null || TruckIdComboBox.getValue() == null ||
                EDeliDateF.getValue() == null || EDeliStatF.getText().isEmpty()) {
            showAlert("Error", "All fields are required. Please complete the form.");
            return;
        }

        // Get input values
        int orderId = OrderIdComboBox.getValue();
        int truckId = TruckIdComboBox.getValue();
        LocalDate date = EDeliDateF.getValue();
        String deliveryStatus = EDeliStatF.getText();

        // Validate delivery status
        if (!deliveryStatus.matches("Pending|In-Transit|Delivered|Failed")) {
            showAlert("Error", "Invalid delivery status. Please use one of: Pending, In-Transit, Delivered, Failed.");
            return;
        }

        // Insert delivery
        String query = "INSERT INTO delivery (OrderId, TruckId, DeliveryD, DeliveryStatus) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, orderId);
        statement.setInt(2, truckId);
        statement.setDate(3, java.sql.Date.valueOf(date));
        statement.setString(4, deliveryStatus);
        statement.executeUpdate();

        // Update order status if delivered
        if ("Delivered".equalsIgnoreCase(deliveryStatus)) {
            String updateOrderStatusQuery = "UPDATE orders SET OrderStatus = 'Affected' WHERE OrderId = ?";
            PreparedStatement updateOrderStatement = connection.prepareStatement(updateOrderStatusQuery);
            updateOrderStatement.setInt(1, orderId);
            updateOrderStatement.executeUpdate();
        }

        showAlert("Success", "Delivery added successfully!");
        loadDeliveries();
        loadValidOrders();
        clearFields();
    } catch (SQLException e) {
        showAlert("Error", "Failed to add delivery. Please check the input data and try again.");
        e.printStackTrace();
    }
}


    private void clearFields() {
        OrderIdComboBox.setValue(null);
        TruckIdComboBox.setValue(null);
        EDeliDateF.setValue(null);
        EDeliStatF.clear();
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
