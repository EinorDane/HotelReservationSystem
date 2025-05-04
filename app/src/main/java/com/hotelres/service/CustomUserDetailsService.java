package com.hotelres.service;

import com.hotelres.database.UserDAO;
import com.hotelres.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    public CustomUserDetailsService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // Retrieve the user from the database using your DAO.
            User user = userDAO.findByUsername(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            
            // Normalize the role by converting it to uppercase.
            // For example, if the stored role is "guest", this becomes "GUEST".
            String role = (user.getRole() != null) ? user.getRole().toUpperCase() : "GUEST";
            
            // Create a list of granted authorities with the expected prefix.
            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            
            // Debugging: Log the loaded user's details.
            // Print the username and the beginning of the password hash (for security purposes, don't log full passwords).
            String passwordPreview = (user.getPassword() != null && user.getPassword().length() > 10) 
                                     ? user.getPassword().substring(0, 10) + "..." 
                                     : user.getPassword();
            System.out.println("DEBUG: Loaded user: " + user.getUsername() + 
                               " | Password hash preview: " + passwordPreview +
                               " | Role: " + role);
            
            // Verify that the password appears to be in BCrypt format.
            if (user.getPassword() == null || 
                (!user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$"))) {
                System.out.println("WARNING: The password for user " + user.getUsername() + " does not appear to be BCrypt hashed.");
            }
            
            // Return a Spring Security User with the username, BCrypt-hashed password, and the authorities.
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        } catch (SQLException ex) {
            throw new UsernameNotFoundException("SQL error while retrieving user: " + username, ex);
        }
    }
}