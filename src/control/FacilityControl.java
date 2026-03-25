package control;

import adt.ArrayListADT;
import adt.ListInterface;
import entity.Room;
import java.io.*;

public class FacilityControl {
    public static final String DEFAULT_ROOM_NAME = "Discussion Room";

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
        loadFacilitiesFromFile();
        if (facilityList.isEmpty()) {
            addFacility(10, "A", 1, 1, "Projector");
            addFacility(20, "B", 2, 10, "Computer x3");
        }
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
    public int addFacility(int capacity, String block, int floor, int roomNumber, String equipment) {
        String generatedId = buildFacilityId(block, floor, roomNumber);

        if (searchFacility(generatedId) != null) {
            return ADD_DUPLICATE_ID;
        }

        String location = buildLocation(block, floor, roomNumber);
        String cleanEquipment = (equipment == null || equipment.trim().isEmpty()) ? "Other" : equipment.trim();
        Room newFacility = new Room(generatedId, DEFAULT_ROOM_NAME, capacity, location, cleanEquipment);
        facilityList.add(newFacility);
        return ADD_OK;
    }

    /**
     * Core Function 2: Remove Facility
     * Marks the facility as deleted (soft delete). Students cannot book it anymore.
     * The facility will be permanently deleted after 3 days.
     */
    public int removeFacility(String facilityId) {
        Room f = searchFacility(facilityId);
        if (f == null) {
            return FACILITY_NOT_FOUND;
        }
        f.setDeleted(true);
        return OPERATION_OK;
    }

    /**
     * Core Function 3: Update Facility Details
     * Finds an existing facility and overwrites its details.
     * Note: Facility ID is kept intact.
     */
    public int updateFacility(String facilityId, int newCapacity, String newBlock, int newFloor,
                              int newRoomNumber, String newEquipment) {
        Room f = searchFacility(facilityId);
        if (f == null) {
            return FACILITY_NOT_FOUND;
        }

        f.setCapacity(newCapacity);
        String location = buildLocation(newBlock, newFloor, newRoomNumber);
        String cleanEquipment = (newEquipment == null || newEquipment.trim().isEmpty()) ? "Other" : newEquipment.trim();
        f.setName(DEFAULT_ROOM_NAME);
        f.setLocation(location);
        f.setEquipment(cleanEquipment);

        return OPERATION_OK;
    }

    /**
     * Core Function 5: Display Facility List
     * Loops through the ADT list and prints all active (non-deleted) facilities in a structured format.
     */
    public void displayFacilityList() {
        if (facilityList.isEmpty()) {
            System.out.println("No facilities are currently registered in the system.");
            return;
        }

        System.out.println("\n--- All Registered Facilities ---");
        for (int i = 1; i <= facilityList.getLength(); i++) {
            Room f = facilityList.getEntry(i);
            if (!f.isDeleted()) {
                System.out.println(f);
            }
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

    public ListInterface<Room> getBookableRooms() {
        ArrayListADT<Room> bookable = new ArrayListADT<>();
        for (int i = 1; i <= facilityList.getLength(); i++) {
            Room r = facilityList.getEntry(i);
            if (r.isBookable()) {
                bookable.add(r);
            }
        }
        return bookable;
    }

    /**
     * Utility Method: To check if a facility exists (useful for other modules)
     */
    public boolean facilityExists(String facilityId) {
        return searchFacility(facilityId) != null;
    }

    /**
     * Load facilities from file
     */
    private void loadFacilitiesFromFile() {
        File file = new File("src/facility.txt");
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    String roomID = parts[0].trim();
                    String name = parts[1].trim();
                    int capacity = Integer.parseInt(parts[2].trim());
                    String location = parts[3].trim();
                    String equipment = parts[4].trim();
                    Room room = new Room(roomID, name, capacity, location, equipment);
                    facilityList.add(room);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading facilities from file: " + e.getMessage());
        }
    }

    /**
     * Save facilities to file
     */
    public void saveFacilitiesToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("src/facility.txt"))) {
            for (int i = 1; i <= facilityList.getLength(); i++) {
                Room room = facilityList.getEntry(i);
                if (!room.isDeleted()) {  // Only save non-deleted facilities
                    writer.println(room.getRoomID() + "|" + room.getName() + "|" + room.getCapacity() + "|" + room.getLocation() + "|" + room.getEquipment());
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving facilities to file: " + e.getMessage());
        }
    }
}
