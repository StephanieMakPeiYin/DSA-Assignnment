package control;

import adt.UserArrayList;
import adt.UserListADT;
import entity.User;
import java.util.List;

public class UserControl {
    private final UserListADT userList;
    
    public UserControl() {
        this.userList = new UserArrayList();
    }

    /**
     * Get all users from the system
     */
    public List<User> getAllUsers() {
        return userList.getAllUsers();
    }

    /**
     * Find a user by email (case-sensitive)
     */
    public User findUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
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
     * Delete a user by email (case-sensitive)
     * Returns true if deletion was successful, false otherwise
     */
    public boolean deleteUser(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return userList.deleteUser(email);
    }

    /**
     * Get total number of users
     */
    public int getTotalUsers() {
        return userList.getSize();
    }
}
