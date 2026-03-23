package control;

import adt.ArrayListADT;
import adt.ListInterface;
import entity.Booking;
import entity.Room;
import util.BookingInputValidator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BookingControl {

    /**
     * Minimum whole calendar days from today until the booking date required to cancel
     * (e.g. 2 means you must cancel at least two days before the booking day).
     */
    public static final int MIN_DAYS_NOTICE_TO_CANCEL = 2;

    /** bookRoom: success */
    public static final int BOOK_OK = 0;
    public static final int BOOK_ROOM_NOT_FOUND = 1;
    public static final int BOOK_SLOT_TAKEN = 2;
    public static final int BOOK_INVALID_DATE = 3;
    public static final int BOOK_INVALID_TIME = 4;
    public static final int BOOK_DATE_IN_PAST = 5;

    public static final int CANCEL_OK = 0;
    public static final int CANCEL_NOT_FOUND = 1;
    public static final int CANCEL_NOT_ACTIVE = 2;
    public static final int CANCEL_TOO_LATE = 3;
    public static final int CANCEL_PAST_BOOKING = 4;
    public static final int CANCEL_INVALID_STORED_DATE = 5;

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
        if (parsedDate.isBefore(today)) {
            return BOOK_DATE_IN_PAST;
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
    public int cancelBooking(String bookingID, String cancelReason, LocalDate currentDate) {
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

        if (bookingDate.isBefore(currentDate)) {
            return CANCEL_PAST_BOOKING;
        }

        long daysUntil = ChronoUnit.DAYS.between(currentDate, bookingDate);
        if (daysUntil < MIN_DAYS_NOTICE_TO_CANCEL) {
            return CANCEL_TOO_LATE;
        }

        b.cancelWithReason(cancelReason);
        return CANCEL_OK;
    }

    public boolean bookingExists(String bookingID) {
        return findBookingById(bookingID) != null;
    }

    public int getCancelEligibility(String bookingID, LocalDate currentDate) {
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
        if (bookingDate.isBefore(currentDate)) {
            return CANCEL_PAST_BOOKING;
        }
        long daysUntil = ChronoUnit.DAYS.between(currentDate, bookingDate);
        if (daysUntil < MIN_DAYS_NOTICE_TO_CANCEL) {
            return CANCEL_TOO_LATE;
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
}
