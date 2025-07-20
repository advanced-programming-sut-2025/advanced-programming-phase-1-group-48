package io.github.some_example_name.model.user;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserManager {
    //private static UserManager instance;
    private static final String USERS_FILE = "users.json";
    private static final String SESSION_FILE = "last_session.txt";
    private static List<User> users = new ArrayList<>();
    private User loggedInUser;
    public static User currentUser = null;


    static {
        loadUsers();
    }

    public static void addUser(User user) {
        users.add(user);
        saveToFile();
    }

    public static User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private static void saveToFile() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type userListType = new TypeToken<ArrayList<User>>() {
            }.getType();
            List<User> loadedUsers = gson.fromJson(reader, userListType);
            if (loadedUsers != null) {
                users = loadedUsers;
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    public static void updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            User currentUser = users.get(i);
            if (currentUser.getUsername().equals(updatedUser.getUsername())) {
                users.set(i, updatedUser);
                saveToFile();
                return;
            }
        }
    }

    public static void updateUsername(String oldUsername, String newUsername) {
        User user = findByUsername(oldUsername);
        if (user != null) {
            user.setUsername(newUsername);
            saveToFile();
        }
    }

    public static boolean userExists(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }

    public static void saveSession(User user) {
        try (PrintWriter pw = new PrintWriter(SESSION_FILE)) {
            pw.println(user.getUsername());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User loadLastSession() {
        File file = new File(SESSION_FILE);
        if (!file.exists()) return null;

        try (Scanner sc = new Scanner(file)) {
            if (sc.hasNextLine()) {
                String username = sc.nextLine();
                return findByUsername(username);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) file.delete();
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }
}
