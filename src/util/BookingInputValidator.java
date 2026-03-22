package util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates and normalizes booking date (ISO yyyy-MM-dd) and time slot (start-end, 24h).
 */
public final class BookingInputValidator {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    private static final DateTimeFormatter TIME_HM = DateTimeFormatter.ofPattern("H:mm");

    private static final Pattern TIME_SLOT = Pattern.compile(
            "^\\s*(.+)\\s*-\\s*(.+)\\s*$");

    private BookingInputValidator() {
    }

    /**
     * Parses a booking date; must be strict ISO {@code yyyy-MM-dd}.
     */
    public static LocalDate parseBookingDate(String input) {
        if (input == null) {
            return null;
        }
        String s = input.trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(s, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Time slot: start-end in 24-hour form, e.g. {@code 9:00-11:00} or {@code 14:30-16:00}.
     * Start must be strictly before end.
     */
    public static boolean isValidTimeSlot(String input) {
        if (input == null) {
            return false;
        }
        Matcher m = TIME_SLOT.matcher(input.trim());
        if (!m.matches()) {
            return false;
        }
        try {
            LocalTime start = LocalTime.parse(m.group(1).trim(), TIME_HM);
            LocalTime end = LocalTime.parse(m.group(2).trim(), TIME_HM);
            return start.isBefore(end);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Normalizes to {@code HH:mm-HH:mm} (zero-padded hours where needed).
     */
    public static String normalizeTimeSlot(String input) {
        if (input == null) {
            return "";
        }
        Matcher m = TIME_SLOT.matcher(input.trim());
        if (!m.matches()) {
            return input.trim();
        }
        try {
            LocalTime start = LocalTime.parse(m.group(1).trim(), TIME_HM);
            LocalTime end = LocalTime.parse(m.group(2).trim(), TIME_HM);
            return String.format("%02d:%02d-%02d:%02d",
                    start.getHour(), start.getMinute(),
                    end.getHour(), end.getMinute());
        } catch (DateTimeParseException e) {
            return input.trim();
        }
    }

    public static String formatDate(LocalDate date) {
        return date == null ? "" : date.format(DATE_FORMAT);
    }
}
