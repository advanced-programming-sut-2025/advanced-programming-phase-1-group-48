package io.github.some_example_name.CommandsAndOutput;

import java.util.Scanner;

public class PlantTest {
    public static void main(String[] args) {
        while (true) {
            Scanner sc = new Scanner(System.in);
            String command = sc.nextLine();
            if (command.equals("exit")) {
                break;
            }
            CheckPlant.controllCommand(command);
        }
    }
}
