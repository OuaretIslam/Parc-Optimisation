package Controllers;

import GestionMode.Orders;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class OrdersMANController {
    @FXML
    private TableView<Orders> OrdersTable;
    @FXML
    private TableColumn<Orders, Integer> OrdersId;
    @FXML
    private TableColumn<Orders, String> OrdersDate;
    @FXML
    private TableColumn<Orders, String> OrdersStatus;
    @FXML
    private TableColumn<Orders, String> PTB;
    @FXML
    private TableColumn<Orders, Integer> TWeight;
    @FXML
    private TableColumn<Orders, String> OrdLoc;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField EOrdTWF; // Total Weight
    @FXML
    private TextField EOrdOSF; // Order Status
    @FXML
    private TextField EOrdPTBF; // Product Details
    @FXML
    private DatePicker EOrdDateF; // Order Date
    @FXML
    private TextField EOrdLocF;

    private ObservableList<Orders> ordersList;

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/gdpdb", "root", ""); // Replace with your DB credentials
    }

    @FXML
    public void initialize() {
        OrdersId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        OrdersDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        OrdersStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        PTB.setCellValueFactory(new PropertyValueFactory<>("productDetails"));
        TWeight.setCellValueFactory(new PropertyValueFactory<>("totalWeight"));
        OrdLoc.setCellValueFactory(new PropertyValueFactory<>("OrderLocation"));

        loadOrders();

        // Add a listener to populate fields when a row is selected
        OrdersTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() > 0) { // Detects single or double clicks
                populateFieldsFromSelectedOrder();
            }
        });
    }

    public ObservableList<Orders> getOrders() throws SQLException {
        ObservableList<Orders> ordersList = FXCollections.observableArrayList();
        String query = "SELECT * FROM orders";
        try (Connection connection = connect();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                ordersList.add(new Orders(
                    resultSet.getInt("OrderId"),
                    resultSet.getDate("OrderDate"),
                    resultSet.getString("OrderStatus"),
                    resultSet.getString("ProductDetails"),
                    resultSet.getInt("TotelWeight"),
                    resultSet.getString("OrderLocation")
                ));
            }
        }
        return ordersList;
    }

    public void loadOrders() {
        try {
            ordersList = getOrders();
            OrdersTable.setItems(ordersList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load orders.");
        }
    }

@FXML
public void addOrder(ActionEvent event) {
    try (Connection conn = connect()) {
        // Get the order status from the input field
        String orderStatus = EOrdOSF.getText();

        // Validate the order status
        if (!isValidOrderStatus(orderStatus)) {
            showAlert("Error", "Invalid order status. Valid values are: Pending, Shipped, Cancelled.");
            return;
        }

        String query = "INSERT INTO orders (OrderDate, OrderStatus, ProductDetails, TotelWeight, OrderLocation) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);

        LocalDate date = EOrdDateF.getValue();
        stmt.setDate(1, java.sql.Date.valueOf(date));
        stmt.setString(2, orderStatus);
        stmt.setString(3, EOrdPTBF.getText());
        stmt.setInt(4, Integer.parseInt(EOrdTWF.getText()));
        stmt.setString(5, EOrdLocF.getText());

        stmt.executeUpdate();
        showAlert("Success", "Order added successfully.");
        clearFields(); // Clear input fields after adding the order
        loadOrders();
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Failed to add order.");
    }
}


@FXML
public void editOrder(ActionEvent event) {
    Orders selectedOrder = OrdersTable.getSelectionModel().getSelectedItem();
    if (selectedOrder == null) {
        showAlert("Warning", "No order selected.");
        return;
    }

    // Check if the DatePicker has a value
    if (EOrdDateF.getValue() == null) {
        showAlert("Warning", "Please select a date.");
        return;
    }

    // Get the order status from the input field
    String orderStatus = EOrdOSF.getText();

    // Validate the order status
    if (!isValidOrderStatus(orderStatus)) {
        showAlert("Error", "Invalid order status. Valid values are: Pending, Shipped, Cancelled.");
        return;
    }

    try (Connection conn = connect()) {
        String query = "UPDATE orders SET OrderStatus = ?, ProductDetails = ?, TotelWeight = ?, OrderDate = ?, OrderLocation=? WHERE OrderId = ?";
        PreparedStatement stmt = conn.prepareStatement(query);

        stmt.setString(1, orderStatus);
        stmt.setString(2, EOrdPTBF.getText());
        stmt.setInt(3, Integer.parseInt(EOrdTWF.getText()));

        // Set the selected date
        LocalDate date = EOrdDateF.getValue();
        stmt.setDate(4, java.sql.Date.valueOf(date));  // This assumes the date is never null here
        stmt.setString(5, EOrdLocF.getText());

        stmt.setInt(6, selectedOrder.getOrderId());

        stmt.executeUpdate();
        showAlert("Success", "Order updated successfully.");
        clearFields();  // Clear input fields after editing the order
        loadOrders();
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Failed to update order.");
    }
}



private boolean isValidOrderStatus(String status) {
    return status.equals("Pending") || status.equals("Shipped")  || status.equals("Cancelled");
}


    @FXML
    public void deleteOrder(ActionEvent event) {
        Orders selectedOrder = OrdersTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showAlert("Warning", "No order selected.");
            return;
        }
        try (Connection conn = connect()) {
            String query = "DELETE FROM orders WHERE OrderId = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedOrder.getOrderId());
            stmt.executeUpdate();
            showAlert("Success", "Order deleted successfully.");
            clearFields(); // Clear input fields after deleting the order
            loadOrders();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete order.");
        }
    }

private void populateFieldsFromSelectedOrder() {
    Orders selectedOrder = OrdersTable.getSelectionModel().getSelectedItem();
    if (selectedOrder != null) {
        EOrdDateF.getEditor().setText(selectedOrder.getOrderDate().toString());
        EOrdOSF.setText(selectedOrder.getOrderStatus());
        EOrdPTBF.setText(selectedOrder.getProductDetails());
        EOrdTWF.setText(String.valueOf(selectedOrder.getTotalWeight()));
        EOrdLocF.setText(selectedOrder.getOrderLocation());
    }
}


    private void clearFields() {
        EOrdDateF.setValue(null);
        EOrdOSF.clear();
        EOrdPTBF.clear();
        EOrdTWF.clear();
        EOrdLocF.clear();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
