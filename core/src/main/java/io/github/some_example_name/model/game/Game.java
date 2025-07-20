package io.github.some_example_name.model.game;


import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.model.Weather.DateAndTime;
import io.github.some_example_name.model.enums.Season;
import io.github.some_example_name.model.farm.FarmTemplate;
import io.github.some_example_name.model.intraction.Friendship;
import io.github.some_example_name.model.intraction.MarriageRequest;
import io.github.some_example_name.model.shop.ShopManager;
import io.github.some_example_name.model.user.User;
import io.github.some_example_name.model.user.UserManager;

import java.util.*;

public class Game {
    private static ShopManager shopManager = new ShopManager();
    private final Map<Player, Integer> selectedMaps = new LinkedHashMap<>();
    private final Map<Player, FarmTemplate> finalizedTemplates = new HashMap<>();
    public Map<String, Map<String, Friendship>> friendships = new HashMap<>();
    private static Map<String, MarriageRequest> marriageRequests = new HashMap<>();
    private final Map<String, Player> players = new HashMap<>();
    private  List<String> usernames;
    private  String creatorUsername;
    private boolean started;
    private  WorldMap worldMap;
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

    public  WorldMap getWorldMap() {
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

    public static Friendship getFriendship(Player p1, Player p2) {
        if (p1 == null || p2 == null) return null;
        String name1 = p1.getName();
        String name2 = p2.getName();

        if (name1.equals(name2)) return null;

        if (name1.compareTo(name2) > 0) {
            String temp = name1;
            name1 = name2;
            name2 = temp;
        }
        return null;
    }

//        friendships.putIfAbsent(name1, new HashMap<>());
//        Map<String, Friendship> innerMap = friendships.get(name1);
//        if (!innerMap.containsKey(name2)) {
//            innerMap.put(name2, new Friendship(p1, p2));
//        }
//
//        return friendships.get(name1).get(name2);
//    }
//
//    public static List<Friendship> getAllFriendshipsOf(Player viewer) {
//        List<Friendship> result = new ArrayList<>();
//        if (viewer == null) return result;
//
//        String viewerName = viewer.getName();
//
//        for (Map.Entry<String, Map<String, Friendship>> outerEntry : friendships.entrySet()) {
//            String name1 = outerEntry.getKey();
//            Map<String, Friendship> innerMap = outerEntry.getValue();
//
//            for (Map.Entry<String, Friendship> innerEntry : innerMap.entrySet()) {
//                String name2 = innerEntry.getKey();
//                Friendship friendship = innerEntry.getValue();
//
//                if (name1.equals(viewerName) || name2.equals(viewerName)) {
//                    result.add(friendship);
//                }
//            }
//        }
//
//        return result;
//    }
//    public static void saveMarriageRequest(String proposerUsername, String targetUsername, String ringName) {
//        marriageRequests.put(targetUsername.toLowerCase(), new MarriageRequest(proposerUsername, targetUsername, ringName));
//    }
    //public static Player getPlayerByUsername(String username) {
//        return player;
//    }

//}
        }
