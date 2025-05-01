package com.hotelres.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class DBConnection {

    private static final Properties dbProperties = new Properties();

    static {
        try (FileInputStream input = new FileInputStream("src/main/resources/db.properties")) { // Adjust path if needed
            dbProperties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading database properties", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = dbProperties.getProperty("db.url");
        String user = dbProperties.getProperty("db.user");
        String password = dbProperties.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }
}