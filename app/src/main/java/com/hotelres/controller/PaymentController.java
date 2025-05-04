package com.hotelres.controller;

import com.hotelres.model.PaymentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    // POST endpoint to initiate a payment (stub implementation)
    @PostMapping("/initiate")
    public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
        // In a full implementation, integrate with a payment gateway (e.g., Stripe, PayPal)
        return ResponseEntity.ok("Payment initiated successfully. Payment ID: 12345");
    }

    // GET endpoint to check the payment status (stub implementation)
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<String> getPaymentStatus(@PathVariable String paymentId) {
        return ResponseEntity.ok("Payment status for " + paymentId + " is: COMPLETED");
    }
}