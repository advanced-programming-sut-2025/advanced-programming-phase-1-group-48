package controllers;

import model.Result;
import model.enums.MenuCommands;
import model.user.User;
import model.user.UserManager;


import java.util.regex.Matcher;

public class ProfileMenuController {
    private static User currentUser = UserManager.getCurrentUser();

    public static void handleUserInfo() {
        System.out.printf("""
                        Username: %s
                        Nickname: %s
                        Highest Score: %d
                        Games Played: %d
                        """,
                currentUser.getUsername(),
                currentUser.getNickname(),
                currentUser.getHighestScore(),
                currentUser.getGameCount()
        );
    }

    public static Result changePassword(String command) {
        Matcher matcher = MenuCommands.CHANGE_PASSWORD.getPattern().matcher(command);
        if (!matcher.matches()) {
            System.out.println("invalid command!");
        }
        String newPassword = matcher.group("newPassword");
        String oldPassword = matcher.group("oldPassword");

        String newHashedPassword ;
        newHashedPassword = LoginController.hashSHA256(newPassword);
        if (!currentUser.getHashedPassword().equals(newHashedPassword)) {
             return Result.failure("Your input password doesn't match your current password!");
        }

        if (newPassword.equals(oldPassword)) {
            return Result.failure("New password must not match your current password!");
        }

        String passwordValidation = SignUpMenuController.validatePassword(newPassword);

        if (!passwordValidation.equals("password is true")){
            return Result.failure(passwordValidation);
        }


        currentUser.setHashedPassword(newHashedPassword);
        UserManager.updateUser(currentUser);
        return Result.success("Password changed successfully!");
    }

    public static Result changeNickname(String command) {
        Matcher matcher = MenuCommands.CHANGE_NICKNAME.getPattern().matcher(command);
        if (!matcher.matches()) {
            System.out.println("invalid command!");
        }
        String newNickname = matcher.group("nickname");
        if (newNickname.equals(currentUser.getNickname())) {
            return Result.failure("new nickname must be different from current nickname!");
        }

        currentUser.setNickname(newNickname);
        UserManager.updateUser(currentUser);
        return Result.success("Nickname changed successfully!");
    }

    public static Result changeEmail(String command) {
        Matcher matcher = MenuCommands.CHANGE_EMAIL.getPattern().matcher(command);
        if (!matcher.matches()) {
            return Result.failure("invalid command!");
        }
        String newEmail = matcher.group("email");

        if (newEmail.equals(currentUser.getEmail())) {
            return Result.failure("new email must be different from current email!");
        }

        String emailValidation = SignUpMenuController.validateEmail(newEmail);

        if (!emailValidation.equals("email is true")){
            return Result.failure(emailValidation);
        }

        currentUser.setEmail(newEmail);
        UserManager.updateUser(currentUser);
        return Result.success("Email changed successfully!");
    }

    public static Result changeUsername(String command) {
        Matcher matcher = MenuCommands.CHANGE_USERNAME.getPattern().matcher(command);
        if (!matcher.matches()) {
            return Result.failure("invalid command!");
        }
        String newUsername = matcher.group("username");

        if (newUsername.equals(currentUser.getUsername())) {
            return  Result.failure("new username equals current username");
        }

        if (UserManager.userExists(newUsername)) {
            return Result.success("This username is already in use!");
        }

        UserManager.updateUsername(currentUser.getUsername(), newUsername);
        currentUser.setUsername(newUsername);
        return Result.success("Username changed successfully!");
    }

}
