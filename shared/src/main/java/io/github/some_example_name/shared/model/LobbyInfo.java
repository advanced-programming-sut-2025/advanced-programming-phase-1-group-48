package io.github.some_example_name.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


@JsonIgnoreProperties(ignoreUnknown = true)
public class LobbyInfo {
    private String id;
    private String name;
    private int maxPlayers;
    private boolean isPrivate;
    private boolean isVisible;
    private String password;

    private int playerCount;


    private String adminId;

    private final List<String> players = Collections.synchronizedList(new ArrayList<>());
    private boolean started;
    private String adminUsername;

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    // --- سازنده‌ها ---
    public LobbyInfo() {
        // برای Jackson
    }

    public LobbyInfo(String id, String name, int maxPlayers, boolean isPrivate, String adminId) {
        this.id = id;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.isPrivate = isPrivate;
        this.adminId = adminId;
        this.started = false;
        this.adminId = adminId;
    }

    public LobbyInfo(String name, boolean isPrivate, boolean isVisible, String password, String creatorUsername) {
        this.name = name;
        this.isPrivate = isPrivate;
        this.password = password;
        this.started = false;
        this.adminId = creatorUsername;

    }


    // --- getters / setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public List<String> getPlayers() {
        synchronized (players) {
            return new ArrayList<>(players);
        }
    }

    public int getPlayersCount() {
        synchronized (players) {
            return players.size();
        }
    }

    // --- عملیات معمول روی لیست پلیرها ---
    public boolean addPlayer(String playerId) {
        System.out.println("addPlayer: " + players.size());
        synchronized (players) {
            if (players.contains(playerId)) return false;
            if (players.size() >= maxPlayers) return false;
            return players.add(playerId);
        }
    }

    public boolean removePlayer(String playerId) {
        synchronized (players) {
            return players.remove(playerId);
        }
    }

    public boolean isFull() {
        synchronized (players) {
            return players.size() >= maxPlayers;
        }
    }

    @Override
    public String toString() {
        return "LobbyInfo{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", maxPlayers=" + maxPlayers +
            ", isPrivate=" + isPrivate +
            ", adminId='" + adminId + '\'' +
            ", players=" + getPlayers() +
            '}';
    }

    public boolean isVisible() {return isVisible;}
    public boolean checkPassword(String password) {return password.equals(this.password);}
    public boolean isStarted() {return started;}
    public String getPassword() {return password;}

    public void setPlayersCount(int players) {
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }


    public void updateJoinTime() {

    }

    public void startGame() {
    }
}
