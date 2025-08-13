package io.github.some_example_name.controllers;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.Main;
import io.github.some_example_name.model.GameAssetManager;
import io.github.some_example_name.model.Player.Player;

import java.util.Map;

public class PlayerController {
    private Player player;

    public PlayerController(Player player){
        this.player = player;
    }

    public void update() {
        handlePlayerInput(); // اول ورودی بگیر

        // 👇 حتما اینو اضافه کن
        player.getPlayerSprite().setPosition(player.getPosX(), player.getPosY());

        if(player.isPlayerIdle()) {
            idleAnimation();
        }

        player.getPlayerSprite().draw(Main.getBatch());

    }



    public void handlePlayerInput(){
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            player.setPosY(player.getPosY() + player.getSpeed()); // حرکت به بالا
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            player.setPosY(player.getPosY() - player.getSpeed()); // حرکت به پایین
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            player.setPosX(player.getPosX() - player.getSpeed()); // چپ
            player.getPlayerSprite().setFlip(true, false); // اگه لازم بود
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            player.setPosX(player.getPosX() + player.getSpeed()); // راست
            player.getPlayerSprite().setFlip(false, false);
        }
    }



    public void idleAnimation(){
        Animation<Texture> animation = GameAssetManager.getGameAssetManager().getCharacter1_idle_animation();

        player.getPlayerSprite().setRegion(animation.getKeyFrame(player.getTime()));

        if (!animation.isAnimationFinished(player.getTime())) {
            player.setTime(player.getTime() + Gdx.graphics.getDeltaTime());
        }
        else {
            player.setTime(0);
        }

        animation.setPlayMode(Animation.PlayMode.LOOP);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
