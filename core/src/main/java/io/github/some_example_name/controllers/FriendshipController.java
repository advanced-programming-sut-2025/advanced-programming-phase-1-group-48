//package controllers;
//
//import model.Player.Player;
//import model.game.Game;
//import model.game.GameManager;
//import model.intraction.Friendship;
//import model.intraction.GiftRecord;
//import model.intraction.MarriageRequest;
//import model.items.Item;
//import model.items.ItemFactory;
//
//import java.util.Map;
//
//public class
//FriendshipController {
//    public static String showFriendships(Player player) {
//        StringBuilder result = new StringBuilder("Friendships:\n");
//        String playerName = player.getName();
//        Game game = GameManager.getCurrentGame();
//
//        for (Map.Entry<String, Map<String, Friendship>> entry : Game.friendships.entrySet()) {
//            String name1 = entry.getKey();
//            Map<String, Friendship> innerMap = entry.getValue();
//
//            for (Map.Entry<String, Friendship> innerEntry : innerMap.entrySet()) {
//                String name2 = innerEntry.getKey();
//                Friendship friendship = innerEntry.getValue();
//
//                if (name1.equals(playerName) || name2.equals(playerName)) {
//                    Player other = name1.equals(playerName) ? friendship.getPlayer2() : friendship.getPlayer1();
//                    int level = friendship.getLevel();
//                    int xp = friendship.getXp();
//
//                    result.append("Friend: ").append(other.getName())
//                            .append(" | Level: ").append(level)
//                            .append(" | XP: ").append(xp)
//                            .append("\n");
//                }
//            }
//        }
//
//        return result.toString();
//    }
//
//    public static String talk(Player sender, String receiverUsername, String message) {
//        Player receiver = Game.getPlayerByUsername(receiverUsername);
//        if (receiver == null) {
//            return ("No player found with that name.");
//        }
//
//        if (!sender.isAdjacentTo(receiver)) {
//            return ("You need to be near the player to talk to them.");
//        }
//
//        Friendship friendship = Game.getFriendship(sender, receiver);
//        friendship.addXp(20, Game.getCurrentDay());
//        friendship.getMessages().add(sender.getName() + ": " + message);
//        String notification =("Notification! "+receiver.getName() + " received a new message from you" + ": " + message);
//        receiver.getNotifications().add(notification);
//        return "You have received a new message from " + receiver.getName() + ": " + message;
//
//    }
//
//    public static String showTalkHistory(Player viewer, String otherUsername) {
//        Player other = Game.getPlayerByUsername(otherUsername);
//        if (other == null) {
//            return ("No player found with that name.");
//
//        }
//
//        Friendship friendship = Game.getFriendship(viewer, other);
//        if (friendship.getMessages().isEmpty()) {
//            return ("No messages exchanged yet.");
//        }
//
//        for (String msg : friendship.getMessages()) {
//            return (msg);
//        }
//        return ("No messages exchanged yet.");
//    }
//
//    public static String sendGift(Player sender, String receiverName, String itemName, int amount) {
//        Player receiver = Game.getPlayerByUsername(receiverName);
//        Friendship friendship = Game.getFriendship(sender, receiver);
//        if (receiver == null) {
//            return ("No player found with name: " + receiverName);
//
//        }
//        if (!sender.isAdjacentTo(receiver)) {
//            return ("You must be adjacent to " + receiverName + " to send a gift.");
//
//        }
//        if (!friendship.canSendGift()) {
//            return ("Your friendship level is too low to send gifts.");
//
//        }
//        if (!sender.getInventory().hasItem(itemName, amount)) {
//            return ("You do not have " + amount + " of " + itemName + " to send.");
//
//        }
//
//        sender.getInventory().removeItem(itemName, amount);
//
//        for (int i = 0; i < amount; i++) {
//            Item item = ItemFactory.createItem(itemName, receiver.getInventory());
//            if (item == null) {
//                return ("Receiver's inventory is full! Only " + i + " out of " + amount + " items were added.");
//            }
//        }
//
//
//        GiftRecord record = new GiftRecord(sender, receiver, itemName, amount, Game.getCurrentDay());
//        friendship = Game.getFriendship(sender, receiver);
//        friendship.addGiftRecord(record);
//
//        String massage=  "Notification! " + receiver.getName() + " received a gift from "
//                + sender.getName() + ": " + amount + "x " + itemName + "Please rate to your gift.";
//
//        receiver.addNotification(massage);
//        return ("You sent " + amount + "x " + itemName + " to " + receiver.getName());
//    }
//
//    public static String listGifts(Player viewer) {
//        Friendship f;
//        boolean any = false;
//        for (Friendship friendship : Game.getAllFriendshipsOf(viewer)) {
//            for (GiftRecord r : friendship.getGiftHistory()) {
//                if (r.getReceiver().equals(viewer)) {
//                    any = true;
//                    return ("[" + r.getDaySent() + "] #" + r.hashCode()
//                            + " from " + r.getSender().getName()
//                            + ": " + r.getAmount() + "x " + r.getItemName()
//                            + (r.getRating() != null ? " (rated " + r.getRating() + ")" : ""));
//                }
//            }
//        }
//        if (!any) {
//            return("You have not received any gifts yet.");
//        }
//        return "";
//    }
//
//    public static String rateGift(Player viewer, int giftHash, int rating) {
//        for (Friendship friendship : Game.getAllFriendshipsOf(viewer)) {
//            for (GiftRecord r : friendship.getGiftHistory()) {
//                if (r.hashCode() == giftHash && r.getReceiver().equals(viewer)) {
//                    if (rating < 1 || rating > 5) {
//                        return ("Invalid rating. Must be between 1 and 5.");
//
//                    }
//                    friendship.rateGift(r, rating);
//                    return ("You rated gift #" + giftHash + " with " + rating);
//                }
//            }
//        }
//        return ("No gift found with id: " + giftHash);
//    }
//
//    public static String giftHistory(Player viewer, String otherName) {
//        Player other = Game.getPlayerByUsername(otherName);
//        if (other == null) {
//            return ("No player named " + otherName);
//
//        }
//        Friendship f = Game.getFriendship(viewer, other);
//        if (f.getGiftHistory().isEmpty()) {
//            return ("No gifts exchanged with " + otherName);
//        }
//        for (GiftRecord r : f.getGiftHistory()) {
//            return ("[" + r.getDaySent() + "] "
//                    + r.getSender().getName() + " → " + r.getReceiver().getName()
//                    + ": " + r.getAmount() + "x " + r.getItemName()
//                    + (r.getRating() != null ? " (rated " + r.getRating() + ")" : ""));
//        }
//        return ("No gifts exchanged with " + otherName);
//    }
//
//    public static String hugPlayer(Player sender, String receiverUsername) {
//        Player receiver = Game.getPlayerByUsername(receiverUsername);
//        if (receiver == null) {
//            return "Player not found!";
//        }
//
//        if (!sender.isAdjacentTo(receiver)) {
//            return "You must be in the same location to hug!";
//        }
//
//        Friendship friendship = Game.getFriendship(sender, receiver);
//        if (friendship == null) {
//            return "You are not friends yet!";
//        }
//
//        if (!friendship.canHug()) {
//            return "You need at least friendship level 2 to hug!";
//        }
//
//        friendship.addXp(60, Game.getCurrentDay());
//        return "You hugged " + receiverUsername + "! (+60 friendship XP)";
//    }
//
//    public static String sendFlower(Player sender, String receiverUsername) {
//        Player receiver = Game.getPlayerByUsername(receiverUsername);
//        if (receiver == null) return "Player not found.";
//
//        if (!sender.isAdjacentTo(receiver)) {
//            return "You must be in the same location to send a flower.";
//        }
//
//        Friendship friendship = Game.getFriendship(sender, receiver);
//        if (friendship == null) return "You are not friends with this player.";
//
//        if (friendship.getLevel() != 2) return "You must be at level 2 friendship to send a flower.";
//        if (friendship.getXp() < 100 * 3) return "You must fill XP of level 2 before sending flower.";
//
//        if (!sender.getInventory().hasItem("flower", 1)) {
//            return "You don't have a flower in your inventory.";
//        }
//
//        sender.getInventory().removeItem("flower",1);
//        ItemFactory.createItem("flower",receiver.getInventory());
//
//        friendship.setLevel(3);
//
//
//        return "You have sent a flower to " + receiverUsername + ". Friendship level increased to 3.";
//    }
//
//    public static String askMarriage(Player proposer, String targetUsername, String ringName) {
//        Player target = Game.getPlayerByUsername(targetUsername);
//        if (!proposer.getGender().equalsIgnoreCase("male")) {
//            return "Only male players can initiate a marriage proposal.";
//        }
//        if (target == null) return "Target player not found.";
//
//        if (!proposer.isAdjacentTo(target)) return "You must be adjacent to the player.";
//
//        Friendship friendship = Game.getFriendship(proposer, target);
//        if (friendship == null || !friendship.canMarry()) {
//            return "Friendship level must be 3 to propose marriage.";
//        }
//
//        if (proposer.getGender() == target.getGender()) {
//            return "Marriage is only allowed between opposite genders.";
//        }
//
//        if (proposer.isMarried() || target.isMarried()) {
//            return "One of the players is already married.";
//        }
//
//        if (!proposer.getInventory().hasItem(ringName, 1)) {
//            return "You don't have the specified ring in your inventory.";
//        }
//
//        Game.saveMarriageRequest(proposer.getName(), target.getName(), ringName);
//
//        String message = proposer.getName() + " has proposed to you! Use 'respond --accept -u " + proposer.getName() +
//                "' to accept or 'respond --reject -u " + proposer.getName() + "' to reject.";
//        target.addNotification(message);
//
//        return "Marriage proposal sent to " + target.getName() + ".";
//    }
//
////    public static String respondMarriage(Player responder, String proposerUsername, boolean accept, boolean reject) {
////
////        MarriageRequest request = Game.getMarriageRequest(responder.getName());
////        if (request == null) return "No marriage request from " + proposerUsername + " found.";
////
////        Player proposer = Game.getPlayerByUsername(proposerUsername);
////        if (proposer == null) return "Proposing player not found.";
////
////        Friendship friendship = Game.getFriendship(proposer, responder);
////        if (friendship == null || !friendship.canMarry()) return "You are not in the correct friendship level to respond.";
////
////        if (reject) {
////            friendship.setLevel(0);
////            proposer.setLowEnergyUntil(Game.getCurrentDay() + 7);
////            Game.removeMarriageRequest(responder.getName());
////            return "You have rejected the proposal. Friendship reset. " + proposerUsername + " will have reduced energy for 7 days.";
////        }
////
////        // accept
////        String ringName = request.getRingName();
////        proposer.getInventory().removeItem(ringName, 1);
////        responder.getInventory().addItem(new Item(ringName)); // فرض بر اینکه کلاس Item وجود دارد
////
////        friendship.setLevel(4);
////
////        // Merge wallets (ساده: wallet مشترک)
////        SharedWallet wallet = new SharedWallet(proposer.getWallet(), responder.getWallet());
////        proposer.setWallet(wallet);
////        responder.setWallet(wallet);
////
////        // اجازه ورود به زمین یکدیگر
////        proposer.addAllowedLand(responder.getLand());
////        responder.addAllowedLand(proposer.getLand());
////
////        // علامت‌گذاری ازدواج
////        proposer.setSpouse(responder);
////        responder.setSpouse(proposer);
////
////        Game.removeMarriageRequest(proposerUsername, responder.getUsername());
////
////        return "You have accepted the proposal! You are now married to " + proposerUsername + ".";
////    }
//
//
//}
