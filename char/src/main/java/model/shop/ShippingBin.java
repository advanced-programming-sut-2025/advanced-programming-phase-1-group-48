//package model.shop;
//
//import model.Player.Player;
//import model.game.Position;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ShippingBin {
//    private Position position;
//    private List<SellItemRequest> pendingSales = new ArrayList<>();
//
//    public ShippingBin(Position position) {
//        this.position = position;
//    }
//
//    public boolean isNear(Player player) {
//
//    }
//
//    public boolean sell(String itemName, int count, Player player) {
//        if (!isNear(player)) {
//            System.out.println("To sell, you must be near a shipping bin.");
//            return false;
//        }
//
//        List<InventoryItem> items = player.getInventory().findItemsByName(itemName);
//        if (player.getInventory().hasItem(itemName)) {
//            System.out.println("This is not in the inventory.");
//            return false;
//        }
//
//        int total = items.stream().mapToInt(InventoryItem::getCount).sum();
//        if (player.getInventory().hasItem(itemName, count)) {
//            System.out.println("There is not enough items in your inventory.");
//            return false;
//        }
//
//        int remaining = count;
//        for (InventoryItem invItem : items) {
//            int take = Math.min(remaining, invItem.getCount());
//            pendingSales.add(new SellItemRequest(invItem.getItem(), take, invItem.getQuality()));
//            invItem.decrease(take);
//            remaining -= take;
//            if (remaining == 0) break;
//        }
//
//        return true;
//    }
//
//    public void processPendingSales(Player player) {
//        for (SellItemRequest req : pendingSales) {
//            int price = calculateSellPrice(req);
//            player.addMoney(price);
//        }
//        pendingSales.clear();
//    }
//
//    private int calculateSellPrice(SellItemRequest req) {
//        int basePrice = req.getItem().getSellPrice();
//        double multiplier = switch (req.getQuality()) {
//            case SILVER -> 1.25;
//            case GOLD -> 1.5;
//            case IRIDIUM -> 2.0;
//            default -> 1.0;
//        };
//        return (int) (basePrice * multiplier) * req.getCount();
//    }
//}
