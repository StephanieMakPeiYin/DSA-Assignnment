package entity;

public class Room {
    private String roomID;
    private String name;
    private int capacity;
    private String location;
    private String equipment;
    private boolean isDeleted = false;
    private long deletionTime = 0;

    // Matches how BookingControl creates rooms: new Room(roomID, location, capacity)
    public Room(String roomID, String location, int capacity) {
        this.roomID = roomID;
        this.location = location;
        this.capacity = capacity;
        this.name = "Discussion Room";
        this.equipment = "Other";
    }

    // Convenience constructor if other modules want to store a separate name
    public Room(String roomID, String name, int capacity, String location, String equipment) {
        this.roomID = roomID;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
        this.equipment = equipment;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
        if (deleted) {
            deletionTime = System.currentTimeMillis();
        } else {
            deletionTime = 0;
        }
    }

    public long getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(long deletionTime) {
        this.deletionTime = deletionTime;
    }

    public boolean isBookable() {
        return !isDeleted;
    }

    public boolean shouldBeDeleted() {
        return isDeleted && deletionTime > 0 && System.currentTimeMillis() >= deletionTime + 3L * 24 * 60 * 60 * 1000;
    }

    @Override
    public String toString() {
        String namePart = (name == null || name.trim().isEmpty()) ? "" : (name.trim() + " - ");
        String equipmentPart = (equipment == null || equipment.trim().isEmpty()) ? "Other" : equipment.trim();
        return roomID + " | " + namePart + location + " | Capacity: " + capacity + " | Equipment: " + equipmentPart;
    }
}

