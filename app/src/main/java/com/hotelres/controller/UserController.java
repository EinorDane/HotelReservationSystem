package com.hotelres.controller;

import com.hotelres.database.UserDAO;
import com.hotelres.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    // Inject both UserDAO and PasswordEncoder
    public UserController(UserDAO userDAO, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
    }
    
    // GET endpoint to retrieve the authenticated user's profile.
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         String username = auth.getName();
         System.out.println("DEBUG: Authenticated user -> " + username);

         try {
             User user = userDAO.findByUsername(username);
             if (user != null) {
                 return ResponseEntity.ok(user);
             } else {
                 return ResponseEntity.status(404).body("User not found.");
             }
         } catch(SQLException e) {
             return ResponseEntity.internalServerError().body("SQL Error retrieving user: " + e.getMessage());
         }
    }

    // PUT endpoint to update the authenticated user's profile (e.g., password update).
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         String username = auth.getName();

         try {
             User existingUser = userDAO.findByUsername(username);
             if (existingUser == null) {
                 return ResponseEntity.status(404).body("User not found.");
             }

             // Encode and update the password
             String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
             existingUser.setPassword(encodedPassword);
             System.out.println("DEBUG: New encoded password: " + encodedPassword);

             userDAO.updateUser(existingUser);
             return ResponseEntity.ok("Profile updated successfully.");
         } catch (SQLException e) {
             return ResponseEntity.internalServerError().body("SQL Error updating profile: " + e.getMessage());
         } catch (RuntimeException e) {
             return ResponseEntity.internalServerError().body("Error updating profile: " + e.getMessage());
         }
    }
}