package adt;

import entity.User;
import util.UserFileManager;
import java.util.ArrayList;
import java.util.List;

public class UserArrayList implements UserListADT {
    private User[] users;
    private int size;
    private static final int INITIAL_CAPACITY = 100;

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
        }
    }

    @Override
    public void addUser(User user) {
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
                // Remove user from array
                for (int j = i; j < size - 1; j++) {
                    users[j] = users[j + 1];
                }
                users[size - 1] = null;
                size--;
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
