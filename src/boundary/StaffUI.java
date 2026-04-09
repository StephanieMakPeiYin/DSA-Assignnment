package boundary;

import adt.ArrayListADT;
import adt.ListInterface;
import control.BookingControl;
import control.FacilityControl;
import control.UserControl;
import entity.Room;
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

    // --- Staff Menu ---
    private void manageUsers() {
        int choice;
        do {
            System.out.println("\n========== MANAGE USERS ==========");
            System.out.println("1. Add new user");
            System.out.println("2. Update user information");
            System.out.println("3. Remove user");
            System.out.println("4. Search user");
            System.out.println("5. View all users");
            System.out.println("6. Recover deleted user");
            System.out.print("Enter your choice (0 to exit): ");

            choice = readMenuChoice(0, 6);

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
                case 6:
                    recoverUser();
                    break;
                case 0:
                    return;
            }
        } while (true);
    }

    // --- View All Users ---
    private void viewAllUsers() {
        System.out.println("\n========== VIEW ALL USERS ==========");
        ListInterface<entity.User> users = userControl.getAllUsers();
        
        // Filter out staff account, separate active and removed users
        ListInterface<entity.User> activeUsers = new ArrayListADT<>();
        ListInterface<entity.User> removedUsers = new ArrayListADT<>();
        
        for (int i = 1; i <= users.getLength(); i++) {
            entity.User user = users.getEntry(i);
            if (!user.getEmail().equals("staff@gmail.com")) {
                if ("active".equals(user.getStatus())) {
                    activeUsers.add(user);
                } else if ("removed".equals(user.getStatus())) {
                    removedUsers.add(user);
                }
            }
        }
        
        if (activeUsers.getLength() == 0 && removedUsers.getLength() == 0) {
            System.out.println("No users found in the system.");
            return;
        }
        
        // Display active users
        if (activeUsers.getLength() > 0) {
            System.out.println(String.format("\n--- ACTIVE USERS (%d) ---\n", activeUsers.getLength()));
            System.out.println(String.format("%-10s %-20s %-25s %-12s %-10s", "User ID", "Name", "Email", "User Type", "Status"));
            System.out.println("-".repeat(80));
            
            for (int i = 1; i <= activeUsers.getLength(); i++) {
                entity.User user = activeUsers.getEntry(i);
                System.out.println(String.format("%-10s %-20s %-25s %-12s %-10s", 
                    user.getUserID(), user.getName(), user.getEmail(), user.getUserType(), user.getStatus()));
            }
        }
        
        // Display removed users if exist
        if (removedUsers.getLength() > 0) {
            System.out.println(String.format("\n--- REMOVED USERS (%d) ---\n", removedUsers.getLength()));
            System.out.println(String.format("%-10s %-20s %-25s %-12s %-10s", "User ID", "Name", "Email", "User Type", "Status"));
            System.out.println("-".repeat(80));
            
            for (int i = 1; i <= removedUsers.getLength(); i++) {
                entity.User user = removedUsers.getEntry(i);
                System.out.println(String.format("%-10s %-20s %-25s %-12s %-10s", 
                    user.getUserID(), user.getName(), user.getEmail(), user.getUserType(), user.getStatus()));
            }
        }
    }

    // --- Search User ---
    private void searchUser() {
        int choice;
        do {
            System.out.println("\n========== SEARCH USER ==========");
            System.out.println("Search by:");
            System.out.println("1. User ID");
            System.out.println("2. Name");
            System.out.println("3. Email");
            System.out.print("Enter your choice (0 to exit): ");
            
            choice = readMenuChoice(0, 3);
            
            if (choice == -1) {
                continue;
            }
            
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
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid choice.");
            }
        } while (true);
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
        
        ListInterface<entity.User> results = userControl.findUsersByName(name);
        
        // Filter out deleted users
        ListInterface<entity.User> activeResults = new ArrayListADT<>();
        for (int i = 1; i <= results.getLength(); i++) {
            entity.User user = results.getEntry(i);
            if ("active".equals(user.getStatus())) {
                activeResults.add(user);
            }
        }
        
        if (activeResults.getLength() == 0) {
            System.out.println(ConsoleColors.error("\n[ERROR] No active users found matching name: " + name));
            return;
        }
        
        System.out.println(ConsoleColors.success("\n[SUCCESS] Found " + activeResults.getLength() + " user(s) matching name: " + name));
        System.out.println(String.format("%-10s %-20s %-25s %-12s", "User ID", "Name", "Email", "User Type"));
        System.out.println("-".repeat(70));
        
        for (int i = 1; i <= activeResults.getLength(); i++) {
            entity.User user = activeResults.getEntry(i);
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

    // --- Remove User ---
    private void deleteUser() {
        while (true) {
            System.out.println("\n========== REMOVE USER ==========");
            System.out.print("Enter email to remove (e.g. user@gmail.com) (0 to exit): ");
            String email = scanner.nextLine().trim();
            
            if (email.equals("0")) {
                System.out.println("Cancelled user removal.");
                return;
            }
            
            if (email.isEmpty()) {
                System.out.println(ConsoleColors.error("\n[ERROR] Email cannot be empty."));
                continue;
            }
            
            // Prevent removing staff account
            if (email.equals("staff@gmail.com")) {
                System.out.println(ConsoleColors.error("\n[ERROR] Cannot remove default staff account."));
                continue;
            }
            
            entity.User userToDelete = userControl.findUserByEmail(email);
            if (userToDelete == null) {
                System.out.println(ConsoleColors.error("\n[ERROR] User not found with email: " + email));
                continue;
            }
            
            if ("removed".equals(userToDelete.getStatus())) {
                System.out.println(ConsoleColors.error("\n[ERROR] This user is already removed."));
                continue;
            }
            
            System.out.println("\nUser to remove:");
            System.out.println("Name: " + userToDelete.getName());
            System.out.println("Email: " + userToDelete.getEmail());
            System.out.println("User Type: " + userToDelete.getUserType());
            
            while (true) {
                System.out.println("\nConfirm removal:");
                System.out.println("[1] Yes, remove this user");
                System.out.println("[2] No, cancel removal");
                System.out.print("Enter your choice: ");
                String confirmation = scanner.nextLine().trim();
                
                if (confirmation.equals("1")) {
                    boolean deleted = userControl.deleteUser(email);
                    if (deleted) {
                        System.out.println(ConsoleColors.success("\n[SUCCESS] User removed successfully."));
                    } else {
                        System.out.println(ConsoleColors.error("\n[ERROR] Failed to remove user."));
                    }
                    return;
                } else if (confirmation.equals("2")) {
                    System.out.println("\nDeletion cancelled.");
                    break;
                } else {
                    System.out.println(ConsoleColors.error("\n[ERROR] Invalid choice. Enter 1 or 2."));
                }
            }
        }
    }

    // --- Recover Deleted User ---
    private void recoverUser() {

        ListInterface<entity.User> allUsers = userControl.getAllUsers();
        
        ListInterface<entity.User> deletedUsers = new ArrayListADT<>();
        for (int i = 1; i <= allUsers.getLength(); i++) {
            entity.User user = allUsers.getEntry(i);
            if ("removed".equals(user.getStatus())) {
                deletedUsers.add(user);
            }
        }
        
        if (deletedUsers.getLength() == 0) {
            System.out.println("\nNo deleted users found in the system.");
            return;
        }
        
        int choice;
        
        do {
            System.out.println("\n========== RECOVER DELETED USER ==========");
            System.out.println("[1] View all deleted users");
            System.out.println("[2] Recover a deleted user");
            System.out.println("[0] Back to menu");
            System.out.print("Enter your choice: ");

            choice = readMenuChoice(0, 2);

            switch (choice) {
                case 1:
                    viewDeletedUsers();
                    break;
                case 2:
                    performRecoverUser();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (true);
    }

    private void viewDeletedUsers() {
        System.out.println("\n========== ALL DELETED USERS ==========");
        ListInterface<entity.User> allUsers = userControl.getAllUsers();
        
        ListInterface<entity.User> deletedUsers = new ArrayListADT<>();
        for (int i = 1; i <= allUsers.getLength(); i++) {
            entity.User user = allUsers.getEntry(i);
            if ("removed".equals(user.getStatus())) {
                deletedUsers.add(user);
            }
        }
        
        if (deletedUsers.getLength() == 0) {
            System.out.println("\nNo deleted users found in the system.");
            return;
        }
        
        System.out.println(String.format("\nTotal Deleted Users: %d\n", deletedUsers.getLength()));
        System.out.println(String.format("%-10s %-20s %-25s %-12s", "User ID", "Name", "Email", "User Type"));
        System.out.println("-".repeat(70));
        
        for (int i = 1; i <= deletedUsers.getLength(); i++) {
            entity.User user = deletedUsers.getEntry(i);
            System.out.println(String.format("%-10s %-20s %-25s %-12s", 
                user.getUserID(), user.getName(), user.getEmail(), user.getUserType()));
        }
    }

    private void performRecoverUser() {
        while (true) {
            System.out.println("\n========== RECOVER DELETED USER ==========");
            System.out.print("Enter email of deleted user to recover (e.g. user@gmail.com) (0 to exit): ");
            String email = scanner.nextLine().trim();
            
            if (email.equals("0")) {
                System.out.println("Cancelled user recovery.");
                return;
            }
            
            if (email.isEmpty()) {
                System.out.println(ConsoleColors.error("\n[ERROR] Email cannot be empty."));
                continue;
            }
            
            entity.User userToRecover = userControl.findUserByEmail(email);
            if (userToRecover == null) {
                System.out.println(ConsoleColors.error("\n[ERROR] User not found with email: " + email));
                continue;
            }
            
            if ("active".equals(userToRecover.getStatus())) {
                System.out.println(ConsoleColors.error("\n[ERROR] This user is already active, not deleted."));
                continue;
            }
            
            if (!"removed".equals(userToRecover.getStatus())) {
                System.out.println(ConsoleColors.error("\n[ERROR] This user does not have a 'removed' status."));
                continue;
            }
            
            System.out.println("\nUser to recover:");
            System.out.println("Name: " + userToRecover.getName());
            System.out.println("Email: " + userToRecover.getEmail());
            System.out.println("User Type: " + userToRecover.getUserType());
            System.out.println("Current Status: " + userToRecover.getStatus());
            
            while (true) {
                System.out.println("\nConfirm recovery:");
                System.out.println("[1] Yes, recover this user");
                System.out.println("[2] No, cancel recovery");
                System.out.print("Enter your choice: ");
                String confirmation = scanner.nextLine().trim();
                
                if (confirmation.equals("1")) {
                    entity.User recoveredUser = new entity.User(userToRecover.getUserID(), userToRecover.getName(), 
                        userToRecover.getEmail(), userToRecover.getPassword(), userToRecover.getUserType(), "active");
                    boolean recovered = userControl.updateUser(recoveredUser);
                    
                    if (recovered) {
                        System.out.println(ConsoleColors.success("\n[SUCCESS] User recovered successfully!"));
                        System.out.println("Status changed to: active");
                    } else {
                        System.out.println(ConsoleColors.error("\n[ERROR] Failed to recover user."));
                    }
                    return;
                } else if (confirmation.equals("2")) {
                    System.out.println("\nRecovery cancelled.");
                    break;
                } else {
                    System.out.println(ConsoleColors.error("\n[ERROR] Invalid choice. Enter 1 or 2."));
                }
            }
        }
    }

    // --- Add New User ---
    private void addNewUser() {
        System.out.println("\n========== ADD NEW USER ==========\n");
        System.out.println("(Enter 0 at any prompt to CANCEL)\n");

        String name;
        while (true) {
            System.out.print("Enter name (no spaces): ");
            name = scanner.nextLine().trim();
            if (name.equals("0")) {
                System.out.println("Cancelled user creation.");
                return;
            }
            if (!isValidName(name)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Name must be 1-20 characters and contain no spaces."));
            } else {
                break;
            }
        }

        String email;
        while (true) {
            System.out.print("Enter email username (without @gmail.com): ");
            String emailLocal = scanner.nextLine().trim();
            if (emailLocal.equals("0")) {
                System.out.println("Cancelled user creation.");
                return;
            }
            if (emailLocal.isEmpty() || emailLocal.contains(" ") || emailLocal.contains("@")) {
                System.out.println(ConsoleColors.error("\n[ERROR] Email username cannot be empty, contain spaces, or '@'."));
            } else {
                email = emailLocal + "@gmail.com";
                if (userControl.findUserByEmail(email) != null) {
                    System.out.println(ConsoleColors.error("\n[ERROR] Email already exists in the system: " + email));
                } else {
                    break;
                }
            }
        }

        String password;
        while (true) {
            System.out.print("Enter password (at least 5 chars with digit): ");
            password = scanner.nextLine();
            if (password.equals("0")) {
                System.out.println("Cancelled user creation.");
                return;
            }
            if (!isValidPassword(password)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Password must be at least 5 chars and contain at least one digit."));
            } else {
                break;
            }
        }

        String userType;
        while (true) {
            System.out.println("Select user type:");
            System.out.println("[1] STAFF\n[2] STUDENT\n[0] cancel");
            System.out.print("Enter choice: ");
            String typeChoice = scanner.nextLine().trim();
            if (typeChoice.equals("0")) {
                System.out.println("Cancelled user creation.");
                return;
            }
            if (typeChoice.equals("1")) {
                userType = "STAFF";
                break;
            } else if (typeChoice.equals("2")) {
                userType = "STUDENT";
                break;
            } else {
                System.out.println(ConsoleColors.error("\n[ERROR] Invalid user type. Choose 1 for STAFF or 2 for STUDENT."));
            }
        }

        entity.User newUser = new entity.User(null, name, email, password, userType);
        userControl.addUser(newUser);
        System.out.println(ConsoleColors.success("\n[SUCCESS] New user added successfully!"));
        System.out.println("User ID: " + newUser.getUserID());
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("User Type: " + userType);
    }

    // --- Update User Information ---
    private void updateUserInformation() {
        System.out.println("\n========== UPDATE USER INFORMATION ==========\n");
        System.out.println("(Enter 0 at any prompt to CANCEL)\n");
        System.out.print("Enter email to update (e.g. users@gmail.com): ");
        String email = scanner.nextLine().trim();

        if (email.equals("0")) {
            System.out.println("Cancelled user update.");
            return;
        }

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

        System.out.println("\n--- Enter new information (press ENTER to keep current or 0 to CANCEL) ---\n");

        String newName;
        while (true) {
            System.out.print("New name [Current name: " + existingUser.getName() + "]: ");
            newName = scanner.nextLine().trim();
            if (newName.equals("0")) {
                System.out.println("Cancelled user update.");
                return;
            }
            if (newName.isEmpty()) {
                newName = existingUser.getName();
                break;
            }
            if (!isValidName(newName)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Name must be 1-20 characters and contain no spaces."));
                continue;
            }
            break;
        }

        String newPassword;
        while (true) {
            System.out.print("New password (at least 5 chars with digit): ");
            newPassword = scanner.nextLine();
            if (newPassword.equals("0")) {
                System.out.println("Cancelled user update.");
                return;
            }
            if (newPassword.isEmpty()) {
                newPassword = existingUser.getPassword();
                break;
            }
            if (!isValidPassword(newPassword)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Password must be at least 5 chars and include at least one number."));
                continue;
            }
            break;
        }

        String newUserType;
        while (true) {
            System.out.println("Select user type:");
            System.out.println("[1] STAFF\n[2] STUDENT\n[0] cancel");
            System.out.print("Enter choice: ");
            String userTypeChoice = scanner.nextLine().trim();
            if (userTypeChoice.equals("0")) {
                System.out.println("Cancelled user update.");
                return;
            }
            if (userTypeChoice.isEmpty()) {
                newUserType = existingUser.getUserType();
                break;
            }
            if (userTypeChoice.equals("1")) {
                newUserType = "STAFF";
                break;
            } else if (userTypeChoice.equals("2")) {
                newUserType = "STUDENT";
                break;
            } else {
                System.out.println(ConsoleColors.error("\n[ERROR] Invalid user type choice."));
            }
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
            System.out.println("2. View booking details");
            System.out.println("3. Search booking by ID");
            System.out.println("4. Filter bookings");
            System.out.println("0. Back to menu");
            System.out.print("Enter your choice: ");

            choice = readMenuChoice(0, 4);

            switch (choice) {
                case 1:
                    viewAllBookings();
                    break;
                case 2:
                    viewBookingDetailsOption();
                    break;
                case 3:
                    searchBookingById();
                    break;
                case 4:
                    filterBookingsMenu();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (true);
    }

    private void filterBookingsMenu() {
        System.out.println("\n========== FILTER BOOKINGS ==========");
        System.out.println("1. Filter by date");
        System.out.println("2. Filter by status");
        System.out.println("0. Back");
        System.out.print("Enter your choice: ");

        int choice = readMenuChoice(0, 2);
        switch (choice) {
            case 1:
                filterBookingsByDate();
                break;
            case 2:
                filterBookingsByStatus();
                break;
            case 0:
            default:
                return;
        }
    }

    private void filterBookingsByStatus() {
        System.out.println("\n========== FILTER BOOKINGS BY STATUS ==========");
        System.out.println("1. Active bookings");
        System.out.println("2. Cancelled bookings");
        System.out.print("Enter your choice: ");

        int choice = readMenuChoice(1, 2);
        String status = (choice == 1) ? "ACTIVE" : "CANCELLED";

        ListInterface<entity.Booking> allBookings = bookingControl.getAllBookingsADT();
        ListInterface<entity.Booking> filtered = new ArrayListADT<>();
        for (int i = 1; i <= allBookings.getLength(); i++) {
            entity.Booking b = allBookings.getEntry(i);
            if (status.equals(b.getStatus())) {
                filtered.add(b);
            }
        }

        if (filtered.getLength() == 0) {
            System.out.println("\nNo " + status.toLowerCase() + " bookings found.");
            return;
        }

        System.out.println(String.format("\n--- %s Bookings (%d) ---\n", status, filtered.getLength()));
        if ("ACTIVE".equals(status)) {
            System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                    "Booking ID", "Room ID", "Date", "Time Slot", "Status", "Student Name", "Student Email"));
            System.out.println("-".repeat(115));
            for (int i = 1; i <= filtered.getLength(); i++) {
                entity.Booking b = filtered.getEntry(i);
                System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                        b.getBookingID(), b.getRoomID(), b.getDate(),
                        b.getTimeSlot(), b.getStatus(),
                        b.getStudentUsername(), b.getStudentEmail()));
            }
        } else {
            System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s %-25s",
                    "Booking ID", "Room ID", "Date", "Time Slot", "Status", "Student Name", "Student Email", "Cancel Reason"));
            System.out.println("-".repeat(140));
            for (int i = 1; i <= filtered.getLength(); i++) {
                entity.Booking b = filtered.getEntry(i);
                String reason = b.getCancelReason() != null ? b.getCancelReason() : "";
                System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s %-25s",
                        b.getBookingID(), b.getRoomID(), b.getDate(),
                        b.getTimeSlot(), b.getStatus(),
                        b.getStudentUsername(), b.getStudentEmail(), reason));
            }
        }
    }

    private void viewAllBookings() {
        System.out.println("\n========== VIEW ALL BOOKINGS ==========");
        ListInterface<entity.Booking> allBookings = bookingControl.getAllBookingsADT();

        if (allBookings.getLength() == 0) {
            System.out.println("No bookings found in the system.");
            return;
        }

        System.out.println(String.format("\nTotal Bookings: %d\n", allBookings.getLength()));
        System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                "Booking ID", "Room ID", "Date", "Time Slot", "Status", "Student Name", "Student Email"));
        System.out.println("-".repeat(115));

        for (int i = 1; i <= allBookings.getLength(); i++) {
            entity.Booking booking = allBookings.getEntry(i);
            System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                    booking.getBookingID(), booking.getRoomID(), booking.getDate(),
                    booking.getTimeSlot(), booking.getStatus(),
                    booking.getStudentUsername(), booking.getStudentEmail()));
        }
    }

    private void viewBookingDetailsOption() {
        System.out.println("\n========== VIEW BOOKING DETAILS ==========");
        ListInterface<entity.Booking> allBookings = bookingControl.getAllBookingsADT();

        if (allBookings.getLength() == 0) {
            System.out.println("No bookings found in the system.");
            return;
        }

        System.out.println(String.format("\nTotal Bookings: %d\n", allBookings.getLength()));
        System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                "Booking ID", "Room ID", "Date", "Time Slot", "Status", "Student Name", "Student Email"));
        System.out.println("-".repeat(115));

        for (int i = 1; i <= allBookings.getLength(); i++) {
            entity.Booking booking = allBookings.getEntry(i);
            System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                    booking.getBookingID(), booking.getRoomID(), booking.getDate(),
                    booking.getTimeSlot(), booking.getStatus(),
                    booking.getStudentUsername(), booking.getStudentEmail()));
        }

        System.out.print("\nEnter booking ID to view details (or 0 to exit): ");
        String bookingID = scanner.nextLine().trim();

        if (bookingID.equals("0")) {
            return;
        }

        if (bookingID.isEmpty()) {
            System.out.println(ConsoleColors.error("[ERROR] Booking ID cannot be empty."));
            return;
        }

        entity.Booking booking = bookingControl.findBookingById(bookingID);

        if (booking == null) {
            System.out.println(ConsoleColors.error("[ERROR] Booking not found with ID: " + bookingID));
            return;
        }

        entity.Room room = facilityControl.searchFacility(booking.getRoomID());

        System.out.println("\n========== BOOKING DETAILS ==========");
        System.out.println("Booking ID   : " + booking.getBookingID());
        System.out.println("Student Name : " + booking.getStudentUsername());
        System.out.println("Student Email: " + booking.getStudentEmail());
        System.out.println("Date         : " + booking.getDate());
        System.out.println("Time Slot    : " + booking.getTimeSlot());
        System.out.println("Status       : " + booking.getStatus());

        if (room != null) {
            System.out.println("\n--- Room Information ---");
            System.out.println("Room ID  : " + room.getRoomID());
            System.out.println("Name     : " + room.getName());
            System.out.println("Capacity : " + room.getCapacity());
            System.out.println("Location : " + room.getLocation());
            System.out.println("Equipment: " + room.getEquipment());
        }

        if ("CANCELLED".equals(booking.getStatus()) && booking.getCancelReason() != null) {
            System.out.println("\n--- Cancellation Information ---");
            System.out.println("Reason: " + booking.getCancelReason());
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
        System.out.println("Booking ID   : " + booking.getBookingID());
        System.out.println("Room ID      : " + booking.getRoomID());
        System.out.println("Date         : " + booking.getDate());
        System.out.println("Time Slot    : " + booking.getTimeSlot());
        System.out.println("Student Name : " + booking.getStudentUsername());
        System.out.println("Student Email: " + booking.getStudentEmail());
        System.out.println("Status       : " + booking.getStatus());
        if ("CANCELLED".equals(booking.getStatus()) && booking.getCancelReason() != null) {
            System.out.println("Cancel Reason: " + booking.getCancelReason());
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

        if (!dateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("\n[ERROR] Invalid date format. Please use YYYY-MM-DD.");
            return;
        }

        ListInterface<entity.Booking> bookingsForDate = bookingControl.getBookingsByDateADT(dateInput);

        if (bookingsForDate.getLength() == 0) {
            System.out.println("\nNo bookings found for date: " + dateInput);
            return;
        }

        System.out.println(String.format("\nBookings for %s: %d bookings\n", dateInput, bookingsForDate.getLength()));
        System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                "Booking ID", "Room ID", "Date", "Time Slot", "Status", "Student Name", "Student Email"));
        System.out.println("-".repeat(115));

        for (int i = 1; i <= bookingsForDate.getLength(); i++) {
            entity.Booking booking = bookingsForDate.getEntry(i);
            System.out.println(String.format("%-10s %-10s %-15s %-15s %-12s %-15s %-20s",
                    booking.getBookingID(), booking.getRoomID(), booking.getDate(),
                    booking.getTimeSlot(), booking.getStatus(),
                    booking.getStudentUsername(), booking.getStudentEmail()));
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
            System.out.println("5. Recover facility");
            System.out.println("0. Back to menu");
            System.out.print("Enter your choice: ");

            choice = readMenuChoice(0, 5);

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
                case 5:
                    recoverFacility();
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

    private void recoverFacility() {
        System.out.println("\n========== RECOVER FACILITY ==========");

        adt.ListInterface<Room> deletedFacilities = facilityControl.getDeletedFacilities();
        if (deletedFacilities.getLength() == 0) {
            System.out.println("No soft-deleted facilities available for recovery.");
            return;
        }

        System.out.println("Soft-deleted facilities:");
        System.out.println(String.format("%-10s %-20s %-30s %-12s", "Facility ID", "Name", "Location", "Equipment"));
        System.out.println("-".repeat(80));
        for (int i = 1; i <= deletedFacilities.getLength(); i++) {
            Room room = deletedFacilities.getEntry(i);
            System.out.println(String.format("%-10s %-20s %-30s %-12s", room.getRoomID(), room.getName(), room.getLocation(), room.getEquipment()));
        }

        System.out.print("Enter facility ID to recover (or 0 to cancel): ");
        String facilityId = scanner.nextLine().trim();
        if (facilityId.equals("0")) {
            System.out.println("Facility recovery cancelled.");
            return;
        }
        
        int result = facilityControl.recoverFacility(facilityId);
        if (result == FacilityControl.OPERATION_OK) {
            System.out.println(ConsoleColors.success("Facility recovered successfully! It will now appear in the facility list."));
        } else if (result == FacilityControl.FACILITY_NOT_FOUND) {
            System.out.println(ConsoleColors.error("Facility not found!"));
        }
    }

    private String selectEquipment() {
        ListInterface<String> selectedEquipment = new ArrayListADT<>();
        
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
        
        if (selectedEquipment.getLength() == 0) {
            return "Other";
        }
        
        return joinEquipmentList(selectedEquipment);
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
        
        ListInterface<String> equipmentList = new ArrayListADT<>();
        if (currentEquipment != null && !currentEquipment.isBlank()) {
            String[] items = currentEquipment.split(", ");
            for (String item : items) {
                equipmentList.add(item.trim());
            }
        }
        
        while (true) {
            System.out.println("\n--- Equipment Management ---");
            if (equipmentList.getLength() > 0) {
                System.out.println("Current equipment:");
                for (int i = 1; i <= equipmentList.getLength(); i++) {
                    System.out.println(i + ". " + equipmentList.getEntry(i));
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
                    if (choice > 0 && choice <= equipmentList.getLength()) {
                        modifyEquipment(equipmentList, choice - 1);
                    } else {
                        System.out.println("Invalid choice.");
                    }
            }
            
            // Check if user wants to finish
            if (askToDone()) {
                if (equipmentList.getLength() == 0) {
                    return "Other";
                }
                return joinEquipmentList(equipmentList);
            }
        }
    }

    private void addNewEquipmentToList(ListInterface<String> equipmentList) {
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

    private void modifyEquipment(ListInterface<String> equipmentList, int index) {
        String currentEquip = equipmentList.getEntry(index + 1);
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
                equipmentList.replace(index + 1, newEquip);
                System.out.println(ConsoleColors.success("Updated to: " + newEquip));
            }
        } else if (choice == 2) {
            equipmentList.remove(index + 1);
            System.out.println(ConsoleColors.success("Deleted: " + currentEquip));
        }
    }

    private String joinEquipmentList(ListInterface<String> equipmentList) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= equipmentList.getLength(); i++) {
            if (i > 1) {
                sb.append(", ");
            }
            sb.append(equipmentList.getEntry(i));
        }
        return sb.toString();
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

    private boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.length() <= 20 && !name.contains(" ");
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 5) {
            return false;
        }
        return password.chars().anyMatch(Character::isDigit);
    }

    private void printStaffMenu() {
        System.out.println("\n=======================");
        System.out.println("=      STAFF PAGE       =");
        System.out.println("=======================");
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
                System.out.println(ConsoleColors.error("[ERROR] Invalid input. Please enter a number between " + min + " and " + max + "."));
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            System.out.println(ConsoleColors.error("[ERROR] Invalid input. Please enter a valid number."));
            return -1;
        }
    }
}
