package com.hotelres.controller;

import com.hotelres.database.UserDAO;
import com.hotelres.database.GuestDAO;
import com.hotelres.model.User;
import com.hotelres.model.Guest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final GuestDAO guestDAO;

    public AuthController(UserDAO userDAO, 
                         PasswordEncoder passwordEncoder,
                         GuestDAO guestDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.guestDAO = guestDAO;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        try {
            // Validate input
            if (user.getUsername() == null || user.getUsername().isEmpty()) {
                return ResponseEntity.badRequest().body("Username is required");
            }
            
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }

            // Set default role
            user.setRole(user.getRole() != null ? user.getRole().toUpperCase() : "GUEST");
            
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Register user
            int userId = userDAO.registerUser(user);
            if (userId == -1) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            // Create guest profile if needed
            if ("GUEST".equals(user.getRole())) {
                Guest guest = new Guest();
                guest.setGuestName(user.getUsername());
                guest.setUserId(userId);
                guestDAO.addGuest(guest);
            }

            return ResponseEntity.ok("User registered successfully! ID: " + userId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            User dbUser = userDAO.findByUsername(user.getUsername());
            if (dbUser == null) {
                return ResponseEntity.status(401).body("User not found");
            }
            if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
                return ResponseEntity.status(401).body("Invalid password");
            }
            // Optionally, return user info or a token here
            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Login failed: " + e.getMessage());
        }
    }
}