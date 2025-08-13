package io.github.some_example_name.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import io.github.some_example_name.Main;
import io.github.some_example_name.network.ClientConnection;
import io.github.some_example_name.shared.model.GameState;
import io.github.some_example_name.shared.model.LobbyInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dialog مرکزی منوی بازی: Save / Resume / Start other / Online players / Reconnect / Auto-update
 *
 * تمام دکمه‌ها handler های ساده دارند (TODO) — بعداً آن‌ها را به منطق شبکه متصل می‌کنیم.
 */
public class GameMenuDialog extends Dialog {
    private final Stage stage;
    private final Skin skin;

    // UI elements that may be updated from outside (مثلاً وقتی پاسخ سرور می‌آید)
    private final ListView<String> onlinePlayersListView;
    private final ListView<String> savedGamesListView;
    private final CheckBox autoUpdateCheck;

    public GameMenuDialog(Stage stage, Skin skin) {
        super("Game Menu", skin);
        this.stage = stage;
        this.skin = skin;

        // main layout: two columns (left: controls, right: dynamic lists)
        getContentTable().pad(10);
        Table left = new Table(skin);
        Table right = new Table(skin);

        // --- Left: buttons ---
        TextButton saveBtn = new TextButton("Save Game", skin);
        TextButton resumeBtn = new TextButton("Continue Saved Game", skin);
        TextButton startOtherBtn = new TextButton("Start / Join Other Game", skin);
        TextButton onlineBtn = new TextButton("Show Online Players", skin);
        TextButton reconnectBtn = new TextButton("Reconnect", skin);

        autoUpdateCheck = new CheckBox("Auto-update player list", skin);
        autoUpdateCheck.setChecked(true); // پیش‌فرض روشن

        left.defaults().pad(6).minWidth(260);
        left.add(saveBtn).row();
        left.add(resumeBtn).row();
        left.add(startOtherBtn).row();
        left.add(onlineBtn).row();
        left.add(reconnectBtn).row();
        left.add(autoUpdateCheck).row();

        // --- Right: lists / details ---
        right.defaults().pad(6);
        Label onlineLabel = new Label("Online players:", skin);
        right.add(onlineLabel).row();
        onlinePlayersListView = new ListView<>(skin);
        right.add(onlinePlayersListView.getMain()).width(320).height(140).row();

        right.add(new Label("Saved games:", skin)).row();
        savedGamesListView = new ListView<>(skin);
        right.add(savedGamesListView.getMain()).width(320).height(140).row();

        // add to content
        getContentTable().add(left).top().left();
        getContentTable().add(right).top().left().padLeft(12);

        // Buttons bottom (Close)
        button("Close", "close");

        // --- Handlers (stubs) ---
        saveBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSavePrompt();
            }
        });

        resumeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                requestSavedGamesFromServerAndShow();
            }
        });

        startOtherBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: navigate back to lobby screen or open lobby dialog
                System.out.println("UI: Start/Join other game pressed");
                // Example: Main.getMain().setScreen(new LobbyScreen(...));
            }
        });

        onlineBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                requestOnlinePlayers();
            }
        });

        reconnectBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attemptReconnect();
            }
        });

        // close action
        this.setModal(true);
        this.setResizable(true);
        this.setMovable(true);
    }

    // ---------- UI helper methods ----------

    private void showSavePrompt() {
        // اول بسازش تا داخل result قابل‌دسترسی باشه
        final TextField nameField = new TextField("autosave-" + System.currentTimeMillis(), skin);

        Dialog d = new Dialog("Save Game", skin) {
            @Override
            protected void result(Object object) {
                if ("save".equals(object)) {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) name = "autosave-" + System.currentTimeMillis();
                    sendSaveRequestToServer(name);
                }
                hide();
            }
        };

        d.getContentTable().pad(8);
        d.getContentTable().add(new Label("Save name:", skin)).left().row();
        d.getContentTable().add(nameField).width(300).row();

        d.button("Cancel", "cancel");
        d.button("Save", "save");

        d.show(stage);
    }



    private void requestSavedGamesFromServerAndShow() {
        // TODO: پیام REQUEST_SAVED_GAMES را به سرور بفرست
        // سپس زمانی که جواب آمد savedGamesListView.setItems(...) را صدا بزن
        System.out.println("UI: request saved games (stub)");
        // نمونه تست محلی:
        List<String> demo = new ArrayList<>();
        demo.add("save-2025-08-13 (2 players)");
        demo.add("save-quick-1");
        savedGamesListView.setItems(demo);

        // وقتی کاربر یک save انتخاب کرد باید RESUME_READY ارسال شود؛
        // savedGamesListView.setSelectionListener((id)-> sendResumeReady(saveId));
    }

    private void requestOnlinePlayers() {
        System.out.println("UI: request online players (stub)");
        // TODO: یا درخواست مستقیم REQUEST_ONLINE_PLAYERS یا اکتفا به broadcast سرور (ONLINE_PLAYERS)
        // نمونه تست محلی:
        List<String> pl = new ArrayList<>();
        pl.add("player1 @ Lobby A");
        pl.add("player2 @ Lobby B");
        onlinePlayersListView.setItems(pl);
    }

    private void attemptReconnect() {
        System.out.println("UI: attempt reconnect (stub)");
        // TODO: صدا زدن client.connect() و handle reconnect flow
        ClientConnection client = MainMenuScreen.getClient(); // ← جایگزین با دسترسی به client شما
        try {
            if (!ClientConnection.isConnected()) {
                client.connect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendSaveRequestToServer(String name) {
        String lobbyId = InsideLobbyScreen.getLobby().getId(); // ← اینجا متد یا متغیر خودتون برای lobbyId فعلی
        if (lobbyId == null || lobbyId.isEmpty()) {
            System.out.println("Cannot send SAVE_GAME request: no current lobby ID");
            return;
        }

        System.out.println("Send SAVE_GAME request: " + name + " (lobbyId=" + lobbyId + ")");

        try {
            ClientConnection.sendMessage(Map.of(
                "type", "SAVE_GAME",
                "lobbyId", lobbyId,
                "name", name
            ));
            LobbyInfo lobbyInfo=null;
            for(LobbyInfo l:GameState.getLobbies()) {
                if (l.getId().equals(lobbyId)) {
                    lobbyInfo = l;
                }
            }
            Main.getMain().setScreen(new InsideLobbyScreen(lobbyInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ------------ external update API --------------
    // استفاده از این متدها وقتی جواب سرور آمد برای آپدیت UI
    public void setOnlinePlayers(List<String> players) {
        onlinePlayersListView.setItems(players);
    }

    public void setSavedGames(List<String> saves) {
        savedGamesListView.setItems(saves);
    }

    public boolean isAutoUpdateEnabled() {
        return autoUpdateCheck.isChecked();
    }

    // یک wrapper کمکی برای List + ScrollPane
    private static class ListView<T> {
        private final Table main;
        private final List<T> items = new ArrayList<>();
        private final List<T> selected = new ArrayList<>();
        private final ListWidget<T> listWidget;

        public ListView(Skin skin) {
            main = new Table(skin);
            listWidget = new ListWidget<>(skin);
            ScrollPane sp = new ScrollPane(listWidget, skin);
            sp.setFadeScrollBars(false);
            main.add(sp).expand().fill();
        }

        public Table getMain() { return main; }

        public void setItems(List<T> data) {
            items.clear();
            items.addAll(data);
            listWidget.setItems(items);
        }
    }

    // ساده‌سازی: ListWidget برای نمایش رشته‌ها (می‌توانی آن را گسترش دهی)
    private static class ListWidget<T> extends Table {
        private final VerticalGroup group;
        private final Skin skin;
        public ListWidget(Skin skin) {
            super(skin);
            this.skin = skin;
            group = new VerticalGroup();
            group.space(4);
            add(group).expand().fill();
        }

        public void setItems(List<T> items) {
            group.clear();
            for (T it : items) {
                Label lbl = new Label(it.toString(), skin);
                lbl.setAlignment(Align.left);
                group.addActor(lbl);
            }
        }
    }
}
