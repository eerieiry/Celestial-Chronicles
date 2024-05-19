package celestialchronicles.celestialchronicles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection connection;

    public static void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:4044/constellationsdb?user=root";
            String username = "root";
            String password = "12345";
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
