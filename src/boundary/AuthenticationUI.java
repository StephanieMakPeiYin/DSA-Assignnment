package boundary;

import control.AuthenticationControl;
import util.ConsoleColors;
import java.util.Scanner;

public class AuthenticationUI {
    private final AuthenticationControl authControl;
    private final Scanner scanner;

    public AuthenticationUI(AuthenticationControl authControl) {
        this.authControl = authControl;
        this.scanner = new Scanner(System.in);
    }

    public boolean startAuthentication() {
        int choice;
        do {
            printAuthenticationMenu();
            choice = readMenuChoice(0, 2);

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegister();
                    break;
                case 0:
                    System.out.println("\nExiting system. Thank you!");
                    return false;
            }

            if (authControl.getCurrentUser() != null) {
                return true;
            }
        } while (true);
    }

    private void handleLogin() {
        System.out.println("\n========== LOGIN ==========");
        System.out.print("Email (e.g. username@gmail.com): ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        String result = authControl.login(email, password);

        if (result.equals("LOGIN_SUCCESS")) {
            System.out.println(ConsoleColors.success("\nLogin successful!"));
            System.out.println("Welcome, " + authControl.getCurrentUser().getName());
            if (authControl.isStaff()) {
                System.out.println("You are logged in as STAFF");
            } else {
                System.out.println("You are logged in as STUDENT");
            }
        } else {
            System.out.println(ConsoleColors.error("\n[ERROR] " + result));
        }
    }

    private void handleRegister() {
        System.out.println("\n========== REGISTER ==========");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Email (format: username@gmail.com): ");
        String email = scanner.nextLine().trim();

        System.out.print("Password (minimum 4 characters): ");
        String password = scanner.nextLine();

        System.out.print("Confirm Password: ");
        String confirmPassword = scanner.nextLine();

        String result = authControl.register(name, email, password, confirmPassword);

        if (result.equals("REGISTER_SUCCESS")) {
            System.out.println(ConsoleColors.success("\nRegistration successful!"));
            entity.User createdUser = authControl.getLastCreatedUser();
            if (createdUser != null) {
                System.out.println("Your User ID: " + createdUser.getUserID());
            }
            System.out.println("Account created for: " + name);
            System.out.println("Email: " + email);
            System.out.println("You can now login with your credentials.");
        } else {
            System.out.println(ConsoleColors.error("\n[ERROR] " + result));
        }
    }

    private void printAuthenticationMenu() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║ TARUMT FACILITIES BOOKING SYSTEM   ║");
        System.out.println("╚════════════════════════════════════╝");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("0. Exit");
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
