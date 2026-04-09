package util;

import adt.ArrayListADT;
import adt.ListInterface;
import entity.User;
import java.io.*;

public class UserFileManager {
    private static final String USER_FILE = "src/users.txt";
    private static final String DELIMITER = "|";

    /**
     * Load all users from the file into custom ADT list.
     */
    public static ListInterface<User> loadUsersADT() {
        ListInterface<User> users = new ArrayListADT<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String userID = parts[0].trim();
                    String name = parts[1].trim();
                    String email = parts[2].trim();
                    String password = parts[3].trim();
                    String userType = parts[4].trim();
                    String status = parts.length > 5 ? parts[5].trim() : "active";
                    users.add(new User(userID, name, email, password, userType, status));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Creating new user database...");
        } catch (IOException e) {
            System.err.println("Error loading users from file: " + e.getMessage());
        }

        return users;
    }

    /**
     * Save users from custom ADT list to file.
     */
    public static void saveUsersADT(ListInterface<User> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (int i = 1; i <= users.getLength(); i++) {
                User user = users.getEntry(i);
                if (user == null) {
                    continue;
                }
                String line = user.getUserID() + DELIMITER + user.getName() + DELIMITER + user.getEmail() + DELIMITER + user.getPassword() + DELIMITER + user.getUserType() + DELIMITER + user.getStatus();
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving users to file: " + e.getMessage());
        }
    }

}
