package com.hotelres.controller;

import com.hotelres.database.ReservationDAO;
import com.hotelres.database.RoomDAO;
import com.hotelres.database.UserDAO;
import com.hotelres.database.GuestDAO;  // Import the GuestDAO
import com.hotelres.model.Reservation;
import com.hotelres.model.Room;
import com.hotelres.model.User;
import com.hotelres.model.Guest;         // Import the Guest model
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final UserDAO userDAO;
    private final GuestDAO guestDAO;   // Declare GuestDAO

    public ReservationController(ReservationDAO reservationDAO, RoomDAO roomDAO, UserDAO userDAO, GuestDAO guestDAO) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.userDAO = userDAO;
        this.guestDAO = guestDAO;   // Inject GuestDAO via constructor
    }
    
    @PreAuthorize("hasAnyRole('GUEST', 'STAFF')")
    @GetMapping
    public ResponseEntity<List<Reservation>> getReservations() {
        List<Reservation> reservations = reservationDAO.getAllReservations();
        return ResponseEntity.ok(reservations);
    }
    
    @PreAuthorize("hasRole('GUEST')")
    @PostMapping
    public ResponseEntity<String> addReservation(@RequestBody Reservation reservation) {
        if (reservation.getCheckInDate() == null || reservation.getCheckOutDate() == null) {
            return ResponseEntity.badRequest().body("Check-in and check-out dates must be provided.");
        }
        if (!reservation.getCheckOutDate().after(reservation.getCheckInDate())) {
            return ResponseEntity.badRequest().body("Check-out date must be after check-in date.");
        }
        if (reservation.getNumberOfGuests() <= 0) {
            return ResponseEntity.badRequest().body("Number of guests must be at least one.");
        }
        Room room = roomDAO.getRoomById(reservation.getRoomId());
        if (room == null) {
            return ResponseEntity.badRequest().body("Invalid room selection.");
        }
        if (!roomDAO.isRoomAvailable(room.getRoomId(), reservation.getCheckInDate(), reservation.getCheckOutDate())) {
            return ResponseEntity.badRequest().body("The selected room is not available for the chosen dates.");
        }
        // Retrieve the authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User authUser;
        try {
            authUser = userDAO.findByUsername(username);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("SQL Error retrieving user: " + e.getMessage());
        }
        if (authUser == null) {
            System.out.println("TEST");
            return ResponseEntity.status(401).body("Authenticated user not found.");
        }
        // Retrieve the corresponding guest record for this user
        Guest guest;
        try {
            guest = guestDAO.findByUserId(authUser.getUserId());
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("SQL Error retrieving guest: " + e.getMessage());
        }
        if (guest == null) {
            return ResponseEntity.internalServerError().body("Guest record not found for user " + authUser.getUserId());
        }
        // Now set the reservation's GuestID properly
        reservation.setGuestId(guest.getGuestId());
        
        // Calculate total cost
        long diffInMillis = Math.abs(reservation.getCheckOutDate().getTime() - reservation.getCheckInDate().getTime());
        long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        if (days == 0) {
            days = 1;
        }
        double totalCost = days * room.getRatePerNight();
        reservation.setTotalCost(totalCost);
        try {
            reservationDAO.addReservation(reservation);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("SQL Error adding reservation: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Error adding reservation: " + e.getMessage());
        }
        return ResponseEntity.ok("Reservation added successfully! Total cost: $" + totalCost);
    }
    
    // ... (the rest of your methods remain unchanged)
    
    @PreAuthorize("hasRole('STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable int id, @RequestBody Reservation reservation) {
        reservation.setReservationId(id);
        if (reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            if (!reservation.getCheckOutDate().after(reservation.getCheckInDate())) {
                return ResponseEntity.badRequest().body("Check-out date must be after check-in date.");
            }
        }
        if (reservation.getNumberOfGuests() <= 0) {
            return ResponseEntity.badRequest().body("Number of guests must be at least one.");
        }
        Room room = roomDAO.getRoomById(reservation.getRoomId());
        if (room != null && reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            long diffInMillis = Math.abs(reservation.getCheckOutDate().getTime() - reservation.getCheckInDate().getTime());
            long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
            if (days == 0) {
                days = 1;
            }
            double totalCost = days * room.getRatePerNight();
            reservation.setTotalCost(totalCost);
        }
        try {
            reservationDAO.updateReservation(reservation);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("SQL Error updating reservation: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Error updating reservation: " + e.getMessage());
        }
        return ResponseEntity.ok("Reservation updated successfully!");
    }
    
    @PreAuthorize("hasRole('STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable int id) {
        try {
            reservationDAO.deleteReservation(id);
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("SQL Error deleting reservation: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body("Error deleting reservation: " + e.getMessage());
        }
        return ResponseEntity.ok("Reservation deleted successfully!");
    }
    
    @GetMapping("/check-role")
    public ResponseEntity<String> checkUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok("Current user role: " + authentication.getAuthorities().toString());
    }
}