package io.github.some_example_name.views;


import io.github.some_example_name.controllers.MainMenuController;
import io.github.some_example_name.controllers.MenuController;
import io.github.some_example_name.model.enums.MenuCommands;

import java.util.Scanner;

public class MainMenu implements AppMenu{
    public void handleInput(String command, Scanner scanner) {
        if (command.equals("Yes")) {
            MainMenuController.start(scanner);
        } else if (command.equals("No")) {
            System.exit(0);
        } else if(command.matches(MenuCommands.LOGOUT.getPattern().pattern())){
            MainMenuController.logout(scanner);
        } else if(command.matches(MenuCommands.SHOW_CURRENT_MENU.getPattern().pattern())){
            MenuController.showCurrentMenu();
        } else if(command.matches(MenuCommands.MENU_ENTRANCE.getPattern().pattern())){
            MenuController.menuEntrance(command);
        }
    }
}
