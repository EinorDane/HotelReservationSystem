package com.hotelres.controller;

import com.hotelres.database.ReservationDAO;
import com.hotelres.database.RoomDAO;
import com.hotelres.model.Reservation;
import com.hotelres.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationDAO reservationDAO;

    @Autowired
    private RoomDAO roomDAO;  // Used for retrieving room details

    // 🔹 Guests & Staff can view reservations
    @PreAuthorize("hasAnyRole('GUEST', 'STAFF')")
    @GetMapping
    public ResponseEntity<List<Reservation>> getReservations() {
        List<Reservation> reservations = reservationDAO.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // 🔹 Guests can add reservations. Compute total cost here based on the room rate and stay duration.
    @PreAuthorize("hasRole('GUEST')")
    @PostMapping
    public ResponseEntity<String> addReservation(@RequestBody Reservation reservation) {
        // Retrieve the room details using RoomDAO to get the room's rate.
        Room room = roomDAO.getRoomById(reservation.getRoomId());
        if (room == null) {
            return ResponseEntity.badRequest().body("Invalid room selection.");
        }
        // Calculate the duration (in days) between check-in and check-out.
        long diffInMillis = Math.abs(reservation.getCheckInDate().getTime() - reservation.getCheckOutDate().getTime());
        long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        double totalCost = days * room.getRatePerNight();
        reservation.setTotalCost(totalCost);

        reservationDAO.addReservation(reservation);
        return ResponseEntity.ok("Reservation added successfully! Total cost: $" + totalCost);
    }

    // 🔹 Staff can update reservations. Recalculate total cost if needed.
    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable int id, @RequestBody Reservation reservation) {
        reservation.setReservationId(id);
        // Optionally recalculate cost if room or dates changed.
        Room room = roomDAO.getRoomById(reservation.getRoomId());
        if (room != null) {
            long diffInMillis = Math.abs(reservation.getCheckOutDate().getTime() - reservation.getCheckInDate().getTime());
            long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            double totalCost = days * room.getRatePerNight();
            reservation.setTotalCost(totalCost);
        }
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
        return ResponseEntity.ok("Current user role: " + authentication.getAuthorities().toString());
    }
}