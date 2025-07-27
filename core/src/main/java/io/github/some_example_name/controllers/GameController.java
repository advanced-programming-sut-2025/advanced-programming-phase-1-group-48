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

    public void updateGame(float delta) {
        String trigger = worldController.checkMapTransition();

        if ("enter_house".equals(trigger)) {
            loadHouseMap();
        } else if ("exit_house".equals(trigger)) {
            loadFarmMap();
        }

        if (view != null) {
            worldController.update(delta);
            playerController.update();
//            weaponController.update();
        }
    }
    public void loadHouseMap() {
        worldController.loadMap("Content (unpacked)/Maps/FarmHouse.tmx");
        //playerController.getPlayer().setPos(START_X, START_Y);
    }

    public void loadFarmMap() {
        worldController.loadMap("Content (unpacked)/Maps/Farm.tmx");
        //playerController.getPlayer().setPos(EXIT_X, EXIT_Y);
    }


    public PlayerController getPlayerController() {
        return playerController;
    }

//    public WeaponController getWeaponController() {
//        return weaponController;
//    }
}
