package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.controllers.SignUpMenuController; // یا SignUpController جدید
import io.github.some_example_name.model.Result;
import io.github.some_example_name.Main;

public class SignupScreen extends ScreenAdapter {
    private final Main game;
    private Stage stage;
    private Skin  skin;

    public SignupScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));

        Table table = new Table(skin);
        table.setFillParent(true);
        table.pad(40);
        stage.addActor(table);

        // فیلدها
        final TextField usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");
        final TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMessageText("Password");
        final TextField passwordConfirmField = new TextField("", skin);
        passwordConfirmField.setPasswordMode(true);
        passwordConfirmField.setPasswordCharacter('*');
        passwordConfirmField.setMessageText("Confirm Password");
        final TextField nicknameField = new TextField("", skin);
        nicknameField.setMessageText("Nickname");
        final TextField emailField = new TextField("", skin);
        emailField.setMessageText("Email");
        final SelectBox<String> genderBox = new SelectBox<>(skin);
        genderBox.setItems("Male", "Female", "Other");


        TextButton btnSignup = new TextButton("Sign Up", skin);
        Label msgLabel     = new Label("", skin);

        table.add(new Label("Sign Up", skin)).colspan(2).padBottom(30).row();
        table.add(usernameField).width(300).colspan(2).pad(5).row();
        table.add(passwordField).width(300).colspan(2).pad(5).row();
        table.add(passwordConfirmField).width(300).colspan(2).pad(5).row();
        table.add(nicknameField).width(300).colspan(2).pad(5).row();
        table.add(emailField).width(300).colspan(2).pad(5).row();
        table.add(genderBox).width(300).colspan(2).pad(5).row();
        table.add(btnSignup).colspan(2).padTop(20).row();
        table.add(msgLabel).colspan(2).padTop(20);

        btnSignup.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String user  = usernameField.getText().trim();
                String pass  = passwordField.getText().trim();
                String passC = passwordConfirmField.getText().trim();
                String nick  = nicknameField.getText().trim();
                String mail  = emailField.getText().trim();
                String gen   = genderBox.getSelected();

                // فراخوانی متد جدید (یا کنونی) کنترلر
                Result result = SignUpMenuController.signup2(
                    user, pass, passC, nick, mail, gen
                );

                // یا اگر overload ساده ساختی:
                // Result result = SignUpMenuController.signup(user, pass, passC, nick, mail, gen);

                msgLabel.setText(result.message());
                if (result.success()) {
                    game.setScreen(new LoginScreen(game));
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
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
