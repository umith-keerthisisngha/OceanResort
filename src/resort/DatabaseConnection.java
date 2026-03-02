package resort;

import java.sql.*;

public class DatabaseConnection {
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ocean_resort_db";
    private static final String DB_USER = "root";  
    private static final String DB_PASSWORD = "";  
    
    private static Connection connection = null;
    
    /**
     * Get database connection
     * Creates new connection if none exists
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("✓ Database connected successfully!");
            }
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            System.err.println("Please add mysql-connector-java.jar to your classpath");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to connect to database!");
            System.err.println("Please check:");
            System.err.println("  1. MySQL server is running");
            System.err.println("  2. Database 'ocean_resort_db' exists");
            System.err.println("  3. Username and password are correct");
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to close database connection");
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Database connection test successful!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Database connection test failed!");
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Execute a test query
     */
    public static void printConnectionInfo() {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("========================================");
                System.out.println("Database Connection Information:");
                System.out.println("========================================");
                System.out.println("Database: " + metaData.getDatabaseProductName());
                System.out.println("Version: " + metaData.getDatabaseProductVersion());
                System.out.println("URL: " + DB_URL);
                System.out.println("User: " + DB_USER);
                System.out.println("========================================");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
