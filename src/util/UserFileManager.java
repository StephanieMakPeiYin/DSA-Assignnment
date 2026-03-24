package util;

import entity.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserFileManager {
    private static final String USER_FILE = "users.txt";
    private static final String DELIMITER = "|";

    /**
     * Load all users from the file
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String name = parts[0].trim();
                    String email = parts[1].trim();
                    String password = parts[2].trim();
                    String userType = parts[3].trim();
                    users.add(new User(name, email, password, userType));
                }
            }
        } catch (FileNotFoundException e) {
            // File doesn't exist yet - first run
            System.out.println("Creating new user database...");
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }
        
        return users;
    }

    /**
     * Save all users to the file
     */
    public static void saveUsers(List<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                String line = user.getName() + DELIMITER + user.getEmail() + DELIMITER + user.getPassword() + DELIMITER + user.getUserType();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

    /**
     * Add a single user to the file (only if not already exists)
     */
    public static void addUserToFile(User user) {
        // Load existing users to check for duplicates
        List<User> existingUsers = loadUsers();
        
        // Check if user already exists (case-sensitive)
        for (User existingUser : existingUsers) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                return; // User already exists, don't add
            }
        }
        
        // User doesn't exist, append to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            String line = user.getName() + DELIMITER + user.getEmail() + DELIMITER + user.getPassword() + DELIMITER + user.getUserType();
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error adding user to file: " + e.getMessage());
        }
    }

    /**
     * Clear all users from the file
     */
    public static void clearUsersFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            // Writing nothing clears the file
        } catch (IOException e) {
            System.err.println("Error clearing users file: " + e.getMessage());
        }
    }
}
