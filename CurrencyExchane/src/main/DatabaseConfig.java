package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:sqlite:C:/Users/iles982/IdeaProjects/Currency exchange/identifier.sqlite";

    public static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Driver not found
        }
        return DriverManager.getConnection(URL);
    }
}
