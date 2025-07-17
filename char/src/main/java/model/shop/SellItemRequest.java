package model.shop;

import model.enums.Quality;
import model.items.Item;

public class SellItemRequest {
    private final Item item;
    private final int count;
    private final Quality quality;

    public SellItemRequest(Item item, int count, Quality quality) {
        this.item = item;
        this.count = count;
        this.quality = quality;
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }

    public Quality getQuality() {
        return quality;
    }
}
