package com.hotelres.controller;

import com.hotelres.database.ReservationDAO;
import com.hotelres.model.Reservation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationDAO reservationDAO;

    // 🔹 Guests & Staff can view reservations
    @PreAuthorize("hasAnyRole('GUEST', 'STAFF')")
    @GetMapping
    public ResponseEntity<List<Reservation>> getReservations() {
        return ResponseEntity.ok(reservationDAO.getAllReservations());
    }

    // 🔹 Guests can add reservations
    @PreAuthorize("hasRole('GUEST')")
    @PostMapping
    public ResponseEntity<String> addReservation(@RequestBody Reservation reservation) {
        reservationDAO.addReservation(reservation);
        return ResponseEntity.ok("Reservation added successfully!");
    }

    // 🔹 Staff can update reservations
    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable int id, @RequestBody Reservation reservation) {
        reservation.setReservationId(id);
        reservationDAO.updateReservation(reservation);
        return ResponseEntity.ok("Reservation updated successfully!");
    }

    // 🔹 Staff can delete reservations
    @PreAuthorize("hasRole('STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable int id) {
        reservationDAO.deleteReservation(id);
        return ResponseEntity.ok("Reservation deleted successfully!");
    }

    // 🔹 Debugging endpoint to check user role
    @GetMapping("/check-role")
    public ResponseEntity<String> checkUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Current user role: " + authentication.getAuthorities());
    }
}