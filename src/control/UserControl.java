package control;

import adt.ArrayListADT;
import entity.User;
import java.util.ArrayList;
import java.util.List;
import util.UserFileManager;

public class UserControl {
    private ArrayListADT<User> userList;
    private int studentIDCounter;
    private int staffIDCounter;
    
    public UserControl() {
        refreshUserList();
    }

    private void refreshUserList() {
        this.userList = new ArrayListADT<>();
        this.studentIDCounter = 1;
        this.staffIDCounter = 1;
        loadUsersFromFile();
    }

    private void loadUsersFromFile() {
        List<User> loadedUsers = UserFileManager.loadUsers();
        for (User user : loadedUsers) {
            userList.add(user);
            updateCountersByUserID(user.getUserID());
        }
    }

    private void updateCountersByUserID(String userID) {
        if (userID == null || userID.length() < 4) {
            return;
        }

        try {
            int id = Integer.parseInt(userID.substring(3));
            if (userID.startsWith("STU") && id >= studentIDCounter) {
                studentIDCounter = id + 1;
            } else if (userID.startsWith("STA") && id >= staffIDCounter) {
                staffIDCounter = id + 1;
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed IDs while loading existing data.
        }
    }

    private String generateUserID(String userType) {
        if ("STUDENT".equals(userType)) {
            return String.format("STU%03d", studentIDCounter++);
        }
        if ("STAFF".equals(userType)) {
            return String.format("STA%03d", staffIDCounter++);
        }
        return "";
    }

    private List<User> toJavaList() {
        List<User> allUsers = new ArrayList<>();
        int total = userList.getLength();
        for (int i = 1; i <= total; i++) {
            User user = userList.getEntry(i);
            if (user != null) {
                allUsers.add(user);
            }
        }
        return allUsers;
    }

    /**
     * Get all users from the system
     */
    public List<User> getAllUsers() {
        refreshUserList();
        return toJavaList();
    }

    /**
     * Find a user by email (case-sensitive)
     */
    public User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        refreshUserList();
        List<User> allUsers = toJavaList();
        for (User user : allUsers) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Find a user by userID
     */
    public User findUserByID(String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            return null;
        }
        refreshUserList();
        List<User> allUsers = toJavaList();
        for (User user : allUsers) {
            if (user.getUserID() != null && user.getUserID().equalsIgnoreCase(userID)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Find a user by name (case-insensitive partial match)
     */
    public List<User> findUsersByName(String name) {
        List<User> results = new java.util.ArrayList<>();
        if (name == null || name.trim().isEmpty()) {
            return results;
        }
        refreshUserList();
        String searchName = name.trim().toLowerCase();
        List<User> allUsers = toJavaList();
        for (User user : allUsers) {
            if (user.getName() != null && user.getName().toLowerCase().contains(searchName)) {
                results.add(user);
            }
        }
        return results;
    }

    /**
     * Delete a user by email (case-sensitive)
     * Returns true if deletion was successful, false otherwise
     */
    public boolean deleteUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        refreshUserList();
        int total = userList.getLength();
        for (int i = 1; i <= total; i++) {
            User user = userList.getEntry(i);
            if (user != null && user.getEmail().equals(email)) {
                user.setStatus("removed");
                userList.replace(i, user);
                UserFileManager.saveUsers(toJavaList());
                return true;
            }
        }
        return false;
    }

    /**
     * Get total number of users
     */
    public int getTotalUsers() {
        refreshUserList();
        return userList.getLength();
    }

    /**
     * Add a new user to the system
     */
    public void addUser(User user) {
        if (user != null) {
            refreshUserList();
            if (user.getUserID() == null || user.getUserID().isEmpty()) {
                user.setUserID(generateUserID(user.getUserType()));
            }
            userList.add(user);
            UserFileManager.saveUsers(toJavaList());
        }
    }

    /**
     * Update an existing user (case-sensitive by email)
     * Returns true if update was successful, false otherwise
     */
    public boolean updateUser(User updatedUser) {
        if (updatedUser == null || updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            return false;
        }
        refreshUserList();
        int total = userList.getLength();
        for (int i = 1; i <= total; i++) {
            User existingUser = userList.getEntry(i);
            if (existingUser != null && existingUser.getEmail().equals(updatedUser.getEmail())) {
                userList.replace(i, updatedUser);
                UserFileManager.saveUsers(toJavaList());
                return true;
            }
        }
        return false;
    }
}
