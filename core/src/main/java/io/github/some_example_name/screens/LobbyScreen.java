package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;

import io.github.some_example_name.model.Lobby.LobbyManager;
import io.github.some_example_name.model.Session;
import io.github.some_example_name.model.GameAssetManager;
import io.github.some_example_name.model.game.Game;
import io.github.some_example_name.model.game.GameManager;
import io.github.some_example_name.controllers.GameController;
import io.github.some_example_name.network.ClientConnection;
import io.github.some_example_name.shared.model.GameState;
import io.github.some_example_name.shared.model.LobbyInfo;
import io.github.some_example_name.views.GameView;

// Dialogs / screens from original code (ممکنه پکیج/نام متفاوت باشه — در صورت خطا اصلاح کن)
import io.github.some_example_name.model.Lobby.CreateLobbyDialog;
import io.github.some_example_name.model.Lobby.PasswordDialog;
import io.github.some_example_name.model.Lobby.JoinByIdDialog;
import io.github.some_example_name.screens.InsideLobbyScreen;

import java.io.IOException;
import java.util.ArrayList;

public class LobbyScreen extends ScreenAdapter {
    private final Main game; // از نسخه دوم
    private Stage stage;
    private static Skin skin;
    private static Table lobbyListTable;

    // از نسخه دوم (شبکه)
    private ClientConnection client;
    private Label statusLabel;

    private volatile GameState pendingState = null;
    private static GameView gameView = null;
    public LobbyScreen(Main game , ClientConnection client) {
        this.game = game;
        this.client = client;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));
        Gdx.input.setInputProcessor(stage);


        // layout اصلی: چپ = لیست لابی‌ها، راست = وضعیت / عنوان لابی
        Table root = new Table(skin);
        root.setFillParent(true);
        root.pad(16);
        stage.addActor(root);

        // چپ: لیست لابی‌ها و کنترل‌ها
        Table left = new Table(skin);
        Label lobbiesTitle = new Label("Lobbies", skin);
        left.add(lobbiesTitle).colspan(2).padBottom(12).row();

        TextButton createBtn = new TextButton("Create Lobby", skin);
        TextButton refreshBtn = new TextButton("Refresh", skin);
        TextButton joinByIdBtn = new TextButton("Join by ID", skin);

        left.add(createBtn).pad(6);
        left.add(refreshBtn).pad(6).row();
        left.add(joinByIdBtn).colspan(2).pad(6).row();

        lobbyListTable = new Table(skin);
        ScrollPane lobbyListPane = new ScrollPane(lobbyListTable, skin);
        lobbyListPane.setFadeScrollBars(false);
        left.add(lobbyListPane).colspan(2).expand().fill().row();

        // راست: عنوان و وضعیت (از نسخه دوم)
        Table right = new Table(skin);
        Label title = new Label("Lobby", skin);
        title.setFontScale(1.5f);
        statusLabel = new Label("⏳ Waiting for other players...", skin);
        right.add(title).padBottom(8).row();
        right.add(statusLabel).pad(6).row();


        // چیدمان root: دو ستون
        root.add(left).expand().fill();
        root.add(right).width(320).padLeft(20).top();
        requestLobbyListFromServer();
        // بارگذاری لیست لابی‌ها
        LobbyManager.updateLobbyList();


        // ----- register client callbacks and ensure connection -----
        if (client != null) {
            // وقتی لیست لابی از سرور رسید — LobbyManager را به‌روز کن و UI را رفرش کن
            client.setOnLobbies(list -> {
                System.out.println("LOBBIES payload JSON: " + list); // debug
                // لیست دریافتی را توی LobbyManager بذار و UI را آپدیت کن
                LobbyManager.setVisibleLobbies(list);
                //Gdx.app.postRunnable(this::updateLobbyList);
            });

            // وقتی جواب JOIN از سرور رسید
            client.setOnJoinAck((lobby, status) -> {
                System.out.println("JOIN_ACK received: " + lobby.getId() + " -> " + status);
                if ("success".equalsIgnoreCase(status)) {
                    System.out.println("success");
//                    LobbyInfo joined = LobbyManager.getLobbyById(lobby.getId());
                    Gdx.app.postRunnable(() -> {
                            Main.getMain().setScreen(new InsideLobbyScreen(lobby));

                    });
                } else {
                    Gdx.app.postRunnable(() -> statusLabel.setText("Join failed: " + status));
                }
            });

            // ensure connected before sending requests (otherwise out == null)
            if (!client.isConnected()) {
//                    client.connect();
            }
        }
// -----------------------------------------------------------
        // لیسنرها
        refreshBtn.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("refreshBtn");
                requestLobbyListFromServer();
            }
        });

        createBtn.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                CreateLobbyDialog dialog = new CreateLobbyDialog(stage, skin);
                dialog.show(stage);
            }
        });

        joinByIdBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("ppppppppppppppppppppppppppppppppppppp");
                new JoinByIdDialog(stage, skin, Session.getCurrentUser().getUsername());
            }
        });


        requestLobbyListFromServer();


        // در اینجا می‌توانیم یک client سراسری هم آماده کنیم اما فعلاً هنگام Join آن را می‌سازیم.
    }

    private void updateLobbyList() {
        lobbyListTable.clear();  // پاک کردن لیست قبلی UI

        for (LobbyInfo lobby : LobbyManager.getVisibleLobbies()) {
            String labelText = lobby.getName() + " | Players: " + lobby.getPlayers() + " / " + lobby.getMaxPlayers() + " | ID: " + lobby.getId();
            Label lobbyLabel = new Label(labelText, skin);
            TextButton joinBtn = new TextButton("Join", skin);

            Table row = new Table(skin);
            row.add(lobbyLabel).expandX().left();
            row.add(joinBtn).right();

            lobbyListTable.add(row).fillX().pad(5).row();

            final LobbyInfo currentLobby = lobby;

            joinBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    // اینجا کدی که موقع کلیک روی Join اجرا میشه
                    System.out.println("Joining lobby: " + currentLobby.getName());
                    // اینجا میتونی متدهای join یا ساخت اتصال شبکه رو صدا بزنی
                }
            });
        }
    }


    public static Table getLobbyListTable() {
        return lobbyListTable;
    }

    public static Skin getSkin() {
        return skin;
    }

    /**
     * ساخت و پیکربندی ClientConnection برای این کلاینت.
     * توجه: امضای واقعی ClientConnection ممکن است متفاوت باشد — در صورت نیاز منطبق کن.
     */
    private void createClientForUser(String playerId) {
        // اگر قبلاً client وجود داره، سعی کن ببندی و دوباره بسازی
        try {
            if (client != null) {
                client.close(); // فرض بر وجود متد close؛ در صورت نبودن، حذف کن
            }
        } catch (Exception ignored) {}

        // وقتی سرور فرمان START داد (mapName, playerId)
        client.setOnStart((mapName, assignedPlayerId) -> {
            System.out.println("🎯 Switching to GameView with map: " + mapName + " id=" + assignedPlayerId);
            // 1. ساخت بازی در کلاینت با playerId
            ArrayList<String> usernames = new ArrayList<>();
            usernames.add(assignedPlayerId);
            GameManager.createGame(usernames, assignedPlayerId);

            // 2. گرفتن گیم ساخته‌شده
            Game game = GameManager.getCurrentGame();

            // 3. ساخت کنترلر و ویو — پاس دادن client و playerId به GameView
            GameController controller = new GameController();
            gameView = new GameView(controller, GameAssetManager.getGameAssetManager().getSkin(), client, assignedPlayerId);

            // 4. لود مپ برای این کلاینت (فرض بر سینکرون بودن load)
            controller.loadFarmMap();

            // 5. اعلام READY به سرور (بعد از بارگذاری منابع)
            try {
                client.sendReady(assignedPlayerId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 6. تغییر صفحه
            Gdx.app.postRunnable(() -> Main.getMain().setScreen(gameView));
        });

        // زمانی که وضعیت بازی رسید
        client.setOnGameState(this::onGameStateReceived);

        try {
//            client.connect();
            statusLabel.setText("✅ Connected to server");
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("❌ Connection failed");
        }
    }

    private void onGameStateReceived(GameState state) {
        System.out.println("✅ GameState received (raw) -> " + (state == null ? "null" : "players=" + state.getPlayerPositions().size()));

        Gdx.app.postRunnable(() -> {
            if (state == null) {
                statusLabel.setText("Received empty game state");
                return;
            }

            // اگر هنوز وارد GameView نشدیم، وضعیت رو ذخیره کن و لِیبل لابی رو آپدیت کن
            if (gameView == null) {
                pendingState = state;
                int n = state.getPlayerPositions().size();
                statusLabel.setText("⏳ Waiting players... current players: " + n);
                System.out.println("Lobby: saved pendingState, players=" + n);
                return;
            }

            // اگر GameView فعال است، مستقیم اعمال کن
            try {
//                gameView.applyGameState(state); // اگر متد private باشه، آن را public کن
                System.out.println("GameView: applied state, players=" + state.getPlayerPositions().size());
            } catch (Exception ex) {
                System.err.println("Failed to apply state to GameView: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        // پاکسازی لابی‌های خالی (همان‌طور که در نسخه اول بود)
        LobbyManager.cleanEmptyLobbies();
    }

    @Override
    public void dispose() {
        try {
            if (client != null) client.close();
        } catch (Exception ignored) {}
        stage.dispose();
        skin.dispose();
    }

    public static GameView getGameView() {
        return gameView;
    }

    private void requestLobbyListFromServer() {
        try {
            System.out.println("requestLobbyListFromServer");
            client.requestLobbyList(lobbies -> {
                // اینجا وقتی جواب سرور آمد، لیست لابی‌ها را به LobbyManager می‌دهیم
                LobbyManager.setVisibleLobbies(lobbies);
                //Gdx.app.postRunnable(() -> updateLobbyList());
            });
        }catch (Exception ignored) {
            System.out.println("requestLobbyListFromServer failed"+ignored.getMessage());
        }

    }

}
