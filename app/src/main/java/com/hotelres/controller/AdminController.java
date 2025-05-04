package com.hotelres.controller;

import com.hotelres.database.ReservationDAO;
import com.hotelres.database.RoomDAO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RoomDAO roomDAO;
    private final ReservationDAO reservationDAO;

    public AdminController(RoomDAO roomDAO, ReservationDAO reservationDAO) {
         this.roomDAO = roomDAO;
         this.reservationDAO = reservationDAO;
    }

    // GET endpoint for a quick summary dashboard
    @GetMapping("/summary")
    public ResponseEntity<?> getSummary() {
         Map<String, Object> summary = new HashMap<>();
         summary.put("totalRooms", roomDAO.getAllRooms().size());
         summary.put("availableRooms", roomDAO.getAvailableRooms().size());
         summary.put("totalReservations", reservationDAO.getAllReservations().size());
         return ResponseEntity.ok(summary);
    }

    // GET endpoint to simulate a database backup operation (stub)
    @GetMapping("/backup")
    public ResponseEntity<String> backupDatabase() {
         // In a real implementation, trigger backup procedures.
         return ResponseEntity.ok("Database backup initiated successfully.");
    }
}