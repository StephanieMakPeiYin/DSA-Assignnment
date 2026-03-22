package entity;

public class Booking {
    private String bookingID;
    private String roomID;
    private String date;
    private String timeSlot;
    private String status;
    private String cancelReason;

    public Booking(String bookingID, String roomID, String date, String timeSlot) {
        this.bookingID = bookingID;
        this.roomID = roomID;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = "ACTIVE";
        this.cancelReason = null;
    }

    public String getBookingID() {
        return bookingID;
    }

    public String getRoomID() {
        return roomID;
    }

    public String getDate() {
        return date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getStatus() {
        return status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void cancelWithReason(String reason) {
        this.status = "CANCELLED";
        String r = reason == null ? "" : reason.trim();
        this.cancelReason = r.isEmpty() ? "Not specified" : r;
    }

    @Override
    public String toString() {
        String base = bookingID + " | " + roomID + " | " + date + " | " + timeSlot + " | " + status;
        if ("CANCELLED".equals(status) && cancelReason != null && !cancelReason.isEmpty()) {
            return base + " | Reason: " + cancelReason;
        }
        return base;
    }
}
