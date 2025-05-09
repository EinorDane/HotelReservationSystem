package com.hotelres;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Test2 {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "newSecret123";

        // Paste the latest stored hash from your database here
        String storedHash = "$2a$10$MB0bXgDVhWpkZYJg3TY.xecn41lwyKEPVIMPGIiTfUXm8OHT2QrKu";

        // Correctly compare raw password with stored hash
        boolean matches = encoder.matches(rawPassword, storedHash);

        // Print results
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Stored hash: " + storedHash);
        System.out.println("Password matches? " + matches);
    }
}