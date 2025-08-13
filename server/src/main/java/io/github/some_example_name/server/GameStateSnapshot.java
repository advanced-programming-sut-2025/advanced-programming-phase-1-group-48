package io.github.some_example_name.server;

import io.github.some_example_name.shared.model.LobbyInfo;

import java.util.List;
import java.util.Map;

public class GameStateSnapshot {
    private Map<String,int[]> playerPositions;
    private Map<String,String> playerMapIds;
    private List<LobbyInfo> lobbies;
    private List<String> playerIds;

    public GameStateSnapshot() {}

    // getters & setters
    public Map<String,int[]> getPlayerPositions() { return playerPositions; }
    public void setPlayerPositions(Map<String,int[]> playerPositions) { this.playerPositions = playerPositions; }
    public Map<String,String> getPlayerMapIds() { return playerMapIds; }
    public void setPlayerMapIds(Map<String,String> playerMapIds) { this.playerMapIds = playerMapIds; }
    public List<LobbyInfo> getLobbies() { return lobbies; }
    public void setLobbies(List<LobbyInfo> lobbies) { this.lobbies = lobbies; }
    public List<String> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
}
