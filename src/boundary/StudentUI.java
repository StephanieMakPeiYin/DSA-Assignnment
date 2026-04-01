package boundary;

import control.BookingControl;
import control.FacilityControl;
import util.ConsoleColors;
import java.util.Scanner;

public class StudentUI {
    private final BookingUI bookingUI;
    private final Scanner scanner;
    private final String studentName;
    private final String studentEmail;

    public StudentUI(FacilityControl facilityControl) {
        this(facilityControl, "Unknown", "unknown@email.com");
    }

    public StudentUI(BookingControl bookingControl, String studentName, String studentEmail) {
        this.bookingUI = new BookingUI(bookingControl, studentName, studentEmail);
        this.scanner = new Scanner(System.in);
        this.studentName = studentName;
        this.studentEmail = studentEmail;
    }

    public StudentUI(FacilityControl facilityControl, String studentName, String studentEmail) {
        this(new BookingControl(facilityControl), studentName, studentEmail);
    }

    public void start() {
        int choice;
        do {
            printStudentMenu();
            choice = readMenuChoice(0, 1);

            switch (choice) {
                case 1:
                    System.out.println("\n[Room Booking Services]");
                    bookingUI.start();
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
