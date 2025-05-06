package com.hotelres;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test2 {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "newSecret123";

        // Paste the latest stored hash from your database here
        String storedHash = "$2a$10$/ldb5/J4H8ZxOY73nxmYOuZBKteUqvgN79L4B5yhmU/KkMvP.0byy";

        // Correctly compare raw password with stored hash
        boolean matches = encoder.matches(rawPassword, storedHash);

        // Print results
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Password matches? " + matches);
    }
}