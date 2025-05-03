package com.hotelres.model;

public class User {
    private int userId;
    private String username;
    private String password; // Stored as hashed
    private String role; // "guest" or "staff"
    private String salt; // Unique salt for each user

    // Constructor
    public User(int userId, String username, String password, String role, String salt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.salt = salt;
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
}