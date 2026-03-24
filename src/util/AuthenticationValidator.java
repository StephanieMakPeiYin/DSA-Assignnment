package util;

public class AuthenticationValidator {
    private static final String VALID_EMAIL_DOMAIN = "@gmail.com";
    private static final String STAFF_PASSWORD = "12345";

    public static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty.";
        }
        if (name.trim().length() < 2) {
            return "Name must be at least 2 characters.";
        }
        return "";
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return email.endsWith(VALID_EMAIL_DOMAIN) && email.contains("@");
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= 4;
    }

    public static boolean isStaffPassword(String password) {
        return password.equals(STAFF_PASSWORD);
    }

    public static String validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email cannot be empty.";
        }
        if (!email.endsWith(VALID_EMAIL_DOMAIN)) {
            return "Email must be in format: username" + VALID_EMAIL_DOMAIN;
        }
        return "";
    }

    public static String validatePasswordFormat(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        if (password.length() < 4) {
            return "Password must be at least 4 characters.";
        }
        return "";
    }
}
