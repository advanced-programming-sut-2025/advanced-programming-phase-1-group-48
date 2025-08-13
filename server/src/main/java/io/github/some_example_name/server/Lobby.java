package io.github.some_example_name.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.some_example_name.shared.model.GameState;
import io.github.some_example_name.shared.model.LobbyInfo;
import io.github.some_example_name.shared.model.actions.Action;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Lobby {
    private final String id;
    private final String name;
    private final int maxPlayers;
    private final boolean isPrivate;
private final String password;
private final String creatorPlayerId;
    private final Set<String> readyPlayers = Collections.synchronizedSet(new HashSet<>());
    private final List<PlayerConnection> players = new CopyOnWriteArrayList<>();
    private volatile boolean gameStarted = false;

    private final GameState gameState = MainServer.getGameState();
    private GameInstance instance;

    private final ObjectMapper mapper = new ObjectMapper();
private String lobbyId;
    private boolean isVisible;
    private String adminId;

    public Lobby(String lobbyId,String name,int maxPlayers, boolean isPrivate,String password, String creatorPlayerId) {
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.isPrivate = isPrivate;
        this.id = UUID.randomUUID().toString();
        this.lobbyId = lobbyId;
        this.password = password;
        this.creatorPlayerId = creatorPlayerId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getMaxPlayers() { return maxPlayers; }
    public boolean isPrivate() { return isPrivate; }
    public int getPlayersCount() { return players.size(); }
    public boolean isGameStarted() { return gameStarted; }

    public GameState getGameState() { return gameState; }

    public synchronized boolean addPlayer(Socket socket) {
        System.out.println("uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
        System.out.println("addPlayer: " + socket);
        if (gameStarted || players.size() >= maxPlayers) return false;
        String pid = "player" + (players.size() + 1);
        try {
            PlayerConnection pc = new PlayerConnection(socket, this, pid);
            players.add(pc);
            pc.startListening();

            // مقداردهی اولیه موقعیت بازیکن
            String mapName = "farm1.tmx";
            int startX = 0;
            int startY = 0;
            synchronized (gameState) {
                gameState.addPlayer(pid, mapName, startX, startY);
            }

            // ارسال وضعیت اولیه به بازیکن
            String init = mapper.writeValueAsString(Map.of("type", "STATE", "payload", gameState));
            pc.sendRaw(init);

            System.out.println("Player joined lobby (" + players.size() + "/" + maxPlayers + ") id=" + pid);

            if (players.size() == maxPlayers) {
                startGameAndNotify();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private synchronized void startGameAndNotify() {
        if (gameStarted) return;
        gameStarted = true;
        System.out.println("🎮 Sending START to " + players.size() + " players...");

        for (PlayerConnection pc : players) {
            String mapName = "farm1.tmx";
            String msg = String.format("{\"type\":\"START\",\"map\":\"%s\",\"playerId\":\"%s\"}", mapName, pc.getPlayerId());
            pc.sendRaw(msg);
            System.out.println("→ Sent START to " + pc.getPlayerId() + ": " + msg);
        }

        readyPlayers.clear();
        System.out.println("Waiting for READY from players...");
    }

    public synchronized void onPlayerReady(PlayerConnection pc) {
        readyPlayers.add(pc.getPlayerId());
        System.out.println("Player READY: " + pc.getPlayerId() + " (" + readyPlayers.size() + "/" + players.size() + ")");
        if (readyPlayers.size() == players.size()) {
            actuallyStartGameInstance();
        }
    }

    private synchronized void actuallyStartGameInstance() {
        if (instance != null) return;
        System.out.println("All players READY — starting GameInstance.");
        instance = new GameInstance(players, gameState, 50);
        new Thread(instance, "GameInstance-" + id).start();
    }

    public synchronized void onActionReceived(PlayerConnection from, Action action) {
        System.out.println("Received action from " + from.getPlayerId() + ": " + action);
        synchronized (gameState) {
            gameState.apply(action);
        }
        try {
            String ack = mapper.writeValueAsString(Map.of("type", "ACK", "actionId", action.getId()));
            from.sendRaw(ack);
        } catch (Exception ignored) {}
    }

    public synchronized void onPlayerDisconnected(PlayerConnection pc) {
        System.out.println("Player disconnected: " + pc.getPlayerId());
        players.remove(pc);
        readyPlayers.remove(pc.getPlayerId());
        if (players.isEmpty() && instance != null) {
            instance.stop();
            instance = null;
            gameStarted = false;
        }
    }

    /** ارسال وضعیت بازی به همه بازیکنان */
    public void broadcastGameState() {
        String stateJson;
        try {
            synchronized (gameState) {
                stateJson = mapper.writeValueAsString(Map.of("type", "STATE", "payload", gameState));
            }
            for (PlayerConnection pc : players) {
                pc.sendRaw(stateJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ارسال پیام متنی به همه بازیکنان */
    public void broadcastMessage(String message) {
        for (PlayerConnection pc : players) {
            pc.sendRaw(message);
        }
    }

    /** ارسال حرکت به همه بازیکنان (مثال) */
    public void broadcastMove(io.github.some_example_name.shared.model.actions.MoveAction move) {
        try {
            String msg = mapper.writeValueAsString(Map.of("type", "MOVE", "payload", move));
            for (PlayerConnection pc : players) {
                pc.sendRaw(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<String> getReadyPlayers() {
        return readyPlayers;
    }

    public List<PlayerConnection> getPlayers() {
        return players;
    }

    public GameInstance getInstance() {
        return instance;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public synchronized void removePlayer(Socket socket) {
        PlayerConnection toRemove = null;
        for (PlayerConnection pc : players) {
            if (pc.getSocket().equals(socket)) {
                toRemove = pc;
                break;
            }
        }
        if (toRemove != null) {
            System.out.println("Removing player: " + toRemove.getPlayerId());
            players.remove(toRemove);
            readyPlayers.remove(toRemove.getPlayerId());

            if (players.isEmpty() && instance != null) {
                instance.stop();
                instance = null;
                gameStarted = false;
                System.out.println("All players left. Game stopped.");
            }
        } else {
            System.out.println("Player with given socket not found.");
        }
    }


    public LobbyInfo toLobbyInfo() {
        LobbyInfo info = new LobbyInfo();
        info.setId(this.id);
        info.setName(this.name);
        info.setMaxPlayers(this.maxPlayers);
        info.setPrivate(this.isPrivate);
        info.setVisible(this.isVisible);
        info.setAdminId(this.adminId);
        synchronized (players) {
            for (PlayerConnection p : players) {
                info.addPlayer(p.getPlayerId());
            }
        }
        return info;
    }

    public String getAdminId() {
        return adminId;
    }

    public String getLobbyId() {
        return lobbyId;
    }
}
