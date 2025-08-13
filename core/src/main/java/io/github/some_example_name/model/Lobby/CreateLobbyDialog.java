package io.github.some_example_name.model.Lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.some_example_name.model.Session;

import io.github.some_example_name.network.ClientConnection;
import io.github.some_example_name.screens.MainMenuScreen;
import io.github.some_example_name.shared.model.LobbyInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateLobbyDialog extends Dialog {
    public CreateLobbyDialog(Stage stage, Skin skin) {
        super("Create Lobby", skin);

        TextField nameField = new TextField("", skin);
        CheckBox privateBox = new CheckBox("Private", skin);
        CheckBox visibleBox = new CheckBox("Visible", skin);
        TextField passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setMessageText("Password");

        getContentTable().add("Lobby Name").left().row();
        getContentTable().add(nameField).width(250).row();
        getContentTable().add(privateBox).left().row();
        getContentTable().add(visibleBox).left().row();
        getContentTable().add(passwordField).width(250).row();

        getButtonTable().add(new TextButton("Cancel", skin));
        TextButton ok = new TextButton("Create", skin);
        getButtonTable().add(ok);

        ok.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                String name = nameField.getText();
                boolean isPrivate = privateBox.isChecked();
                boolean isVisible = visibleBox.isChecked();
                String password = isPrivate ? passwordField.getText() : null;
                String username = Session.getCurrentUser().getUsername();

                // ساختن JSON برای ارسال به سرور
                Map<String, Object> lobbyData = new HashMap<>();
                lobbyData.put("type", "CREATE_LOBBY");
                lobbyData.put("name", name);
                lobbyData.put("private", isPrivate);
                lobbyData.put("visible", isVisible);
                lobbyData.put("password", password);
                lobbyData.put("owner", username);

                try {
                    MainMenuScreen.getClient().sendMessage(lobbyData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // اینجا دیگه لابی محلی نساز، فقط دکمه رو غیرفعال یا دیالوگ رو ببند
                hide();
            }
        });


    }
}
