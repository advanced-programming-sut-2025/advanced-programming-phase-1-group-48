package io.github.some_example_name.CommandsAndOutput;


import io.github.some_example_name.controllers.PlantController;
import io.github.some_example_name.model.Plant.CraftInfo;

public class CheckPlant {
    public static void controllCommand(String command) {
        String[] parts = command.trim().split("\\s+");
        switch (parts[0]) {
            case "craftinfo":
                if (parts.length < 3 || !parts[1].equals("-n")) {
                    System.out.println("Usage: craftinfo -n <name>");
                    break;
                }else {
                    CraftInfo.craftInfo(parts[2]);
                }
            case "plant":

                if(PlantController.Plant(parts[2],parts[4])){
                    System.out.println("Plant " + parts[2]);
                }else {
                    System.out.println("You don't have a plant++++++++");
                }

                break;
                case "plantinfo":
                    int u = Integer.parseInt(parts[1]);
                    int i = Integer.parseInt(parts[2]);
                    System.out.println(PlantController.showPlantInfo(u,i));
                    break;
            case "plow":
                if(PlantController.plow(parts[1])){
                    System.out.println("plow successfully");
                }else{
                    System.out.println("plow failed");
                }
        }
    }

}
