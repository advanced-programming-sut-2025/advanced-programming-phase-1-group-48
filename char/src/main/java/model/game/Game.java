package model.game;

import model.Player.Player;
import model.Weather.DateAndTime;
import model.enums.Season;
import model.enums.TileType;
import model.farm.FarmRegion;
import model.farm.FarmTemplate;
import model.intraction.Friendship;
import model.intraction.MarriageRequest;
import model.shop.ShopManager;
import model.user.User;
import model.user.UserManager;

import java.util.*;

public class Game {
    private static ShopManager shopManager = new ShopManager();
    private final Map<Player, Integer> selectedMaps = new LinkedHashMap<>();
    private final Map<Player, FarmTemplate> finalizedTemplates = new HashMap<>();
    public Map<String, Friendship> friendships = new HashMap<>();
    private static Map<String, MarriageRequest> marriageRequests = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();
    private  List<String> usernames;
    private  String creatorUsername;
    private boolean started;
    private WorldMap worldMap;
    private transient Player currentPlayer;
    private int currentTurnIndex = 0;
    public Game() {}



    public Game(List<String> usernames, String creatorUsername) {
        this.usernames = usernames;
        this.creatorUsername = creatorUsername;
        this.started = false;
        createPlayersFromUsernames();
    }

    private void createPlayersFromUsernames() {
        for (String username : usernames) {
            User user = UserManager.findByUsername(username);
            Player player = new Player(username, new Position(0, 0), null,user);
            players.put(username, player);
        }
    }

//    public void addPlayer(String username) {
//        if (!players.containsKey(username)) {
//            Player player = new Player(username, new Position(0, 0), null);
//            players.put(username, player);
//        }
//    }

    //    public Player getCurrentPlayer() {
//        return currentPlayer;
//    }
    public static ShopManager getShopManager() {
        return shopManager;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public Player getPlayer(String username) {
        return players.get(username);
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public boolean isUserInGame(String username) {
        return players.containsKey(username);
    }

    public String getNextUserToSelectMap() {
        return usernames.get(selectedMaps.size());
    }

    public boolean allMapsSelected() {
        return selectedMaps.size() == players.size();
    }

    public void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public WorldMap getWorldMap() {
        return worldMap;
    }

    public void setMap(Player player, int mapNumber) {
        selectedMaps.put(player, mapNumber);
    }

    public void setTemplate(Player player, FarmTemplate template) {
        finalizedTemplates.put(player, template);
    }

    public Map<Player, FarmTemplate> getFinalTemplates() {
        return finalizedTemplates;
    }

    public void advanceTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % getPlayers().size();
        if (currentTurnIndex == 0) {
            DateAndTime.advanceHour(1);

        }
    }

    private String makeKey(String username1, String username2) {
        if (username1.compareTo(username2) < 0) {
            return username1 + "#" + username2;
        } else {
            return username2 + "#" + username1;
        }
    }


    public Player getCurrentPlayerForPlay() {
        return getPlayers().get(currentTurnIndex);
    }


    public int getCurrentTurnIndex() {
        return currentTurnIndex;
    }


    public Collection<Player> getAllPlayers() {
        return players.values();
    }

    public void start() {
        this.started = true;
    }

    public boolean isStarted() {
        return started;
    }

    public static long getCurrentHour() {
        return DateAndTime.getHour();
    }
    public static int getCurrentDay(){
        return DateAndTime.getDay();
    }
    public static Season getSeason() {
        return DateAndTime.getCurrentSeason();
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public Friendship getFriendship(Player p1, Player p2) {
        String key = makeKey(p1.getUsername(), p2.getUsername());
        return friendships.get(key);
    }

    public List<Friendship> getAllFriendshipsOf(Player viewer) {
        List<Friendship> result = new ArrayList<>();
        if (viewer == null) return result;

        String viewerUsername = viewer.getUsername();

        for (Map.Entry<String, Friendship> entry : friendships.entrySet()) {
            Friendship friendship = entry.getValue();
            Player p1 = friendship.getPlayer1();
            Player p2 = friendship.getPlayer2();

            if (p1 != null && p1.getUsername().equals(viewerUsername) ||
                    p2 != null && p2.getUsername().equals(viewerUsername)) {
                result.add(friendship);
            }
        }

        return result;
    }

    public static void saveMarriageRequest(String proposerUsername, String targetUsername, String ringName) {
        marriageRequests.put(targetUsername.toLowerCase(), new MarriageRequest(proposerUsername, targetUsername, ringName));
    }
    public MarriageRequest getMarriageRequest(String targetUsername) {
        return marriageRequests.get(targetUsername);
    }
    public void removeMarriageRequest(String targetUsername) {
        marriageRequests.remove(targetUsername);
    }

    public Player getPlayerByUsername(String username) {
        return players.get(username);
    }
    public void addFriendship(Player p1, Player p2) {
        String key = makeKey(p1.getUsername(), p2.getUsername());
        if (!friendships.containsKey(key)) {
            friendships.put(key, new Friendship(p1, p2));
        }
    }
    public Friendship getOrCreateFriendship(Player p1, Player p2) {
        Friendship friendship = getFriendship(p1, p2);
        if (friendship == null) {
            addFriendship(p1, p2);
            friendship = getFriendship(p1, p2);
        }
        return friendship;
    }
}
