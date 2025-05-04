package com.hotelres.model;

public class Room {
    private int roomId;
    private String roomNumber;
    private String roomType;
    private int capacity;
    private double ratePerNight;
    private boolean reserved;

    // Default constructor (for JSON deserialization)
    public Room() {
    }

    // Parameterized constructor
    public Room(int roomId, String roomNumber, String roomType, int capacity, double ratePerNight, boolean reserved) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.capacity = capacity;
        this.ratePerNight = ratePerNight;
        this.reserved = reserved;
    }

    // Getters and setters
    public int getRoomId() {
        return roomId;
    }
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    public String getRoomType() {
        return roomType;
    }
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public double getRatePerNight() {
        return ratePerNight;
    }
    public void setRatePerNight(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }
    public boolean isReserved() {
        return reserved;
    }
    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }
}