package io.github.some_example_name.controllers;


import io.github.some_example_name.model.Result;
import io.github.some_example_name.model.Session;
import io.github.some_example_name.model.enums.MenuCommands;
import io.github.some_example_name.model.user.User;
import io.github.some_example_name.model.user.UserManager;
import io.github.some_example_name.views.MainMenu;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.regex.Matcher;

public class LoginController {
    public static Result login(String command, Scanner scanner) {
        Matcher matcher = MenuCommands.LOGIN.getPattern().matcher(command);
        if (!matcher.matches()) {
            return Result.failure("invalid command!");
        }
        String username = removeQuotes(matcher.group("username"));
        String password = removeQuotes(matcher.group("password"));
        boolean stayLoggedIn = matcher.group("stay") != null;
        User user = UserManager.findByUsername(username);

        if (user == null) {
            return Result.failure("invalid username!");
        }
        if (!checkPassword(password, user)) {
            return Result.failure("invalid password!");
        }
        Session.setCurrentUser(user);
        if (stayLoggedIn) UserManager.saveSession(user);
        String mainMenu = "main menu";
        MenuController.setMenu(new MainMenu(), mainMenu);
        UserManager.setCurrentUser(user);
        return Result.success("Login successful! You are now in main menu!");
    }

    public static Result handleForgotPassword(String command, Scanner scanner) {
        Matcher matcher = MenuCommands.FORGET_PASSWORD.getPattern().matcher(command);
        if (!matcher.matches()) {
            return Result.failure("invalid command!");
        }
        String username = matcher.group("username");


        User user = UserManager.findByUsername(username);
        if (user == null) {
            return Result.failure("invalid username!");
        }


        System.out.println(" Your security question is : " + user.getSecurityQuestion());
        System.out.print("Please enter your password in this form: answer -a <answer>");
        String answerCommand = scanner.nextLine();
        Matcher matcher2 = MenuCommands.ANSWER.getPattern().matcher(answerCommand);
        if (!matcher2.matches()) {
            return Result.failure("invalid answer!");
        }
        String answer = matcher2.group("answer");

        if (verifySecurityAnswer(answer, user)) {
            String message = processPasswordReset(user, scanner);
            return Result.success(message);
        } else {
            return Result.failure("Your answer is incorrect! Now you are redirecting to the login menu!");
        }
    }

    private static String processPasswordReset(User user, Scanner scanner) {
        System.out.println("Here is your choice for your password reset");
        System.out.println("1. generate new random password");
        System.out.println("2. enter new password");
        System.out.print("enter 1 or 2 : ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        String newPassword;
        if (choice == 1) {
            newPassword = SignUpMenuController.generateStrongPassword();
            return ("You new password is : " + newPassword);
        } else {
            System.out.print("Enter your new password: ");
            newPassword = scanner.nextLine();
        }
        newPassword = hashSHA256(newPassword);


        user.setHashedPassword(newPassword);
        UserManager.updateUser(user);
        return ("password changed successfully!");
    }

    private static String removeQuotes(String value) {
        return value.startsWith("\"") && value.endsWith("\"")
                ? value.substring(1, value.length() - 1)
                : value;
    }

    public static boolean checkPassword(String inputPassword, User user) {
        String inputHashed = hashSHA256(inputPassword);
        return inputHashed.equals(user.getHashedPassword());
    }

    public static String hashSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static boolean verifySecurityAnswer(String inputAnswer, User user) {
        String inputHashed = hashSHA256(inputAnswer);
        return inputHashed.equals(user.getHashedSecurityAnswer());
    }
}
