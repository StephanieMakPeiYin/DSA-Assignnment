package boundary;

import control.FacilityControl;
import entity.Room;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacilityUI {

    private final FacilityControl control;
    private final Scanner scanner = new Scanner(System.in);

    public FacilityUI() {
        this(new FacilityControl());
    }

    public FacilityUI(FacilityControl control) {
        this.control = control == null ? new FacilityControl() : control;
    }

    public void start() {
        int choice;
        do {
            printMainFacilityMenu();
            choice = readMenuChoice(0, 5);

            switch (choice) {
                case 1 -> displayRooms();
                case 2 -> searchRoom();
                case 3 -> addRoom();
                case 4 -> updateRoom();
                case 5 -> removeRoom();
                case 0 -> System.out.println("Returning to main menu...");
            }
        } while (choice != 0);
    }

    private void printMainFacilityMenu() {
        System.out.println("\n========== ROOM MANAGEMENT ==========");
        System.out.println("1. Display all rooms");
        System.out.println("2. Search for a room");
        System.out.println("3. Add new room");
        System.out.println("4. Update room details");
        System.out.println("5. Remove a room");
        System.out.println("0. Back to Main Menu");
        System.out.println("------------------------------------");
    }

    // --- Core Function 1: Display ---
    private void displayRooms() {
        control.displayFacilityList();
        pause();
    }

    // --- Core Function 2: Search ---
    private void searchRoom() {
        System.out.println("\n--- Search Room ---");
        String id = readRequiredLine("Enter Room ID to search (e.g., A101, B210): ");

        Room r = control.searchFacility(id);
        if (r != null) {
            System.out.println("\nRoom Found:");
            System.out.println(r.toString());
        } else {
            System.out.println("No room found with ID: " + id.toUpperCase());
        }
        pause();
    }

    // --- Core Function 3: Add ---
    private void addRoom() {
        System.out.println("\n--- Add New Room ---");

        String name = readRequiredLine("Enter Room Name (e.g., Meeting Room A): ");
        int capacity = readValidInt("Enter Capacity (1-30): ", 1, 30);
        String block = readRequiredLine("Enter Block (e.g., A, B, DK): ");
        int floor = readValidInt("Enter Floor (1-5): ", 1, 5);
        int roomNum = readValidInt("Enter Room Number (1-99): ", 1, 99);

        int result = control.addFacility(name, capacity, block, floor, roomNum);

        if (result == FacilityControl.ADD_OK) {
            System.out.println("Room successfully added to the system!");
        } else if (result == FacilityControl.ADD_DUPLICATE_ID) {
            System.out.println("Error: A room already exists at that exact Block, Floor, and Room.");
        } else {
            System.out.println("Could not add the room.");
        }
        pause();
    }

    // --- Core Function 4: Update ---
    private void updateRoom() {
        System.out.println("\n--- Update Room Details ---");
        String id = readRequiredLine("Enter Room ID to update (e.g., A101, B210): ");

        Room existing = control.searchFacility(id);
        if (existing == null) {
            System.out.println("No room found with ID: " + id.toUpperCase());
            pause();
            return;
        }

        System.out.println("\nCurrent Details: " + existing.toString());
        System.out.println("Enter new details below:");

        String currentBlock = extractBlockFromLocation(existing.getLocation());
        int currentFloor = extractFloorFromLocation(existing.getLocation());
        int currentRoomNum = extractRoomNumFromLocation(existing.getLocation());

        String newName = readOptionalLine("Enter New Name (press Enter to keep [" + existing.getName() + "]): ", existing.getName());
        int newCapacity = readOptionalInt("Enter New Capacity (1-30) (press Enter to keep [" + existing.getCapacity() + "]): ", 1, 30, existing.getCapacity());
        String newBlock = readOptionalLine("Enter New Block (e.g., A, B, DK) (press Enter to keep [" + currentBlock + "]): ", currentBlock);
        int newFloor = readOptionalInt("Enter New Floor (1-5) (press Enter to keep [" + currentFloor + "]): ", 1, 5, currentFloor);
        int newRoomNum = readOptionalInt("Enter New Room Number (1-99) (press Enter to keep [" + currentRoomNum + "]): ", 1, 99, currentRoomNum);

        int result = control.updateFacility(id, newName, newCapacity, newBlock, newFloor, newRoomNum);

        if (result == FacilityControl.OPERATION_OK) {
            System.out.println("Room details updated successfully!");
        } else {
            System.out.println("Failed to update room.");
        }
        pause();
    }

    // --- Core Function 5: Remove ---
    private void removeRoom() {
        System.out.println("\n--- Remove Room ---");
        String id = readRequiredLine("Enter Room ID to remove (e.g., A101, B210): ");

        Room r = control.searchFacility(id);
        if (r == null) {
            System.out.println("No room found with ID: " + id.toUpperCase());
            pause();
            return;
        }

        System.out.println("\nRoom to remove: " + r.toString());
        if (!readConfirmation("Are you sure you want to PERMANENTLY delete this room? (y/n): ")) {
            System.out.println("Deletion aborted.");
            pause();
            return;
        }

        int result = control.removeFacility(id);
        if (result == FacilityControl.OPERATION_OK) {
            System.out.println("Room removed successfully.");
        } else {
            System.out.println("Failed to remove room.");
        }
        pause();
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    private int readMenuChoice(int min, int max) {
        return readValidInt("Enter choice (" + min + "-" + max + "): ", min, max);
    }

    private int readValidInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Input cannot be empty. Try again.");
                    continue;
                }
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid whole number. Try again.");
            }
        }
    }

    private String readOptionalLine(String prompt, String currentValue) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null) {
                return currentValue;
            }
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
            return currentValue;
        }
    }

    private int readOptionalInt(String prompt, int min, int max, int currentValue) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    return currentValue;
                }
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid whole number. Try again.");
            }
        }
    }

    private String readRequiredLine(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null) {
                System.out.println("End of input.");
                return "";
            }
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }

    private boolean readConfirmation(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();
            if (line == null) {
                return false;
            }
            String s = line.trim().toLowerCase();
            if (s.equals("y") || s.equals("yes")) {
                return true;
            }
            if (s.equals("n") || s.equals("no")) {
                return false;
            }
            System.out.println("Please type y or n (or yes / no).");
        }
    }

    private void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private static String extractBlockFromLocation(String location) {
        if (location == null) return "A";
        Pattern p = Pattern.compile("Block\\s+([A-Za-z]+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(location);
        if (m.find()) {
            return m.group(1).toUpperCase();
        }
        return "A";
    }

    private static int extractFloorFromLocation(String location) {
        if (location == null) return 1;
        Pattern p = Pattern.compile("Floor\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(location);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }

    private static int extractRoomNumFromLocation(String location) {
        if (location == null) return 1;
        Pattern p = Pattern.compile("Room\\s+(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(location);
        if (m.find()) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1;
    }
}

