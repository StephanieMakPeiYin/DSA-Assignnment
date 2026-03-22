package entity;

public class Room {
    private String roomID;
    private String location;
    private int capacity;

    public Room(String roomID, String location, int capacity) {
        this.roomID = roomID;
        this.location = location;
        this.capacity = capacity;
    }

    public String getRoomID() { return roomID; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }

    @Override
    public String toString() {
        return roomID + " | " + location + " | Capacity: " + capacity;
    }
}
