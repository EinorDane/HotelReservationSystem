package com.hotelres.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DBConnection {

    private static final Properties dbProperties = new Properties();

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("Warning: db.properties file not found! Database connection may fail.");
            } else {
                dbProperties.load(input);
            }

            // Ensure the MySQL driver is registered
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver not found!");
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = dbProperties.getProperty("db.url");
        String user = dbProperties.getProperty("db.user");
        String password = dbProperties.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException("Missing database configuration in db.properties!");
        }

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database connection successful!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }
}