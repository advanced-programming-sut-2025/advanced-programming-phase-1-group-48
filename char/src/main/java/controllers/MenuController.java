package controllers;

import model.Result;
import model.enums.MenuCommands;
import views.*;

import java.util.Scanner;
import java.util.regex.Matcher;

public class MenuController {
    private static AppMenu currentMenu;
    private static String currentMenuName;

    public static void setMenu(AppMenu menu, String menuName) {
        currentMenu = menu;
        currentMenuName = menuName;
    }

    public static void showCurrentMenu() {
        System.out.println(currentMenuName);
    }

    public static void handleCommand(String command, Scanner scanner) {
        if (currentMenu != null) {
            currentMenu.handleInput(command, scanner);
        }
    }

    public static void menuEntrance(String command) {
        Matcher matcher = MenuCommands.MENU_ENTRANCE.getPattern().matcher(command);
        if (!matcher.matches()) {
            System.out.println("invalid command!");
        }
        String menuName = matcher.group("menuName");
        if (currentMenuName.equals("main menu")) {
            if (menuName.equals("game menu")) {
                System.out.println("Now you have entered into game menu");
                setMenu(new GameMenu(), "game menu");
            } else if (menuName.equals("profile menu")) {
                System.out.println("Now you have entered into profile menu");
                setMenu(new ProfileMenu(), "profile menu");
            } else if (menuName.equals("login menu")) {
                System.out.println("Now you have entered into login menu");
                setMenu(new LoginMenu(), "login menu");
            } else if (menuName.equals("signup menu")) {
                System.out.println("Now you have entered into signup menu");
                setMenu(new SignUpMenu(), "signup menu");
            } else {
                System.out.println("Your request is invalid");
            }
        } else if (currentMenuName.equals("signup menu")) {
            if (menuName.equals("login menu")) {
                System.out.println("Now you have entered into login menu");
                setMenu(new LoginMenu(), "login menu");
            } else {
                System.out.println("Your request is invalid");
            }
        } else if (currentMenuName.equals("login menu")) {
            if (menuName.equals("signup menu")) {
                System.out.println("Now you have entered into signup menu");
                setMenu(new SignUpMenu(), "signup menu");
            } else {
                System.out.println("Your request is invalid");
            }
        } else if (currentMenuName.equals("profile menu")) {
            if (menuName.equals("main menu")) {
                System.out.println("Now you have entered into main menu");
                setMenu(new MainMenu(), "main menu");
            } else if (menuName.equals("login menu")) {
                System.out.println("Now you have entered into login menu");
                setMenu(new LoginMenu(), "login menu");
            } else if (menuName.equals("signup menu")) {
                System.out.println("Now you have entered into signup menu");
                setMenu(new SignUpMenu(), "signup menu");
            } else {
                System.out.println("Your request is invalid");
            }
        } else if (currentMenuName.equals("game menu")) {
            if (menuName.equals("main menu")) {
                System.out.println("Now you have entered into main menu");
                setMenu(new MainMenu(), "main menu");
            } else if (menuName.equals("login menu")) {
                System.out.println("Now you have entered into login menu");
                setMenu(new LoginMenu(), "login menu");
            } else if (menuName.equals("signup menu")) {
                System.out.println("Now you have entered into signup menu");
                setMenu(new SignUpMenu(), "signup menu");
            } else if (menuName.equals("profile menu")) {
                System.out.println("Now you have entered into profile menu");
                setMenu(new ProfileMenu(), "profile menu");
            } else {
                System.out.println("Your request is invalid");
            }
        } else {
            System.out.println("Your request is invalid");
        }
    }
}