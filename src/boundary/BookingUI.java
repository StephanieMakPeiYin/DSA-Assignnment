package boundary;

import control.BookingControl;
import control.FacilityControl;
import util.BookingInputValidator;
import util.ConsoleColors;

import java.time.LocalDate;
import java.util.Scanner;

public class BookingUI {

    private final BookingControl control;
    private final Scanner scanner = new Scanner(System.in);

    public BookingUI() {
        this(new FacilityControl());
    }

    public BookingUI(FacilityControl facilityControl) {
        this.control = new BookingControl(facilityControl);
    }

    public void start() {
        int choice;
        do {
            printMainBookingMenu();
            choice = readMenuChoice(0, 4);

            switch (choice) {
                case 1 -> roomsSubMenu();
                case 2 -> bookRoom();
                case 3 -> cancelBooking();
                case 4 -> bookingsSubMenu();
                case 0 -> System.out.println("Returning to main menu...");
            }
        } while (choice != 0);
    }

    private void printMainBookingMenu() {
        System.out.println("\n========== BOOKING MODULE ==========");
        System.out.println("1. View Rooms");
        System.out.println("2. Book Room");
        System.out.println("3. Cancel Booking");
        System.out.println("4. View Bookings");
        System.out.println("0. Back to Main Menu");
        System.out.println("--------------------------------------");
    }

    private void roomsSubMenu() {
        int sub;
        do {
            System.out.println("\n--- Rooms ---");
            System.out.println("1. View all rooms");
            System.out.println("2. Filter available rooms (by date & time)");
            System.out.println("0. Back");
            sub = readMenuChoice(0, 2);
            switch (sub) {
                case 1 -> {
                    control.displayRooms();
                    pause();
                }
                case 2 -> filterAvailableRooms();
                case 0 -> { /* back */ }
            }
        } while (sub != 0);
    }

    private void bookingsSubMenu() {
        int sub;
        do {
            System.out.println("\n--- View Bookings ---");
            System.out.println("Which bookings to show?");
            System.out.println("1. All");
            System.out.println("2. Active only");
            System.out.println("3. Cancelled only");
            System.out.println("0. Back");
            sub = readMenuChoice(0, 3);
            switch (sub) {
                case 1 -> {
                    control.displayBookings("ALL");
                    pause();
                }
                case 2 -> {
                    control.displayBookings("ACTIVE");
                    pause();
                }
                case 3 -> {
                    control.displayBookings("CANCELLED");
                    pause();
                }
                case 0 -> { /* back */ }
            }
        } while (sub != 0);
    }


    private int readMenuChoice(int min, int max) {
        while (true) {
            System.out.print("Enter choice (" + min + "-" + max + "): ");
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Input cannot be empty. Try again.");
                    continue;
                }
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid whole number. Try again.");
            }
        }
    }

    private void filterAvailableRooms() {
        System.out.println("\n--- Filter available rooms ---");
        System.out.println("Date must be " + BookingInputValidator.DATE_PATTERN + " (e.g. 2025-06-15).");
        System.out.println("Time slot must be like 09:00-11:00 or 14:30-16:00 (24-hour, start before end).");

        LocalDate date = readValidBookingDate("Date: ", true);
        String timeSlot = readValidTimeSlot("Time slot: ");

        String iso = BookingInputValidator.formatDate(date);
        String normalizedTime = BookingInputValidator.normalizeTimeSlot(timeSlot);
        control.filterAvailableRooms(iso, normalizedTime);
        pause();
    }

    private void bookRoom() {
        System.out.println("\n--- New booking ---");

        control.displayRooms();
        String roomID = readExistingRoomId("Room ID (e.g. A101): ");

        LocalDate date = readValidBookingDate("Date (yyyy-MM-dd e.g. 2026-03-25): ", false);
        String timeSlot = readValidTimeSlot("Time slot (H:mm-H:mm e.g. 9:00-11:00): ");

        int result = control.bookRoom(roomID, BookingInputValidator.formatDate(date), timeSlot);
        switch (result) {
            case BookingControl.BOOK_OK ->
                System.out.println(ConsoleColors.success("Booking successful. Your booking is saved as ACTIVE."));
            case BookingControl.BOOK_SLOT_TAKEN ->
                System.out.println("That room is already booked for this date and time.");
            case BookingControl.BOOK_INVALID_DATE ->
                System.out.println("Invalid date (internal). Use yyyy-MM-dd.");
            case BookingControl.BOOK_INVALID_TIME ->
                System.out.println("Invalid time slot (internal).");
            case BookingControl.BOOK_DATE_IN_PAST ->
                System.out.println("Booking date cannot be today or in the past.");
            case BookingControl.BOOK_OUTSIDE_BOOKING_WINDOW ->
                System.out.println("Bookings are only available for the next 3 days.");
            default ->
                System.out.println("Could not complete booking.");
        }
        pause();
    }

    /**
     * Rejects unknown room IDs before asking for date/time.
     */
    private String readExistingRoomId(String prompt) {
        while (true) {
            String id = readRequiredLine(prompt);
            if (control.roomExists(id)) {
                return id;
            }
            System.out.println("Unknown room ID. Choose from the list above.");
        }
    }

    private LocalDate readValidBookingDate(String prompt, boolean allowPast) {
        while (true) {
            String raw = readRequiredLine(prompt);
            LocalDate d = BookingInputValidator.parseBookingDate(raw);
            if (d == null) {
                System.out.println("Invalid date. Use format yyyy-MM-dd (e.g. 2026-03-25).");
                continue;
            }
            if (!allowPast) {
                LocalDate today = LocalDate.now();
                if (d.isBefore(today) || d.isEqual(today)) {
                    System.out.println("Date cannot be today or in the past. Choose a future date.");
                    continue;
                }
                long daysFromToday = java.time.temporal.ChronoUnit.DAYS.between(today, d);
                if (daysFromToday > BookingControl.MAX_BOOKING_DAYS_AHEAD) {
                    System.out.println("Bookings are only available for the next 3 days.");
                    continue;
                }
            }
            return d;
        }
    }

    private String readValidTimeSlot(String prompt) {
        while (true) {
            String raw = readRequiredLine(prompt);
            if (BookingInputValidator.isValidTimeSlot(raw)) {
                return BookingInputValidator.normalizeTimeSlot(raw);
            }
            System.out.println("Invalid time slot. Use 24-hour format H:mm-H:mm with start before end (e.g. 09:00-11:00).");
        }
    }

    private void cancelBooking() {
        System.out.println("\n--- Cancel booking ---");
        
        int activeCount = control.countActiveBookings();
        if (activeCount == 0) {
            System.out.println("No active bookings recorded yet.");
            pause();
            return;
        }
        
        control.displayBookings("ACTIVE");
        
        String id;
        while (true) {
            id = readRequiredLine("Enter booking ID to cancel (e.g. B1, or 0 to return): ");
            
            if (id.equals("0")) {
                System.out.println("Returning to menu...");
                return;
            }
            
            int eligible = control.getCancelEligibility(id);
            if (eligible == BookingControl.CANCEL_OK) {
                break;
            }
            if (eligible == BookingControl.CANCEL_NOT_FOUND) {
                System.out.println("No booking with that ID. Please try again.");
                continue;
            }
            printCancelRejection(eligible);
            return;
        }

        if (!readConfirmation("Are you sure you want to cancel this booking? (y/n): ")) {
            System.out.println("Cancellation aborted.");
            pause();
            return;
        }

        String reason = readCancelReason();
        int result = control.cancelBooking(id, reason);
        if (result == BookingControl.CANCEL_OK) {
            System.out.println(ConsoleColors.success("Booking cancelled successfully."));
        } else {
            System.out.println("Could not cancel (please try again). Code: " + result);
        }
        pause();
    }


    private String readCancelReason() {
        System.out.println("\nReason for cancellation:");
        System.out.println("1. Schedule conflict");
        System.out.println("2. Room no longer needed");
        System.out.println("3. Wrong date or time booked");
        System.out.println("4. Found another venue");
        System.out.println("5. Others (type your own)");
        int c = readMenuChoice(1, 5);
        return switch (c) {
            case 1 -> "Schedule conflict";
            case 2 -> "Room no longer needed";
            case 3 -> "Wrong date or time booked";
            case 4 -> "Found another venue";
            case 5 -> readRequiredLine("Describe your reason: ");
            default -> "Not specified";
        };
    }

    private void printCancelRejection(int code) {
        switch (code) {
            case BookingControl.CANCEL_NOT_FOUND ->
                System.out.println("No booking with that ID.");
            case BookingControl.CANCEL_NOT_ACTIVE ->
                System.out.println("That booking is not active (already cancelled).");
            case BookingControl.CANCEL_PAST_BOOKING ->
                System.out.println("That booking date has already passed.");
            case BookingControl.CANCEL_INVALID_STORED_DATE ->
                System.out.println("This booking has an invalid stored date; contact support.");
            default ->
                System.out.println("Cannot cancel this booking.");
        }
    }

    private boolean readConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null) {
                return false;
            }
            String s = line.trim().toLowerCase();
            if (s.equals("y") || s.equals("yes")) {
                return true;
            }
            if (s.equals("n") || s.equals("no")) {
                return false;
            }
            System.out.println("Please type y or n (or yes / no).");
        }
    }

    private String readRequiredLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null) {
                System.out.println("End of input.");
                return "";
            }
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    private void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
