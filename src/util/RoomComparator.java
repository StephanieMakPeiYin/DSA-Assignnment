package util;

import entity.Room;
import java.util.Comparator;

/**
 * Comparator for sorting rooms by location (block) and then by capacity category.
 * Order: Block A, B, C
 * Capacity Categories: Small (10-15), Medium (16-22), Large (23-30), Extra Large (31+)
 */
public class RoomComparator implements Comparator<Room> {

    @Override
    public int compare(Room room1, Room room2) {
        // Extract block from location (e.g., "Block A, Floor 1, Room 01" -> "A")
        String block1 = extractBlock(room1.getLocation());
        String block2 = extractBlock(room2.getLocation());

        // Compare blocks alphabetically (A, B, C, ...)
        int blockComparison = block1.compareTo(block2);
        if (blockComparison != 0) {
            return blockComparison;
        }

        // If same block, sort by capacity category
        return getCapacityCategory(room1.getCapacity()) - getCapacityCategory(room2.getCapacity());
    }

    /**
     * Extracts the block letter from a location string.
     * Example: "Block A, Floor 1, Room 01" -> "A"
     */
    private String extractBlock(String location) {
        if (location == null || location.isEmpty()) {
            return "";
        }
        // Look for "Block X" pattern
        int blockIndex = location.indexOf("Block ");
        if (blockIndex >= 0) {
            int letterIndex = blockIndex + 6; // "Block ".length()
            if (letterIndex < location.length()) {
                return String.valueOf(location.charAt(letterIndex));
            }
        }
        return "";
    }

    /**
     * Categorizes capacity into groups.
     * Small (10-15): 1
     * Medium (16-22): 2
     * Large (23-30): 3
     * Extra Large (31+): 4
     */
    private int getCapacityCategory(int capacity) {
        if (capacity >= 10 && capacity <= 15) {
            return 1;
        } else if (capacity >= 16 && capacity <= 22) {
            return 2;
        } else if (capacity >= 23 && capacity <= 30) {
            return 3;
        } else if (capacity >= 31) {
            return 4;
        }
        return 0; // Less than 10
    }
}
