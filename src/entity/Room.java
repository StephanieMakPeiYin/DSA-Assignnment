package entity;

public class Room {
    private String roomID;
    private String name; // Optional display name (can be merged into location by controllers)
    private int capacity;
    private String location;

    // Matches how BookingControl creates rooms: new Room(roomID, location, capacity)
    public Room(String roomID, String location, int capacity) {
        this.roomID = roomID;
        this.location = location;
        this.capacity = capacity;
        this.name = "";
    }

    // Convenience constructor if other modules want to store a separate name
    public Room(String roomID, String name, int capacity, String location) {
        this.roomID = roomID;
        this.name = name;
        this.capacity = capacity;
        this.location = location;
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

    @Override
    public String toString() {
        String namePart = (name == null || name.trim().isEmpty()) ? "" : (name.trim() + " - ");
        return roomID + " | " + namePart + location + " | Capacity: " + capacity;
    }
}

