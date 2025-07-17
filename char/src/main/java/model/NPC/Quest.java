package model.NPC;


import model.items.Item;
import model.user.User;
import model.quests.QuestReward;

public class Quest {
    private String title;
    private String description;
    private Item requiredItem;
    private QuestReward reward;
    private int requiredFriendshipLevel;
    private int activationDay;
    private boolean isTaken;
    private boolean isDone;
    private User doneBy;
    private NPC owner;

    public boolean isAvailable(User user, int currentDay, int friendshipLevel) {
        return !isDone &&
                !isTaken &&
                friendshipLevel >= requiredFriendshipLevel &&
                currentDay >= activationDay;
    }

    public boolean complete(User user) {
        if (isDone || doneBy != null) return false;
        isDone = true;
        doneBy = user;
        reward.giveTo(user);
        return true;
    }

    public int getRequiredFriendshipLevel() {
        return requiredFriendshipLevel;
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public int getActivationDay() {
        return activationDay;
    }

    public QuestReward getReward() {
        return reward;
    }

    public Item getRequiredItem() {
        return requiredItem;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }

    public NPC getOwner() {
        return owner;
    }
}

