package com.hotelres.database;

import com.hotelres.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDAO.class);

    // Check if a username already exists in the Users table.
    public boolean doesUsernameExist(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE Username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    // Register a new user using BCrypt for password encryption.
    public int registerUser(User user) throws SQLException {
        if (doesUsernameExist(user.getUsername())) {
            LOGGER.warn("Username already exists: {}", user.getUsername());
            return -1;
        }
        String sql = "INSERT INTO Users (Username, Password, Role) VALUES (?, ?, ?)";
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(user.getPassword());

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

    // Retrieve all users (if needed)
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
                        rs.getString("Role")
                );
                users.add(user);
            }
        }
        return users;
    }

    // Find a user by username.
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
                    user.setPassword(rs.getString("Password")); // BCrypt hash stored here.
                    user.setRole(rs.getString("Role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error finding user by username: {}", e.getMessage());
            throw e;
        }
        return null;
    }

    // Update a user's password (for demonstration; extend if additional fields need updating).
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET Password = ? WHERE UserID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setInt(2, user.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error updating user: {}", e.getMessage());
            throw e;
        }
    }
}