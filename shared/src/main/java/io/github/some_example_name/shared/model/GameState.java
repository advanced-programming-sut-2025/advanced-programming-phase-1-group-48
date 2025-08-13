package io.github.some_example_name.shared.model;

import io.github.some_example_name.shared.model.actions.Action;
import io.github.some_example_name.shared.model.actions.MoveAction;

import java.io.Serializable;
import java.util.*;

public class GameState implements Serializable {
    private Map<String, int[]> playerPositions = new HashMap<>();
    private Map<String, String> playerMapIds = new HashMap<>(); // playerId → mapId
    private static List<LobbyInfo> lobbies = new ArrayList<>();
    private List<String> playerIds = new ArrayList<>();
    // --- لابی‌ها ---
    public static List<LobbyInfo> getLobbies() {
        return lobbies;
    }

    public void setLobbies(List<LobbyInfo> lobbies) {
        this.lobbies = lobbies;
    }

    public LobbyInfo getLobby(String id) {
        System.out.println("id lobby: " + id);
        for (LobbyInfo li : lobbies) {
            System.out.println("lobby: " + li+"id: " + li.getId());
            if (li.getId().equals(id)) {
                return li;
            }
        }
        return null;
    }

    public void addLobby(LobbyInfo lobby) {
        lobbies.add(lobby);
    }

    public void removeLobby(String id) {
        lobbies.removeIf(li -> li.getId().equals(id));
    }

    public boolean joinLobby(String lobbyId, String playerId) {
        LobbyInfo li = getLobby(lobbyId);
        if (li == null) return false;
        synchronized (li) {
            if (li.isFull()) return false;
            li.addPlayer(playerId);
            return true;
        }
    }

    public void leaveLobby(String lobbyId, String playerId) {
        LobbyInfo li = getLobby(lobbyId);
        if (li != null) {
            li.removePlayer(playerId);
        }
    }

    // --- پلیرها ---
    public Map<String, String> getPlayerMapIds() {
        return playerMapIds;
    }

    public synchronized void addPlayer(String playerId, String mapId, int x, int y) {

        setPlayerMapId(playerId, mapId);
        setPlayerPosition(playerId, x, y);
    }

    public synchronized void removePlayerId(String playerId) {
        if (playerId == null) return;
        playerIds.remove(playerId);
        System.out.println("GameState: removed playerId " + playerId);
    }

    public synchronized boolean hasPlayerId(String playerId) {
        return playerIds.contains(playerId);
    }

    public synchronized List<String> getPlayerIds() {
        return new ArrayList<>(playerIds);
    }

    public synchronized void addPlayerId(String playerId) {
        if (playerId == null) return;
        if (!playerIds.contains(playerId)) playerIds.add(playerId);
        System.out.println("GameState: added playerId " + playerId);
    }

    public void apply(Action action) {
        if (action instanceof MoveAction) {
            MoveAction move = (MoveAction) action;
            int[] pos = playerPositions.getOrDefault(move.getPlayerId(), new int[]{0, 0});
            pos[0] += move.getDx();
            pos[1] += move.getDy();
            playerPositions.put(move.getPlayerId(), pos);
        }
    }

    public synchronized void applyMove(String playerId, int dx, int dy) {
        int[] pos = playerPositions.getOrDefault(playerId, new int[]{0,0});
        pos[0] += dx;
        pos[1] += dy;
        playerPositions.put(playerId, pos);
        System.out.println("applyMove " + playerId + " -> " + dx + "," + dy);
    }

    public synchronized void setPlayerPosition(String playerId, int x, int y) {
        playerPositions.put(playerId, new int[]{x, y});
        System.out.println("add position: " + playerPositions.size());
    }

    public synchronized Map<String, int[]> getPlayerPositions() {
        return Collections.unmodifiableMap(new HashMap<>(playerPositions));
    }

    public synchronized void setPlayerMapId(String playerId, String mapId) {
        playerMapIds.put(playerId, mapId);
    }

}
