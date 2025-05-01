package com.hotelres.model;

import java.sql.Date;

public class Reservation {
    private int reservationId;  // Unique identifier for the reservation
    private int guestId;        // ID of the guest making the reservation
    private int roomId;         // ID of the room being reserved
    private Date checkInDate;   // Check-in date for the reservation
    private Date checkOutDate;  // Check-out date for the reservation
    private int numberOfGuests; // Number of guests for the reservation

    // Constructor
    public Reservation(int reservationId, int guestId, int roomId, Date checkInDate, Date checkOutDate, int numberOfGuests) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
    }

    // Getters and Setters
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
}