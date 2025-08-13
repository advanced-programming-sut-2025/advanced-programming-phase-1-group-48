package io.github.some_example_name.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.some_example_name.shared.model.GameState;
import io.github.some_example_name.shared.model.LobbyInfo;
import io.github.some_example_name.shared.model.PlayerModel;
import io.github.some_example_name.shared.model.actions.MoveAction;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MainServer {
    private static final int PORT = 8080;
    private static final GameState gameState = new GameState();
    private final ObjectMapper mapper = new ObjectMapper();

    // Thread-safe lists for clients and lobbies
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Lobby> lobbies = new CopyOnWriteArrayList<>();

    private final ScheduledExecutorService broadCaster = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<String, ClientHandler> playerMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new MainServer().start();
    }

    public static GameState getGameState() {
        return gameState;
    }

    public void start() {
        // start broadcaster for game state
        broadCaster.scheduleAtFixedRate(this::broadcastState, 0, 50, TimeUnit.MILLISECONDS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);

                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            broadCaster.shutdown();
        }
    }

    // ارسال وضعیت بازی به همه کلاینت‌ها
    private void broadcastState() {
        try {
            if (gameState.getPlayerPositions() == null || gameState.getPlayerPositions().isEmpty()) {
                return; // وضعیت خالی ارسال نمی‌شود
            }
            String json = mapper.writeValueAsString(Map.of("type", "STATE", "payload", gameState));
            for (ClientHandler c : clients) {
                c.send(json);
            }
        } catch (Exception e) {
            System.err.println("broadcastState failed: " + e.getMessage());
        }
    }

    // ارسال لیست لابی‌ها به همه کلاینت‌ها
//    private void broadcastLobbies() {
//        try {
//            System.out.println("broadcastLobbies");
//            List<Map<String, Object>> dtos = lobbies.stream()
//                .map(l -> {
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("id", l.getId());
//                    map.put("name", l.getName());
//                    map.put("players", l.getPlayers().size());
//                    map.put("maxPlayers", l.getMaxPlayers());
//                    map.put("private", l.isPrivate());
//                    return map;
//                })
//                .collect(Collectors.toList());
//
//            String json = mapper.writeValueAsString(Map.of("type", "LOBBIES", "payload", dtos));
//            for (ClientHandler c : clients) {
//                c.send(json);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void broadcastLobbies() {
        try {
            String json = mapper.writeValueAsString(
                Map.of("type", "LOBBIES", "payload", lobbies.stream()
                    .map(Lobby::toLobbyInfo) // متدی که Lobby رو به LobbyInfo تبدیل کنه
                    .collect(Collectors.toList()))
            );
            for (ClientHandler c : clients) {
                c.send(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ارسال لیست لابی‌ها فقط به یک کلاینت خاص
    private void sendLobbiesToClient(ClientHandler client) {
        try {
            System.out.println("sendLobbiesToClient");
            List<Map<String, Object>> dtos = gameState.getLobbies().stream()
                .map(l -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", l.getId());
                    map.put("name", l.getName());
                    map.put("players", l.getPlayers().size());
                    map.put("maxPlayers", l.getMaxPlayers());
                    map.put("private", l.isPrivate());
                    return map;
                })
                .collect(Collectors.toList());

            String json = mapper.writeValueAsString(Map.of("type", "LOBBIES", "payload", dtos));
            client.send(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // کلاس داخلی برای هندل کردن هر کلاینت
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private Lobby joinedLobby = null;
        private final String playerId = UUID.randomUUID().toString();

        ClientHandler(Socket socket) throws JsonProcessingException {
            this.socket = socket;
            System.out.println("ClientHandler started for: " + socket.getInetAddress());
            playerMap.put(playerId, this);
            gameState.addPlayerId(playerId);
            // می‌تونی این playerId رو به کلاینت بفرستی بلافاصله:
//            try {
//                String assignJson = mapper.writeValueAsString(Map.of("type","ASSIGN_ID","playerId", playerId));
//                out.println(assignJson);
//                System.out.println("Client connected: " + assignJson);
//                out.flush();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            send(mapper.writeValueAsString(Map.of("type","ASSIGN_ID","playerId", playerId)));
        }

        public void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                try {
                    String assignJson = mapper.writeValueAsString(Map.of("type","ASSIGN_ID","playerId", playerId));
                    out.println(assignJson);
                    out.flush();
                    System.out.println("Sent ASSIGN_ID to " + socket.getInetAddress() + ": " + assignJson);
                } catch (Exception e) {
                    System.err.println("Failed to send ASSIGN_ID: " + e.getMessage());
                    e.printStackTrace();
                }
                // ارسال وضعیت اولیه بازی (اختیاری)
                if (gameState.getPlayerPositions() != null && !gameState.getPlayerPositions().isEmpty()) {
                    String initState = mapper.writeValueAsString(Map.of("type", "STATE", "payload", gameState));
                    send(initState);
                }

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;

                    try {
                        JsonNode node = mapper.readTree(line);
                        if (!node.has("type")) {
                            System.err.println("Message without type: " + line);
                            continue;
                        }

                        String type = node.get("type").asText();

                        switch (type) {
                            case "move":
                                handleMove(node);
                                break;
                            case "CREATE_LOBBY":
                                System.out.println("Create lobby");
                                handleCreateLobby(node);
                                break;
                            case "REQUEST_LOBBIES":
                                System.out.println("Request lobbies");
                                sendLobbiesToClient(this);
                                break;
                            case "JOIN_LOBBY":
                                System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyy");
                                System.out.println("Join lobby");
                                handleJoinLobby(node);
                                break;
                            case "START_GAME":
                                System.out.println("Start game*");
                                handleStartGame(node);
                                break;
                            case "READY":
                                System.out.println("Player is ready.");
                                break;

                            default:
                                System.err.println("Unknown message type: " + type);
                        }
                    } catch (Exception ex) {
                        System.err.println("Error parsing message: " + ex.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("Client disconnected: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void handleMove(JsonNode node) throws IOException {
            MoveAction mv = mapper.treeToValue(node, MoveAction.class);
            synchronized (gameState) {
                gameState.applyMove(mv.getPlayerId(), mv.getDx(), mv.getDy());
            }
            String moveJson = mapper.writeValueAsString(mv);
            for (ClientHandler client : clients) {
                client.send(moveJson);
            }
        }

        private void handleCreateLobby(JsonNode node) {
            try {
                System.out.println("Create lobby2");
                String name = node.has("name") ? node.get("name").asText() : "Lobby";
                int maxPlayers = node.has("maxPlayers") ? node.get("maxPlayers").asInt() : 4;
                boolean priv = node.has("private") && node.get("private").asBoolean();
                boolean visible = node.has("visible") && node.get("visible").asBoolean();
                String password = node.has("password") ? node.get("password").asText() : null;
                String creatorPlayerId = node.has("playerId") ? node.get("playerId").asText() : this.playerId;


                String lobbyId = UUID.randomUUID().toString();

                Lobby serverLobby = new Lobby(lobbyId, name, maxPlayers, priv, password, creatorPlayerId);
                System.out.println("serverlobby: " + serverLobby.getLobbyId());
                lobbies.add(serverLobby);

                LobbyInfo dto = new LobbyInfo(lobbyId, name, maxPlayers, priv, creatorPlayerId);
                dto.setVisible(visible);
                dto.addPlayer(creatorPlayerId);// سازنده اولین پلیر است
                dto.setAdminId(this.playerId); // یا username
                dto.setAdminUsername(this.playerId);
                gameState.addLobby(dto);

                this.send(mapper.writeValueAsString(Map.of("type","CREATED_LOBBY","payload", dto)));
                broadcastLobbies();
            } catch (Exception e) {
                System.err.println("Failed to create lobby: " + e.getMessage());
            }
        }

        private void handleStartGame(JsonNode node) {
            try {
                System.out.println("111");
                String lobbyId = node.has("lobbyId") ? node.get("lobbyId").asText() : null;
                System.out.println(lobbyId);
                if (lobbyId == null) {
                    System.out.println("2222");
                    send(mapper.writeValueAsString(Map.of("type","ERROR","message","missing_lobbyId")));
                    return;
                }

                // پیدا کردن لابی سرور-side
//                Lobby serverLobby = lobbies.stream()
//                    .filter(l -> l.getId().equals(lobbyId))
//                    .findFirst()
//                    .orElse(null);
                Lobby serverL = null;
                for(Lobby lobby2 : lobbies) {
                    System.out.println("kkkk: " + lobby2.getLobbyId());
                    if(lobby2.getLobbyId().equals(lobbyId)) {
                        serverL = lobby2;
                        System.out.println("1010010");
                        break;
                    }
                }
                if (serverL == null) {
                    System.out.println("3333");
                    send(mapper.writeValueAsString(Map.of("type","ERROR","message","lobby_not_found","lobbyId", lobbyId)));
                    return;
                }

                // علامت زدن لابی به عنوان started (در DTO داخل GameState)
                LobbyInfo info = MainServer.getGameState().getLobby(lobbyId);
                if (info != null) {
                    System.out.println("5555");
                    info.setStarted(true);
                }

                // بررسی اینکه فرستنده ادمین هست یا نه
                String adminId = info.getAdminId(); // فرض: Lobby.getAdminId() وجود دارد و یک String است (playerId)
                String adminUsername = info.getAdminUsername();
                System.out.println(adminId+"++");
                if (adminId == null || !adminId.equals(this.playerId)) {
                    System.out.println(adminId+"++++"+this.playerId);
                    send(mapper.writeValueAsString(Map.of("type","ERROR","message","not_admin")));
                    return;
                }

                // شرایط شروع (اختیاری): حداقل بازیکن
                System.out.println(info.getPlayers().size());
                if (info.getPlayers().size() < 2) {
                    System.out.println("4444");
                    send(mapper.writeValueAsString(Map.of("type","ERROR","message","not_enough_players")));
                    return;
                }

                // انتخاب مپ (می‌تونی منطق پیچیده‌تری بذاری)
                String mapName = "farm1.tmx";

                // ارسال پیام START به هر عضو لابی
                // serverLobby.getPlayers() باید لیست playerId (String) باشد؛ اما اگر نوع دیگری برگشت می‌دهد، با احتیاط تبدیل می‌کنیم
                LobbyInfo linfo = MainServer.getGameState().getLobby(lobbyId);
                if (linfo == null) {
//                    System.out.println("handleStartGame: player " + pid + " not connected; skipping.");
                }
                Lobby lobby = new Lobby(lobbyId,linfo.getName(),4,linfo.isPrivate(),"password",linfo.getAdminId());
                for (String pid : info.getPlayers()) {
                    ClientHandler  socket= playerMap.get(pid);
                    lobby.addPlayer(socket.getSocket());
                }
                for (String pid : info.getPlayers()) {
                    ClientHandler recipient = playerMap.get(pid);
                    System.out.println("l89: "+ recipient);
                    if (recipient == null) {
                        System.out.println("handleStartGame: player " + pid + " not connected; skipping.");
                        continue;
                    }

                    Map<String,Object> msg = new HashMap<>();
                    msg.put("type", "START");
                    msg.put("map", mapName);
                    msg.put("playerId", pid);

                    String json = mapper.writeValueAsString(msg);
                    recipient.send(json);
                    System.out.println("handleStartGame: sent START to player " + pid+":::"+json);

                    Map<String,Object> msg2 = new HashMap<>();
                    msg2.put("type", "STATE");
                    msg2.put("payload", gameState); // اگر GameState به صورت POJO است، Jackson خودش JSON می‌کند

                    String jsonState = mapper.writeValueAsString(msg2);
                    recipient.send(jsonState);
                    System.out.println("handleStartGame: sent STATE to player " + pid);
                }

                // broadcast لابی‌ها تا همه بدانند این لابی started شده
                broadcastLobbies();

            } catch (Exception e) {
                System.err.println("handleStartGame failed: " + e.getMessage());
                e.printStackTrace();
                try {
                    send(mapper.writeValueAsString(Map.of("type","ERROR","message","start_failed","detail", e.getMessage())));
                } catch (Exception ignored) {}
            }
        }


        private void handleJoinLobby(JsonNode node) {

            try {
                System.out.println("handleJoinLobby");
                String lobbyId = node.get("lobbyId").asText();
                String user = node.has("username") ? node.get("username").asText() : this.playerId;
                Optional<Lobby> optLobby = lobbies.stream()
                    .filter(l -> l.getId().equals(lobbyId))
                    .findFirst();

                if (optLobby.isPresent()) {
                    System.out.println(optLobby.get().getLobbyId());
                    Lobby lobby = optLobby.get();
                    if(lobby==null) {
                        System.out.println("iiiiiiiiiiiiiiiiiiii");
                    }
                    boolean success = lobby.addPlayer(socket);
                    if (success) {
                        System.out.println("Lobby joined: " + lobby.getName());
                        System.out.println("Lobby id: " + lobby.getId()+"size: "+lobby.getPlayers().size());
                        // آپدیت GameState
                        gameState.joinLobby(lobbyId, this.playerId);
                        playerMap.put(this.playerId, this);
                        joinedLobby = lobby;
                        if(joinedLobby == null) {
                            System.out.println("nullllll");
                        }
                        Map<String,Object> payload = Map.of(
                            "status", "success",
                            "lobby", Map.of(
                                "id", lobby.getId(),
                                "name", lobby.getName(),
                                "players", lobby.getPlayers().size(),
                                "maxPlayers", lobby.getMaxPlayers(),
                                "private", lobby.isPrivate()
                                // هر فیلد دیگری که لازم داری
                            )
                        );
                        LobbyInfo lobbyInfo = gameState.getLobby(lobbyId);
                        lobbyInfo.addPlayer(playerId);
                        send(mapper.writeValueAsString(Map.of("type","JOIN_ACK","payload", Map.of("status","success","lobby", lobbyInfo))));
                        broadcastLobbies();
                    } else {
                        LobbyInfo lobbyInfo = gameState.getLobby(lobbyId);
                        lobbyInfo.addPlayer(playerId);
                        send(mapper.writeValueAsString(Map.of("type","JOIN_ACK","payload", Map.of("status","success","lobby", lobbyInfo))));
                    }
                } else {
                    LobbyInfo lobbyInfo = gameState.getLobby(lobbyId);
                    lobbyInfo.addPlayer(playerId);
                    send(mapper.writeValueAsString(Map.of("type","JOIN_ACK","payload", Map.of("status","success","lobby", lobbyInfo))));
                }
            } catch (Exception e) {
                System.err.println("Failed to join lobby: " + e.getMessage());
            }
        }

        private void cleanup() {
            clients.remove(this);
            playerMap.remove(playerId);
            gameState.removePlayerId(playerId);
            if (joinedLobby != null) {
//                joinedLobby.removePlayer(playerId); // فرض بر اینه که Lobby سرور-side متد مناسب داره
                MainServer.getGameState().leaveLobby(joinedLobby.getId(), playerId);
                joinedLobby.removePlayer(socket);
                broadcastLobbies();
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            System.out.println("Client disconnected: " + socket.getInetAddress());
        }

        public Socket getSocket() {
            return socket;
        }
    }


}
