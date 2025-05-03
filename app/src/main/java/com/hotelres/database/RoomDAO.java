package com.hotelres.database;

import com.hotelres.model.Room;
import org.springframework.stereotype.Repository; // <-- Import the annotation
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomDAO {

    // Method to add a new room (with the Reserved field)
    public void addRoom(Room room) {
        String sql = "INSERT INTO Rooms (RoomNumber, RoomType, Capacity, RatePerNight, Reserved) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setDouble(4, room.getRatePerNight());
            pstmt.setBoolean(5, room.isReserved());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to get all rooms from the database
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("RoomID"),
                    rs.getString("RoomNumber"),
                    rs.getString("RoomType"),
                    rs.getInt("Capacity"),
                    rs.getDouble("RatePerNight"),
                    rs.getBoolean("Reserved")   // Retrieve the reserved status from the DB
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // New method: Return only available (not reserved) rooms
    public List<Room> getAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms WHERE Reserved = false";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("RoomID"),
                    rs.getString("RoomNumber"),
                    rs.getString("RoomType"),
                    rs.getInt("Capacity"),
                    rs.getDouble("RatePerNight"),
                    rs.getBoolean("Reserved")
                );
                availableRooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return availableRooms;
    }

    // Method to update a room, updating the Reserved flag as well
    public void updateRoom(Room room) {
        String sql = "UPDATE Rooms SET RoomNumber = ?, RoomType = ?, Capacity = ?, RatePerNight = ?, Reserved = ? WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setInt(3, room.getCapacity());
            pstmt.setDouble(4, room.getRatePerNight());
            pstmt.setBoolean(5, room.isReserved());
            pstmt.setInt(6, room.getRoomId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a room based on RoomID
    public void deleteRoom(int roomId) {
        String sql = "DELETE FROM Rooms WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // NEW: Method to retrieve a Room by its RoomID.
    public Room getRoomById(int roomId) {
        Room room = null;
        String sql = "SELECT * FROM Rooms WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    room = new Room(
                        rs.getInt("RoomID"),
                        rs.getString("RoomNumber"),
                        rs.getString("RoomType"),
                        rs.getInt("Capacity"),
                        rs.getDouble("RatePerNight"),
                        rs.getBoolean("Reserved")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }
}