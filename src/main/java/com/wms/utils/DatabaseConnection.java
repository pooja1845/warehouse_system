package com.wms.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/wms_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Admin@123";

    private static Connection connection = null;

    private DatabaseConnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Ensure the driver is loaded properly (optional but good practice)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connection established.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to the database!");
            e.printStackTrace();
        }
        return connection;
    }
    
    public static void checkConnection() {
        // Just calling getConnection to force the lazy initialization
        getConnection();
    }
}
