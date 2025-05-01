package com.hotelres.database;

import com.hotelres.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.sql.*;

public class UserDAO {

    // Method to hash a password using SHA-256 with salting
    private String hashPassword(String password, String salt) {
        try {
            String saltedPassword = salt + password; // Combine salt with password
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes) + ":" + salt; // Store hash + salt together
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    // ✅ Check if a username already exists before registration
    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, username exists
            }
        }
        return false;
    }

    // ✅ Register user safely, preventing duplicate usernames
    public int registerUser(User user) throws SQLException {
        if (doesUsernameExist(user.getUsername())) {
            System.out.println("⚠️ Error: Username already exists. Please choose a different one.");
            return -1; // Prevent duplicate registration
        }

        String sql = "INSERT INTO Users (Username, Password, Role) VALUES (?, ?, ?)";
        String salt = UUID.randomUUID().toString();
        String hashedPassword = hashPassword(user.getPassword(), salt);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getRole());

            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

    // Retrieve user and verify password during login
    public User getUserByUsername(String username, String inputPassword) throws SQLException {
        String sql = "SELECT * FROM Users WHERE Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("Password");

                // ✅ Check if the stored password has a salt
                if (!storedPassword.contains(":")) {
                    System.out.println("⚠️ Your account was created before password hashing was implemented.");
                    System.out.println("🔄 Please reset your password for security.");
                    return null; // Force user to reset password
                }

                String[] parts = storedPassword.split(":"); // Separate hash and salt safely
                String storedHash = parts[0];
                String storedSalt = parts[1];

                String inputHash = hashPassword(inputPassword, storedSalt); // Hash input password with stored salt

                if (!inputHash.equals(storedHash)) {
                    return null; // Password mismatch
                }

                return new User(
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    storedPassword, // Keep the hashed password
                    rs.getString("Role")
                );
            }
        }
        return null; // User not found
    }
}