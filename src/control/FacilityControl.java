package control;

import adt.ArrayListADT;
import adt.ListInterface;
import entity.Room;

public class FacilityControl {

    // --- Status Codes for Add Operation ---
    public static final int ADD_OK = 0;
    public static final int ADD_DUPLICATE_ID = 1;

    // --- Status Codes for Remove/Update Operations ---
    public static final int OPERATION_OK = 0;
    public static final int FACILITY_NOT_FOUND = 1;

    // The data structure holding all facilities
    private ListInterface<Room> facilityList = new ArrayListADT<>();

    // Optional: Pre-load some data for testing
    public FacilityControl() {
        addFacility("Meeting Room 1", 10, "A", 1, 1);
        addFacility("Main Lecture Hall", 150, "B", 2, 10);
    }

    /**
     * Helper Method: Normalizes the input ID to ensure consistent searching
     */
    private String normalizeFacilityId(String id) {
        return id == null ? "" : id.trim().toUpperCase();
    }

    private String normalizeBlock(String block) {
        return block == null ? "" : block.trim().toUpperCase();
    }

    private String buildFacilityId(String block, int floor, int roomNumber) {
        return normalizeBlock(block) + floor + String.format("%02d", roomNumber);
    }

    private String buildLocation(String block, int floor, int roomNumber) {
        return "Block " + normalizeBlock(block) + ", Floor " + floor + ", Room " + roomNumber;
    }

    /**
     * Core Function 4: Search Facility
     * Finds and returns a Room object based on its generated ID.
     */
    public Room searchFacility(String facilityId) {
        String key = normalizeFacilityId(facilityId);
        if (key.isEmpty()) {
            return null;
        }

        for (int i = 1; i <= facilityList.getLength(); i++) {
            Room f = facilityList.getEntry(i);
            if (normalizeFacilityId(f.getRoomID()).equals(key)) {
                return f;
            }
        }
        return null; // Not found
    }

    /**
     * Core Function 1: Add Facility
     * Creates a new Facility. If the generated ID already exists, it rejects it.
     */
    public int addFacility(String name, int capacity, String block, int floor, int roomNumber) {
        String generatedId = buildFacilityId(block, floor, roomNumber);

        if (searchFacility(generatedId) != null) {
            return ADD_DUPLICATE_ID;
        }

        String location = buildLocation(block, floor, roomNumber);
        String cleanName = (name == null) ? "" : name.trim();
        Room newFacility = new Room(generatedId, cleanName, capacity, location);
        facilityList.add(newFacility);
        return ADD_OK;
    }

    /**
     * Core Function 2: Remove Facility
     * Searches for a facility by ID and removes it from the list.
     */
    public int removeFacility(String facilityId) {
        Room f = searchFacility(facilityId);
        if (f == null) {
            return FACILITY_NOT_FOUND;
        }

        facilityList.remove(f);
        return OPERATION_OK;
    }

    /**
     * Core Function 3: Update Facility Details
     * Finds an existing facility and overwrites its details.
     * Note: Facility ID is kept intact.
     */
    public int updateFacility(String facilityId, String newName, int newCapacity, String newBlock, int newFloor,
                              int newRoomNumber) {
        Room f = searchFacility(facilityId);
        if (f == null) {
            return FACILITY_NOT_FOUND;
        }

        String cleanName = (newName == null) ? "" : newName.trim();
        f.setCapacity(newCapacity);
        String location = buildLocation(newBlock, newFloor, newRoomNumber);
        f.setName(cleanName);
        f.setLocation(location);

        return OPERATION_OK;
    }

    /**
     * Core Function 5: Display Facility List
     * Loops through the ADT list and prints all facilities in a structured format.
     */
    public void displayFacilityList() {
        if (facilityList.isEmpty()) {
            System.out.println("No facilities are currently registered in the system.");
            return;
        }

        System.out.println("\n--- All Registered Facilities ---");
        for (int i = 1; i <= facilityList.getLength(); i++) {
            System.out.println(facilityList.getEntry(i));
        }
    }

    /**
     * Room iteration helpers (used by BookingControl to list/select rooms).
     * The ADT uses 1-based indexing for positions.
     */
    public int getRoomsLength() {
        return facilityList.getLength();
    }

    public Room getRoomAt(int position) {
        return facilityList.getEntry(position);
    }

    /**
     * Utility Method: To check if a facility exists (useful for other modules)
     */
    public boolean facilityExists(String facilityId) {
        return searchFacility(facilityId) != null;
    }
}
