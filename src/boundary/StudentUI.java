package boundary;

import control.BookingControl;
import control.FacilityControl;
import control.UserControl;
import util.ConsoleColors;
import java.util.Scanner;

public class StudentUI {
    private final BookingUI bookingUI;
    private final Scanner scanner;
    private String studentName;
    private String studentEmail;
    private final UserControl userControl;

    public StudentUI(FacilityControl facilityControl) {
        this(facilityControl, "Unknown", "unknown@email.com");
    }

    public StudentUI(BookingControl bookingControl, String studentName, String studentEmail) {
        this(bookingControl, studentName, studentEmail, new UserControl());
    }

    public StudentUI(BookingControl bookingControl, String studentName, String studentEmail, UserControl userControl) {
        this.bookingUI = new BookingUI(bookingControl, studentName, studentEmail);
        this.scanner = new Scanner(System.in);
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.userControl = userControl;
    }

    public StudentUI(FacilityControl facilityControl, String studentName, String studentEmail) {
        this(new BookingControl(facilityControl), studentName, studentEmail, new UserControl());
    }

    public void start() {
        int choice;
        do {
            printStudentMenu();
            choice = readMenuChoice(0, 2);

            switch (choice) {
                case 1:
                    System.out.println("\n[Room Booking Services]");
                    bookingUI.start();
                    break;
                case 2:
                    updateOwnProfile();
                    break;
                case 0:
                    System.out.println(ConsoleColors.success("\nLogout successful. Thank you!"));
                    return;
                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        } while (true);
    }

    private void printStudentMenu() {
        System.out.println("\n==================================");
        System.out.println("= TARUMT FACILITIES BOOKING SYSTEM  =");
        System.out.println("==================================");
        System.out.println("1. Room Booking");
        System.out.println("2. Update Profile");
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

    // --- Update Own Profile ---
    private void updateOwnProfile() {
        System.out.println("\n========== UPDATE YOUR PROFILE ==========\n");
        
        entity.User currentUser = userControl.findUserByEmail(studentEmail);
        if (currentUser == null) {
            System.out.println(ConsoleColors.error("\n[ERROR] Could not find your account."));
            return;
        }

        System.out.println("========== CURRENT PROFILE ==========");
        System.out.println("Name: " + currentUser.getName());
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Password: " + currentUser.getPassword());

        System.out.println("\n--- Enter new information (press ENTER to keep current or 0 to CANCEL) ---\n");

        String newName;
        while (true) {
            System.out.print("New name [Current name: " + currentUser.getName() + "]: ");
            newName = scanner.nextLine().trim();
            if (newName.equals("0")) {
                System.out.println("Cancelled profile update.");
                return;
            }
            if (newName.isEmpty()) {
                newName = currentUser.getName();
                break;
            }
            if (!isValidName(newName)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Name must be 1-20 characters and contain no spaces."));
                continue;
            }
            break;
        }

        String newEmail;
        while (true) {
            System.out.print("New email [Current email: " + currentUser.getEmail() + "]: ");
            newEmail = scanner.nextLine().trim();
            if (newEmail.equals("0")) {
                System.out.println("Cancelled profile update.");
                return;
            }
            if (newEmail.isEmpty()) {
                newEmail = currentUser.getEmail();
                break;
            }
            if (!isValidEmail(newEmail)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Email must be in format: username@gmail.com"));
                continue;
            }
            // Check if new email is the same as an existing user's email (excluding current user)
            if (userControl.isEmailInUse(newEmail, currentUser.getEmail())) {
                System.out.println(ConsoleColors.error("\n[ERROR] This email is already in use by another user. Please choose a different email."));
                continue;
            }
            break;
        }

        String newPassword;
        while (true) {
            System.out.print("New password (at least 5 chars with digit): ");
            newPassword = scanner.nextLine();
            if (newPassword.equals("0")) {
                System.out.println("Cancelled profile update.");
                return;
            }
            if (newPassword.isEmpty()) {
                newPassword = currentUser.getPassword();
                break;
            }
            if (!isValidPassword(newPassword)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Password must be at least 5 chars and include at least one number."));
                continue;
            }
            
            // Confirm password
            System.out.print("Confirm new password: ");
            String confirmPassword = scanner.nextLine();
            if (!newPassword.equals(confirmPassword)) {
                System.out.println(ConsoleColors.error("\n[ERROR] Passwords do not match. Please try again."));
                continue;
            }
            break;
        }

        entity.User updatedUser = new entity.User(currentUser.getUserID(), newName, newEmail, newPassword, currentUser.getUserType(), currentUser.getStatus());
        boolean success = userControl.updateUser(currentUser.getEmail(), updatedUser);

        if (success) {
            System.out.println(ConsoleColors.success("\n[SUCCESS] Profile updated successfully!"));
            System.out.println("Name: " + newName);
            System.out.println("Email: " + newEmail);
            // Update local variables for future reference
            this.studentName = newName;
            this.studentEmail = newEmail;
        } else {
            System.out.println(ConsoleColors.error("\n[ERROR] Failed to update profile."));
        }
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

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.endsWith("@gmail.com") && email.contains("@") && email.indexOf("@") > 0;
    }
}
