package com.hotelres;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordMatchTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "newSecret123";
        String hashPasswordTest = encoder.encode(rawPassword);
        System.out.println("Raw password: " + rawPassword);
        System.out.println("Encoded password: " + hashPasswordTest);

        // Paste the latest stored hash from your database here
        String storedHash = "$2a$10$MB0bXgDVhWpkZYJg3TY.xecn41lwyKEPVIMPGIiTfUXm8OHT2QrKu";

        boolean matches = encoder.matches(rawPassword, encoder.encode(rawPassword));
        System.out.println("Password matches? " + matches);
    }
}