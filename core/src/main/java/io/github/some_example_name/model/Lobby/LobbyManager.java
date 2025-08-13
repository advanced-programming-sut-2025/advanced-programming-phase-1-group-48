package io.github.some_example_name.model.Lobby;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import io.github.some_example_name.model.Session;
import io.github.some_example_name.screens.LobbyScreen;
import io.github.some_example_name.screens.MainMenuScreen;
import io.github.some_example_name.shared.model.database.DatabaseLobbyManager;
import org.json.JSONObject;

import io.github.some_example_name.network.ClientConnection;
import java.io.IOException;
import java.util.*;
import io.github.some_example_name.shared.model.LobbyInfo;

public class LobbyManager {
    private static final Map<String, LobbyInfo> lobbies = new HashMap<>();

    public static void addLobby(LobbyInfo lobby) {
        lobbies.put(lobby.getId(), lobby);
    }

    public static void removeLobby(String id) {
        lobbies.remove(id);
        DatabaseLobbyManager.deleteLobby(id);
    }

    public static LobbyInfo getLobbyById(String id) {
        return lobbies.get(id);
    }

    public static List<LobbyInfo> getVisibleLobbies() {
        List<LobbyInfo> result = new ArrayList<>();
        for (LobbyInfo lobby : lobbies.values()) {
            if (lobby.isVisible()) result.add(lobby);
        }
        return result;
    }

    public static LobbyInfo joinById(String id, String password, String username) {
        LobbyInfo lobby = getLobbyById(id);
        if (lobby == null) return null;
        if (lobby.isPrivate() && !lobby.checkPassword(password)) return null;
        lobby.addPlayer(username);
        return lobby;
    }

    public static void cleanEmptyLobbies() {
        long now = System.currentTimeMillis();
        List<String> toRemove = new ArrayList<>();

        for (LobbyInfo lobby : lobbies.values()) {
            boolean shouldRemove = false;

            if (lobby.getPlayers().isEmpty()) {
                shouldRemove = true;
            } else if (!lobby.isStarted()) {
                shouldRemove = true;
            }

            if (shouldRemove) {
                toRemove.add(lobby.getId());
            }
        }

        for (String id : toRemove) {
            lobbies.remove(id);
            DatabaseLobbyManager.deleteLobby(id); // ← حذف از دیتابیس
        }
    }


    public static void createLobby(String name, boolean isPrivate, boolean isVisible, String password, String creatorUsername) throws IOException {
        // داخل ok listener
        Map<String,Object> lobbyData = new HashMap<>();
        lobbyData.put("type", "CREATE_LOBBY");
        lobbyData.put("name", name);
        lobbyData.put("private", isPrivate);
        lobbyData.put("visible", isVisible);
        lobbyData.put("password", password);
        lobbyData.put("maxPlayers", 4);
        lobbyData.put("playerId", Session.getCurrentUser().getUsername()); // مهم!
        MainMenuScreen.getClient().sendMessage(lobbyData); // یا send(json)

    }

    public static Collection<LobbyInfo> getAllLobbies() {
        return lobbies.values();
    }
    public static boolean lobbyExists(String lobbyId) {
        return lobbies.containsKey(lobbyId);
    }

    public static void saveAllLobbiesToDatabase() {
        for (LobbyInfo lobby : lobbies.values()) {
            DatabaseLobbyManager.saveLobby(lobby);
        }
    }

    public static void loadAllLobbiesFromDatabase() {
        List<LobbyInfo> loaded = DatabaseLobbyManager.loadAllVisibleLobbies();
        for (LobbyInfo lobby : loaded) {
            lobbies.put(lobby.getId(), lobby);
        }
    }

    private static List<LobbyInfo> visibleLobbies = new ArrayList<>();

    public static void setVisibleLobbies(List<LobbyInfo> lobbiesFromServer) {
        System.out.println("setVisibleLobbies: received " + lobbiesFromServer.size() + " lobbies from server");

        visibleLobbies.clear();
        visibleLobbies.addAll(lobbiesFromServer);

        for (LobbyInfo lobby : visibleLobbies) {
            System.out.println("Adding lobby: " + lobby.getName() + " (" + lobby.getPlayers() + "/" + lobby.getMaxPlayers() + ")");
        }

        // آپدیت مستقیم UI بعد از دریافت
        updateLobbyList();
    }

    public static List<LobbyInfo> getVisibleLobby(){
        return visibleLobbies;
    }

    public static void updateLobbyList() {
        System.out.println("updateLobbyList: visible lobbies count = " + visibleLobbies.size());

        LobbyScreen.getLobbyListTable().clear();  // پاک کردن لیست قبلی UI

        for (LobbyInfo lobby : visibleLobbies) {
            String labelText = lobby.getName() + " | Players: " + lobby.getPlayers() + " / " + lobby.getMaxPlayers() + " | ID: " + lobby.getId();
            Label lobbyLabel = new Label(labelText, LobbyScreen.getSkin());
            TextButton joinBtn = new TextButton("Join", LobbyScreen.getSkin());

            Table row = new Table(LobbyScreen.getSkin());
            row.add(lobbyLabel).expandX().left();
            row.add(joinBtn).right();

            LobbyScreen.getLobbyListTable().add(row).fillX().pad(5).row();

            final LobbyInfo currentLobby = lobby;

            joinBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    System.out.println("Joining lobby: " + currentLobby.getName());

                    System.out.println(currentLobby.getId()+": "+currentLobby.getPassword()+":"+Session.getCurrentUser().getUsername());
                    try {
                        sendJoinLobbyRequest(currentLobby.getId(), "", Session.getCurrentUser().getUsername());
                        System.out.println("joining...");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        }
    }

    public static void sendJoinLobbyRequest(String lobbyId, String password, String username) throws IOException {
        ClientConnection.sendJoinLobby(lobbyId, username, password);

    }







}
