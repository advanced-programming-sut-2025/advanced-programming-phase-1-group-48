package io.github.some_example_name.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseSavedGameManager {
    private final Connection conn;
    private final ObjectMapper mapper = new ObjectMapper();

    public DatabaseSavedGameManager(Connection conn) throws SQLException {
        this.conn = conn;
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS saved_games (id TEXT PRIMARY KEY, name TEXT NOT NULL, lobby_id TEXT, players_json TEXT, state_json TEXT, saved_at INTEGER NOT NULL, status TEXT NOT NULL, version INTEGER NOT NULL)");
        }
    }

    public void saveSavedGame(SavedGame sg) throws Exception {
        String sql = "INSERT INTO saved_games(id,name,lobby_id,players_json,state_json,saved_at,status,version) VALUES (?,?,?,?,?,?,?,?)";
        String playersJson = mapper.writeValueAsString(sg.getPlayers());
        String stateJson = mapper.writeValueAsString(sg.getSnapshot());
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sg.getId());
            ps.setString(2, sg.getName());
            ps.setString(3, sg.getLobbyId());
            ps.setString(4, playersJson);
            ps.setString(5, stateJson);
            ps.setLong(6, sg.getSavedAt());
            ps.setString(7, sg.getStatus());
            ps.setInt(8, sg.getVersion());
            ps.executeUpdate();
        }
    }

    public SavedGame loadSavedGame(String id) throws Exception {
        String sql = "SELECT * FROM saved_games WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                SavedGame sg = new SavedGame();
                sg.setId(rs.getString("id"));
                sg.setName(rs.getString("name"));
                sg.setLobbyId(rs.getString("lobby_id"));
                sg.setPlayers(Arrays.asList(mapper.readValue(rs.getString("players_json"), String[].class)));
                GameStateSnapshot snap = mapper.readValue(rs.getString("state_json"), GameStateSnapshot.class);
                sg.setSnapshot(snap);
                sg.setSavedAt(rs.getLong("saved_at"));
                sg.setStatus(rs.getString("status"));
                sg.setVersion(rs.getInt("version"));
                return sg;
            }
        }
    }

    public List<SavedGame> listSavedGamesForLobby(String lobbyId) throws Exception {
        String sql = "SELECT * FROM saved_games WHERE lobby_id = ?";
        List<SavedGame> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lobbyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SavedGame sg = new SavedGame();
                    sg.setId(rs.getString("id"));
                    sg.setName(rs.getString("name"));
                    sg.setLobbyId(rs.getString("lobby_id"));
                    sg.setPlayers(Arrays.asList(mapper.readValue(rs.getString("players_json"), String[].class)));
                    sg.setSnapshot(mapper.readValue(rs.getString("state_json"), GameStateSnapshot.class));
                    sg.setSavedAt(rs.getLong("saved_at"));
                    sg.setStatus(rs.getString("status"));
                    sg.setVersion(rs.getInt("version"));
                    out.add(sg);
                }
            }
        }
        return out;
    }
}
