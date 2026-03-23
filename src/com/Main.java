package com;

import java.util.Scanner;
import boundary.BookingUI;
import boundary.FacilityUI;
import control.FacilityControl;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        FacilityControl facilityControl = new FacilityControl();
        BookingUI bookingUI = new BookingUI(facilityControl);
        FacilityUI facilityUI = new FacilityUI(facilityControl);

        // TODO: Initialize other modules
        // UserManagementUI userUI = new UserManagementUI();
        // FacilityManagementUI facilityUI = new FacilityManagementUI();

        int choice;

        do {
            printMainMenu();

            choice = readMainMenuChoice(scanner);

            switch (choice) {

                case 1:
                    System.out.println("\n[User Management]");
                    // TODO: userUI.start();
                    System.out.println("Feature coming soon.");
                    break;

                case 2:
                    System.out.println("\n[Room Management]");
                    facilityUI.start();
                    break;

                case 3:
                    System.out.println("\n[Room Booking Services]");
                    bookingUI.start();
                    break;

                case 0:
                    System.out.println("\nThank you for using TARUMT Facilities System.");
                    break;

                default:
                    System.out.println("\nInvalid selection. Please try again.");
            }

        } while (choice != 0);

        scanner.close();
    }

    private static int readMainMenuChoice(Scanner scanner) {
        while (true) {
            System.out.print("Select service (0-3): ");
            try {
                String line = scanner.nextLine();
                if (line == null) {
                    return 0;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    System.out.println("Please enter a number between 0 and 3.");
                    continue;
                }
                int v = Integer.parseInt(line);
                if (v < 0 || v > 3) {
                    System.out.println("Please enter a number between 0 and 3.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a whole number (0-3).");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n===========================================");
        System.out.println("     TARUMT FACILITIES MANAGEMENT SYSTEM   ");
        System.out.println("===========================================");
        System.out.println(" 1. Manage Users");
        System.out.println(" 2. Manage Rooms");
        System.out.println(" 3. Room Booking Services");
        System.out.println(" 0. Exit");
        System.out.println("===========================================");
    }
}
