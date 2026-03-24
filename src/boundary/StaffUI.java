package boundary;

import control.UserControl;
import control.BookingControl;
import control.FacilityControl;
import java.util.Scanner;

public class StaffUI {
    @SuppressWarnings("unused")
    private final UserControl userControl;
    private final BookingControl bookingControl;
    @SuppressWarnings("unused")
    private final FacilityControl facilityControl;
    private final Scanner scanner;

    public StaffUI(UserControl userControl, BookingControl bookingControl, FacilityControl facilityControl) {
        this.userControl = userControl;
        this.bookingControl = bookingControl;
        this.facilityControl = facilityControl;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int choice;
        do {
            printStaffMenu();
            choice = readMenuChoice(0, 4);

            switch (choice) {
                case 1:
                    manageUsers();
                    break;
                case 2:
                    viewBookingDetails();
                    break;
                case 3:
                    manageFacilities();
                    break;
                case 0:
                    System.out.println("\nLogout successful. Thank you!");
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (true);
    }

    private void manageUsers() {
        int choice;
        do {
            System.out.println("\n========== MANAGE USERS ==========");
            System.out.println("1. View all users");
            System.out.println("2. Search user by email");
            System.out.println("3. Delete user");
            System.out.println("0. Back to menu");
            System.out.print("Enter your choice: ");

            choice = readMenuChoice(0, 3);

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    searchUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (true);
    }

    private void viewAllUsers() {
        System.out.println("\n========== VIEW ALL USERS ==========");
        java.util.List<entity.User> users = userControl.getAllUsers();
        
        // Filter out staff account
        java.util.List<entity.User> studentUsers = new java.util.ArrayList<>();
        for (entity.User user : users) {
            if (!user.getEmail().equals("staff@gmail.com")) {
                studentUsers.add(user);
            }
        }
        
        if (studentUsers.isEmpty()) {
            System.out.println("No users found in the system.");
            return;
        }
        
        System.out.println(String.format("\nTotal Users: %d\n", studentUsers.size()));
        System.out.println(String.format("%-25s %-25s %-12s", "Name", "Email", "User Type"));
        System.out.println("-".repeat(62));
        
        for (entity.User user : studentUsers) {
            System.out.println(String.format("%-25s %-25s %-12s", 
                user.getName(), user.getEmail(), user.getUserType()));
        }
    }

    private void searchUser() {
        System.out.println("\n========== SEARCH USER ==========");
        System.out.print("Enter email to search: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println("\n[ERROR] Email cannot be empty.");
            return;
        }
        
        entity.User user = userControl.findUserByEmail(email);
        
        if (user == null) {
            System.out.println("\n[ERROR] User not found with email: " + email);
            return;
        }
        
        System.out.println("\n========== USER DETAILS ==========");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("User Type: " + user.getUserType());
    }

    private void deleteUser() {
        System.out.println("\n========== DELETE USER ==========");
        System.out.print("Enter email to delete: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println("\n[ERROR] Email cannot be empty.");
            return;
        }
        
        // Prevent deleting staff account
        if (email.equals("staff@gmail.com")) {
            System.out.println("\n[ERROR] Cannot delete default staff account.");
            return;
        }
        
        entity.User userToDelete = userControl.findUserByEmail(email);
        if (userToDelete == null) {
            System.out.println("\n[ERROR] User not found with email: " + email);
            return;
        }
        
        System.out.println("\nUser to delete:");
        System.out.println("Name: " + userToDelete.getName());
        System.out.println("Email: " + userToDelete.getEmail());
        System.out.println("User Type: " + userToDelete.getUserType());
        
        System.out.print("\nAre you sure you want to delete this user? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes")) {
            System.out.println("\nDeletion cancelled.");
            return;
        }
        
        boolean deleted = userControl.deleteUser(email);
        if (deleted) {
            System.out.println("\n[SUCCESS] User deleted successfully.");
        } else {
            System.out.println("\n[ERROR] Failed to delete user.");
        }
    }

    private void viewBookingDetails() {
        int choice;
        do {
            System.out.println("\n========== VIEW BOOKING DETAILS ==========");
            System.out.println("1. View all bookings");
            System.out.println("2. Search booking by ID");
            System.out.println("3. Filter bookings by date");
            System.out.println("0. Back to menu");
            System.out.print("Enter your choice: ");

            choice = readMenuChoice(0, 3);

            switch (choice) {
                case 1:
                    viewAllBookings();
                    break;
                case 2:
                    searchBookingById();
                    break;
                case 3:
                    filterBookingsByDate();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (true);
    }

    private void viewAllBookings() {
        System.out.println("\n========== VIEW ALL BOOKINGS ==========");
        java.util.List<entity.Booking> allBookings = bookingControl.getAllBookings();
        
        if (allBookings.isEmpty()) {
            System.out.println("No bookings found in the system.");
            return;
        }
        
        System.out.println(String.format("\nTotal Bookings: %d\n", allBookings.size()));
        System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s", "Booking ID", "Room ID", "Date", "Time Slot", "Status"));
        System.out.println("-".repeat(70));
        
        for (entity.Booking booking : allBookings) {
            String status = booking.getStatus();
            String lines = String.format("%-10s %-10s %-15s %-15s %-12s", 
                booking.getBookingID(), booking.getRoomID(), booking.getDate(), 
                booking.getTimeSlot(), status);
            System.out.println(lines);
            
            if ("CANCELLED".equals(status) && booking.getCancelReason() != null) {
                System.out.println("             Cancellation Reason: " + booking.getCancelReason());
            }
        }
    }

    private void searchBookingById() {
        System.out.println("\n========== SEARCH BOOKING ==========");
        System.out.print("Enter booking ID (e.g. B1, B2): ");
        String bookingID = scanner.nextLine().trim();
        
        if (bookingID.isEmpty()) {
            System.out.println("\n[ERROR] Booking ID cannot be empty.");
            return;
        }
        
        entity.Booking booking = bookingControl.findBookingById(bookingID);
        
        if (booking == null) {
            System.out.println("\n[ERROR] Booking not found with ID: " + bookingID);
            return;
        }
        
        System.out.println("\n========== BOOKING DETAILS ==========");
        System.out.println("Booking ID: " + booking.getBookingID());
        System.out.println("Room ID: " + booking.getRoomID());
        System.out.println("Date: " + booking.getDate());
        System.out.println("Time Slot: " + booking.getTimeSlot());
        System.out.println("Status: " + booking.getStatus());
        if ("CANCELLED".equals(booking.getStatus()) && booking.getCancelReason() != null) {
            System.out.println("Cancellation Reason: " + booking.getCancelReason());
        }
    }

    private void filterBookingsByDate() {
        System.out.println("\n========== FILTER BOOKINGS BY DATE ==========");
        System.out.print("Enter date (YYYY-MM-DD, e.g. 2026-03-25): ");
        String dateInput = scanner.nextLine().trim();
        
        if (dateInput.isEmpty()) {
            System.out.println("\n[ERROR] Date cannot be empty.");
            return;
        }
        
        // Validate date format
        if (!dateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("\n[ERROR] Invalid date format. Please use YYYY-MM-DD.");
            return;
        }
        
        java.util.List<entity.Booking> bookingsForDate = bookingControl.getBookingsByDate(dateInput);
        
        if (bookingsForDate.isEmpty()) {
            System.out.println("\nNo bookings found for date: " + dateInput);
            return;
        }
        
        System.out.println(String.format("\nBookings for %s: %d bookings\n", dateInput, bookingsForDate.size()));
        System.out.println(String.format("%-10s %-10s %-15s %-12s", "Booking ID", "Room ID", "Time Slot", "Status"));
        System.out.println("-".repeat(60));
        
        for (entity.Booking booking : bookingsForDate) {
            String status = booking.getStatus();
            System.out.println(String.format("%-10s %-10s %-15s %-12s", 
                booking.getBookingID(), booking.getRoomID(), booking.getTimeSlot(), status));
            
            if ("CANCELLED".equals(status) && booking.getCancelReason() != null) {
                System.out.println("             Cancellation Reason: " + booking.getCancelReason());
            }
        }
    }

    private void manageFacilities() {
        System.out.println("\n========== MANAGE FACILITIES ==========");
        System.out.println("1. View all facilities");
        System.out.println("2. Add new facility");
        System.out.println("3. Update facility");
        System.out.println("4. Delete facility");
        System.out.println("0. Back to menu");
        System.out.print("Enter your choice: ");

        int choice = readMenuChoice(0, 4);

        switch (choice) {
            case 1:
                System.out.println("\n[View All Facilities]");
                System.out.println("Feature coming soon.");
                break;
            case 2:
                System.out.println("\n[Add New Facility]");
                System.out.println("Feature coming soon.");
                break;
            case 3:
                System.out.println("\n[Update Facility]");
                System.out.println("Feature coming soon.");
                break;
            case 4:
                System.out.println("\n[Delete Facility]");
                System.out.println("Feature coming soon.");
                break;
            case 0:
                break;
        }
    }

    private void printStaffMenu() {
        System.out.println("\n╔═══════════════════════╗");
        System.out.println("║      STAFF PAGE       ║");
        System.out.println("╚═══════════════════════╝");
        System.out.println("1. Manage Users");
        System.out.println("2. View Booking Details");
        System.out.println("3. Manage Facilities");
        System.out.println("0. Logout & Exit");
        System.out.print("Enter your choice: ");
    }

    private int readMenuChoice(int min, int max) {
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < min || choice > max) {
                System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return -1;
        }
    }
}
