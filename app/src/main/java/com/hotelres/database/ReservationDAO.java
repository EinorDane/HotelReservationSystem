package com.hotelres.database;

import com.hotelres.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReservationDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationDAO.class);

    public void addReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO Reservations (GuestID, RoomID, CheckInDate, CheckOutDate, NumberOfGuests, TotalCost) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservation.getGuestId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, new java.sql.Date(reservation.getCheckInDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(reservation.getCheckOutDate().getTime()));
            pstmt.setInt(5, reservation.getNumberOfGuests());
            pstmt.setDouble(6, reservation.getTotalCost());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error adding reservation: {}", e.getMessage());
            throw e;
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
                        rs.getInt("NumberOfGuests"),
                        rs.getDouble("TotalCost")
                ));
            }
        } catch (SQLException e) {
            LOGGER.error("Error fetching reservations: {}", e.getMessage());
            throw new RuntimeException("Error fetching reservations", e);
        }
        return reservations;
    }

    public void updateReservation(Reservation reservation) throws SQLException {
        String sql = "UPDATE Reservations SET GuestID = ?, RoomID = ?, CheckInDate = ?, CheckOutDate = ?, NumberOfGuests = ?, TotalCost = ? WHERE ReservationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservation.getGuestId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, new java.sql.Date(reservation.getCheckInDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(reservation.getCheckOutDate().getTime()));
            pstmt.setInt(5, reservation.getNumberOfGuests());
            pstmt.setDouble(6, reservation.getTotalCost());
            pstmt.setInt(7, reservation.getReservationId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error updating reservation: {}", e.getMessage());
            throw e;
        }
    }

    public void deleteReservation(int reservationId) throws SQLException {
        String sql = "DELETE FROM Reservations WHERE ReservationID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error deleting reservation: {}", e.getMessage());
            throw e;
        }
    }
}