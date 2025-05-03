package com.hotelres.controller;

import com.hotelres.database.GuestDAO;
import com.hotelres.model.Guest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestDAO guestDAO;

    // Single constructor, no need for @Autowired here
    public GuestController(GuestDAO guestDAO) {
        this.guestDAO = guestDAO;
    }

    // GET all guests
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<List<Guest>> getAllGuests() {
        List<Guest> guests = guestDAO.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    // POST a new guest (guest registration can be public)
    @PostMapping
    public ResponseEntity<String> addGuest(@RequestBody Guest guest) {
        try {
            int guestId = guestDAO.addGuest(guest);
            return ResponseEntity.ok("Guest added successfully! Guest ID: " + guestId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error adding guest: " + e.getMessage());
        }
    }

    // PUT to update guest info
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> updateGuest(@PathVariable int id, @RequestBody Guest guest) {
        guest.setGuestId(id);
        guestDAO.updateGuest(guest);
        return ResponseEntity.ok("Guest updated successfully!");
    }

    // DELETE a guest
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<String> deleteGuest(@PathVariable int id) {
        guestDAO.deleteGuest(id);
        return ResponseEntity.ok("Guest deleted successfully!");
    }
}