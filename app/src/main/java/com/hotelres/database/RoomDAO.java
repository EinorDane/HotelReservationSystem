package com.hotelres.database;

import com.hotelres.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoomDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoomDAO.class);

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
            LOGGER.error("Error adding room: {}", e.getMessage());
            e.printStackTrace();
        }
    }

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
                        rs.getBoolean("Reserved")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching all rooms: {}", e.getMessage());
            e.printStackTrace();
        }
        return rooms;
    }

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
            LOGGER.error("Error fetching available rooms: {}", e.getMessage());
            e.printStackTrace();
        }
        return availableRooms;
    }

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
            LOGGER.error("Error updating room: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteRoom(int roomId) {
        String sql = "DELETE FROM Rooms WHERE RoomID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error deleting room: {}", e.getMessage());
            e.printStackTrace();
        }
    }

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
            LOGGER.error("Error retrieving room by ID: {}", e.getMessage());
            e.printStackTrace();
        }
        return room;
    }

    public boolean isRoomAvailable(int roomId, java.util.Date checkIn, java.util.Date checkOut) {
        String sql = "SELECT COUNT(*) FROM Reservations " +
                     "WHERE RoomID = ? " +
                     "AND NOT (CheckOutDate <= ? OR CheckInDate >= ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setDate(2, new java.sql.Date(checkIn.getTime()));
            pstmt.setDate(3, new java.sql.Date(checkOut.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking room availability: {}", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error checking room availability", e);
        }
        return false;
    }
}