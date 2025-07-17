package model.NPC;

import model.user.User;
import model.items.Item;

import java.util.List;

public class NPCManager {
    private List<NPC> npcs;

    public NPC getNPCByName(String name) {
        //TODO
        return null;
    }

    public void handleMeetCommand(User user, String npcName) {
        // اجرای meet NPC
    }

    public void handleGiftCommand(User user, String npcName, Item item) {
        // اجرای gift NPC
    }

    public void handleFriendshipList(User user) {
        // چاپ friendship NPC list
    }
}

