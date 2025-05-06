package com.hotelres.database;

import com.hotelres.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class UserDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);
    // Use the same encoder as in other parts of the app
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register a new user with proper BCrypt password encoding
    public int registerUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (Username, Password, Role) VALUES (?, ?, ?)";
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("DEBUG: Registering user " + user.getUsername() + " with hashed password: " + hashedPassword);

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

    // Retrieve user by username and log password hash for debugging
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM Users WHERE Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    user.setPassword(rs.getString("Password")); // Stored BCrypt hash
                    user.setRole(rs.getString("Role"));

                    // Log the retrieved hash so you can compare it
                    System.out.println("DEBUG: Retrieved user -> Username: " + user.getUsername() +
                                       " | Stored Password Hash: " + user.getPassword());
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding user by username: {}", e.getMessage());
            throw e;
        }
        return null;
    }

    // Update the user's password, with extra logging
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET Password = ? WHERE Username = ?";
        System.out.println("DEBUG: Updating user " + user.getUsername() + " with new hashed password: " + user.getPassword());
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error updating user: {}", e.getMessage());
            throw e;
        }
    }
}