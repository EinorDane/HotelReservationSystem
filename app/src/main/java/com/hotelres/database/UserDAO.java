package com.hotelres.database;

import com.hotelres.model.User;
import org.springframework.stereotype.Repository;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository // ✅ This registers UserDAO as a Spring component
public class UserDAO {

    public String hashPassword(String password, String salt) {
        try {
            String saltedPassword = salt + password;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0; 
        }
    }

    public int registerUser(User user) throws SQLException {
        if (doesUsernameExist(user.getUsername())) {
            System.out.println("⚠️ Error: Username already exists.");
            return -1;
        }

        String sql = "INSERT INTO Users (Username, Password, Role, Salt) VALUES (?, ?, ?, ?)";
        String salt = UUID.randomUUID().toString();
        String hashedPassword = hashPassword(user.getPassword(), salt);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, salt);

            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("UserID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Role"),
                        rs.getString("Salt")
                );
                users.add(user);
            }
        }
        return users;
    }
}