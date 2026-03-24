package entity;

public class User {
    private String userID;
    private String name;
    private String email;
    private String password;
    private String userType; // "STAFF" or "STUDENT"
    private String status; // "active" or "removed"

    public User(String userID, String name, String email, String password, String userType) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.status = "active";
    }

    public User(String userID, String name, String email, String password, String userType, String status) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
