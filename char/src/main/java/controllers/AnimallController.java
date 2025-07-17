package controllers;

import model.Animal.Animal;
import model.Animal.AnimalKind;
import model.Player.Player;
import model.game.Game;
import model.game.GameManager;
import java.util.Map;

public class AnimallController {
    public static void caress(AnimalKind kind) {
        Game game = GameManager.getCurrentGame();
        model.Player.Player player = game.getCurrentPlayerForPlay();

        Map<String , Animal> brought = player.getBroughtAnimal();
        Animal animal = brought.get(kind);

        if (animal == null) {
            System.out.println("No animal of type " + kind.name() + " was found!");
            return;
        }

        try {
            animal.pet();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }



}
