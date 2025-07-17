package model.NPC;

import model.user.User;
import model.game.Position;
import model.items.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NPC {
    private String name;
    private String job;
    private String personality;
    private Position homeLocation;
    private List<String> favoriteItems;
    private List<Quest> quests;

    private Map<User, Integer> friendshipPoints;

    public NPC() {

    }

    public int getFriendshipLevel(User user) {
        return friendshipPoints.getOrDefault(user, 0) / 200;
    }

    public void talk(User user) {
    }

    public void receiveGift(User user, Item item) {
    }

//    public List<Quest> getAvailableQuests(User user, GameTime time) {
//
//    }
public boolean likesItem(String itemName) {
    return favoriteItems.contains(itemName);
}


//    public Optional<Item> dailyGift(User user) {
//
//    }
}

