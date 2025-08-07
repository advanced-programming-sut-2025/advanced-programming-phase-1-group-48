package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.controllers.LoginController;

import io.github.some_example_name.model.Result;
import io.github.some_example_name.model.Session;
import io.github.some_example_name.model.game.Game;

import java.util.Scanner;

public class LoginScreen extends ScreenAdapter {
    private final Main game;          // کلاس اصلیِ Game
    private Stage stage;
    private Skin skin;
    private Table table;

    public LoginScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 1) بارگذاری / ساخت Stage و Skin
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin  = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));

        // 2) ساخت ویجت‌ها
        TextField usernameField = new TextField("", skin);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        TextButton loginBtn = new TextButton("Login", skin);
        Label msgLabel  = new Label("", skin);

        // 3) چیدمان با Table
        table = new Table(skin);
        table.setFillParent(true);
        table.pad(50);
        table.add(new Label("Username:", skin)).left();
        table.add(usernameField).width(200).row();
        table.add(new Label("Password:", skin)).left();
        table.add(passwordField).width(200).row();
        table.add(loginBtn).colspan(2).padTop(20).row();
        table.add(msgLabel).colspan(2).padTop(10);
        stage.addActor(table);

        // 4) Event Listener برای دکمه
        loginBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String user = usernameField.getText().trim();
                String pass = passwordField.getText().trim();

                // ← به جای متد قدیمی login(...) از login2 استفاده کن:
                Result result = LoginController.login2(user, pass);

                msgLabel.setText(result.message());
                // ← حتماً isSuccess (نه success) را بررسی کن
                if (result.success()) {
                    System.out.println("Session logged in? " + Session.isLoggedIn());
                    game.setScreen(new MainMenuScreen(game));
                } else {
                    System.out.println("Login failed, staying on LoginScreen");
                }
            }
        });

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f,0.1f,0.1f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
