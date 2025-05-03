package com.hotelres.model;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Properties;
import java.io.FileInputStream; // Ensure the file path is correct
import java.io.IOException;

public class DatabaseManager {

    private String url;
    private String user;
    private String password;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
        }
    }

    public DatabaseManager() {
        loadDbProperties(); // Load properties in the constructor
    }

    private void loadDbProperties() {
        Properties dbProperties = new Properties();
        try (FileInputStream input = new FileInputStream("src/main/resources/db.properties")) { // Adjusted path to match Maven standard
            dbProperties.load(input);
            this.url = dbProperties.getProperty("db.url");
            this.user = dbProperties.getProperty("db.user");
            this.password = dbProperties.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Error loading database properties", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

      /**
       * Helper method to check if an ID exists in a specific table.
       * @param tableName The name of the table (e.g., "Guests", "Rooms").
       * @param idColumnName The name of the ID column (e.g., "GuestID", "RoomID").
       * @param id The ID value to check for.
       * @return true if the ID exists, false otherwise or if an error occurs.
       */
     private boolean entityExists(String tableName, String idColumnName, int id) {
         // Basic input validation to prevent SQL injection if tableName/idColumnName were dynamic (they aren't here)
         if (tableName == null || tableName.trim().isEmpty() || idColumnName == null || idColumnName.trim().isEmpty() || tableName.contains(" ") || idColumnName.contains(" ")) {
             System.err.println("Invalid table or column name provided for existence check.");
             return false;
         }
         String query = "SELECT 1 FROM `" + tableName + "` WHERE `" + idColumnName + "` = ? LIMIT 1"; // Use backticks for safety
         try (Connection connection = getConnection();
              PreparedStatement ps = connection.prepareStatement(query)) {
             ps.setInt(1, id);
             try (ResultSet rs = ps.executeQuery()) {
                 return rs.next(); // Returns true if a row is found
             }
         } catch (SQLException e) {
             System.err.println("Error checking existence for " + tableName + "." + idColumnName + " = " + id + ": " + e.getMessage());
             // Log the stack trace for debugging if needed
             // e.printStackTrace();
             return false; // Assume not exists if error occurs
         }
     }

     /**
      * Adds a new room to the database, checking for duplicate room numbers first.
      * @param roomNumber The unique number/identifier for the room.
      * @param roomType The type of room (e.g., "Single", "Double", "Suite").
      * @param capacity The maximum number of occupants.
      * @param ratePerNight The cost per night.
      */
     public void addRoom(String roomNumber, String roomType, int capacity, double ratePerNight) {
         // Input validation
         if (roomNumber == null || roomNumber.trim().isEmpty() || roomType == null || roomType.trim().isEmpty() || capacity <= 0 || ratePerNight < 0) {
             System.err.println("Invalid room data provided. Room not added.");
             return;
         }

         // Check if the room number already exists
         String checkQuery = "SELECT 1 FROM Rooms WHERE RoomNumber = ? LIMIT 1";
         try (Connection connection = getConnection();
              PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
             checkStatement.setString(1, roomNumber);
             try (ResultSet resultSet = checkStatement.executeQuery()) {
                 if (resultSet.next()) {
                     System.out.println("Room number '" + roomNumber + "' already exists. Please use a different room number.");
                     return; // Exit the method if the room number exists
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error checking if room exists: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             return; // Exit the method if there's an error during the check
         }

         // Proceed to insert the new room
         String insertQuery = "INSERT INTO Rooms (RoomNumber, RoomType, Capacity, RatePerNight) VALUES (?, ?, ?, ?)";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

             preparedStatement.setString(1, roomNumber);
             preparedStatement.setString(2, roomType);
             preparedStatement.setInt(3, capacity);
             preparedStatement.setDouble(4, ratePerNight);

             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Room '" + roomNumber + "' added successfully!");
             } else {
                 // This should ideally not happen if the check passed and no other error occurred
                 System.err.println("Room '" + roomNumber + "' could not be added for an unknown reason.");
             }
         } catch (SQLException e) {
             System.err.println("Error adding room '" + roomNumber + "': " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

     /**
      * Displays all rooms currently in the database.
      */
     public void viewRooms() {
         String query = "SELECT RoomID, RoomNumber, RoomType, Capacity, RatePerNight FROM Rooms ORDER BY RoomNumber"; // Added ordering
         System.out.println("\n--- Room Listing ---");
         boolean found = false;
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query);
              ResultSet resultSet = preparedStatement.executeQuery()) {

             while (resultSet.next()) {
                 found = true;
                 int roomId = resultSet.getInt("RoomID");
                 String roomNumber = resultSet.getString("RoomNumber");
                 String roomType = resultSet.getString("RoomType");
                 int capacity = resultSet.getInt("Capacity");
                 double ratePerNight = resultSet.getDouble("RatePerNight");
                 System.out.printf("ID: %-4d | Number: %-8s | Type: %-10s | Capacity: %-2d | Rate: $%.2f%n",
                                   roomId, roomNumber, roomType, capacity, ratePerNight);
             }
             if (!found) {
                 System.out.println("No rooms found in the database.");
             }

         } catch (SQLException e) {
             System.err.println("Error viewing rooms: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
         System.out.println("--- End of Listing ---");
     }

     /**
      * Updates the details of an existing room.
      * @param roomId The ID of the room to update.
      * @param roomNumber The new room number.
      * @param roomType The new room type.
      * @param capacity The new capacity.
      * @param ratePerNight The new rate per night.
      */
     public void updateRoom(int roomId, String roomNumber, String roomType, int capacity, double ratePerNight) {
         // Input validation
         if (roomNumber == null || roomNumber.trim().isEmpty() || roomType == null || roomType.trim().isEmpty() || capacity <= 0 || ratePerNight < 0) {
             System.err.println("Invalid room data provided. Update failed.");
             return;
         }

         // Check if the new room number conflicts with another existing room
         String checkQuery = "SELECT RoomID FROM Rooms WHERE RoomNumber = ? AND RoomID != ? LIMIT 1";
         try (Connection connection = getConnection();
              PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
             checkStatement.setString(1, roomNumber);
             checkStatement.setInt(2, roomId);
             try (ResultSet rs = checkStatement.executeQuery()) {
                 if (rs.next()) {
                     int conflictingRoomId = rs.getInt("RoomID");
                     System.err.println("Error: Room number '" + roomNumber + "' is already assigned to Room ID " + conflictingRoomId + ". Update failed.");
                     return;
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error checking room number conflict during update: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             return; // Stop update if check fails
         }

         // Proceed with the update
         String query = "UPDATE Rooms SET RoomNumber = ?, RoomType = ?, Capacity = ?, RatePerNight = ? WHERE RoomID = ?";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query)) {

             preparedStatement.setString(1, roomNumber);
             preparedStatement.setString(2, roomType);
             preparedStatement.setInt(3, capacity);
             preparedStatement.setDouble(4, ratePerNight);
             preparedStatement.setInt(5, roomId);

             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Room ID " + roomId + " updated successfully!");
             } else {
                 System.out.println("Room with ID " + roomId + " not found or data was unchanged. No update performed.");
             }
         } catch (SQLException e) {
             System.err.println("Error updating room ID " + roomId + ": " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

     /**
      * Deletes a room from the database after checking for existing reservations.
      * @param roomId The ID of the room to delete.
      */
     public void deleteRoom(int roomId) {
         // Check for existing reservations before deleting
         String checkReservationsQuery = "SELECT 1 FROM Reservations WHERE RoomID = ? LIMIT 1";
         try (Connection connection = getConnection();
              PreparedStatement checkStmt = connection.prepareStatement(checkReservationsQuery)) {
             checkStmt.setInt(1, roomId);
             try (ResultSet rs = checkStmt.executeQuery()) {
                 if (rs.next()) {
                     System.out.println("Cannot delete Room ID " + roomId + " as it has existing reservations. Please delete associated reservations first.");
                     return; // Stop deletion
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error checking reservations for room deletion (Room ID " + roomId + "): " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             return; // Stop deletion if check fails
         }

         // Proceed with deletion if no reservations found
         String deleteQuery = "DELETE FROM Rooms WHERE RoomID = ?";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

             preparedStatement.setInt(1, roomId);
             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Room ID " + roomId + " deleted successfully!");
             } else {
                 System.out.println("Room with ID " + roomId + " not found. No deletion performed.");
             }
         } catch (SQLException e) {
             System.err.println("Error deleting room ID " + roomId + ": " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

     // --- Guest Management Methods ---

     /**
      * Adds a new guest to the database, checking for duplicates based on name and phone number.
      * Consider using email or phone as a more reliable unique identifier check if appropriate.
      * @param guestName Guest's full name.
      * @param address Guest's address.
      * @param phoneNumber Guest's phone number.
      * @param emailAddress Guest's email address.
      */
     public void addGuest(String guestName, String address, String phoneNumber, String emailAddress) {
         // Basic Input Validation
         if (guestName == null || guestName.trim().isEmpty() || phoneNumber == null || phoneNumber.trim().isEmpty()) {
              System.err.println("Guest name and phone number are required. Guest not added.");
              return;
          }
          // Optional: More specific validation for phone/email format

         // Check if the guest already exists (using Name and Phone as per original code)
         // Consider uniqueness constraint on PhoneNumber or EmailAddress in the database for better data integrity
         String checkQuery = "SELECT 1 FROM Guests WHERE GuestName = ? AND PhoneNumber = ? LIMIT 1";
         try (Connection connection = getConnection();
              PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {

             checkStatement.setString(1, guestName);
             checkStatement.setString(2, phoneNumber);
             try (ResultSet resultSet = checkStatement.executeQuery()) {
                 if (resultSet.next()) {
                     System.out.println("Guest '" + guestName + "' with phone number '" + phoneNumber + "' already exists.");
                     return; // Exit if duplicate found
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error checking if guest exists: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             return; // Exit if error during check
         }

         // Proceed to insert the new guest
         String insertQuery = "INSERT INTO Guests (GuestName, Address, PhoneNumber, EmailAddress) VALUES (?, ?, ?, ?)";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

             preparedStatement.setString(1, guestName);
             preparedStatement.setString(2, address); // Allow null or empty address/email if needed
             preparedStatement.setString(3, phoneNumber);
             preparedStatement.setString(4, emailAddress);

             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Guest '" + guestName + "' added successfully!");
             } else {
                 System.err.println("Guest '" + guestName + "' could not be added for an unknown reason.");
             }
         } catch (SQLException e) {
             System.err.println("Error adding guest '" + guestName + "': " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

      /**
      * Displays all guests currently in the database.
      */
     public void viewGuests() {
         String query = "SELECT GuestID, GuestName, Address, PhoneNumber, EmailAddress FROM Guests ORDER BY GuestName";
         System.out.println("\n--- Guest Listing ---");
         boolean found = false;
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query);
              ResultSet resultSet = preparedStatement.executeQuery()) {

             while (resultSet.next()) {
                 found = true;
                 int guestId = resultSet.getInt("GuestID");
                 String guestName = resultSet.getString("GuestName");
                 String address = resultSet.getString("Address");
                 String phoneNumber = resultSet.getString("PhoneNumber");
                 String emailAddress = resultSet.getString("EmailAddress");
                 System.out.printf("ID: %-4d | Name: %-20s | Phone: %-15s | Email: %-25s | Address: %s%n",
                                   guestId, guestName, phoneNumber, emailAddress, address);
             }
             if (!found) {
                 System.out.println("No guests found in the database.");
             }
         } catch (SQLException e) {
             System.err.println("Error viewing guests: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
         System.out.println("--- End of Listing ---");
     }

     /**
      * Updates the details of an existing guest.
      * @param guestId The ID of the guest to update.
      * @param guestName The new name.
      * @param address The new address.
      * @param phoneNumber The new phone number.
      * @param emailAddress The new email address.
      */
     public void updateGuest(int guestId, String guestName, String address, String phoneNumber, String emailAddress) {
         // Basic Input Validation
         if (guestName == null || guestName.trim().isEmpty() || phoneNumber == null || phoneNumber.trim().isEmpty()) {
              System.err.println("Guest name and phone number are required. Update failed.");
              return;
          }

         // Optional: Check for conflicts if phone/email should be unique across other guests
         // String conflictCheckQuery = "SELECT GuestID FROM Guests WHERE (PhoneNumber = ? OR EmailAddress = ?) AND GuestID != ? LIMIT 1";
         // ... implementation similar to room number conflict check ...

         String query = "UPDATE Guests SET GuestName = ?, Address = ?, PhoneNumber = ?, EmailAddress = ? WHERE GuestID = ?";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query)) {

             preparedStatement.setString(1, guestName);
             preparedStatement.setString(2, address);
             preparedStatement.setString(3, phoneNumber);
             preparedStatement.setString(4, emailAddress);
             preparedStatement.setInt(5, guestId);

             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Guest ID " + guestId + " updated successfully!");
             } else {
                 System.out.println("Guest with ID " + guestId + " not found or data was unchanged. No update performed.");
             }
         } catch (SQLException e) {
             System.err.println("Error updating guest ID " + guestId + ": " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

     /**
      * Deletes a guest from the database after checking for existing reservations.
      * @param guestId The ID of the guest to delete.
      */
     public void deleteGuest(int guestId) {
         // Check for existing reservations before deleting a guest
         String checkReservationsQuery = "SELECT 1 FROM Reservations WHERE GuestID = ? LIMIT 1";
         try (Connection connection = getConnection();
              PreparedStatement checkStmt = connection.prepareStatement(checkReservationsQuery)) {
             checkStmt.setInt(1, guestId);
             try (ResultSet rs = checkStmt.executeQuery()) {
                 if (rs.next()) {
                     System.out.println("Cannot delete Guest ID " + guestId + " as they have existing reservations. Please delete associated reservations first.");
                     return; // Stop deletion
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error checking reservations for guest deletion (Guest ID " + guestId + "): " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             return; // Stop deletion if check fails
         }

         // Proceed with deletion
         String deleteQuery = "DELETE FROM Guests WHERE GuestID = ?";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

             preparedStatement.setInt(1, guestId);
             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Guest ID " + guestId + " deleted successfully!");
             } else {
                 System.out.println("Guest with ID " + guestId + " not found. No deletion performed.");
             }
         } catch (SQLException e) {
             System.err.println("Error deleting guest ID " + guestId + ": " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

     // --- Reservation Management Methods ---

     /**
      * Adds a new reservation, performing validations first.
      * @param guestId ID of the guest making the reservation.
      * @param roomId ID of the room being reserved.
      * @param checkInDate Check-in date (String in 'YYYY-MM-DD' format recommended for comparison).
      * @param checkOutDate Check-out date (String in 'YYYY-MM-DD' format recommended for comparison).
      * @param numberOfGuests Number of guests for the reservation.
      */
     public void addReservation(int guestId, int roomId, String checkInDate, String checkOutDate, int numberOfGuests) {
         // Validate date strings and range
         if (checkInDate == null || checkOutDate == null || checkInDate.compareTo(checkOutDate) >= 0) {
             System.err.println("Invalid date range: Check-in date must be before check-out date and dates cannot be null. Reservation failed.");
             return;
         }
         // Optional: Add more robust date format validation (e.g., using SimpleDateFormat or java.time.LocalDate)

         if (numberOfGuests <= 0) {
             System.err.println("Number of guests must be positive. Reservation failed.");
             return;
         }

         // Validate GuestID existence
         if (!entityExists("Guests", "GuestID", guestId)) {
             System.err.println("Error: Guest with ID " + guestId + " does not exist. Reservation failed.");
             return;
         }

         // Validate RoomID existence
         if (!entityExists("Rooms", "RoomID", roomId)) {
             System.err.println("Error: Room with ID " + roomId + " does not exist. Reservation failed.");
             return;
         }

         // Check for overlapping reservations for the *same room*
         // A room is unavailable if an existing reservation overlaps: (Existing Start < New End) AND (Existing End > New Start)
         String checkOverlapQuery = "SELECT 1 FROM Reservations WHERE RoomID = ? AND CheckInDate < ? AND CheckOutDate > ? LIMIT 1";
         try (Connection connection = getConnection();
              PreparedStatement checkStatement = connection.prepareStatement(checkOverlapQuery)) {

             checkStatement.setInt(1, roomId);
             checkStatement.setString(2, checkOutDate); // Existing reservation must start before the new one ends
             checkStatement.setString(3, checkInDate);  // Existing reservation must end after the new one starts

             try (ResultSet resultSet = checkStatement.executeQuery()) {
                 if (resultSet.next()) {
                     System.out.println("Room ID " + roomId + " is already booked during the specified dates (" + checkInDate + " to " + checkOutDate + "). Reservation failed.");
                     return; // Exit if there's an overlap
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error checking for reservation overlap: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             return; // Exit if error during check
         }

         // Debugging message (can be removed in production)
         // System.out.println("Attempting to add reservation for GuestID: " + guestId + ", RoomID: " + roomId);

         // Proceed to insert the new reservation
         String insertQuery = "INSERT INTO Reservations (GuestID, RoomID, CheckInDate, CheckOutDate, NumberOfGuests) VALUES (?, ?, ?, ?, ?)";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

             preparedStatement.setInt(1, guestId);
             preparedStatement.setInt(2, roomId);
             // Assuming DB column type is DATE or compatible VARCHAR format (like 'YYYY-MM-DD')
             preparedStatement.setString(3, checkInDate);
             preparedStatement.setString(4, checkOutDate);
             preparedStatement.setInt(5, numberOfGuests);

             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Reservation added successfully for Guest ID " + guestId + " in Room ID " + roomId + " from " + checkInDate + " to " + checkOutDate + ".");
             } else {
                 // Should not happen if previous checks passed and no other errors occurred
                 System.err.println("Reservation could not be added for an unknown reason.");
             }
         } catch (SQLException e) {
             System.err.println("Error adding reservation: " + e.getMessage());
             // Specific check for foreign key errors is less needed now due to entityExists checks, but can be kept as a fallback
             if (e.getMessage().toLowerCase().contains("foreign key constraint")) {
                 System.err.println("Hint: Double-check if Guest ID " + guestId + " and Room ID " + roomId + " still exist.");
             }
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }


     /**
      * Updates an existing reservation.
      * @param reservationId The ID of the reservation to update.
      * @param guestId The potentially updated Guest ID.
      * @param roomId The potentially updated Room ID.
      * @param checkInDate The potentially updated check-in date ('YYYY-MM-DD' format).
      * @param checkOutDate The potentially updated check-out date ('YYYY-MM-DD' format).
      * @param numberOfGuests The potentially updated number of guests.
      */
     public void updateReservation(int reservationId, int guestId, int roomId, String checkInDate, String checkOutDate, int numberOfGuests) {
         // Validate date strings and range
         if (checkInDate == null || checkOutDate == null || checkInDate.compareTo(checkOutDate) >= 0) {
             System.err.println("Invalid date range: Check-in date must be before check-out date and dates cannot be null. Update failed.");
             return;
         }
         if (numberOfGuests <= 0) {
             System.err.println("Number of guests must be positive. Update failed.");
             return;
         }

         // Validate GuestID existence
         if (!entityExists("Guests", "GuestID", guestId)) {
             System.err.println("Error: Guest with ID " + guestId + " does not exist. Update failed.");
             return;
         }

         // Validate RoomID existence
         if (!entityExists("Rooms", "RoomID", roomId)) {
             System.err.println("Error: Room with ID " + roomId + " does not exist. Update failed.");
             return;
         }

         // Check for overlapping reservations for the *same room*, EXCLUDING the reservation being updated
         String checkOverlapQuery = "SELECT 1 FROM Reservations WHERE RoomID = ? AND ReservationID != ? AND CheckInDate < ? AND CheckOutDate > ? LIMIT 1";
         try (Connection connection = getConnection();
              PreparedStatement checkStatement = connection.prepareStatement(checkOverlapQuery)) {

             checkStatement.setInt(1, roomId);
             checkStatement.setInt(2, reservationId); // Exclude the current reservation from the check
             checkStatement.setString(3, checkOutDate); // New End Date
             checkStatement.setString(4, checkInDate);  // New Start Date

             try (ResultSet resultSet = checkStatement.executeQuery()) {
                 if (resultSet.next()) {
                     System.out.println("Cannot update reservation: Room ID " + roomId + " is booked by another reservation during the specified dates (" + checkInDate + " to " + checkOutDate + "). Update failed.");
                     return; // Exit if there's an overlap
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error checking for reservation overlap during update: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             return; // Exit if error during check
         }

         // Proceed with the update
         String updateQuery = "UPDATE Reservations SET GuestID = ?, RoomID = ?, CheckInDate = ?, CheckOutDate = ?, NumberOfGuests = ? WHERE ReservationID = ?";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

             preparedStatement.setInt(1, guestId);
             preparedStatement.setInt(2, roomId);
             preparedStatement.setString(3, checkInDate);
             preparedStatement.setString(4, checkOutDate);
             preparedStatement.setInt(5, numberOfGuests);
             preparedStatement.setInt(6, reservationId);

             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Reservation ID " + reservationId + " updated successfully!");
             } else {
                 System.out.println("Reservation with ID " + reservationId + " not found or data was unchanged. No update performed.");
             }
         } catch (SQLException e) {
             System.err.println("Error updating reservation ID " + reservationId + ": " + e.getMessage());
             if (e.getMessage().toLowerCase().contains("foreign key constraint")) {
                 System.err.println("Hint: Double-check if the new Guest ID " + guestId + " or Room ID " + roomId + " exist.");
             }
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

     /**
      * Deletes a specific reservation by its ID.
      * @param reservationId The ID of the reservation to delete.
      */
     public void deleteReservation(int reservationId) {
         String query = "DELETE FROM Reservations WHERE ReservationID = ?";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query)) {

             preparedStatement.setInt(1, reservationId);
             int affectedRows = preparedStatement.executeUpdate();
             if (affectedRows > 0) {
                 System.out.println("Reservation ID " + reservationId + " deleted successfully!");
             } else {
                 System.out.println("Reservation with ID " + reservationId + " not found. No deletion performed.");
             }
         } catch (SQLException e) {
             System.err.println("Error deleting reservation ID " + reservationId + ": " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
     }

     /**
      * Displays all reservations, joining with Guest and Room tables for more details.
      */
     public void viewReservations() {
         String query = "SELECT r.ReservationID, r.CheckInDate, r.CheckOutDate, r.NumberOfGuests, " +
                        "g.GuestID, g.GuestName, rm.RoomID, rm.RoomNumber " +
                        "FROM Reservations r " +
                        "JOIN Guests g ON r.GuestID = g.GuestID " +
                        "JOIN Rooms rm ON r.RoomID = rm.RoomID " +
                        "ORDER BY r.CheckInDate, g.GuestName"; // Order by check-in date, then guest name

         System.out.println("\n--- Reservation Listing ---");
         boolean found = false;
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query);
              ResultSet resultSet = preparedStatement.executeQuery()) {

             while (resultSet.next()) {
                 found = true;
                 int reservationId = resultSet.getInt("ReservationID");
                 int guestId = resultSet.getInt("GuestID");
                 String guestName = resultSet.getString("GuestName");
                 int roomId = resultSet.getInt("RoomID");
                 String roomNumber = resultSet.getString("RoomNumber");
                 String checkInDate = resultSet.getString("CheckInDate"); // Or use getDate() if column type is DATE
                 String checkOutDate = resultSet.getString("CheckOutDate");
                 int numberOfGuests = resultSet.getInt("NumberOfGuests");
                 System.out.printf("Res ID: %-4d | Guest: %-20s (ID:%d) | Room: %-8s (ID:%d) | CheckIn: %-10s | CheckOut: %-10s | Guests: %d%n",
                                   reservationId, guestName, guestId, roomNumber, roomId, checkInDate, checkOutDate, numberOfGuests);
             }
             if (!found) {
                 System.out.println("No reservations found in the database.");
             }
         } catch (SQLException e) {
             System.err.println("Error viewing reservations: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
         System.out.println("--- End of Listing ---");
     }


     // --- Utility and Search Methods ---

     /**
      * Retrieves a list of all Room IDs from the database, ordered by ID.
      * @return A List of Integer containing all Room IDs. Returns an empty list if none found or error occurs.
      */
     public List<Integer> getAllRoomIds() {
         List<Integer> roomIds = new ArrayList<>();
         String query = "SELECT RoomID FROM Rooms ORDER BY RoomID";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query);
              ResultSet resultSet = preparedStatement.executeQuery()) {

             while (resultSet.next()) {
                 roomIds.add(resultSet.getInt("RoomID"));
             }
         } catch (SQLException e) {
             System.err.println("Error retrieving room IDs: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
         return roomIds;
     }

     /**
      * Retrieves a list of all Guest IDs from the database, ordered by ID.
      * @return A List of Integer containing all Guest IDs. Returns an empty list if none found or error occurs.
      */
     public List<Integer> getAllGuestIds() {
         List<Integer> guestIds = new ArrayList<>();
         String query = "SELECT GuestID FROM Guests ORDER BY GuestID";
         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query);
              ResultSet resultSet = preparedStatement.executeQuery()) {

             while (resultSet.next()) {
                 guestIds.add(resultSet.getInt("GuestID"));
             }
         } catch (SQLException e) {
             System.err.println("Error retrieving guest IDs: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
         return guestIds;
     }

     /**
      * Searches for available rooms based on type, max price, and date range.
      * Requires a 'Room' class in 'com.hotelres.model' with a constructor:
      * Room(int id, String number, String type, int capacity, double rate)
      *
      * @param roomType The desired type of room (case-sensitive).
      * @param maxPrice The maximum price per night (inclusive).
      * @param startDate The desired check-in date (java.util.Date).
      * @param endDate The desired check-out date (java.util.Date).
      * @return A List of available Room objects matching the criteria. Returns empty list on error or no match.
      */
     public List<Room> searchRooms(String roomType, double maxPrice, Date startDate, Date endDate) {
         List<Room> availableRooms = new ArrayList<>();

         // Validate inputs
         if (roomType == null || roomType.trim().isEmpty() || maxPrice < 0) {
             System.err.println("Invalid search criteria: Room type cannot be empty and max price cannot be negative.");
             return availableRooms;
         }
         if (startDate == null || endDate == null || !endDate.after(startDate)) {
             System.err.println("Invalid date range for room search. Check-out date must be after check-in date.");
             return availableRooms;
         }

         // SQL to find rooms matching type/price that DON'T have an overlapping reservation.
         String query = "SELECT * FROM Rooms " +
                        "WHERE RoomType = ? AND RatePerNight <= ? AND RoomID NOT IN (" +
                        "  SELECT RoomID FROM Reservations " +
                        "  WHERE CheckInDate < ? AND CheckOutDate > ?" + // Overlap condition
                        ") ORDER BY RatePerNight, RoomNumber"; // Order results

         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query)) {

             preparedStatement.setString(1, roomType);
             preparedStatement.setDouble(2, maxPrice);
             // Convert java.util.Date to java.sql.Date for PreparedStatement
             preparedStatement.setDate(3, new java.sql.Date(endDate.getTime()));   // New End Date
             preparedStatement.setDate(4, new java.sql.Date(startDate.getTime())); // New Start Date

             try (ResultSet resultSet = preparedStatement.executeQuery()) {
                 while (resultSet.next()) {
                     // *** IMPORTANT: Assumes Room class exists as described in comments ***
                     try {
                         Room room = new Room(
                             resultSet.getInt("RoomID"),
                             resultSet.getString("RoomNumber"),
                             resultSet.getString("RoomType"),
                             resultSet.getInt("Capacity"),
                             resultSet.getDouble("RatePerNight"),
                             resultSet.getBoolean("Reserved")
                         );
                         availableRooms.add(room);
                     } catch (Exception e) {
                         // Catch potential errors if the Room class/constructor is missing or incorrect
                         System.err.println("CRITICAL: Error creating Room object during search. Check Room class definition in com.hotelres.model: " + e.getMessage());
                         // Optionally re-throw or handle more severely as this indicates a programming error
                         // throw new RuntimeException("Failed to instantiate Room object", e);
                         break; // Stop processing further results if Room object creation fails
                     }
                 }
             }
         } catch (SQLException e) {
             System.err.println("Error searching for available rooms: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
         return availableRooms;
     }

     /**
      * Checks if a specific room is available for the given date range using java.util.Date.
      *
      * @param roomId The ID of the room to check.
      * @param startDate The desired check-in date (java.util.Date).
      * @param endDate The desired check-out date (java.util.Date).
      * @return true if the room is available, false otherwise (including invalid dates or errors).
      */
     public boolean isRoomAvailable(int roomId, Date startDate, Date endDate) {
         // Validate dates
         if (startDate == null || endDate == null || !endDate.after(startDate)) {
             System.err.println("Invalid date range provided for availability check.");
             return false; // Cannot be available with invalid dates
         }

         // SQL to count overlapping reservations for the specific room
         String query = "SELECT 1 FROM Reservations " + // Just need to know if at least one exists
                        "WHERE RoomID = ? AND CheckInDate < ? AND CheckOutDate > ? LIMIT 1"; // Overlap condition

         try (Connection connection = getConnection();
              PreparedStatement preparedStatement = connection.prepareStatement(query)) {

             preparedStatement.setInt(1, roomId);
             // Convert java.util.Date to java.sql.Date
             preparedStatement.setDate(2, new java.sql.Date(endDate.getTime()));   // New End Date
             preparedStatement.setDate(3, new java.sql.Date(startDate.getTime())); // New Start Date

             try (ResultSet resultSet = preparedStatement.executeQuery()) {
                 // If resultSet.next() is true, an overlapping reservation exists, so room is NOT available.
                 // If resultSet.next() is false, no overlap found, room IS available.
                 return !resultSet.next();
             }
         } catch (SQLException e) {
             System.err.println("Error checking room availability for Room ID " + roomId + ": " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
         }
         // Return false if an error occurred
         return false;
     }


     // --- Data Cleanup Method ---

     /**
      * Deletes ALL data from Reservations, Guests, and Rooms tables.
      * Resets AUTO_INCREMENT counters (MySQL specific).
      * USE WITH EXTREME CAUTION! Primarily for testing environments.
      */
     public void cleanUpTestData() {
         System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
         System.out.println("!!! WARNING: Attempting to DELETE ALL data from Reservations, !!!");
         System.out.println("!!!          Guests, and Rooms tables and reset counters.     !!!");
         System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

         // Optional: Add a confirmation step here for safety
         java.util.Scanner scanner = new java.util.Scanner(System.in);
         System.out.print(">>> Type 'YES DELETE ALL' to confirm data wipe: ");
         String confirmation = scanner.nextLine();
         if (!"YES DELETE ALL".equals(confirmation)) {
         System.out.println("Cleanup cancelled by user.");
         scanner.close(); // Close scanner to avoid resource leak
         scanner = null; // Set to null to avoid accidental reuse
            return;
        }
        System.out.println("Proceeding with data cleanup...");
        scanner.close(); // Close scanner to avoid resource leak
        scanner = null; // Set to null to avoid accidental reuse

         String deleteReservationsQuery = "DELETE FROM Reservations";
         String deleteGuestsQuery = "DELETE FROM Guests";
         String deleteRoomsQuery = "DELETE FROM Rooms";

         // Reset auto-increment counters (MySQL specific syntax)
         String resetReservationsAI = "ALTER TABLE Reservations AUTO_INCREMENT = 1";
         String resetGuestsAI = "ALTER TABLE Guests AUTO_INCREMENT = 1";
         String resetRoomsAI = "ALTER TABLE Rooms AUTO_INCREMENT = 1";

         Connection connection = null;
         Statement statement = null;
         boolean originalAutoCommit = true;
         boolean fkChecksDisabled = false;

         try {
             connection = getConnection();
             // Manage transaction manually for safety
             originalAutoCommit = connection.getAutoCommit();
             connection.setAutoCommit(false);

             statement = connection.createStatement();

             // Turn off foreign key checks temporarily (MySQL specific, use with caution)
             System.out.println("Disabling foreign key checks...");
             statement.execute("SET FOREIGN_KEY_CHECKS=0");
             fkChecksDisabled = true;

             // Execute delete queries
             System.out.println("Deleting reservations...");
             int resDeleted = statement.executeUpdate(deleteReservationsQuery);
             System.out.println("Deleting guests...");
             int guestsDeleted = statement.executeUpdate(deleteGuestsQuery);
             System.out.println("Deleting rooms...");
             int roomsDeleted = statement.executeUpdate(deleteRoomsQuery);

             System.out.println("Deleted " + resDeleted + " reservations.");
             System.out.println("Deleted " + guestsDeleted + " guests.");
             System.out.println("Deleted " + roomsDeleted + " rooms.");

             // Reset auto-increment counters
             System.out.println("Resetting auto-increment counters...");
             statement.executeUpdate(resetReservationsAI);
             statement.executeUpdate(resetGuestsAI);
             statement.executeUpdate(resetRoomsAI);
             System.out.println("Auto-increment counters reset.");

             // Commit the changes
             connection.commit();
             System.out.println("Data cleanup committed successfully!");

         } catch (SQLException e) {
             System.err.println("ERROR during data cleanup: " + e.getMessage());
             // e.printStackTrace(); // Uncomment for detailed debugging
             System.err.println("Attempting to rollback changes...");
             if (connection != null) {
                 try {
                     connection.rollback();
                     System.err.println("Rollback successful.");
                 } catch (SQLException rollbackEx) {
                     System.err.println("CRITICAL ERROR during rollback: " + rollbackEx.getMessage());
                 }
             }
         } finally {
             // Ensure foreign key checks are re-enabled and connection settings restored
             if (statement != null && fkChecksDisabled) {
                 try {
                     System.out.println("Re-enabling foreign key checks...");
                     statement.execute("SET FOREIGN_KEY_CHECKS=1");
                     System.out.println("Foreign key checks re-enabled.");
                 } catch (SQLException fkEx) {
                     System.err.println("ERROR re-enabling foreign key checks: " + fkEx.getMessage());
                 }
             }
             if (connection != null) {
                 try {
                     // Restore original auto-commit state
                     connection.setAutoCommit(originalAutoCommit);
                     // Close the connection
                     connection.close();
                 } catch (SQLException closeEx) {
                     System.err.println("Error restoring auto-commit or closing connection: " + closeEx.getMessage());
                 }
             }
             // Close statement implicitly handled by try-with-resources if used, but we used manual statement here.
             // If statement wasn't null and wasn't closed by connection closing, close it explicitly.
              if (statement != null) {
                  try {
                      statement.close();
                  } catch (SQLException stmtCloseEx) {
                     // Ignore or log
                  }
              }
         }
     }

 } // End of DatabaseManager class