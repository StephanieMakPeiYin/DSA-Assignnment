package adt;

import entity.User;
import util.UserFileManager;
import java.util.ArrayList;
import java.util.List;

public class UserArrayList implements UserListADT {
    private User[] users;
    private int size;
    private static final int INITIAL_CAPACITY = 100;
    private int studentIDCounter = 1;
    private int staffIDCounter = 1;

    public UserArrayList() {
        users = new User[INITIAL_CAPACITY];
        size = 0;
        loadUsersFromFile();
    }

    private void loadUsersFromFile() {
        List<User> loadedUsers = UserFileManager.loadUsers();
        for (User user : loadedUsers) {
            if (size >= users.length) {
                resize();
            }
            users[size++] = user;
            
            // Update ID counters based on loaded users
            String userID = user.getUserID();
            if (userID != null) {
                if (userID.startsWith("STU")) {
                    updateStudentCounter(userID);
                } else if (userID.startsWith("STA")) {
                    updateStaffCounter(userID);
                }
            }
        }
    }

    private void updateStudentCounter(String userID) {
        try {
            int id = Integer.parseInt(userID.substring(3));
            if (id >= studentIDCounter) {
                studentIDCounter = id + 1;
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed IDs and continue loading other records.
        }
    }

    private void updateStaffCounter(String userID) {
        try {
            int id = Integer.parseInt(userID.substring(3));
            if (id >= staffIDCounter) {
                staffIDCounter = id + 1;
            }
        } catch (NumberFormatException ignored) {
            // Ignore malformed IDs and continue loading other records.
        }
    }

    private String generateUserID(String userType) {
        if ("STUDENT".equals(userType)) {
            return String.format("STU%03d", studentIDCounter++);
        } else if ("STAFF".equals(userType)) {
            return String.format("STA%03d", staffIDCounter++);
        }
        return "";
    }

    @Override
    public void addUser(User user) {
        if (user.getUserID() == null || user.getUserID().isEmpty()) {
            user.setUserID(generateUserID(user.getUserType()));
        }
        if (size >= users.length) {
            resize();
        }
        users[size++] = user;
        UserFileManager.addUserToFile(user);
    }

    @Override
    public User findUserByEmail(String email) {
        for (int i = 0; i < size; i++) {
            if (users[i].getEmail().equalsIgnoreCase(email)) {
                return users[i];
            }
        }
        return null;
    }

    @Override
    public boolean userExists(String email) {
        return findUserByEmail(email) != null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            allUsers.add(users[i]);
        }
        return allUsers;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean deleteUser(String email) {
        for (int i = 0; i < size; i++) {
            if (users[i].getEmail().equals(email)) {
                // Soft delete: mark status as "removed" instead of physically deleting
                users[i].setStatus("removed");
                // Update file
                UserFileManager.saveUsers(getAllUsers());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateUser(User updatedUser) {
        if (updatedUser == null || updatedUser.getEmail() == null || updatedUser.getEmail().trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (users[i].getEmail().equals(updatedUser.getEmail())) {
                users[i] = updatedUser;
                // Update file
                UserFileManager.saveUsers(getAllUsers());
                return true;
            }
        }
        return false;
    }

    private void resize() {
        User[] newUsers = new User[users.length * 2];
        System.arraycopy(users, 0, newUsers, 0, size);
        users = newUsers;
    }
}
