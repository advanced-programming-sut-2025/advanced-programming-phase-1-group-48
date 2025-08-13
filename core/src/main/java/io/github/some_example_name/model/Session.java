package io.github.some_example_name.model;


import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.model.user.User;

public class Session {
    private static User currentUser;
    private static String currentPlayerId;
    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }

    public static String getCurrentPlayerId() {return currentPlayerId;}
    public static void setCurrentPlayerId(String id) {
        System.out.println("set PLayerId");currentPlayerId=id;}
}


