package com.hotelres.database;

import com.hotelres.model.Guest;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GuestDAO {

    // Method to add a new guest (Now includes UserID)
    public int addGuest(Guest guest) throws SQLException {
        String sql = "INSERT INTO Guests (GuestName, Address, PhoneNumber, EmailAddress, UserID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, guest.getGuestName());
            pstmt.setString(2, guest.getAddress());
            pstmt.setString(3, guest.getPhoneNumber());
            pstmt.setString(4, guest.getEmailAddress());
            pstmt.setInt(5, guest.getUserId()); // Added UserID support

            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Return generated GuestID
            } else {
                throw new SQLException("Failed to retrieve generated GuestID.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding guest: " + e.getMessage());
            throw e;
        }
    }

    // Method to get all guests (Now retrieves UserID)
    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM Guests";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Guest guest = new Guest(
                        rs.getInt("GuestID"),
                        rs.getString("GuestName"),
                        rs.getString("Address"),
                        rs.getString("PhoneNumber"),
                        rs.getString("EmailAddress"),
                        rs.getInt("UserID") // Added UserID retrieval
                );
                guests.add(guest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return guests;
    }

    // Method to update a guest (Includes UserID)
    public void updateGuest(Guest guest) {
        String sql = "UPDATE Guests SET GuestName = ?, Address = ?, PhoneNumber = ?, EmailAddress = ?, UserID = ? WHERE GuestID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, guest.getGuestName());
            pstmt.setString(2, guest.getAddress());
            pstmt.setString(3, guest.getPhoneNumber());
            pstmt.setString(4, guest.getEmailAddress());
            pstmt.setInt(5, guest.getUserId()); // Updated to support UserID
            pstmt.setInt(6, guest.getGuestId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to delete a guest
    public void deleteGuest(int guestId) {
        String sql = "DELETE FROM Guests WHERE GuestID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}