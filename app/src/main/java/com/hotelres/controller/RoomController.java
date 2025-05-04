package com.hotelres.controller;

import com.hotelres.database.RoomDAO;
import com.hotelres.model.Room;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomDAO roomDAO;

    public RoomController(RoomDAO roomDAO) {
        this.roomDAO = roomDAO;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailableRooms();
    }

    @PostMapping
    public void addRoom(@RequestBody Room room) {
        roomDAO.addRoom(room);
    }

    @PutMapping("/{id}")
    public void updateRoom(@PathVariable int id, @RequestBody Room room) {
        room.setRoomId(id);
        roomDAO.updateRoom(room);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable int id) {
        roomDAO.deleteRoom(id);
    }
}