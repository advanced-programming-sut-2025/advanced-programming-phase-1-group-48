package io.github.some_example_name.controllers;


import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.views.GameView;

public class GameController {
    private GameView view;
    private PlayerController playerController;
    private WorldController worldController;
//    private WeaponController weaponController;


    public void setView(GameView view) {
        this.view = view;
        playerController = new PlayerController(new Player());
        worldController = new WorldController(playerController);
//        weaponController = new WeaponController(new Weapon());
    }

    public void updateGame() {
        if (view != null) {
            worldController.update();
            playerController.update();
//            weaponController.update();
        }
    }

    public PlayerController getPlayerController() {
        return playerController;
    }

//    public WeaponController getWeaponController() {
//        return weaponController;
//    }
}
