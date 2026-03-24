package control;

import adt.UserArrayList;
import adt.UserListADT;
import entity.User;
import util.AuthenticationValidator;
import java.util.List;

public class AuthenticationControl {
    private final UserListADT userList;
    private User currentUser;

    public AuthenticationControl() {
        this.userList = new UserArrayList();
        this.currentUser = null;
        initializeStaffAccount();
    }

    private void initializeStaffAccount() {
        // Initialize with a default staff account
        User staffAccount = new User("Staff", "staff@gmail.com", "12345", "STAFF");
        userList.addUser(staffAccount);
    }

    public String login(String email, String password) {
        // Validate input
        String emailError = AuthenticationValidator.validateEmailFormat(email);
        if (!emailError.isEmpty()) {
            return "LOGIN_FAIL: " + emailError;
        }

        String passwordError = AuthenticationValidator.validatePasswordFormat(password);
        if (!passwordError.isEmpty()) {
            return "LOGIN_FAIL: " + passwordError;
        }

        // Check if user exists (case-sensitive email matching)
        User user = null;
        List<User> allUsers = userList.getAllUsers();
        for (User u : allUsers) {
            if (u.getEmail().equals(email)) {
                user = u;
                break;
            }
        }
        
        if (user == null) {
            return "LOGIN_FAIL: User not found. Please register or check your email.";
        }

        // Verify password (case-sensitive)
        if (!user.getPassword().equals(password)) {
            return "LOGIN_FAIL: Incorrect password.";
        }

        this.currentUser = user;
        return "LOGIN_SUCCESS";
    }

    public String register(String name, String email, String password, String confirmPassword) {
        // Validate name
        String nameError = AuthenticationValidator.validateName(name);
        if (!nameError.isEmpty()) {
            return "REGISTER_FAIL: " + nameError;
        }
        
        // Validate input
        String emailError = AuthenticationValidator.validateEmailFormat(email);
        if (!emailError.isEmpty()) {
            return "REGISTER_FAIL: " + emailError;
        }

        String passwordError = AuthenticationValidator.validatePasswordFormat(password);
        if (!passwordError.isEmpty()) {
            return "REGISTER_FAIL: " + passwordError;
        }

        // Check if passwords match (case-sensitive)
        if (!password.equals(confirmPassword)) {
            return "REGISTER_FAIL: Passwords do not match.";
        }

        // Check if user already exists (case-sensitive)
        if (userList.userExists(email)) {
            return "REGISTER_FAIL: Email already registered. Please login instead.";
        }

        // Create new student account
        User newUser = new User(name, email, password, "STUDENT");
        userList.addUser(newUser);
        return "REGISTER_SUCCESS";
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isStaff() {
        return currentUser != null && currentUser.getUserType().equals("STAFF");
    }

    public boolean isStudent() {
        return currentUser != null && currentUser.getUserType().equals("STUDENT");
    }
}
