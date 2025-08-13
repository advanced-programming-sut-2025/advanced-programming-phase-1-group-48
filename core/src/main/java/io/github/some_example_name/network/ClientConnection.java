package io.github.some_example_name.network;

import com.badlogic.gdx.Gdx;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.some_example_name.model.Lobby.LobbyManager;
import io.github.some_example_name.screens.InsideLobbyScreen;

import io.github.some_example_name.model.Session;
import io.github.some_example_name.screens.LobbyScreen;
import io.github.some_example_name.screens.MainMenuScreen;
import io.github.some_example_name.shared.model.GameState;
import io.github.some_example_name.shared.model.LobbyInfo;

import io.github.some_example_name.shared.model.actions.Action;
import io.github.some_example_name.shared.model.database.DatabaseLobbyManager;
import io.github.some_example_name.views.GameView;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ClientConnection {
    private final String host;
    private final int port;
    private BiConsumer<String,String> onStart; // (mapName, playerId)
    public void setOnStart(BiConsumer<String,String> listener) { this.onStart = listener; }
    private static Socket socket;
    private static PrintWriter out;
    private BufferedReader reader;
    private static final ObjectMapper mapper = new ObjectMapper();

    // callback// دریافت پیام START -> mapName
    private Consumer<GameState> onState;     // دریافت payload STATE -> GameState
    private Consumer<List<LobbyInfo>> lobbyListCallback;
    private volatile boolean running = false;
    private Thread readerThread;
    // برای JOIN_ACK
    private BiConsumer<LobbyInfo,String> onJoinAck;
    public void setOnJoinAck(BiConsumer<LobbyInfo,String> cb) { this.onJoinAck = cb; }

    public ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

//    /** ثبت callback برای START */
//    public void setOnStart(Consumer<String> listener) {
//        this.onStart = listener;
//
//    }

    /** ثبت callback برای GameState */
    public void setOnGameState(Consumer<GameState> listener) {
        System.out.println("setOnGameState66666666666666");
        this.onState = listener;
    }

    /**
     * اتصال به سرور و شروع نخ دریافت.
     * باید از ترد گرافیکی فراخوانده شود (یا در show()).
     */
    public void connect() throws IOException {
        if (running) return;
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        running = true;

        readerThread = new Thread(() -> {
            try {
                String line;
                while (running && (line = reader.readLine()) != null) {
                    // اگر خط خالی یا whitespace است نادیده بگیر
                    if (line.trim().isEmpty()) continue;

                    try {
                        System.out.println("ClientConnection: got line -> " + line);
                        JsonNode node = mapper.readTree(line);
                        JsonNode typeNode = node.get("type");
                        if (typeNode == null) continue;
                        String type = typeNode.asText();

                        switch (type) {
                            case "START": {
                                JsonNode mapNode = node.get("map");
                                JsonNode pidNode  = node.get("playerId");
                                String mapName = mapNode != null ? mapNode.asText() : null;
                                String playerId = pidNode != null ? pidNode.asText() : null;
                                if (onStart != null) {
                                    Gdx.app.postRunnable(() -> onStart.accept(mapName, playerId));
                                }
                                String myPlayerId = Session.getCurrentPlayerId();
                                if (myPlayerId == null) {
                                    System.err.println("Cannot start local game: currentPlayerId is null!");
                                    break;
                                }

                                // چون در لحظه START ممکنه STATE هنوز نیومده باشه، mapName رو می‌گیریم و state رو null می‌ذاریم
                                Gdx.app.postRunnable(() -> {
                                    try {
                                        System.out.println("Starting local game for playerId=" + myPlayerId + " with map=" + mapName);
                                        InsideLobbyScreen.startLocalGame(myPlayerId, mapName, null);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                                break;
                            }
                            case "STATE": {
                                JsonNode payload = node.get("payload");
                                GameState state = mapper.treeToValue(payload, GameState.class);
                                GameView gameView= InsideLobbyScreen.getGameView();
                                if (state.getPlayerPositions() == null || state.getPlayerPositions().isEmpty() ||
                                    state.getPlayerMapIds() == null || state.getPlayerMapIds().isEmpty()) {
                                    System.out.println("Received empty or incomplete STATE, ignoring.");
                                    break; // به‌روزرسانی انجام نشود
                                }
                                if(gameView != null) {
                                    this.setOnGameState(this::onGameStateReceived);

                                    gameView.applyGameState(state);
                                }

                                if (onState != null) {
//                                   InsideLobbyScreen.onGameStateReceived(state);
                                    Gdx.app.postRunnable(() -> onState.accept(state));
                                }
                                break;
                            }
//                            case "STATE": {
//                                System.out.println("ClientConnection: got state -> " + node);
//                                JsonNode payload = node.get("payload");
//                                GameState state = null;
//                                try {
//                                    state = mapper.treeToValue(payload, GameState.class);
//                                } catch (Exception e) {
//                                    System.err.println("Failed to parse STATE payload: " + e.getMessage());
//                                    e.printStackTrace();
//                                    break;
//                                }
//
//                                // اگر state کلأ null بود نادیده بگیر
//                                if (state == null) {
//                                    System.out.println("Received null STATE, ignoring.");
//                                    break;
//                                }
//
//                                // وضعیت را لاگ کن برای دیباگ
//                                System.out.println("Received STATE: players=" + (state.getPlayerPositions() == null ? 0 : state.getPlayerPositions().size())
//                                    + " lobbies=" + (state.getLobbies() == null ? 0 : state.getLobbies().size()));
//
//                                // اگر حداقل یک لابی که started==true دارد وجود دارد، یا playerPositions غیرخالی است -> پردازش کن
////                                boolean hasPlayers = state.getPlayerPositions() != null && !state.getPlayerPositions().isEmpty();
////                                boolean anyLobbyStarted = state.getLobbies() != null && state.getLobbies().stream().anyMatch(l -> l.isStarted());
////
////                                if (!hasPlayers && !anyLobbyStarted) {
////                                    System.out.println("Received empty or incomplete STATE, ignoring.");
////                                    break;
////                                }
//                                System.out.println("give game view");
//                                // اگر GameView از LobbyScreen موجود است، مستقیم اعمال کن
////                                GameView gameView = LobbyScreen.getGameView();
////                                if (gameView != null) {
////                                    try {
////                                        gameView.applyGameState(state);
////                                        System.out.println("Applied STATE to existing GameView.");
////                                    } catch (Exception ex) {
////                                        System.err.println("Failed to apply state to GameView: " + ex.getMessage());
////                                        ex.printStackTrace();
////                                    }
////                                }
//
//                                // صدا زدن callback ثبت شده در کلاینت (مثلاً InsideLobbyScreen.onGameStateReceived)
//                                if (onState != null) {
//                                    final GameState st = state;
//                                    Gdx.app.postRunnable(() -> onState.accept(st));
//                                }
//                                break;
//                            }

                            case "LOBBIES": {
                                JsonNode payload = node.get("payload");
                                // debug print
                                System.out.println("LOBBIES payload JSON: " + payload.toString());

                                List<LobbyInfo> list = new ArrayList<>();
                                if (payload != null && payload.isArray()) {
                                    for (JsonNode item : payload) {
                                        try {
                                            String id = item.has("id") ? item.get("id").asText() : UUID.randomUUID().toString();
                                            String name = item.has("name") ? item.get("name").asText() : "Lobby";
                                            int players = item.has("players") ? item.get("players").asInt() : 0;
                                            int maxPlayers = item.has("maxPlayers") ? item.get("maxPlayers").asInt() : 4;
                                            boolean priv = item.has("private") && item.get("private").asBoolean();
                                            boolean visible = item.has("visible") && item.get("visible").asBoolean();

                                            // ایجاد LobbyInfo با setter ها (یا سازنده مناسب)
                                            LobbyInfo li = new LobbyInfo();
                                            li.setId(id);
                                            li.setName(name);
                                            li.setPlayersCount(players);   // مطمئن شو این setter/فیلد در LobbyInfo داری
                                            li.setMaxPlayers(maxPlayers);
                                            li.setPrivate(priv);
                                            li.setVisible(visible);

                                            list.add(li);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                if (onLobbies != null) {
                                    Gdx.app.postRunnable(() -> onLobbies.accept(list));
                                }
                                break;
                            }
                            case "JOIN_ACK": {
                                System.out.println("RAW JOIN_ACK message: " + node.toPrettyString());

                                JsonNode payload = node.get("payload");
                                String status = payload.get("status").asText();

                                // لابی رو از payload.lobby بساز
                                JsonNode lobbyNode = payload.get("lobby");
                                LobbyInfo lobby = new LobbyInfo();
                                lobby.setId(lobbyNode.get("id").asText());
                                lobby.setName(lobbyNode.get("name").asText());
                                lobby.setAdminUsername("player");
//                                lobby.setPlayers(lobbyNode.get("players").asInt());
                                lobby.setMaxPlayers(lobbyNode.get("maxPlayers").asInt());
                                lobby.setPrivate(lobbyNode.get("private").asBoolean());
// اگر فیلدهای دیگری مثل password یا creatorUsername داری، اینجا ست کن


                                System.out.println("JOIN_ACK parsed: " + lobby.getName() + " (" + lobby.getId() + ") status=" + status);
                                if (onJoinAck != null) {
                                    Gdx.app.postRunnable(() -> onJoinAck.accept(lobby, status));
                                }
                                break;
                            }
                            case "JOIN_LOBBY_RESULT": {
                                JsonNode payloadNode = node.get("payload");
                                if (payloadNode != null) {
                                    boolean success = payloadNode.has("success") && payloadNode.get("success").asBoolean(false);
                                    if (success) {
                                        String lobbyId = payloadNode.has("lobbyId") ? payloadNode.get("lobbyId").asText() : "unknown";
                                        System.out.println("Joined lobby successfully: " + lobbyId);
                                        // اینجا می‌تونی وارد صفحه لابی بشی یا UI را آپدیت کنی
                                    } else {
                                        String error = payloadNode.has("error") ? payloadNode.get("error").asText() : "Unknown error";
                                        System.out.println("Failed to join lobby: " + error);
                                        // می‌توانی پیام خطا را به کاربر نمایش دهی
                                    }
                                } else {
                                    System.out.println("JOIN_LOBBY_RESULT received but no payload found");
                                }
                                break;
                            }
                            case "CREATED_LOBBY": {
                                JsonNode payload = node.get("payload");
                                LobbyInfo lobby = mapper.treeToValue(payload, LobbyInfo.class);

                                // اگر adminUsername خالیه، از Session پرش کن
                                if (lobby.getAdminUsername() == null || lobby.getAdminUsername().isEmpty()) {
                                    lobby.setAdminUsername(Session.getCurrentUser().getUsername());
                                }

                                // اضافه کردن به لیست محلی و ذخیره در DB
                                LobbyManager.addLobby(lobby);
                                DatabaseLobbyManager.saveLobby(lobby);
                                System.out.println("lobby created");
                                // باز کردن صفحه لابی روی UI thread
                                Gdx.app.postRunnable(() -> {
                                    io.github.some_example_name.Main.getMain()
                                        .setScreen(new io.github.some_example_name.screens.InsideLobbyScreen(lobby));
                                });
                                break;
                            }
                            // در ClientConnection.readerThread (case ASSIGN_ID)
                            case "ASSIGN_ID": {
                                JsonNode pidNode = node.get("playerId");
                                if (pidNode != null) {
                                    String assigned = pidNode.asText();
                                    System.out.println("Assigned ID: " + assigned);
                                    // فرض: کلاس shared.model.Session یا محلی که user info نگه میداری
                                    Session.setCurrentPlayerId(assigned); // پیاده‌سازیش پایین توضیح داده شده
                                    System.out.println("Assigned playerId = " + assigned);
                                }
                                break;
                            }


                            case "ACK": {
                                // در صورت نیاز پردازش ACK
                                break;
                            }
                            default: {
                                // پیام خام یا دلخواه — می‌تونی لاگ کنی یا پردازش کنی
                                System.out.println("ClientConnection: unknown message type: " + type);
                            }
                        }
                    } catch (Exception je) {
                        System.err.println("ClientConnection: failed parsing JSON line: " + je.getMessage());
                        je.printStackTrace();
                    }
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("ClientConnection: reader IO error: " + e.getMessage());
                }
            } finally {
                closeSilently();
            }
        }, "ClientConnection-Reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }
    private static volatile io.github.some_example_name.shared.model.GameState pendingState = null;
    private void onGameStateReceived(GameState state) {
        System.out.println("✅ GameState received (raw) -> " + (state == null ? "null" : "players=" + state.getPlayerPositions().size()));
            GameView gameView = InsideLobbyScreen.getGameView();

        // اگر UI می‌خوای آپدیت کنی، باید داخل نخ گرافیکی باشه:
        Gdx.app.postRunnable(() -> {
            if (state == null) {
                // فقط لاگ باشه
//                statusLabel.setText("Received empty game state");
                return;
            }

            // 1) اگر هنوز وارد GameView نشدیم، وضعیت رو ذخیره کن و لِیبل لابی رو آپدیت کن
            if (gameView == null) {
                pendingState = state;
                // آپدیت تعداد پلیرها در لابی (اختیاری)
                int n = state.getPlayerPositions().size();
//                statusLabel.setText("⏳ Waiting players... current players: " + n);
                System.out.println("Lobby: saved pendingState, players=" + n);
                return;
            }

            // 2) اگر GameView فعال است، مستقیم اعمال کن
            try {
                // اگر GameView.applyGameState() خصوصی است، آن را به public تغییر بده
                gameView.applyGameState(state);
                System.out.println("GameView: applied state, players=" + state.getPlayerPositions().size());
            } catch (Exception ex) {
                System.err.println("Failed to apply state to GameView: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

//    public static void onGameStateReceived(GameState state) {
//        Gdx.app.postRunnable(() -> {
//            if (state == null) {
////                statusLabel.setText("Received empty game state");
//                return;
//            }
//
//            GameView gameView = InsideLobbyScreen.getGameView();
//
//            // اول: آپدیت pendingState همیشه مفید است
//            pendingState = state;
//
//            // آیا لابی ما در این state وجود دارد؟
//            LobbyInfo myLobby = null;
//            if (state.getLobbies() != null) {
//                for (LobbyInfo li : state.getLobbies()) {
//                    if (li != null && li.getId() != null && li.getId().equals(lobby.getId())) {
//                        myLobby = li;
//                        break;
//                    }
//                }
//            }
//
//            // اگر لابی پیدا نشد، فقط UI را آپدیت کن و بیرون برو
//            if (myLobby == null) {
////                statusLabel.setText("Waiting players... current players: " + (state.getPlayerPositions() == null ? 0 : state.getPlayerPositions().size()));
//                return;
//            }
//
//            // اگر لابی started == true و هنوز داخل GameView نیستیم -> شروع محلی بازی
//            if (myLobby.isStarted() && gameView == null) {
//                System.out.println("Detected lobby started via STATE -> starting locally for player.");
//
//                // playerId خودمان را از Session بگیر (حتماً ASSIGN_ID باید ذخیره شده باشد)
//                String myPlayerId = Session.getCurrentPlayerId();
//                if (myPlayerId == null) {
//                    System.err.println("Cannot start local game: Session.currentPlayerId is null!");
//                    return;
//                }
//
//                // ایجاد و ورود به بازی
////                startLocalGame(myPlayerId,null, state);
//                return;
//            }
//
//            if(gameView == null) {
//                System.out.println("Detected lobby started via STATE -> starting locally for player.");
//            }
//            // اگر قبلاً gameView ساخته شده بود، فقط state رو اعمال کن
//            if (gameView != null) {
//                try {
//                    System.out.println("calling...");
//                    if(state==null){
//                        System.out.println("state==null");
//                    }
//                    gameView.applyGameState(state);
//                } catch (Exception ex) {
//                    System.err.println("Failed to apply state to GameView: " + ex.getMessage());
//                    ex.printStackTrace();
//                }
//            } else {
//                // update UI: تعداد پلیرها نمایش داده شود
////                statusLabel.setText("Waiting players... current players: " + (state.getPlayerPositions() == null ? 0 : state.getPlayerPositions().size()));
//            }
//        });
//    }

    /**
     * ارسال اکشن به سرور (به صورت JSON در یک خط)
     */
    public synchronized void sendAction(Action a) throws IOException {
        if (!isConnected()) throw new IOException("not connected");
        String json = mapper.writeValueAsString(a);
        System.out.println("ClientConnection: sent action: " + json);
        out.println(json); // println اضافهٔ '\n' را می‌زند
        out.flush();
    }

    public static boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    /** بستن اتصال */
    public synchronized void close() {
        running = false;
        closeSilently();
        if (readerThread != null) {
            try {
                readerThread.join(200);
            } catch (InterruptedException ignored) {}
        }
    }

    private void closeSilently() {
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (reader != null) reader.close(); } catch (Exception ignored) {}
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
        out = null; reader = null; socket = null;
    }

    public static synchronized void sendMessage(java.util.Map<String,Object> msg) throws IOException {
        if (!isConnected()) throw new IOException("not connected");
        String json = mapper.writeValueAsString(msg);
        out.println(json);
        out.flush();
    }

    // ثبت callback در ClientConnection
    private Consumer<List<LobbyInfo>> onLobbies;
    public void setOnLobbies(Consumer<List<LobbyInfo>> listener) { this.onLobbies = listener; }

    // ارسال درخواستی برای لیست
    public void requestLobbies() throws IOException {
        sendMessage(Map.of("type","REQUEST_LOBBIES"));
    }

    public void sendCreateLobby(String name, int maxPlayers, boolean priv, String password) throws IOException {
        sendMessage(Map.of("type","CREATE_LOBBY","name",name,"maxPlayers",maxPlayers,"private",priv,"password",password));
    }

    public static void sendJoinLobby(String lobbyId, String username, String password) throws IOException {
        sendMessage(Map.of("type","JOIN_LOBBY","lobbyId", lobbyId, "username", username, "password", password));
    }


    public void sendReady(String playerId) throws IOException {
        System.out.println("ClientConnection: sendReady");
        sendMessage(java.util.Map.of("type","READY","playerId", playerId));

    }

    public static void send(String message) {
        System.out.println("ClientConnection: sent message: " + message);
        out.println(message);
    }

    public void requestLobbyList(Consumer<List<LobbyInfo>> callback) {
        System.out.println("ClientConnection: requestLobbyList");
        // ارسال دستور به سرور برای درخواست لیست لابی‌ها
        out.println("{\"type\":\"REQUEST_LOBBIES\"}");


        // وقتی جواب سرور رسید (مثلاً در متد handleMessage یا چیزی مشابه)
        // جواب رو باید به لیست لابی‌ها تبدیل کنید و callback.accept(lobbies) را صدا بزنید
        this.lobbyListCallback = callback;
    }

    // فرض کنیم این متد در ClientConnection برای دریافت پیام سرور هست
    private void handleServerMessage(String message) {
        if (message.startsWith("REQUEST_LOBBIES")) {
            List<LobbyInfo> lobbies = parseLobbyList(message);
            if (lobbyListCallback != null) {
                lobbyListCallback.accept(lobbies);
            }
        }
    }


    private List<LobbyInfo> parseLobbyList(String message) {
        System.out.println("ClientConnection: parseLobbyList: "+message);
        List<LobbyInfo> lobbies = new ArrayList<>();

        // حذف پیش‌وند پیام
        String data = message.substring("REQUEST_LOBBIES".length()).trim();

        if (data.isEmpty()) return lobbies;

        String[] lobbyStrings = data.split(";");

        for (String lobbyStr : lobbyStrings) {
            String[] parts = lobbyStr.split("\\|");
            if (parts.length < 3) continue;

            String name = parts[0];
            int playersCount = Integer.parseInt(parts[1]);
            String id = parts[2];

            // ساخت LobbyInfo — فرض می‌کنیم سازنده مناسب داری
            LobbyInfo lobby = new LobbyInfo();
            // برای ساده‌سازی، فقط سایز پلیرها رو ست کردیم (باید لیست پلیرها رو هم درست کنی اگر لازمه)
            lobbies.add(lobby);
        }

        return lobbies;
    }


}
