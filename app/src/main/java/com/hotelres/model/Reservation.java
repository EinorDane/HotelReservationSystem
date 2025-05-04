package com.hotelres.model;

import java.util.Date;

public class Reservation {
    private int reservationId;
    private int guestId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private int numberOfGuests;
    private double totalCost;

    // Default no-argument constructor (for JSON deserialization)
    public Reservation() {
    }

    // Parameterized constructor
    public Reservation(int reservationId, int guestId, int roomId, Date checkInDate, Date checkOutDate, int numberOfGuests, double totalCost) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.totalCost = totalCost;
    }

    // Getters and setters
    public int getReservationId() {
        return reservationId;
    }
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
    public int getGuestId() {
        return guestId;
    }
    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }
    public int getRoomId() {
        return roomId;
    }
    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    public Date getCheckInDate() {
        return checkInDate;
    }
    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }
    public Date getCheckOutDate() {
        return checkOutDate;
    }
    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    public int getNumberOfGuests() {
        return numberOfGuests;
    }
    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    public double getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}