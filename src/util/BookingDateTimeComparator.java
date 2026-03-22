package util;

import entity.Booking;
import java.util.Comparator;

public class BookingDateTimeComparator implements Comparator<Booking> {

    @Override
    public int compare(Booking b1, Booking b2) {

        int dateCompare = b1.getDate().compareTo(b2.getDate());

        if (dateCompare != 0) {
            return dateCompare;
        }

        return b1.getTimeSlot().compareTo(b2.getTimeSlot());
    }
}
