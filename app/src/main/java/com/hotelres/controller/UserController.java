package com.hotelres.controller;

import com.hotelres.database.UserDAO;
import com.hotelres.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserDAO userDAO;

    // ✅ Remove @Autowired, Spring will detect this constructor automatically
    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        try {
            byte[] saltBytes = new byte[16];
            new SecureRandom().nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes);

            String hashedPassword = userDAO.hashPassword(user.getPassword(), salt);
            user.setSalt(salt);
            user.setPassword(hashedPassword);
            user.setRole("guest");

            int userId = userDAO.registerUser(user);
            return ResponseEntity.ok("User registered successfully! ID: " + userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error registering user.");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}