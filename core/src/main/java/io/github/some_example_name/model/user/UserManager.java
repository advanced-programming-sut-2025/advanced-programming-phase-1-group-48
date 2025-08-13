package io.github.some_example_name.model.user;


import io.github.some_example_name.model.Session;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class UserManager {
    //private static UserManager instance;
    private static final String USERS_FILE = "users.json";
    private static final String SESSION_FILE = "last_session.txt";
    private static List<User> users = new ArrayList<>();
    private User loggedInUser;
    public static User currentUser = null;
    private static String DB_URL="jdbc:sqlite:C:\\\\Users\\\\Dotcom\\\\Desktop\\\\Project+phase3 - Copy (3)\\\\core\\\\db\\\\mydatabase.db";
    private static String DB_USER="";
    private static String DB_PASS="";

//    static {
//        try (InputStream input = new FileInputStream("config.properties")) {
//            Properties prop = new Properties();
//            prop.load(input);
//            DB_URL = prop.getProperty("db.url");
//            DB_USER = prop.getProperty("db.user");
//            DB_PASS = prop.getProperty("db.pass");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


//    static {
//        //loadUsers();
//    }

    public static void addUser(User user) {
        users.add(user);
        saveUserToDatabase(user);
        //saveToFile();
    }

    public static User findByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    private static void saveUserToDatabase(User user) {
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User(
                    rs.getString("username"),
                    rs.getString("hashed_password"), // هش‌شده
                    rs.getString("nickname"),
                    rs.getString("email"),
                    rs.getString("gender"),
                    rs.getString("security_question"),
                    rs.getString("hashed_answer")    // هش جواب
                );
                users.add(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void loadUsersFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM users";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("password"), // اینجا پسورد هش شده است
                    rs.getString("nickname"),
                    rs.getString("email"),
                    rs.getString("gender"),
                    rs.getString("securityQuestion"),
                    rs.getString("securityAnswer")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



//    private static void saveToFile() {
//        try (FileWriter writer = new FileWriter(USERS_FILE)) {
//            Gson gson = new GsonBuilder()
//                    .setPrettyPrinting()
//                    .create();
//            gson.toJson(users, writer);
//        } catch (IOException e) {
//            System.err.println("Error saving users: " + e.getMessage());
//        }
//    }
//
//    private static void loadUsers() {
//        File file = new File(USERS_FILE);
//        if (!file.exists()) return;
//
//        try (FileReader reader = new FileReader(file)) {
//            Gson gson = new Gson();
//            Type userListType = new TypeToken<ArrayList<User>>() {
//            }.getType();
//            List<User> loadedUsers = gson.fromJson(reader, userListType);
//            if (loadedUsers != null) {
//                users = loadedUsers;
//            }
//        } catch (IOException e) {
//            System.err.println("Error loading users: " + e.getMessage());
//        }
//    }

    public static void updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            User currentUser = users.get(i);
            if (currentUser.getUsername().equals(updatedUser.getUsername())) {
                users.set(i, updatedUser);
                //saveToFile();
                return;
            }
        }
    }

    public static void updateUsername(String oldUsername, String newUsername) {
        User user = findByUsername(oldUsername);
        if (user != null) {
            user.setUsername(newUsername);
            //saveToFile();
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

    public static void loadSession() {
        File file = new File("session.txt");
        if (file.exists()) {
            try (Scanner sc = new Scanner(file)) {
                String username = sc.nextLine();
                User user = findByUsername(username);
                if (user != null) {
                    Session.setCurrentUser(user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
