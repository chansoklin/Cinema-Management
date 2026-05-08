package util;

import java.io.*;
import java.sql.*;
import java.util.Properties;

public class ConfigTest {
    public static void main(String[] args) {
        System.out.println("=== Database Configuration Test ===\n");

        // Test 1: Find config.properties
        System.out.println("1. Looking for config.properties...");
        String[] paths = {
                "src/main/resources/config.properties",
                "config.properties",
                "../config.properties",
                "src/config.properties"
        };

        InputStream input = null;
        String foundPath = null;

        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                foundPath = path;
                try {
                    input = new FileInputStream(file);
                    System.out.println("   ✓ Found at: " + path);
                    break;
                } catch (FileNotFoundException e) {
                    // Continue
                }
            }
        }

        // Try classloader
        if (input == null) {
            input = ConfigTest.class.getClassLoader().getResourceAsStream("config.properties");
            if (input != null) {
                foundPath = "classpath:config.properties";
                System.out.println("   ✓ Found in classpath");
            }
        }

        if (input == null) {
            System.out.println("   ✗ config.properties NOT FOUND!");
            System.out.println("\nPlease create config.properties at: src/main/resources/config.properties");
            return;
        }

        // Test 2: Load properties
        System.out.println("\n2. Loading configuration...");
        Properties props = new Properties();
        try {
            props.load(input);
            System.out.println("   ✓ Properties loaded successfully");
        } catch (IOException e) {
            System.out.println("   ✗ Failed to load properties: " + e.getMessage());
            return;
        }

        // Test 3: Display configuration (hide password)
        System.out.println("\n3. Configuration values:");
        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        System.out.println("   URL: " + (url != null ? url : "NOT SET"));
        System.out.println("   Username: " + (username != null ? username : "NOT SET"));
        System.out.println("   Password: " + (password != null && !password.isEmpty() ? "********" : "(empty)"));

        // Test 4: Test database connection
        System.out.println("\n4. Testing database connection...");

        if (url == null || username == null) {
            System.out.println("   ✗ Missing database configuration!");
            return;
        }

        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("   ✓ MySQL driver loaded");

            // Try to connect
            System.out.println("   Attempting to connect to: " + url);
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("   ✓ Database connection SUCCESSFUL!");

            // Test query
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DATABASE()");
            if (rs.next()) {
                System.out.println("   Current database: " + rs.getString(1));
            }

            rs.close();
            stmt.close();
            conn.close();

            System.out.println("\n✓ All tests passed! Database is ready.");

        } catch (ClassNotFoundException e) {
            System.out.println("   ✗ MySQL driver not found!");
            System.out.println("   Please download mysql-connector-java-8.0.33.jar and add to classpath");
        } catch (SQLException e) {
            System.out.println("   ✗ Database connection FAILED!");
            System.out.println("   Error: " + e.getMessage());
            System.out.println("\nPossible solutions:");
            System.out.println("   1. Check if MySQL is running: brew services list");
            System.out.println("   2. Start MySQL: brew services start mysql");
            System.out.println("   3. Check username/password in config.properties");
            System.out.println("   4. Create database: mysql -u root -e 'CREATE DATABASE cinema_db'");
        }
    }
}