package com.hotelres.model;

public class Guest {
    private int guestId;
    private String guestName;
    private String address;
    private String phoneNumber;
    private String emailAddress;
    private int userId; // Added UserID field

    // Updated Constructor
    public Guest(int guestId, String guestName, String address, String phoneNumber, String emailAddress, int userId) {
        this.guestId = guestId;
        this.guestName = guestName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.userId = userId;
    }

    // Getters and Setters
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}