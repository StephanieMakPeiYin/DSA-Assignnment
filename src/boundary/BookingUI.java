package boundary;

import control.BookingControl;
import control.FacilityControl;
import entity.Booking;
import util.BookingInputValidator;
import util.ConsoleColors;

import java.time.LocalDate;
import java.util.Scanner;

public class BookingUI {

    private final BookingControl control;
    private final Scanner scanner = new Scanner(System.in);
    private final String studentName;
    private final String studentEmail;

    public BookingUI() {
        this(new FacilityControl(), "Unknown", "unknown@email.com");
    }

    public BookingUI(FacilityControl facilityControl) {
        this(new BookingControl(facilityControl), "Unknown", "unknown@email.com");
    }

    public BookingUI(BookingControl bookingControl, String studentName, String studentEmail) {
        this.control = bookingControl;
        this.studentName = studentName == null ? "Unknown" : studentName;
        this.studentEmail = studentEmail == null ? "unknown@email.com" : studentEmail;
    }

    public BookingUI(FacilityControl facilityControl, String studentName, String studentEmail) {
        this(new BookingControl(facilityControl), studentName, studentEmail);
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
            System.out.println("2. Sort rooms (by location & capacity)");
            System.out.println("0. Back");
            sub = readMenuChoice(0, 2);
            switch (sub) {
                case 1 -> {
                    control.displayRooms();
                    pause();
                }
                case 2 -> viewSortedRoomsAndAvailability();
                case 0 -> { /* back */ }
            }
        } while (sub != 0);
    }

    /**
     * Displays sorted rooms by selected block and capacity, then asks if user wants to check availability.
     */
    private void viewSortedRoomsAndAvailability() {
        // Step 1: Ask for block location
        String block = readBlockSelection();

        // Step 2: Ask for capacity range
        int[] capacityRange = readCapacityRangeSelection();
        int minCapacity = capacityRange[0];
        int maxCapacity = capacityRange[1];

        // Step 3: Display sorted rooms by block and capacity (with equipment)
        control.displayRoomsByBlockAndCapacity(block, minCapacity, maxCapacity);

        // Step 4: Ask if user wants to check availability
        if (!readConfirmation("Do you want to view availability time & date for a room? (y/n): ")) {
            System.out.println("Returning to menu...");
            pause();
            return;
        }

        // User wants to see availability - ask for room ID
        String roomID = readExistingRoomId("Enter Room ID to view availability (e.g. A101): ");

        // Ask for date
        LocalDate date = readValidBookingDate("Enter date to check (yyyy-MM-dd e.g. 2026-03-25): ", false);
        String isoDate = BookingInputValidator.formatDate(date);

        // Display available slots
        control.displayAvailableSlots(roomID, isoDate);
        pause();
    }

    /**
     * Reads block selection from user (A, B, or C).
     */
    private String readBlockSelection() {
        while (true) {
            System.out.println("\nSelect block location:");
            System.out.println("1. Block A");
            System.out.println("2. Block B");
            System.out.println("3. Block C");
            System.out.print("Enter choice (1-3): ");
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Input cannot be empty. Try again.");
                    continue;
                }
                int choice = Integer.parseInt(line);
                switch (choice) {
                    case 1 -> {
                        System.out.println("Selected: Block A");
                        return "A";
                    }
                    case 2 -> {
                        System.out.println("Selected: Block B");
                        return "B";
                    }
                    case 3 -> {
                        System.out.println("Selected: Block C");
                        return "C";
                    }
                    default -> System.out.println("Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid number. Try again.");
            }
        }
    }

    /**
     * Reads capacity range selection from user.
     */
    private int[] readCapacityRangeSelection() {
        while (true) {
            System.out.println("\nSelect capacity range:");
            System.out.println("1. Small (10-15)");
            System.out.println("2. Medium (16-22)");
            System.out.println("3. Large (23-30)");
            System.out.println("4. Extra Large (31+)");
            System.out.print("Enter choice (1-4): ");
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Input cannot be empty. Try again.");
                    continue;
                }
                int choice = Integer.parseInt(line);
                switch (choice) {
                    case 1 -> {
                        System.out.println("Selected: Small (10-15)");
                        return new int[]{10, 15};
                    }
                    case 2 -> {
                        System.out.println("Selected: Medium (16-22)");
                        return new int[]{16, 22};
                    }
                    case 3 -> {
                        System.out.println("Selected: Large (23-30)");
                        return new int[]{23, 30};
                    }
                    case 4 -> {
                        System.out.println("Selected: Extra Large (31+)");
                        return new int[]{31, Integer.MAX_VALUE};
                    }
                    default -> System.out.println("Please enter a number between 1 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid number. Try again.");
            }
        }
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
                    control.displayBookingsForStudent(studentName, "ALL");
                    pause();
                }
                case 2 -> {
                    control.displayBookingsForStudent(studentName, "ACTIVE");
                    pause();
                }
                case 3 -> {
                    control.displayBookingsForStudent(studentName, "CANCELLED");
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

    private void bookRoom() {
        System.out.println("\n--- New Booking ---");
        displayBookingPolicy();

        int maxBookableCapacity = control.getMaximumBookableCapacity();
        if (maxBookableCapacity <= 0) {
            System.out.println("No rooms are currently available for booking.");
            pause();
            return;
        }

        // Step 1: Ask for date
        LocalDate bookingDate = readValidBookingDate("Enter booking date (yyyy-MM-dd e.g. 2026-03-25): ", false);
        String isoDate = BookingInputValidator.formatDate(bookingDate);

        // Step 2: Ask for capacity
        int requiredCapacity = readCapacity(
                "Enter required capacity (1-" + maxBookableCapacity + ", or 0 to return): ",
                maxBookableCapacity);
        if (requiredCapacity == 0) {
            System.out.println("Returning to menu...");
            return;
        }

        // Step 3: Show rooms that can support that capacity (sorted)
        control.displayRoomsByCapacity(requiredCapacity);

        // Step 4: User picks Room ID
        String roomID = readExistingRoomId("Enter Room ID to proceed (e.g. A101): ");

        // Step 5: Show available time slots for that date
        control.displayAvailableSlots(roomID, isoDate);

        // Get available slots for validation
        adt.ListInterface<String> availableSlots = control.getAvailableSlotsForRoom(roomID, isoDate);
        if (availableSlots.getLength() == 0) {
            System.out.println("No available time slots for this room on this date.");
            pause();
            return;
        }

        // Step 6: User selects a slot (can enter 1-5 or full slot format)
        String selectedSlot = readSelectedSlot("Select a time slot by number (1-5) or full format (e.g. 09:00-11:00): ", availableSlots);

        // Step 7: Confirm booking (show date, time, room id)
        System.out.println("\n========== BOOKING CONFIRMATION ==========");
        System.out.println("Room ID: " + roomID);
        System.out.println("Date: " + isoDate);
        System.out.println("Time: " + selectedSlot);
        System.out.println("==========================================");

        if (!readConfirmation("Confirm this booking? (y/n): ")) {
            System.out.println("Booking cancelled.");
            pause();
            return;
        }

        // Proceed with booking
        int result = control.bookRoom(roomID, isoDate, selectedSlot, studentName, studentEmail);
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
     * Displays the booking policy to users.
     */
    private void displayBookingPolicy() {
        System.out.println("\n========== BOOKING POLICY ==========");
        System.out.println("Bookings are only available for the next 3 days");
        System.out.println("Students must check in/out at the counter");
        System.out.println("Room booked will be forfeited after 10 minutes if no-show");
        System.out.println("====================================\n");
    }

    /**
     * Reads and validates the required capacity from user.
     */
    private int readCapacity(String prompt, int maxCapacity) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Capacity cannot be empty. Please try again.");
                    continue;
                }
                int capacity = Integer.parseInt(line);
                if (capacity == 0) {
                    return 0;
                }
                if (capacity < 0) {
                    System.out.println("Capacity cannot be negative. Please try again.");
                    continue;
                }
                if (capacity > maxCapacity) {
                    System.out.println("Invalid capacity. Maximum supported capacity is " + maxCapacity + ".");
                    continue;
                }
                return capacity;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number between 1 and " + maxCapacity + ", or 0 to return.");
            }
        }
    }

    /**
     * Reads and validates the selected time slot from user.
     * Accepts both number (1-5) and full slot format (09:00-11:00).
     * Ensures the selected slot is in the available slots list.
     */
    private String readSelectedSlot(String prompt, adt.ListInterface<String> availableSlots) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            // Try to convert number input to slot
            String selected = convertNumberToSlot(input);

            // Check if it's a valid preset slot
            if (!control.isValidPresetSlot(selected)) {
                System.out.println("That is not a valid time slot. Please select from the available slots.");
                continue;
            }

            // Check if it's in the available slots for this room/date
            boolean found = false;
            for (int i = 1; i <= availableSlots.getLength(); i++) {
                if (availableSlots.getEntry(i).equals(selected)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("That time slot is not available. Please select from the available slots.");
                continue;
            }

            return selected;
        }
    }

    /**
     * Converts number input (1-5) to full time slot format.
     * If input is not a number, assumes it's already in slot format.
     */
    private String convertNumberToSlot(String input) {
        try {
            int slotNum = Integer.parseInt(input.trim());
            if (slotNum >= 1 && slotNum <= 5) {
                return BookingControl.AVAILABLE_TIME_SLOTS[slotNum - 1];
            }
            return input; // Return as-is if out of range
        } catch (NumberFormatException e) {
            return input; // Not a number, return as-is
        }
    }

    /**
     * Rejects unknown room IDs before asking for date/time.
     */
    private String readExistingRoomId(String prompt) {
        while (true) {
            String id = readRequiredLine(prompt);
            String normalizedId = id.trim().toUpperCase();
            if (control.roomExists(normalizedId)) {
                return normalizedId;
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

    private void cancelBooking() {
        System.out.println("\n--- Cancel booking ---");

        // Check whether this student has any active bookings
        boolean hasActiveBooking = false;
        adt.ListInterface<Booking> allBookings = control.getAllBookingsADT();
        for (int i = 1; i <= allBookings.getLength(); i++) {
            Booking b = allBookings.getEntry(i);
            if (studentName.equalsIgnoreCase(b.getStudentUsername()) && "ACTIVE".equals(b.getStatus())) {
                hasActiveBooking = true;
                break;
            }
        }
        if (!hasActiveBooking) {
            System.out.println("No active bookings recorded yet.");
            pause();
            return;
        }

        // Show only this student's active bookings
        control.displayBookingsForStudent(studentName, "ACTIVE");

        String id;
        while (true) {
            id = readRequiredLine("Enter booking ID to cancel (e.g. B1, or 0 to return): ");

            if (id.equals("0")) {
                System.out.println("Returning to menu...");
                return;
            }

            // Verify the booking belongs to this student
            Booking target = control.findBookingById(id);
            if (target != null && !studentName.equalsIgnoreCase(target.getStudentUsername())) {
                System.out.println("That booking does not belong to you. Please try again.");
                continue;
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
