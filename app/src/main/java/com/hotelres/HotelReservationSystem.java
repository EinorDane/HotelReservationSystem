package com.hotelres;

import com.hotelres.database.GuestDAO;
import com.hotelres.database.ReservationDAO;
import com.hotelres.database.UserDAO;
import com.hotelres.model.Guest;
import com.hotelres.model.Reservation;
import com.hotelres.model.User;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class HotelReservationSystem {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        UserDAO userDAO = new UserDAO();
        GuestDAO guestDAO = new GuestDAO();
        ReservationDAO reservationDAO = new ReservationDAO();

        System.out.println("Welcome to the Hotel Reservation System!");
        System.out.println("Do you have an account? (yes/no)");
        String hasAccount = scanner.nextLine();

        User loggedInUser = null;

        if (hasAccount.equalsIgnoreCase("no")) {
            System.out.println("Creating a new account...");
            
            System.out.println("Enter a new username:");
            String newUsername = scanner.nextLine();

            System.out.println("Enter a new password:");
            String newPassword = scanner.nextLine();

            System.out.println("Are you a guest or staff? (guest/staff)");
            String role = scanner.nextLine().toLowerCase();

            User newUser = new User(0, newUsername, newPassword, role);
            int userId = userDAO.registerUser(newUser);

            if (userId > 0) {
                System.out.println("Account created successfully! Your UserID: " + userId);
                loggedInUser = userDAO.getUserByUsername(newUsername);
            } else {
                System.out.println("Error creating account. Exiting.");
                return;
            }
        } else {
            System.out.println("Enter username:");
            String username = scanner.nextLine();

            System.out.println("Enter password:");
            String password = scanner.nextLine();

            loggedInUser = userDAO.getUserByUsername(username);

            if (loggedInUser == null || !loggedInUser.getPassword().equals(password)) {
                System.out.println("Invalid login. Exiting system.");
                return; // Exit if login fails
            }

            System.out.println("Login successful! You are logged in as " + loggedInUser.getRole());
        }

        // Step 2: Role-Based Functionality
        if (loggedInUser.getRole().equalsIgnoreCase("guest")) {
            handleGuestOperations(scanner, loggedInUser, guestDAO, reservationDAO);
        } else if (loggedInUser.getRole().equalsIgnoreCase("staff")) {
            handleStaffOperations(scanner, guestDAO, reservationDAO);
        } else {
            System.out.println("Unknown role detected. Exiting.");
        }
    }

    private static void handleGuestOperations(Scanner scanner, User loggedInUser, GuestDAO guestDAO, ReservationDAO reservationDAO) throws SQLException {
        System.out.println("Enter your full name:");
        String guestName = scanner.nextLine();

        System.out.println("Enter your address:");
        String guestAddress = scanner.nextLine();

        System.out.println("Enter your phone number:");
        String guestPhone = scanner.nextLine();

        System.out.println("Enter your email:");
        String guestEmail = scanner.nextLine();

        Guest guest = new Guest(0, guestName, guestAddress, guestPhone, guestEmail, loggedInUser.getUserId());
        int guestId = guestDAO.addGuest(guest);
        guest.setGuestId(guestId);

        System.out.println("Profile created successfully. Proceeding to reservation...");

        List<Integer> availableRoomIds = reservationDAO.getAvailableRoomIds();
        if (availableRoomIds.isEmpty()) {
            System.out.println("No rooms available.");
            return;
        }

        System.out.println("Available rooms:");
        for (int i = 0; i < availableRoomIds.size(); i++) {
            System.out.println((i + 1) + ". Room ID: " + availableRoomIds.get(i));
        }

        System.out.println("Select a room by entering the corresponding number:");
        int selectedRoomId = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Enter check-in date (YYYY-MM-DD):");
        String checkInDate = scanner.nextLine();

        System.out.println("Enter check-out date (YYYY-MM-DD):");
        String checkOutDate = scanner.nextLine();

        Reservation reservation = new Reservation(0, guest.getGuestId(), selectedRoomId, Date.valueOf(checkInDate), Date.valueOf(checkOutDate), 1);
        reservationDAO.addReservation(reservation);
        System.out.println("Reservation added successfully.");
    }

    private static void handleStaffOperations(Scanner scanner, GuestDAO guestDAO, ReservationDAO reservationDAO) throws SQLException {
        System.out.println("Staff Menu:");
        System.out.println("1. View all guests");
        System.out.println("2. View all reservations");
        System.out.println("3. Delete a reservation");

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                List<Guest> guests = guestDAO.getAllGuests();
                for (Guest g : guests) {
                    System.out.println("Guest ID: " + g.getGuestId() + ", Name: " + g.getGuestName());
                }
                break;
            case 2:
                List<Reservation> reservations = reservationDAO.getAllReservations();
                for (Reservation r : reservations) {
                    System.out.println("Reservation ID: " + r.getReservationId() + ", Guest ID: " + r.getGuestId() + ", Room ID: " + r.getRoomId());
                }
                break;
            case 3:
                System.out.println("Enter Reservation ID to delete:");
                int reservationId = scanner.nextInt();
                reservationDAO.deleteReservation(reservationId);
                System.out.println("Reservation deleted successfully.");
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }
}