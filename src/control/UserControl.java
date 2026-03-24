package control;

import adt.UserArrayList;
import adt.UserListADT;
import entity.User;
import java.util.List;

public class UserControl {
    private UserListADT userList;
    
    public UserControl() {
        this.userList = new UserArrayList();
    }

    private void refreshUserList() {
        this.userList = new UserArrayList();
    }

    /**
     * Get all users from the system
     */
    public List<User> getAllUsers() {
        refreshUserList();
        return userList.getAllUsers();
    }

    /**
     * Find a user by email (case-sensitive)
     */
    public User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        refreshUserList();
        // Find with exact case match
        List<User> allUsers = userList.getAllUsers();
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
        List<User> allUsers = userList.getAllUsers();
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
        List<User> allUsers = userList.getAllUsers();
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
        return userList.deleteUser(email);
    }

    /**
     * Get total number of users
     */
    public int getTotalUsers() {
        refreshUserList();
        return userList.getSize();
    }

    /**
     * Add a new user to the system
     */
    public void addUser(User user) {
        if (user != null) {
            refreshUserList();
            userList.addUser(user);
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
        return userList.updateUser(updatedUser);
    }
}
