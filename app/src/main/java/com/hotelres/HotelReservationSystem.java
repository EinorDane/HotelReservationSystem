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
import java.util.logging.Logger;

public class HotelReservationSystem {
    private static final Logger LOGGER = Logger.getLogger(HotelReservationSystem.class.getName());

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            UserDAO userDAO = new UserDAO();
            GuestDAO guestDAO = new GuestDAO();
            ReservationDAO reservationDAO = new ReservationDAO();

            System.out.println("Welcome to the Hotel Reservation System!");
            System.out.print("Do you have an account? (yes/no): ");
            String hasAccount = scanner.nextLine().trim().toLowerCase();

            if (!hasAccount.equals("yes") && !hasAccount.equals("no")) {
                System.out.println("❌ Invalid response. Please answer with 'yes' or 'no'.");
                return;
            }

            User loggedInUser = null;

            if (hasAccount.equals("no")) {
                loggedInUser = handleUserRegistration(scanner, userDAO);
            } else {
                loggedInUser = handleUserLogin(scanner, userDAO);
            }

            if (loggedInUser == null) {
                System.out.println("❌ Authentication failed. Exiting.");
                return;
            }

            System.out.println("✅ Login successful! You are logged in as " + loggedInUser.getRole());

            if ("guest".equalsIgnoreCase(loggedInUser.getRole())) {
                handleGuestOperations(scanner, loggedInUser, guestDAO, reservationDAO);
            } else if ("staff".equalsIgnoreCase(loggedInUser.getRole())) {
                handleStaffOperations(scanner, guestDAO, reservationDAO);
            } else {
                System.out.println("❌ Unknown role detected. Exiting.");
            }
        } catch (Exception e) {
            LOGGER.severe("Unexpected error: " + e.getMessage());
        }
    }

    private static User handleUserRegistration(Scanner scanner, UserDAO userDAO) {
        System.out.println("Creating a new account...");
        
        System.out.print("Enter a new username: ");
        String newUsername = scanner.nextLine().trim();

        if (userDAO.doesUsernameExist(newUsername)) {
            System.out.println("⚠️ Error: Username already exists. Please choose a different one.");
            return null;
        }

        System.out.print("Enter a new password: ");
        String newPassword = scanner.nextLine().trim();

        System.out.print("Are you a guest or staff? (guest/staff): ");
        String role = scanner.nextLine().trim().toLowerCase();

        User newUser = new User(0, newUsername, newPassword, role);
        int userId = userDAO.registerUser(newUser);

        if (userId > 0) {
            System.out.println("✅ Account created successfully! Your UserID: " + userId);
            return userDAO.getUserByUsername(newUsername, newPassword);
        } else {
            System.out.println("❌ Error creating account.");
            return null;
        }
    }

    private static User handleUserLogin(Scanner scanner, UserDAO userDAO) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        return userDAO.getUserByUsername(username, password);
    }

    private static void handleGuestOperations(Scanner scanner, User loggedInUser, GuestDAO guestDAO, ReservationDAO reservationDAO) {
        try {
            System.out.print("Enter your full name: ");
            String guestName = scanner.nextLine().trim();

            System.out.print("Enter your address: ");
            String guestAddress = scanner.nextLine().trim();

            System.out.print("Enter your phone number: ");
            String guestPhone = scanner.nextLine().trim();

            System.out.print("Enter your email: ");
            String guestEmail = scanner.nextLine().trim();

            Guest guest = new Guest(0, guestName, guestAddress, guestPhone, guestEmail, loggedInUser.getUserId());
            int guestId = guestDAO.addGuest(guest);
            guest.setGuestId(guestId);

            System.out.println("✅ Profile created successfully. Proceeding to reservation...");

            List<Integer> availableRoomIds = reservationDAO.getAvailableRoomIds();
            if (availableRoomIds.isEmpty()) {
                System.out.println("⚠️ No rooms available.");
                return;
            }

            System.out.println("Available rooms:");
            for (int i = 0; i < availableRoomIds.size(); i++) {
                System.out.println((i + 1) + ". Room ID: " + availableRoomIds.get(i));
            }

            System.out.print("Select a room by entering the corresponding number: ");
            int selectedRoomId = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            System.out.print("Enter check-in date (YYYY-MM-DD): ");
            String checkInDate = scanner.nextLine().trim();

            System.out.print("Enter check-out date (YYYY-MM-DD): ");
            String checkOutDate = scanner.nextLine().trim();

            Reservation reservation = new Reservation(0, guest.getGuestId(), selectedRoomId, Date.valueOf(checkInDate), Date.valueOf(checkOutDate), 1);
            reservationDAO.addReservation(reservation);
            System.out.println("✅ Reservation added successfully.");
        } catch (Exception e) {
            LOGGER.severe("Error processing guest operations: " + e.getMessage());
        }
    }

    private static void handleStaffOperations(Scanner scanner, GuestDAO guestDAO, ReservationDAO reservationDAO) {
        try {
            System.out.println("Staff Menu:");
            System.out.println("1. View all guests");
            System.out.println("2. View all reservations");
            System.out.println("3. Delete a reservation");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline

            switch (choice) {
                case 1:
                    List<Guest> guests = guestDAO.getAllGuests();
                    guests.forEach(g -> System.out.println("Guest ID: " + g.getGuestId() + ", Name: " + g.getGuestName()));
                    break;
                case 2:
                    List<Reservation> reservations = reservationDAO.getAllReservations();
                    reservations.forEach(r -> System.out.println("Reservation ID: " + r.getReservationId() + ", Guest ID: " + r.getGuestId() + ", Room ID: " + r.getRoomId()));
                    break;
                case 3:
                    System.out.print("Enter Reservation ID to delete: ");
                    int reservationId = scanner.nextInt();
                    reservationDAO.deleteReservation(reservationId);
                    System.out.println("✅ Reservation deleted successfully.");
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing staff operations: " + e.getMessage());
        }
    }
}