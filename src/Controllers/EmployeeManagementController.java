package Controllers;

import GestionMode.Employee;
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


public class EmployeeManagementController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Button backButton, addButton, editButton, deleteButton;
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, Integer> colId;
    @FXML
    private TableColumn<Employee, String> colUsername, colPassword, colRole;

    private ObservableList<Employee> employeeList;

    // Database connection credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GdpDb"; // Adjust URL and DB name
    private static final String DB_USER = "root"; // Database username
    private static final String DB_PASSWORD = "";

    @Override
public void initialize(URL url, ResourceBundle rb) {
    employeeList = FXCollections.observableArrayList();
    employeeTable.setItems(employeeList); // Bind TableView to employeeList
    colId.setCellValueFactory(new PropertyValueFactory<>("userId"));
    colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
    colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
    colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    loadEmployeeData(); // Load initial data
}

public void loadEmployeeData() {
    employeeList.clear(); // Clear the list before reloading
    employeeList.addAll(getUserData());
}


    private ObservableList<Employee> getUserData() {
    ObservableList<Employee> data = FXCollections.observableArrayList();
    // Fetch only employees
    String query = "SELECT UserId, Username, Password, Role FROM users WHERE Role = 'Employe'";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            data.add(new Employee(
                rs.getInt("UserId"), // Include UserId
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("Role")
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
    private void handleAddButton(ActionEvent event) {
        openFormWindow();
    }

@FXML
private void handleEditButton(ActionEvent event) {
    Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
    if (selectedEmployee != null) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/EmployeeEdit.fxml"));
            Parent root = loader.load();

            EmployeeEditController editController = loader.getController();
            editController.setEmployeeData(selectedEmployee); // Pass the selected employee data
            editController.setManagementController(this); // Pass the main controller reference

            Stage stage = new Stage();
            stage.setTitle("Edit Employee");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the edit window.");
        }
    } else {
        showAlert("Error", "No employee selected to edit.");
    }
}



@FXML
private void handleDeleteButton(ActionEvent event) {
    Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
    if (selectedEmployee != null) {
        if (deleteEmployeeFromDatabase(selectedEmployee.getUserId())) {
            loadEmployeeData(); // Reload data
            showAlert("Success", "Employee deleted successfully!");
        } else {
            showAlert("Error", "Failed to delete employee.");
        }
    } else {
        showAlert("Error", "No employee selected!");
    }
}


public void addEmployee(Employee newEmployee) {
    if (insertEmployeeIntoDatabase(newEmployee)) {
        loadEmployeeData(); // Reload data
        showAlert("Success", "Employee added successfully!");
    } else {
        showAlert("Error", "Failed to add employee.");
    }
}


private boolean insertEmployeeIntoDatabase(Employee employee) {
    // Automatically enforce the role as "Employee"
    String query = "INSERT INTO users (Username, Password, Role) VALUES (?, ?, 'Employe')";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, employee.getUsername());
        stmt.setString(2, employee.getPassword());
        int affectedRows = stmt.executeUpdate();

        if (affectedRows > 0) {
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setUserId(generatedKeys.getInt(1));
                }
            }
            return true;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}


    public boolean updateEmployeeInDatabase(Employee employee) {
        String query = "UPDATE users SET Username = ?, Password = ?, Role = ? WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getRole());
            stmt.setInt(4, employee.getUserId());
            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteEmployeeFromDatabase(int userId) {
        String query = "DELETE FROM users WHERE UserId = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
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
            showAlert("Error", "Failed to load the page.");
        }
    }

    private void openFormWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/EmployeeForm.fxml"));
            Parent root = loader.load();

            EmployeeFormController formController = loader.getController();
            formController.setGestionEmpController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Employee");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open the form.");
        }
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
