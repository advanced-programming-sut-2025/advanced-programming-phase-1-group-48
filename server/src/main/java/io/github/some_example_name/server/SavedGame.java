package io.github.some_example_name.server;

import java.io.Serializable;
import java.util.List;

public class SavedGame implements Serializable {
    private String id;
    private String name;
    private String lobbyId;
    private List<String> players;
    private GameStateSnapshot snapshot; // optional: keep in-memory snapshot
    private long savedAt;
    private String status;
    private int version;

    public SavedGame() {}

    public SavedGame(String id, String name, String lobbyId, List<String> players, GameStateSnapshot snapshot, long savedAt) {
        this.id = id; this.name = name; this.lobbyId = lobbyId; this.players = players;
        this.snapshot = snapshot; this.savedAt = savedAt;
        this.status = "AVAILABLE"; this.version = 1;
    }

    // getters/setters...
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLobbyId() { return lobbyId; }
    public void setLobbyId(String lobbyId) { this.lobbyId = lobbyId; }
    public List<String> getPlayers() { return players; }
    public void setPlayers(List<String> players) { this.players = players; }
    public GameStateSnapshot getSnapshot() { return snapshot; }
    public void setSnapshot(GameStateSnapshot snapshot) { this.snapshot = snapshot; }
    public long getSavedAt() { return savedAt; }
    public void setSavedAt(long savedAt) { this.savedAt = savedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
