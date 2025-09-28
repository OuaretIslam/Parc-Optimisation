package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/GdpDb"; // Adjust URL and DB name
    private static final String USER = "root"; // Database username
    private static final String PASSWORD = ""; // Database password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public static String getUserRole(String username, String password) throws SQLException {
        String role = null;
        // Modify the query to check the username and password as plain text
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // No hashing of the password

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = rs.getString("role");
            }
        }
        return role;
    }
}
