package io.github.some_example_name.model.Lobby;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LobbyInfo {
    private String name;
    private String id;
    private boolean isPrivate;
    private boolean isVisible;
    private String password;

    // این مقدار را سرور به صورت عدد می‌فرستد ("players": 0)
    @JsonProperty("players")
    private int playersCount = 0;

    // این هم از سرور می‌آید اگر فرستاده شود
    @JsonProperty("maxPlayers")
    private int maxPlayers = 4;

    // لیست اسامی بازیکنان را خودِ کلاینت نگه می‌دارد (میتواند از DB یا بعداً از سرور پر شود)
    private final List<String> playersList = new ArrayList<>();

    private String adminUsername;
    private long lastJoinTime;
    private boolean started = false;

    // ctor پیش‌فرض برای Jackson
    public LobbyInfo() {}

    public LobbyInfo(String name, boolean isPrivate, boolean isVisible, String password, String creatorUsername) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.isPrivate = isPrivate;
        this.isVisible = isVisible;
        this.password = password;
        this.adminUsername = creatorUsername;
        this.playersList.add(creatorUsername);
        this.playersCount = this.playersList.size();
        this.lastJoinTime = System.currentTimeMillis();
    }

    // ---------- getters/setters برای فیلدها (Jackson به آنها نیاز دارد) ----------
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }

    public boolean isVisible() { return isVisible; }
    public void setVisible(boolean visible) { isVisible = visible; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // playersCount مپ به key "players" در JSON دارد
    @JsonProperty("players")
    public int getPlayersCount() { return playersCount; }
    @JsonProperty("players")
    public void setPlayersCount(int playersCount) {
        this.playersCount = playersCount;
        // توجه: playersList را تغییر نمیدهیم چون نام‌ها را نداریم؛ فقط شمارش را نگه می‌داریم
    }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }

    // این متد همان چیزی است که بقیه‌ی کد انتظار دارد: لیست اسامی
    // اگر playersList خالی باشد، می‌توان fallback به playersCount داشت (اختیاری)
    @JsonIgnore
    public List<String> getPlayers() { return playersList; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    public boolean isStarted() { return started; }
    public void setStarted(boolean started) { this.started = started; }

    public long getLastJoinTime() { return lastJoinTime; }
    public void setLastJoinTime(long lastJoinTime) { this.lastJoinTime = lastJoinTime; }

    // ---------- متدهای کاری که LobbyManager و DB انتظار دارند ----------
    public void addPlayer(String username) {
        if (username == null) return;
        if (!playersList.contains(username)) {
            playersList.add(username);
        }
        // هم شمارش را به‌روز کن
        playersCount = playersList.size();
        updateJoinTime();
    }

    public void removePlayer(String username) {
        playersList.remove(username);
        playersCount = playersList.size();
        if (playersList.isEmpty()) return;
        if (adminUsername != null && adminUsername.equals(username)) {
            // انتخاب ادمین جدید (اگر لازم بود)
            adminUsername = playersList.get(0);
        }
        updateJoinTime();
    }

    public boolean checkPassword(String input) {
        if (password == null) return input == null;
        return password.equals(input);
    }

    public void startGame() { this.started = true; }

    public void updateJoinTime() { this.lastJoinTime = System.currentTimeMillis(); }

    // برای نمایش در لیست لابی‌ها می‌تونی helper اضافه کنی
    @Override
    public String toString() {
        int visibleCount = playersList.isEmpty() ? playersCount : playersList.size();
        return name + " (" + visibleCount + "/" + maxPlayers + ")";
    }

}
