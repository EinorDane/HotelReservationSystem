package com.hotelres.database;

import com.hotelres.model.Reservation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class ReservationDAO {
    private static final Logger LOGGER = Logger.getLogger(ReservationDAO.class.getName());

    public void addReservation(Reservation reservation) {
        String sql = "INSERT INTO Reservations (GuestID, RoomID, CheckInDate, CheckOutDate, NumberOfGuests) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservation.getGuestId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, reservation.getCheckInDate());
            pstmt.setDate(4, reservation.getCheckOutDate());
            pstmt.setInt(5, reservation.getNumberOfGuests());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.severe("Error adding reservation: " + e.getMessage());
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM Reservations";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservations.add(new Reservation(
                        rs.getInt("ReservationID"),
                        rs.getInt("GuestID"),
                        rs.getInt("RoomID"),
                        rs.getDate("CheckInDate"),
                        rs.getDate("CheckOutDate"),
                        rs.getInt("NumberOfGuests")
                ));
            }
        } catch (SQLException e) {
            LOGGER.severe("Error fetching reservations: " + e.getMessage());
        }
        return reservations;
    }

    public void updateReservation(Reservation reservation) {
        String sql = "UPDATE Reservations SET GuestID = ?, RoomID = ?, CheckInDate = ?, CheckOutDate = ?, NumberOfGuests = ? WHERE ReservationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservation.getGuestId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, reservation.getCheckInDate());
            pstmt.setDate(4, reservation.getCheckOutDate());
            pstmt.setInt(5, reservation.getNumberOfGuests());
            pstmt.setInt(6, reservation.getReservationId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.severe("Error updating reservation: " + e.getMessage());
        }
    }

    public void deleteReservation(int reservationId) {
        String sql = "DELETE FROM Reservations WHERE ReservationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.severe("Error deleting reservation: " + e.getMessage());
        }
    }

    // 🔹 Method to get available room IDs (rooms not currently reserved)
public List<Integer> getAvailableRoomIds() {
    List<Integer> availableRoomIds = new ArrayList<>();
    String sql = "SELECT RoomID FROM rooms WHERE RoomID NOT IN " +
                 "(SELECT RoomID FROM reservations WHERE CheckInDate <= CURDATE() AND CheckOutDate >= CURDATE())";

    try (Connection conn = DBConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            availableRoomIds.add(rs.getInt("RoomID"));
        }
    } catch (SQLException e) {
        LOGGER.severe("Error fetching available rooms: " + e.getMessage());
    }
    return availableRoomIds;
    }
}