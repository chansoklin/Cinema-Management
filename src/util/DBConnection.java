package util;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

public class DBConnection {
    private static Connection connection;
    private static Properties props = new Properties();
    
    static {
        try {
            // Try multiple locations for config.properties
            InputStream input = null;
            
            // Try from resources directory
            java.io.File configFile = new java.io.File("src/resources/config.properties");
            if (configFile.exists()) {
                input = new java.io.FileInputStream(configFile);
                System.out.println("✓ Config loaded from src/resources/");
            }
            
            // Try from classpath
            if (input == null) {
                input = DBConnection.class.getClassLoader().getResourceAsStream("config.properties");
                if (input != null) {
                    System.out.println("✓ Config loaded from classpath");
                }
            }
            
            // Try from out directory
            if (input == null) {
                configFile = new java.io.File("out/resources/config.properties");
                if (configFile.exists()) {
                    input = new java.io.FileInputStream(configFile);
                    System.out.println("✓ Config loaded from out/resources/");
                }
            }
            
            if (input != null) {
                props.load(input);
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("✓ Database driver loaded");
                input.close();
            } else {
                System.out.println("⚠ config.properties not found, using defaults");
                props.setProperty("db.url", "jdbc:mysql://localhost:3306/cinema_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                props.setProperty("db.username", "root");
                props.setProperty("db.password", "");
            }
        } catch (Exception e) {
            System.err.println("Error loading config: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = props.getProperty("db.url");
            String username = props.getProperty("db.username");
            String password = props.getProperty("db.password", "");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Database connected!");
        }
        return connection;
    }
    
    public static void testConnection() {
        try {
            Connection conn = getConnection();
            System.out.println("✓ Database test: SUCCESS!");
            System.out.println("  Database: " + conn.getCatalog());
            conn.close();
        } catch (SQLException e) {
            System.err.println("✗ Database test: FAILED - " + e.getMessage());
        }
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
