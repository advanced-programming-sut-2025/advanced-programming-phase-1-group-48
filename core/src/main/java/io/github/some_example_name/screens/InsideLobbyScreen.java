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
import io.github.some_example_name.controllers.GameController;
import io.github.some_example_name.model.GameAssetManager;
import io.github.some_example_name.model.Lobby.LobbyManager;

import io.github.some_example_name.model.Session;
import io.github.some_example_name.model.game.Game;
import io.github.some_example_name.model.game.GameManager;
import io.github.some_example_name.network.ClientConnection;
import io.github.some_example_name.shared.model.GameState;
import io.github.some_example_name.shared.model.LobbyInfo;

import io.github.some_example_name.views.GameView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Modernized Inside Lobby UI (replaced original):
 * - two-column layout: players list + lobby controls
 * - top bar with title, id and status
 * - admin actions (Kick) and Start button logic preserved
 */
public class InsideLobbyScreen extends ScreenAdapter {
    private static LobbyInfo lobby;
    private Stage stage;
    private Skin skin;
    private Table playerListTable;
    private TextButton startButton;
    private TextButton leaveButton;
    private TextButton backButton;
    private Label titleLabel;
    private Label idLabel;
    private static Label statusLabel;
    private int lastPlayerListHash = -1;

    // Game state / view handling
    private static GameView gameView = null;
    private static volatile GameState pendingState = null;

    public InsideLobbyScreen(LobbyInfo lobby) {
        this.lobby = lobby;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));
        Gdx.input.setInputProcessor(stage);

        titleLabel = new Label(lobby.getName(), skin);
        titleLabel.setFontScale(1.4f);
        idLabel = new Label("ID: " + lobby.getId(), skin);
        statusLabel = new Label("Waiting for players...", skin);
        statusLabel.setFontScale(0.9f);

        leaveButton = new TextButton("Leave", skin);
        backButton = new TextButton("Back", skin);
        startButton = new TextButton("Start Game", skin);
        startButton.getLabel().setFontScale(1.1f);

        // Top bar
        Table top = new Table(skin);
        top.setFillParent(false);
        top.add(titleLabel).left().pad(10);
        top.add(idLabel).left().pad(10);
        top.add(statusLabel).expandX().center();
        top.add(backButton).right().pad(6);
        top.add(leaveButton).right().pad(6);

        // Left: players list
        playerListTable = new Table(skin);
        ScrollPane playerScroll = new ScrollPane(playerListTable, skin);
        playerScroll.setFadeScrollBars(false);
        playerScroll.setScrollingDisabled(true, false);

        // Right: lobby controls
        Table right = new Table(skin);
        right.defaults().pad(6).fillX();
        Label lobbySettings = new Label("Lobby Controls", skin);
        lobbySettings.setFontScale(1.1f);
        right.add(lobbySettings).row();
        right.add(startButton).minWidth(220).padTop(10).row();
        right.add(new Label("Players allowed: " + lobby.getMaxPlayers(), skin)).row();
        right.add(new Label("Private: " + (lobby.isPrivate() ? "Yes" : "No"), skin)).row();
        right.add(new Label("Admin: " + (lobby.getAdminUsername() == null ? "-" : lobby.getAdminUsername()), skin)).row();

        // Root layout
        Table root = new Table(skin);
        root.setFillParent(true);
        root.top().pad(12);
        root.add(top).colspan(2).expandX().fillX().row();

        root.add(playerScroll).expand().fill().pad(12).minWidth(350);
        root.add(right).expandY().top().pad(12).row();

        stage.addActor(root);

        // Listeners
        leaveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String username = Session.getCurrentUser().getUsername();
                lobby.removePlayer(username);
                if (lobby.getPlayers().isEmpty()) LobbyManager.removeLobby(lobby.getId());
                Gdx.app.postRunnable(() -> Main.getMain().setScreen(new LobbyScreen(Main.getMain(), MainMenuScreen.getClient())));
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.postRunnable(() -> Main.getMain().setScreen(new LobbyScreen(Main.getMain(), MainMenuScreen.getClient())));
            }
        });

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ClientConnection client = MainMenuScreen.getClient();
                // ثبت handler برای STATE بلافاصله (تمام پلیرها)

// ثبت handler برای START (برای همه بازیکن‌ها)
                client.setOnStart((mapName, playerId) -> Gdx.app.postRunnable(() -> {
                    System.out.println("Global START received: map=" + mapName + " playerId=" + playerId);
                    // playerId خودمون را از Session بگیریم (ASSIGN_ID باید قبلاً ذخیره شده باشد)
                    String myPlayerId = Session.getCurrentPlayerId();
                    if (myPlayerId == null) {
                        System.err.println("start: Session playerId is null — cannot start local game.");
                        return;
                    }
                    startLocalGame(myPlayerId, mapName, null); // maybeState null (یا ارسال state اگر داری)
                }));


                System.out.println("Start pressed");

                if (!canStartGame()) {
                    statusLabel.setText("Only admin can start the game and at least 2 players required.");
                    return;
                }

                // ثبت callback ها قبل از ارسال درخواست
//                client.setOnStart((mapName, playerId) -> Gdx.app.postRunnable(() -> {
//                    System.out.println("onStart callback: map=" + mapName + " playerId=" + Session.getCurrentPlayerId());
//                    ArrayList<String> usernames = new ArrayList<>();
//                    usernames.add(playerId);
//                    GameManager.createGame(usernames, playerId);
//                    Game game = GameManager.getCurrentGame();
//                    GameController controller = new GameController();
//                    gameView = new GameView(controller, GameAssetManager.getGameAssetManager().getSkin(), client, playerId);
//                    controller.loadFarmMap();
//
//                    // اگر چیزی از سرور قبلاً به عنوان STATE دریافت شد، اعمالش کن
//                    if (pendingState != null) {
//                        try {
//                            gameView.applyGameState(pendingState);
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//
//                    // ارسال READY با شناسهٔ خود بازیکن (نباید adminUsername باشه)
//                    try {
//                        String myPlayerId = Session.getCurrentUser().getUsername(); // یا هرجا playerId ذخیره کردی
//                        client.sendReady(myPlayerId);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Main.getMain().setScreen(gameView);
//                }));
//
//                client.setOnGameState(InsideLobbyScreen.this::onGameStateReceived);

                // اگر اتصال برقرار نیست آن را برقرار کن (در غیر اینصورت فقط پیام START را بفرست)
                try {
                    if (!client.isConnected()) {
//                        client.connect();
                    }

                    // ارسال درخواست شروع بازی به سرور (admin این را ارسال می‌کند)
                    client.sendMessage(Map.of("type", "START_GAME", "lobbyId", lobby.getId()));
                    System.out.println("START_GAME sent to server for lobby " + lobby.getId());

                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("❌ Could not send start request");
                }
            }
        });


        updatePlayerList();
    }

    public static void onGameStateReceived(GameState state) {
        Gdx.app.postRunnable(() -> {
            if (state == null) {
                statusLabel.setText("Received empty game state");
                return;
            }

            // اول: آپدیت pendingState همیشه مفید است
            pendingState = state;

            // آیا لابی ما در این state وجود دارد؟
            LobbyInfo myLobby = null;
            if (state.getLobbies() != null) {
                for (LobbyInfo li : state.getLobbies()) {
                    if (li != null && li.getId() != null && li.getId().equals(lobby.getId())) {
                        myLobby = li;
                        break;
                    }
                }
            }

            // اگر لابی پیدا نشد، فقط UI را آپدیت کن و بیرون برو
            if (myLobby == null) {
                statusLabel.setText("Waiting players... current players: " + (state.getPlayerPositions() == null ? 0 : state.getPlayerPositions().size()));
                return;
            }

            // اگر لابی started == true و هنوز داخل GameView نیستیم -> شروع محلی بازی
            if (myLobby.isStarted() && gameView == null) {
                System.out.println("Detected lobby started via STATE -> starting locally for player.");

                // playerId خودمان را از Session بگیر (حتماً ASSIGN_ID باید ذخیره شده باشد)
                String myPlayerId = Session.getCurrentPlayerId();
                if (myPlayerId == null) {
                    System.err.println("Cannot start local game: Session.currentPlayerId is null!");
                    return;
                }

                // ایجاد و ورود به بازی
//                startLocalGame(myPlayerId,null, state);
                return;
            }

            if(gameView == null) {
                System.out.println("Detected lobby started via STATE -> starting locally for player.");
            }
            // اگر قبلاً gameView ساخته شده بود، فقط state رو اعمال کن
            if (gameView != null) {
                try {
                    System.out.println("calling...");
                    if(state==null){
                        System.out.println("state==null");
                    }
                    gameView.applyGameState(state);
                } catch (Exception ex) {
                    System.err.println("Failed to apply state to GameView: " + ex.getMessage());
                    ex.printStackTrace();
                }
            } else {
                // update UI: تعداد پلیرها نمایش داده شود
                statusLabel.setText("Waiting players... current players: " + (state.getPlayerPositions() == null ? 0 : state.getPlayerPositions().size()));
            }
        });
    }


    private void updatePlayerList() {
        playerListTable.clear();
        String admin = lobby.getAdminUsername();
        for (String username : lobby.getPlayers()) {
            boolean isAdmin = username.equals(admin);
            playerListTable.add(buildPlayerRow(username, isAdmin)).expandX().fillX().row();
        }
        lastPlayerListHash = lobby.getPlayers().hashCode();
    }

    private Table buildPlayerRow(String username, boolean isAdmin) {
        Table row = new Table(skin);
        Label avatar = new Label("🟢", skin);
        avatar.setFontScale(1.1f);
        Label name = new Label(username + (isAdmin ? " (Admin)" : ""), skin);
        name.setFontScale(1.0f);
        CheckBox ready = new CheckBox("Ready", skin);
        ready.setDisabled(true);

        row.add(avatar).left().padRight(8);
        row.add(name).left().expandX();
        row.add(ready).right().padRight(6);

        if (Session.getCurrentUser() != null && Session.getCurrentUser().getUsername().equals(lobby.getAdminUsername())
            && !username.equals(Session.getCurrentUser().getUsername())) {
            TextButton kick = new TextButton("Kick", skin);
            kick.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        MainMenuScreen.getClient().sendMessage(java.util.Map.of("type", "KICK_PLAYER", "lobbyId", lobby.getId(), "username", username));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            row.add(kick).right();
        }

        return row;
    }

    private void updatePlayerListIfNeeded() {
        int currentHash = lobby.getPlayers().hashCode();
        if (currentHash != lastPlayerListHash) {
            updatePlayerList();
        }
    }

    private boolean canStartGame() {
        System.out.println("jjjj: " + lobby.getAdminId()+":"+lobby.getAdminUsername()+":"+Session.getCurrentPlayerId());
        System.out.println("size: " + lobby.getPlayers().size());
        String admin = lobby.getAdminUsername();
        boolean isAdmin = admin != null && admin.equals(Session.getCurrentPlayerId());
//        boolean enoughPlayers = lobby.getPlayers().size() >= 2; // یا شرط دلخواهت
        System.out.println("canStartGame -> isAdmin: " + isAdmin + " enoughPlayers: ");
        return isAdmin;
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.06f, 0.07f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
// داخل render
//        Gdx.app.log("LobbyDebug", "canStartGame = " + canStartGame());
//        System.out.println("startButton.isDisabled() = " + startButton.isDisabled());

//        startButton.setDisabled(!canStartGame());
        updatePlayerListIfNeeded();
        LobbyManager.cleanEmptyLobbies();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public static void startLocalGame(String playerId, String mapName, GameState maybeState) {
        try {
            ClientConnection client = MainMenuScreen.getClient();
            System.out.println("🎯 Switching to GameView with map: " + mapName + " id=" + playerId);
            // 1. ساخت بازی در کلاینت با playerId
            ArrayList<String> usernames = new ArrayList<>();
            usernames.add(playerId);
            GameManager.createGame(usernames, playerId);

            // 2. گرفتن گیم ساخته‌شده
            Game game = GameManager.getCurrentGame();

            // 3. ساخت کنترلر و ویو — پاس دادن client و playerId به GameView
            GameController controller = new GameController();
            gameView = new GameView(controller, GameAssetManager.getGameAssetManager().getSkin(), client, playerId);

            // 4. لود مپ برای این کلاینت (فرض بر سینکرون بودن load)
            controller.loadFarmMap();

            // 5. اعلام READY به سرور (بعد از بارگذاری منابع)
            try {
                client.sendReady(playerId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 6. تغییر صفحه
            Main.getMain().setScreen(gameView);
        } catch (Exception e) {
            System.err.println("startLocalGame failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static GameView getGameView() {
        return gameView;
    }

    public static LobbyInfo getLobby() {
        return lobby;
    }
}
