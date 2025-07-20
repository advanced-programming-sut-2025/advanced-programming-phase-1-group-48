package io.github.some_example_name.model.NPC;


import io.github.some_example_name.model.items.Item;
import io.github.some_example_name.model.user.User;

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

