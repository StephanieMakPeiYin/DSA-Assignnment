package control;

import adt.ArrayListADT;
import adt.ListInterface;
import entity.Booking;
import entity.Room;
import util.BookingInputValidator;
import util.BookingDateTimeComparator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingControl {

    /**
     * Maximum days in the future for booking (users can only book for the next 3 days).
     */
    public static final int MAX_BOOKING_DAYS_AHEAD = 3;

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

    public int bookRoom(String roomID, String date, String timeSlot) {
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
        bookingList.add(new Booking(bookingID, canonicalRoomId, d, t));
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
        return CANCEL_OK;
    }

    public boolean bookingExists(String bookingID) {
        return findBookingById(bookingID) != null;
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

    public int countActiveBookings() {
        int count = 0;
        for (int i = 1; i <= bookingList.getLength(); i++) {
            if ("ACTIVE".equalsIgnoreCase(bookingList.getEntry(i).getStatus())) {
                count++;
            }
        }
        return count;
    }

    private boolean matchesStatusFilter(Booking b, String filter) {
        if (filter == null || "ALL".equalsIgnoreCase(filter)) {
            return true;
        }
        return filter.equalsIgnoreCase(b.getStatus());
    }

    public void displayBookings(String statusFilter) {
        int count = countMatching(statusFilter);
        if (count == 0) {
            System.out.println(emptyFilterMessage(statusFilter));
            return;
        }
        System.out.println("\n--- Bookings (" + filterLabel(statusFilter) + ") ---");
        if ("ACTIVE".equalsIgnoreCase(statusFilter)) {
            System.out.println("ID | Room | Date | Time | Status");
        } else {
            System.out.println("ID | Room | Date | Time | Status | (Reason if cancelled)");
        }
        for (int i = 1; i <= bookingList.getLength(); i++) {
            Booking b = bookingList.getEntry(i);
            if (matchesStatusFilter(b, statusFilter)) {
                System.out.println(b);
            }
        }
    }

    private int countMatching(String statusFilter) {
        int n = 0;
        for (int i = 1; i <= bookingList.getLength(); i++) {
            if (matchesStatusFilter(bookingList.getEntry(i), statusFilter)) {
                n++;
            }
        }
        return n;
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
        int len = facilityControl.getRoomsLength();
        if (len == 0) {
            System.out.println("No rooms in the system.");
            return;
        }
        System.out.println("\n--- All rooms ---");
        System.out.println("Room ID | Location | Capacity");
        for (int i = 1; i <= len; i++) {
            System.out.println(facilityControl.getRoomAt(i));
        }
    }

    public void filterAvailableRooms(String isoDate, String timeSlot) {
        String d = isoDate == null ? "" : isoDate.trim();
        String t = timeSlot == null ? "" : timeSlot.trim();

        System.out.println("\n--- Rooms available for this date & time ---");
        int shown = 0;
        int len = facilityControl.getRoomsLength();
        for (int i = 1; i <= len; i++) {
            Room room = facilityControl.getRoomAt(i);
            boolean isBooked = false;
            for (int j = 1; j <= bookingList.getLength(); j++) {
                Booking b = bookingList.getEntry(j);
                if (b.getRoomID().equalsIgnoreCase(room.getRoomID())
                        && b.getDate().equals(d)
                        && b.getTimeSlot().equals(t)
                        && "ACTIVE".equals(b.getStatus())) {
                    isBooked = true;
                    break;
                }
            }
            if (!isBooked) {
                System.out.println(room);
                shown++;
            }
        }
        if (shown == 0) {
            System.out.println("No rooms are free for that slot (all booked or no rooms).");
        }
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
        return removed ? REMOVE_OK : REMOVE_NOT_FOUND;
    }

    /**
     * Uses ADT sort() method to sort all bookings by date and time
     * using the BookingDateTimeComparator, then displays them.
     */
    public void displaySortedBookings(String statusFilter) {
        // Filter bookings into a temporary list
        ListInterface<Booking> filteredList = new ArrayListADT<>();
        for (int i = 1; i <= bookingList.getLength(); i++) {
            Booking b = bookingList.getEntry(i);
            if (matchesStatusFilter(b, statusFilter)) {
                filteredList.add(b);
            }
        }

        if (filteredList.isEmpty()) {
            System.out.println(emptyFilterMessage(statusFilter));
            return;
        }

        // Sort the filtered list using ADT sort() with BookingDateTimeComparator
        filteredList.sort(new BookingDateTimeComparator());

        System.out.println("\n--- Bookings (" + filterLabel(statusFilter) + ") - Sorted by Date & Time ---");
        if ("ACTIVE".equalsIgnoreCase(statusFilter)) {
            System.out.println("ID | Room | Date | Time | Status");
        } else {
            System.out.println("ID | Room | Date | Time | Status | (Reason if cancelled)");
        }
        for (int i = 1; i <= filteredList.getLength(); i++) {
            System.out.println(filteredList.getEntry(i));
        }
    }

    /**
     * Uses ADT remove() method to permanently delete all bookings with past dates.
     * Useful for cleaning up old data. Returns the count of removed bookings.
     */
    public int removeExpiredBookings() {
        LocalDate today = LocalDate.now();
        int removedCount = 0;

        // Iterate through the list and remove past bookings
        for (int i = bookingList.getLength(); i >= 1; i--) {
            Booking b = bookingList.getEntry(i);
            LocalDate bookingDate = BookingInputValidator.parseBookingDate(b.getDate());
            if (bookingDate != null && bookingDate.isBefore(today)) {
                bookingList.remove(i);
                removedCount++;
            }
        }

        return removedCount;
    }

    /**
     * Uses ADT contains() method to check if a booking exists in the list.
     * More efficient existence check using ADT's built-in method.
     */
    public boolean bookingExistsInList(Booking booking) {
        return bookingList.contains(booking);
    }

    /**
     * Get all bookings as a list
     */
    public java.util.List<Booking> getAllBookings() {
        java.util.List<Booking> allBookings = new java.util.ArrayList<>();
        for (int i = 1; i <= bookingList.getLength(); i++) {
            allBookings.add(bookingList.getEntry(i));
        }
        return allBookings;
    }

    /**
     * Get bookings for a specific date
     */
    public java.util.List<Booking> getBookingsByDate(String date) {
        java.util.List<Booking> bookingsForDate = new java.util.ArrayList<>();
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
}

