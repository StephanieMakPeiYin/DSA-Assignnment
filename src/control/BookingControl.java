package control;

import adt.ArrayListADT;
import adt.ListInterface;
import entity.Booking;
import entity.Room;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import util.BookingInputValidator;
import util.RoomComparator;

public class BookingControl {

    /**
     * Maximum days in the future for booking (users can only book for the next 3 days).
     */
    public static final int MAX_BOOKING_DAYS_AHEAD = 3;

    /**
     * Predefined available time slots for all rooms.
     */
    public static final String[] AVAILABLE_TIME_SLOTS = {
        "09:00-11:00",
        "11:00-13:00",
        "13:00-15:00",
        "15:00-17:00",
        "17:00-19:00"
    };

    /** bookRoom: success */
    public static final int BOOK_OK = 0;
    public static final int BOOK_ROOM_NOT_FOUND = 1;
    public static final int BOOK_SLOT_TAKEN = 2;
    public static final int BOOK_INVALID_DATE = 3;
    public static final int BOOK_INVALID_TIME = 4;
    public static final int BOOK_DATE_IN_PAST = 5;
    public static final int BOOK_OUTSIDE_BOOKING_WINDOW = 6;

    public static final int CANCEL_OK = 0;
    public static final int CANCEL_NOT_FOUND = 1;
    public static final int CANCEL_NOT_ACTIVE = 2;
    public static final int CANCEL_PAST_BOOKING = 3;
    public static final int CANCEL_INVALID_STORED_DATE = 4;

    /** removeBooking: success */
    public static final int REMOVE_OK = 0;
    public static final int REMOVE_NOT_FOUND = 1;

    private ListInterface<Booking> bookingList = new ArrayListADT<>();
    private final FacilityControl facilityControl;

    private int idCounter = 1;

    public BookingControl() {
        this(new FacilityControl());
    }

    public BookingControl(FacilityControl facilityControl) {
        this.facilityControl = facilityControl == null ? new FacilityControl() : facilityControl;
        loadBookingsFromFile();
    }

    private String generateBookingID() {
        return "B" + (idCounter++);
    }

    public Room findRoomById(String roomID) {
        return facilityControl.searchFacility(roomID);
    }

    public Booking findBookingById(String bookingID) {
        String id = bookingID == null ? "" : bookingID.trim();
        if (id.isEmpty()) {
            return null;
        }
        for (int i = 1; i <= bookingList.getLength(); i++) {
            Booking b = bookingList.getEntry(i);
            if (b.getBookingID().equalsIgnoreCase(id)) {
                return b;
            }
        }
        return null;
    }

    public boolean roomExists(String roomID) {
        return facilityControl.facilityExists(roomID);
    }

    public int bookRoom(String roomID, String date, String timeSlot, String studentUsername, String studentEmail) {
        Room room = findRoomById(roomID);
        if (room == null) {
            return BOOK_ROOM_NOT_FOUND;
        }

        LocalDate parsedDate = BookingInputValidator.parseBookingDate(date);
        if (parsedDate == null) {
            return BOOK_INVALID_DATE;
        }
        if (!BookingInputValidator.isValidTimeSlot(timeSlot)) {
            return BOOK_INVALID_TIME;
        }

        LocalDate today = LocalDate.now();
        if (parsedDate.isBefore(today) || parsedDate.isEqual(today)) {
            return BOOK_DATE_IN_PAST;
        }

        long daysFromToday = ChronoUnit.DAYS.between(today, parsedDate);
        if (daysFromToday > MAX_BOOKING_DAYS_AHEAD) {
            return BOOK_OUTSIDE_BOOKING_WINDOW;
        }

        String d = BookingInputValidator.formatDate(parsedDate);
        String t = BookingInputValidator.normalizeTimeSlot(timeSlot);
        String canonicalRoomId = room.getRoomID();

        for (int i = 1; i <= bookingList.getLength(); i++) {
            Booking b = bookingList.getEntry(i);
            if (b.getRoomID().equalsIgnoreCase(canonicalRoomId)
                    && b.getDate().equals(d)
                    && b.getTimeSlot().equals(t)
                    && "ACTIVE".equals(b.getStatus())) {
                return BOOK_SLOT_TAKEN;
            }
        }

        String bookingID = generateBookingID();
        bookingList.add(new Booking(bookingID, canonicalRoomId, d, t, studentUsername, studentEmail));
        saveBookingsToFile();
        return BOOK_OK;
    }

    /**
     * Cancels with a reason; reason is stored on the booking when successful.
     */
    public int cancelBooking(String bookingID, String cancelReason) {
        Booking b = findBookingById(bookingID);
        if (b == null) {
            return CANCEL_NOT_FOUND;
        }
        if (!"ACTIVE".equals(b.getStatus())) {
            return CANCEL_NOT_ACTIVE;
        }

        LocalDate bookingDate = BookingInputValidator.parseBookingDate(b.getDate());
        if (bookingDate == null) {
            return CANCEL_INVALID_STORED_DATE;
        }

        LocalDate today = LocalDate.now();
        if (bookingDate.isBefore(today)) {
            return CANCEL_PAST_BOOKING;
        }

        b.cancelWithReason(cancelReason);
        saveBookingsToFile();
        return CANCEL_OK;
    }

    public int getCancelEligibility(String bookingID) {
        Booking b = findBookingById(bookingID);
        if (b == null) {
            return CANCEL_NOT_FOUND;
        }
        if (!"ACTIVE".equals(b.getStatus())) {
            return CANCEL_NOT_ACTIVE;
        }
        LocalDate bookingDate = BookingInputValidator.parseBookingDate(b.getDate());
        if (bookingDate == null) {
            return CANCEL_INVALID_STORED_DATE;
        }
        LocalDate today = LocalDate.now();
        if (bookingDate.isBefore(today)) {
            return CANCEL_PAST_BOOKING;
        }
        return CANCEL_OK;
    }

    private boolean matchesStatusFilter(Booking b, String filter) {
        if (filter == null || "ALL".equalsIgnoreCase(filter)) {
            return true;
        }
        return filter.equalsIgnoreCase(b.getStatus());
    }

    private boolean matchesStudentFilter(Booking b, String username) {
        if (username == null || username.isEmpty()) {
            return true;
        }
        return username.equalsIgnoreCase(b.getStudentUsername());
    }

    /**
     * Display bookings belonging to a specific student (filtered by username).
     */
    public void displayBookingsForStudent(String username, String statusFilter) {
        int count = 0;
        for (int i = 1; i <= bookingList.getLength(); i++) {
            Booking b = bookingList.getEntry(i);
            if (matchesStudentFilter(b, username) && matchesStatusFilter(b, statusFilter)) {
                count++;
            }
        }
        if (count == 0) {
            System.out.println(emptyFilterMessage(statusFilter));
            return;
        }
        System.out.println("\n--- Your Bookings (" + filterLabel(statusFilter) + ") ---");
        if ("ACTIVE".equalsIgnoreCase(statusFilter)) {
            System.out.println("ID | Room | Date | Time | Status");
        } else {
            System.out.println("ID | Room | Date | Time | Status | Cancellation Reason");
        }
        for (int i = 1; i <= bookingList.getLength(); i++) {
            Booking b = bookingList.getEntry(i);
            if (matchesStudentFilter(b, username) && matchesStatusFilter(b, statusFilter)) {
                System.out.println(b);
            }
        }
    }

    private static String filterLabel(String statusFilter) {
        if (statusFilter == null || "ALL".equalsIgnoreCase(statusFilter)) {
            return "all";
        }
        return statusFilter;
    }

    private String emptyFilterMessage(String statusFilter) {
        if (bookingList.isEmpty()) {
            return "No bookings recorded yet.";
        }
        if ("ACTIVE".equalsIgnoreCase(statusFilter)) {
            return "No active bookings.";
        }
        if ("CANCELLED".equalsIgnoreCase(statusFilter)) {
            return "No cancelled bookings.";
        }
        return "No bookings to show.";
    }

    public void displayRooms() {
        ListInterface<Room> bookableRooms = facilityControl.getBookableRooms();
        int len = bookableRooms.getLength();
        if (len == 0) {
            System.out.println("No rooms available for booking.");
            return;
        }
        System.out.println("\n--- Available rooms ---");
        System.out.println("Room ID | Location | Capacity");
        for (int i = 1; i <= len; i++) {
            System.out.println(bookableRooms.getEntry(i));
        }
    }

    /**
     * Displays available rooms that can support the given capacity.
     * Sorted by location and capacity. Shows Room ID, Location, Capacity, and Equipment.
     * Returns a list of filtered room IDs, or null if no rooms found.
     */
    public ListInterface<String> displayRoomsByCapacity(int requiredCapacity) {
        ListInterface<Room> bookableRooms = facilityControl.getBookableRooms();
        int len = bookableRooms.getLength();

        // Filter rooms by capacity and convert to array
        ArrayListADT<Room> filteredRooms = new ArrayListADT<>();
        for (int i = 1; i <= len; i++) {
            Room room = bookableRooms.getEntry(i);
            if (room.getCapacity() >= requiredCapacity) {
                filteredRooms.add(room);
            }
        }

        if (filteredRooms.getLength() == 0) {
            System.out.println("No rooms available with capacity " + requiredCapacity + " or more.");
            return null;
        }

        // Sort using ADT sort() with RoomComparator
        RoomComparator comparator = new RoomComparator();
        filteredRooms.sort(comparator);

        // Create a list of room IDs for validation later
        ListInterface<String> roomIds = new ArrayListADT<>();
        
        // Display filtered and sorted rooms with equipment (without category column)
        System.out.println("\n--- Rooms with capacity " + requiredCapacity + "+ ---");
        System.out.println("Room ID | Location | Capacity | Equipment");
        for (int i = 1; i <= filteredRooms.getLength(); i++) {
            Room room = filteredRooms.getEntry(i);
            System.out.println(room.getRoomID() + " | " + room.getLocation() + " | " + room.getCapacity() + " | " + room.getEquipment());
            roomIds.add(room.getRoomID());
        }
        return roomIds;
    }

    /**
     * Returns the maximum capacity among all currently bookable rooms.
     * Returns 0 when there are no bookable rooms.
     */
    public int getMaximumBookableCapacity() {
        ListInterface<Room> bookableRooms = facilityControl.getBookableRooms();
        int maxCapacity = 0;
        for (int i = 1; i <= bookableRooms.getLength(); i++) {
            Room room = bookableRooms.getEntry(i);
            if (room.getCapacity() > maxCapacity) {
                maxCapacity = room.getCapacity();
            }
        }
        return maxCapacity;
    }

    /**
     * Displays available rooms filtered by block location and capacity range.
     * Sorted by location and capacity. Shows Room ID, Location, Capacity, and Equipment.
     * Returns a list of filtered room IDs, or null if no rooms found.
     */
    public ListInterface<String> displayRoomsByBlockAndCapacity(String block, int minCapacity, int maxCapacity) {
        ListInterface<Room> bookableRooms = facilityControl.getBookableRooms();
        int len = bookableRooms.getLength();

        String blockLetter = block == null ? "" : block.trim().toUpperCase();

        // Filter rooms by block and capacity range
        ArrayListADT<Room> filteredRooms = new ArrayListADT<>();
        for (int i = 1; i <= len; i++) {
            Room room = bookableRooms.getEntry(i);
            // Check if room is in the selected block
            if (isRoomInBlock(room.getLocation(), blockLetter)) {
                // Check if room meets capacity range
                if (room.getCapacity() >= minCapacity && room.getCapacity() <= maxCapacity) {
                    filteredRooms.add(room);
                }
            }
        }

        if (filteredRooms.getLength() == 0) {
            System.out.println("\nNo rooms available in Block " + blockLetter + " with capacity " + minCapacity + "-" + (maxCapacity == Integer.MAX_VALUE ? "unlimited" : maxCapacity) + ".");
            return null;
        }

        // Sort using ADT sort() with RoomComparator
        RoomComparator comparator = new RoomComparator();
        filteredRooms.sort(comparator);

        // Create a list of room IDs for validation later
        ListInterface<String> roomIds = new ArrayListADT<>();
        
        // Display rooms with equipment (without category column)
        System.out.println("\n--- Rooms in Block " + blockLetter + " with capacity " + minCapacity + "-" + (maxCapacity == Integer.MAX_VALUE ? "unlimited" : maxCapacity) + " ---");
        System.out.println("Room ID | Location | Capacity | Equipment");
        for (int i = 1; i <= filteredRooms.getLength(); i++) {
            Room room = filteredRooms.getEntry(i);
            System.out.println(room.getRoomID() + " | " + room.getLocation() + " | " + room.getCapacity() + " | " + room.getEquipment());
            roomIds.add(room.getRoomID());
        }
        return roomIds;
    }

    /**
     * Helper method to check if a room location belongs to the given block.
     */
    private boolean isRoomInBlock(String location, String blockLetter) {
        if (location == null || location.isEmpty() || blockLetter == null || blockLetter.isEmpty()) {
            return false;
        }
        // Check if location contains "Block X" where X matches blockLetter
        String blockPattern = "BLOCK " + blockLetter;
        return location.toUpperCase().contains(blockPattern);
    }

    /**
     * Displays available time slots for a specific date and room.
     * Only shows slots that are not booked.
     */
    public void displayAvailableSlots(String roomID, String isoDate) {
        String d = isoDate == null ? "" : isoDate.trim();
        String normalizedRoomID = roomID == null ? "" : roomID.trim().toUpperCase();

        System.out.println("\n--- Available time slots for " + roomID + " on " + d + " ---");
        int available = 0;

        for (String slot : AVAILABLE_TIME_SLOTS) {
            boolean isBooked = false;
            for (int i = 1; i <= bookingList.getLength(); i++) {
                Booking b = bookingList.getEntry(i);
                if (b.getRoomID().equalsIgnoreCase(normalizedRoomID)
                        && b.getDate().equals(d)
                        && b.getTimeSlot().equals(slot)
                        && "ACTIVE".equals(b.getStatus())) {
                    isBooked = true;
                    break;
                }
            }
            
            if (!isBooked) {
                System.out.println((available + 1) + ". " + slot);
                available++;
            }
        }

        if (available == 0) {
            System.out.println("No available time slots for this room on this date.");
        }
    }

    /**
     * Checks if a given time slot is one of the predefined available slots.
     */
    public boolean isValidPresetSlot(String timeSlot) {
        String normalized = timeSlot == null ? "" : timeSlot.trim();
        for (String slot : AVAILABLE_TIME_SLOTS) {
            if (slot.equals(normalized)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of available time slots for a specific date and room.
     */
    public ListInterface<String> getAvailableSlotsForRoom(String roomID, String isoDate) {
        String d = isoDate == null ? "" : isoDate.trim();
        String normalizedRoomID = roomID == null ? "" : roomID.trim().toUpperCase();

        ListInterface<String> availableSlots = new ArrayListADT<>();

        for (String slot : AVAILABLE_TIME_SLOTS) {
            boolean isBooked = false;
            for (int i = 1; i <= bookingList.getLength(); i++) {
                Booking b = bookingList.getEntry(i);
                if (b.getRoomID().equalsIgnoreCase(normalizedRoomID)
                        && b.getDate().equals(d)
                        && b.getTimeSlot().equals(slot)
                        && "ACTIVE".equals(b.getStatus())) {
                    isBooked = true;
                    break;
                }
            }

            if (!isBooked) {
                availableSlots.add(slot);
            }
        }

        return availableSlots;
    }

    /**
     * Uses ADT remove() method to permanently delete a booking from the list.
     * Returns REMOVE_OK if successful, REMOVE_NOT_FOUND otherwise.
     */
    public int removeBooking(String bookingID) {
        Booking b = findBookingById(bookingID);
        if (b == null) {
            return REMOVE_NOT_FOUND;
        }
        boolean removed = bookingList.remove(b);
        if (removed) {
            saveBookingsToFile();
        }
        return removed ? REMOVE_OK : REMOVE_NOT_FOUND;
    }

    /**
     * Get all bookings as a list
     */
    public ListInterface<Booking> getAllBookingsADT() {
        ListInterface<Booking> allBookings = new ArrayListADT<>();
        for (int i = 1; i <= bookingList.getLength(); i++) {
            allBookings.add(bookingList.getEntry(i));
        }
        return allBookings;
    }

    /**
     * Get bookings for a specific date
     */
    public ListInterface<Booking> getBookingsByDateADT(String date) {
        ListInterface<Booking> bookingsForDate = new ArrayListADT<>();
        if (date == null || date.trim().isEmpty()) {
            return bookingsForDate;
        }
        for (int i = 1; i <= bookingList.getLength(); i++) {
            Booking b = bookingList.getEntry(i);
            if (b.getDate().equals(date)) {
                bookingsForDate.add(b);
            }
        }
        return bookingsForDate;
    }

    /**
     * Load bookings from file (bookings.txt)
     * File format: bookingID|roomID|date|timeSlot|status|cancelReason|studentUsername|studentEmail
     */
    private void loadBookingsFromFile() {
        java.io.File file = new java.io.File("src/bookings.txt");
        if (!file.exists()) {
            return;
        }
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String bookingID = parts[0].trim();
                    String roomID = parts[1].trim();
                    String date = parts[2].trim();
                    String timeSlot = parts[3].trim();
                    String status = parts[4].trim();
                    String cancelReason = parts.length > 5 ? parts[5].trim() : "";
                    String studentUsername = parts.length > 6 ? parts[6].trim() : "Unknown";
                    String studentEmail = parts.length > 7 ? parts[7].trim() : "Unknown";
                    
                    Booking booking = new Booking(bookingID, roomID, date, timeSlot, studentUsername, studentEmail);
                    booking.setStatus(status);
                    if ("CANCELLED".equals(status) && !cancelReason.isEmpty()) {
                        booking.cancelWithReason(cancelReason);
                    }
                    bookingList.add(booking);
                    
                    // Update idCounter based on loaded bookings
                    try {
                        int id = Integer.parseInt(bookingID.substring(1));
                        if (id >= idCounter) {
                            idCounter = id + 1;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Error loading bookings from file: " + e.getMessage());
        }
    }

    /**
     * Save bookings to file (bookings.txt)
     * File format: bookingID|roomID|date|timeSlot|status|cancelReason|studentUsername|studentEmail
     */
    public void saveBookingsToFile() {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter("src/bookings.txt"))) {
            for (int i = 1; i <= bookingList.getLength(); i++) {
                Booking booking = bookingList.getEntry(i);
                String cancelReason = booking.getCancelReason() != null ? booking.getCancelReason() : "";
                writer.println(booking.getBookingID() + "|" + booking.getRoomID() + "|" + 
                              booking.getDate() + "|" + booking.getTimeSlot() + "|" + 
                              booking.getStatus() + "|" + cancelReason + "|" + booking.getStudentUsername() + "|" + booking.getStudentEmail());
            }
        } catch (java.io.IOException e) {
            System.out.println("Error saving bookings to file: " + e.getMessage());
        }
    }
}

