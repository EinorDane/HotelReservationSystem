package com.hotelres.model;

public class PaymentRequest {
    private int reservationId;
    private double amount;
    private String paymentMethod; // e.g., "CreditCard", "PayPal"

    // Getters and setters
    public int getReservationId() {
        return reservationId;
    }
    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}