
import controllers.MenuController;
import views.MainMenu;

import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        String mainMenu = "main menu";
        MenuController.setMenu(new MainMenu(), mainMenu );
        System.out.println("Welcome\uD83E\uDEF6");
        System.out.println("Enter <Yes> If you want to continue, Enter <No> Otherwise.");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String command = scanner.nextLine().trim();
            MenuController.handleCommand(command,scanner);
        }
    }
}