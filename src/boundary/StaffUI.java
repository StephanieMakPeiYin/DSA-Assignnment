package boundary;

import control.BookingControl;
import control.FacilityControl;
import control.UserControl;
import entity.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.ConsoleColors;

public class StaffUI {
    @SuppressWarnings("unused")
    private final UserControl userControl;
    private final BookingControl bookingControl;
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
                    System.out.println(ConsoleColors.success("\nLogout successful. Thank you!"));
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
            System.out.println("1. Add new user");
            System.out.println("2. Update user information");
            System.out.println("3. Delete user");
            System.out.println("4. Search user");
            System.out.println("5. View all users");
            System.out.println("0. Back to menu");
            System.out.print("Enter your choice: ");

            choice = readMenuChoice(0, 5);

            switch (choice) {
                case 1:
                    addNewUser();
                    break;
                case 2:
                    updateUserInformation();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    searchUser();
                    break;
                case 5:
                    viewAllUsers();
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
        
        // Filter out staff account and deleted users
        java.util.List<entity.User> activeUsers = new java.util.ArrayList<>();
        for (entity.User user : users) {
            if (!user.getEmail().equals("staff@gmail.com") && "active".equals(user.getStatus())) {
                activeUsers.add(user);
            }
        }
        
        if (activeUsers.isEmpty()) {
            System.out.println("No active users found in the system.");
            return;
        }
        
        System.out.println(String.format("\nTotal Active Users: %d\n", activeUsers.size()));
        System.out.println(String.format("%-10s %-20s %-25s %-12s", "User ID", "Name", "Email", "User Type"));
        System.out.println("-".repeat(70));
        
        for (entity.User user : activeUsers) {
            System.out.println(String.format("%-10s %-20s %-25s %-12s", 
                user.getUserID(), user.getName(), user.getEmail(), user.getUserType()));
        }
    }

    private void searchUser() {
        System.out.println("\n========== SEARCH USER ==========");
        System.out.println("Search by:");
        System.out.println("1. User ID");
        System.out.println("2. Name");
        System.out.println("3. Email");
        System.out.print("Enter your choice (1-3): ");
        
        int choice = readMenuChoice(1, 3);
        
        switch (choice) {
            case 1:
                searchByUserID();
                break;
            case 2:
                searchByName();
                break;
            case 3:
                searchByEmail();
                break;
            default:
                System.out.println("\nInvalid choice.");
        }
    }

    private void searchByUserID() {
        System.out.print("Enter User ID to search (e.g. STU001, STA001): ");
        String userID = scanner.nextLine().trim();
        
        if (userID.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] User ID cannot be empty."));
            return;
        }
        
        entity.User user = userControl.findUserByID(userID);
        
        if (user == null || "removed".equals(user.getStatus())) {
            System.out.println(ConsoleColors.error("\n[ERROR] User not found with ID: " + userID));
            return;
        }
        
        displayUserDetails(user);
    }

    private void searchByName() {
        System.out.print("Enter name to search (partial match supported): ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] Name cannot be empty."));
            return;
        }
        
        java.util.List<entity.User> results = userControl.findUsersByName(name);
        
        // Filter out deleted users
        java.util.List<entity.User> activeResults = new java.util.ArrayList<>();
        for (entity.User user : results) {
            if ("active".equals(user.getStatus())) {
                activeResults.add(user);
            }
        }
        
        if (activeResults.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] No active users found matching name: " + name));
            return;
        }
        
        System.out.println(ConsoleColors.success("\n[SUCCESS] Found " + activeResults.size() + " user(s) matching name: " + name));
        System.out.println(String.format("%-10s %-20s %-25s %-12s", "User ID", "Name", "Email", "User Type"));
        System.out.println("-".repeat(70));
        
        for (entity.User user : activeResults) {
            System.out.println(String.format("%-10s %-20s %-25s %-12s", 
                user.getUserID(), user.getName(), user.getEmail(), user.getUserType()));
        }
    }

    private void searchByEmail() {
        System.out.print("Enter email to search: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] Email cannot be empty."));
            return;
        }
        
        entity.User user = userControl.findUserByEmail(email);
        
        if (user == null || "removed".equals(user.getStatus())) {
            System.out.println(ConsoleColors.error("\n[ERROR] User not found with email: " + email));
            return;
        }
        
        displayUserDetails(user);
    }

    private void displayUserDetails(entity.User user) {
        System.out.println("\n========== USER DETAILS ==========");
        System.out.println("User ID: " + user.getUserID());
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("User Type: " + user.getUserType());
        System.out.println("Status: " + user.getStatus());
    }

    private void deleteUser() {
        System.out.println("\n========== DELETE USER ==========");
        System.out.print("Enter email to delete: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] Email cannot be empty."));
            return;
        }
        
        // Prevent deleting staff account
        if (email.equals("staff@gmail.com")) {
            System.out.println(ConsoleColors.error("\n[ERROR] Cannot delete default staff account."));
            return;
        }
        
        entity.User userToDelete = userControl.findUserByEmail(email);
        if (userToDelete == null) {
            System.out.println(ConsoleColors.error("\n[ERROR] User not found with email: " + email));
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
            System.out.println(ConsoleColors.success("\n[SUCCESS] User deleted successfully."));
        } else {
            System.out.println(ConsoleColors.error("\n[ERROR] Failed to delete user."));
        }
    }

    private void addNewUser() {
        System.out.println("\n========== ADD NEW USER ==========");
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        
        if (name.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] Name cannot be empty."));
            return;
        }
        
        System.out.print("Enter email (format: username@gmail.com): ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] Email cannot be empty."));
            return;
        }
        
        if (!email.endsWith("@gmail.com")) {
            System.out.println(ConsoleColors.error("\n[ERROR] Email must be in format: username@gmail.com"));
            return;
        }
        
        if (userControl.findUserByEmail(email) != null) {
            System.out.println(ConsoleColors.error("\n[ERROR] Email already exists in the system."));
            return;
        }
        
        System.out.print("Enter password (minimum 4 characters): ");
        String password = scanner.nextLine();
        
        if (password.length() < 4) {
            System.out.println(ConsoleColors.error("\n[ERROR] Password must be at least 4 characters."));
            return;
        }
        
        System.out.print("Enter user type (STAFF/STUDENT): ");
        String userType = scanner.nextLine().trim().toUpperCase();
        
        if (!userType.equals("STAFF") && !userType.equals("STUDENT")) {
            System.out.println(ConsoleColors.error("\n[ERROR] User type must be STAFF or STUDENT."));
            return;
        }
        
        entity.User newUser = new entity.User(null, name, email, password, userType);
        userControl.addUser(newUser);
        System.out.println(ConsoleColors.success("\n[SUCCESS] New user added successfully!"));
        System.out.println("User ID: " + newUser.getUserID());
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("User Type: " + userType);
    }

    private void updateUserInformation() {
        System.out.println("\n========== UPDATE USER INFORMATION ==========");
        System.out.print("Enter email of user to update: ");
        String email = scanner.nextLine().trim();
        
        if (email.isEmpty()) {
            System.out.println(ConsoleColors.error("\n[ERROR] Email cannot be empty."));
            return;
        }
        
        entity.User existingUser = userControl.findUserByEmail(email);
        if (existingUser == null || "removed".equals(existingUser.getStatus())) {
            System.out.println(ConsoleColors.error("\n[ERROR] User not found with email: " + email));
            return;
        }
        
        if (existingUser.getEmail().equals("staff@gmail.com")) {
            System.out.println(ConsoleColors.error("\n[ERROR] Cannot modify the default staff account."));
            return;
        }
        
        System.out.println("\n========== CURRENT USER DETAILS ==========");
        System.out.println("User ID: " + existingUser.getUserID());
        System.out.println("Name: " + existingUser.getName());
        System.out.println("Email: " + existingUser.getEmail());
        System.out.println("User Type: " + existingUser.getUserType());
        System.out.println("Status: " + existingUser.getStatus());
        
        System.out.println("\n--- Enter new information (press Enter to keep current) ---");
        
        System.out.print("New name [" + existingUser.getName() + "]: ");
        String newName = scanner.nextLine().trim();
        if (newName.isEmpty()) {
            newName = existingUser.getName();
        }
        
        System.out.print("New password (minimum 4 characters) [keep current]: ");
        String newPassword = scanner.nextLine();
        if (newPassword.isEmpty()) {
            newPassword = existingUser.getPassword();
        } else if (newPassword.length() < 4) {
            System.out.println(ConsoleColors.error("\n[ERROR] Password must be at least 4 characters."));
            return;
        }
        
        System.out.print("New user type (STAFF/STUDENT) [" + existingUser.getUserType() + "]: ");
        String newUserType = scanner.nextLine().trim().toUpperCase();
        if (newUserType.isEmpty()) {
            newUserType = existingUser.getUserType();
        } else if (!newUserType.equals("STAFF") && !newUserType.equals("STUDENT")) {
            System.out.println(ConsoleColors.error("\n[ERROR] User type must be STAFF or STUDENT."));
            return;
        }
        
        entity.User updatedUser = new entity.User(existingUser.getUserID(), newName, existingUser.getEmail(), newPassword, newUserType, existingUser.getStatus());
        boolean success = userControl.updateUser(updatedUser);
        
        if (success) {
            System.out.println(ConsoleColors.success("\n[SUCCESS] User information updated successfully!"));
            System.out.println("Name: " + newName);
            System.out.println("User Type: " + newUserType);
        } else {
            System.out.println(ConsoleColors.error("\n[ERROR] Failed to update user information."));
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
            System.out.println(ConsoleColors.error("\n[ERROR] Booking ID cannot be empty."));
            return;
        }
        
        entity.Booking booking = bookingControl.findBookingById(bookingID);
        
        if (booking == null) {
            System.out.println(ConsoleColors.error("\n[ERROR] Booking not found with ID: " + bookingID));
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
        int choice;
        do {
            System.out.println("\n========== MANAGE FACILITIES ==========");
            System.out.println("1. View all facilities");
            System.out.println("2. Add new facility");
            System.out.println("3. Update facility");
            System.out.println("4. Delete facility");
            System.out.println("0. Back to menu");
            System.out.print("Enter your choice: ");

            choice = readMenuChoice(0, 4);

            switch (choice) {
                case 1:
                    viewAllFacilities();
                    break;
                case 2:
                    addNewFacility();
                    break;
                case 3:
                    updateFacility();
                    break;
                case 4:
                    deleteFacility();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (true);
    }

    private void viewAllFacilities() {
        System.out.println("\n========== VIEW ALL FACILITIES ==========");
        facilityControl.displayFacilityList();
    }

    private void addNewFacility() {
        System.out.println("\n========== ADD NEW FACILITY ==========");
        
        while (true) {
            try {
                System.out.print("Enter capacity (1-30) or 0 to exit: ");
                int capacity = Integer.parseInt(scanner.nextLine().trim());
                if (capacity == 0) {
                    return;
                }
                if (capacity < 1 || capacity > 30) {
                    System.out.println(ConsoleColors.error("Capacity must be between 1 and 30."));
                    continue;
                }

                System.out.print("Enter block (e.g., A, B): ");
                String block = scanner.nextLine().trim();

                System.out.print("Enter floor (1-5) or 0 to exit: ");
                int floor = Integer.parseInt(scanner.nextLine().trim());
                if (floor == 0) {
                    return;
                }
                if (floor < 1 || floor > 5) {
                    System.out.println(ConsoleColors.error("Floor must be between 1 and 5."));
                    continue;
                }

                System.out.print("Enter room number (1-99) or 0 to exit: ");
                int roomNumber = Integer.parseInt(scanner.nextLine().trim());
                if (roomNumber == 0) {
                    return;
                }
                if (roomNumber < 1 || roomNumber > 99) {
                    System.out.println(ConsoleColors.error("Room number must be between 1 and 99."));
                    continue;
                }
                
                String equipment = selectEquipment();

                int result = facilityControl.addFacility(capacity, block, floor, roomNumber, equipment);
                if (result == FacilityControl.ADD_OK) {
                    System.out.println(ConsoleColors.success("Facility added successfully!"));
                    return;
                } else if (result == FacilityControl.ADD_DUPLICATE_ID) {
                    System.out.println(ConsoleColors.error("Facility with this ID already exists!"));
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println(ConsoleColors.error("Invalid input. Please enter valid numbers."));
            }
        }
    }

    private void updateFacility() {
        System.out.println("\n========== UPDATE FACILITY ==========");
        System.out.print("Enter facility ID to update: ");
        String facilityId = scanner.nextLine().trim();
        
        Room currentFacility = facilityControl.searchFacility(facilityId);
        if (currentFacility == null) {
            System.out.println(ConsoleColors.error("Facility not found!"));
            return;
        }

        try {
            while (true) {
                System.out.print("Enter new capacity (1-30) or 0 to keep current: ");
                int capacity = Integer.parseInt(scanner.nextLine().trim());
                if (capacity == 0) {
                    capacity = currentFacility.getCapacity();
                } else if (capacity < 1 || capacity > 30) {
                    System.out.println(ConsoleColors.error("Capacity must be between 1 and 30."));
                    continue;
                }
                
                // Parse current location to keep block, floor, room number unchanged
                String[] locationParts = parseLocation(currentFacility.getLocation());
                String block = locationParts[0];
                int floor = Integer.parseInt(locationParts[1]);
                int roomNumber = Integer.parseInt(locationParts[2]);
                
                String equipment = updateEquipment(currentFacility.getEquipment());

                int result = facilityControl.updateFacility(facilityId, capacity, block, floor, roomNumber, equipment);
                if (result == FacilityControl.OPERATION_OK) {
                    System.out.println(ConsoleColors.success("Facility updated successfully!"));
                    return;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.error("Invalid input. Please enter valid numbers."));
        }
    }

    private void deleteFacility() {
        System.out.println("\n========== DELETE FACILITY ==========");
        System.out.print("Enter facility ID to delete: ");
        String facilityId = scanner.nextLine().trim();
        
        int result = facilityControl.removeFacility(facilityId);
        if (result == FacilityControl.OPERATION_OK) {
            System.out.println(ConsoleColors.success("Facility deleted successfully!"));
        } else if (result == FacilityControl.FACILITY_NOT_FOUND) {
            System.out.println(ConsoleColors.error("Facility not found!"));
        }
    }

    private String selectEquipment() {
        List<String> selectedEquipment = new ArrayList<>();
        
        while (true) {
            System.out.println("\nSelect equipment (multiple allowed):");
            System.out.println("1. Projector");
            System.out.println("2. Computer");
            System.out.println("3. Charging Port");
            System.out.println("4. Other");
            System.out.println("5. Done");
            System.out.print("Enter your choice: ");
            
            int choice = readMenuChoice(1, 5);
            if (choice == -1) continue;
            
            if (choice == 5) {
                break;
            }
            
            String equipment = "";
            switch (choice) {
                case 1:
                    equipment = selectQuantity("Projector");
                    break;
                case 2:
                    equipment = selectQuantity("Computer");
                    break;
                case 3:
                    equipment = selectQuantity("Charging Port");
                    break;
                case 4:
                    System.out.print("Enter custom equipment name: ");
                    String customName = scanner.nextLine().trim();
                    if (customName.isEmpty()) {
                        customName = "Other";
                    }
                    equipment = selectQuantity(customName);
                    break;
            }
            
            // Check if already selected
            if (!selectedEquipment.contains(equipment)) {
                selectedEquipment.add(equipment);
                System.out.println("Added: " + equipment);
            } else {
                System.out.println("Already selected: " + equipment);
            }
        }
        
        if (selectedEquipment.isEmpty()) {
            return "Other";
        }
        
        return String.join(", ", selectedEquipment);
    }

    private String selectQuantity(String item) {
        System.out.print("Enter quantity for " + item + " (1-5): ");
        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            if (quantity < 1 || quantity > 5) {
                System.out.println("Invalid quantity. Setting to 1.");
                quantity = 1;
            }
            return item + " x" + quantity;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Setting to 1.");
            return item + " x1";
        }
    }

    private String updateEquipment(String currentEquipment) {
        System.out.println("\n========== UPDATE EQUIPMENT ==========");
        System.out.println("Current equipment: " + currentEquipment);
        
        List<String> equipmentList = new ArrayList<>();
        if (currentEquipment != null && !currentEquipment.isBlank()) {
            String[] items = currentEquipment.split(", ");
            for (String item : items) {
                equipmentList.add(item.trim());
            }
        }
        
        while (true) {
            System.out.println("\n--- Equipment Management ---");
            if (!equipmentList.isEmpty()) {
                System.out.println("Current equipment:");
                for (int i = 0; i < equipmentList.size(); i++) {
                    System.out.println((i + 1) + ". " + equipmentList.get(i));
                }
            } else {
                System.out.println("No equipment currently assigned.");
            }
            System.out.println("5. Add new equipment");
            System.out.println("6. Keep same");
            System.out.print("Enter your choice: ");
            
            int choice = readMenuChoice(1, 6);
            if (choice == -1) continue;
            
            switch (choice) {
                case 5:
                    // Add new equipment
                    addNewEquipmentToList(equipmentList);
                    break;
                case 6:
                    // Keep same
                    return currentEquipment;
                default:
                    // Modify existing equipment (1-4 or higher if more items)
                    if (choice > 0 && choice <= equipmentList.size()) {
                        modifyEquipment(equipmentList, choice - 1);
                    } else {
                        System.out.println("Invalid choice.");
                    }
            }
            
            // Check if user wants to finish
            if (askToDone()) {
                if (equipmentList.isEmpty()) {
                    return "Other";
                }
                return String.join(", ", equipmentList);
            }
        }
    }

    private void addNewEquipmentToList(List<String> equipmentList) {
        System.out.println("\nAdd Equipment:");
        System.out.println("1. Projector");
        System.out.println("2. Computer");
        System.out.println("3. Charging Port");
        System.out.println("4. Other");
        System.out.print("Select equipment type: ");
        
        int choice = readMenuChoice(1, 4);
        if (choice == -1) return;
        
        String equipment = "";
        switch (choice) {
            case 1:
                equipment = selectQuantity("Projector");
                break;
            case 2:
                equipment = selectQuantity("Computer");
                break;
            case 3:
                equipment = selectQuantity("Charging Port");
                break;
            case 4:
                System.out.print("Enter custom equipment name: ");
                String customName = scanner.nextLine().trim();
                if (customName.isEmpty()) {
                    customName = "Other";
                }
                equipment = selectQuantity(customName);
                break;
        }
        
        if (!equipmentList.contains(equipment)) {
            equipmentList.add(equipment);
            System.out.println(ConsoleColors.success("Added: " + equipment));
        } else {
            System.out.println(ConsoleColors.error("Equipment already exists."));
        }
    }

    private void modifyEquipment(List<String> equipmentList, int index) {
        String currentEquip = equipmentList.get(index);
        System.out.println("\nModify: " + currentEquip);
        System.out.println("1. Change quantity");
        System.out.println("2. Delete");
        System.out.print("Select action: ");
        
        int choice = readMenuChoice(1, 2);
        if (choice == -1) return;
        
        if (choice == 1) {
            // Extract equipment name and change quantity
            String[] parts = currentEquip.split(" x");
            if (parts.length == 2) {
                String name = parts[0].trim();
                String newEquip = selectQuantity(name);
                equipmentList.set(index, newEquip);
                System.out.println(ConsoleColors.success("Updated to: " + newEquip));
            }
        } else if (choice == 2) {
            equipmentList.remove(index);
            System.out.println(ConsoleColors.success("Deleted: " + currentEquip));
        }
    }

    private boolean askToDone() {
        System.out.print("Done with equipment? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    private String[] parseLocation(String location) {
        // Location format: "Block A, Floor 1, Room 1"
        String[] parts = location.split(", ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid location format");
        }
        
        String block = parts[0].replace("Block ", "").trim();
        String floor = parts[1].replace("Floor ", "").trim();
        String room = parts[2].replace("Room ", "").trim();
        
        return new String[]{block, floor, room};
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
