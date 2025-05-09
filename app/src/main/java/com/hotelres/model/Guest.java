package com.hotelres.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Guest {
    
    @JsonProperty("GuestID")
    private int guestId;
    
    @JsonProperty("GuestName")
    private String guestName;
    
    @JsonProperty("Address")
    private String address;
    
    @JsonProperty("PhoneNumber")
    private String phoneNumber;
    
    @JsonProperty("EmailAddress")
    private String emailAddress;
    
    @JsonProperty("UserID")
    private int userId; // Links the guest profile to a User record

    // Default constructor (for JSON deserialization)
    public Guest() {
    }

    // Parameterized constructor
    public Guest(int guestId, String guestName, String address, String phoneNumber, String emailAddress, int userId) {
        this.guestId = guestId;
        this.guestName = guestName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.userId = userId;
    }

    public int getGuestId() {
        return guestId;
    }
    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
    public String getGuestName() {
        return guestName;
    }
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
}