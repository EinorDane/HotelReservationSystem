package com.hotelres;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordMatchTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "newSecret123";

        // Paste the latest stored hash from your database here
        String storedHash = "$2a$10$[PUT YOUR LATEST HASH FROM MySQL HERE]";

        boolean matches = encoder.matches(rawPassword, storedHash);
        System.out.println("Password matches? " + matches);
    }
}