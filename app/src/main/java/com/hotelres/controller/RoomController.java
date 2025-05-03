package com.hotelres.controller;

import com.hotelres.database.RoomDAO;
import com.hotelres.model.Room;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomDAO roomDAO = new RoomDAO();

    // GET all rooms
    @GetMapping
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    // GET only available rooms
    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailableRooms();
    }

    // POST a new room
    @PostMapping
    public void addRoom(@RequestBody Room room) {
        roomDAO.addRoom(room);
    }

    // PUT to update a room
    @PutMapping("/{id}")
    public void updateRoom(@PathVariable int id, @RequestBody Room room) {
        room.setRoomId(id);
        roomDAO.updateRoom(room);
    }

    // DELETE a room
    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable int id) {
        roomDAO.deleteRoom(id);
    }
}