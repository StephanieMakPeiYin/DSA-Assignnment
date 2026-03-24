package adt;

import entity.User;
import java.util.List;

public interface UserListADT {
    void addUser(User user);
    User findUserByEmail(String email);
    boolean userExists(String email);
    int getSize();
    List<User> getAllUsers();
    boolean deleteUser(String email);
}
