package model.quests;


import model.user.User;
import model.items.Item;

public class QuestReward {
    private int goldReward;
    private int friendshipBoost;
    private Item itemReward;

    public QuestReward(int gold, int friendship, Item item) {
        this.goldReward = gold;
        this.friendshipBoost = friendship;
        this.itemReward = item;
    }

    public void giveTo(User user) {
    }
}
