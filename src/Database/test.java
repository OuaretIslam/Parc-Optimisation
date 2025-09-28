package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class test {

    public static void main(String[] args) {
        // Replace with your actual database connection details
        String url = "jdbc:mysql://localhost:3306/GdpDb"; // MySQL example, change URL accordingly
        String username = "root";
        String password = "";

        // Try to establish the connection
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            if (connection != null) {
                System.out.println("Database connection successful!");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}
