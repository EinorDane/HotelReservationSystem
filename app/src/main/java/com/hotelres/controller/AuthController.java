package com.hotelres.controller;

import com.hotelres.database.UserDAO;
import com.hotelres.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder; // Import PasswordEncoder
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder; // Inject PasswordEncoder

    // Modify the constructor to inject both UserDAO and PasswordEncoder
    public AuthController(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }

    // POST endpoint for user signup/registration.
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        try {
            // Encode the user's plain text password using BCrypt before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            int userId = userDAO.registerUser(user);
            if (userId == -1) {
                return ResponseEntity.badRequest().body("Username already exists.");
            }
            return ResponseEntity.ok("User registered successfully! User ID: " + userId);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("SQL Error: " + e.getMessage());
        }
    }
}