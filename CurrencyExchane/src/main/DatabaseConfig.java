package main;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
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

//    private static HikariDataSource dataSource;
//
//    static {
//        HikariConfig config = new HikariConfig();
//        config.setJdbcUrl("jdbc:sqlite:C:/Users/iles982/IdeaProjects/Currency exchange/identifier.sqlite");
////        config.setUsername("your_db_user");
////        config.setPassword("your_db_password");
//
//        // Pool configuration
//        config.setMaximumPoolSize(10);
//        config.setMinimumIdle(2);
//        config.setIdleTimeout(30000);
//        config.setConnectionTimeout(20000);
//        config.setMaxLifetime(1800000);
//
//        dataSource = new HikariDataSource(config);
//    }
//
//    public static DataSource getDataSource() {
//        return dataSource;
//    }
}
