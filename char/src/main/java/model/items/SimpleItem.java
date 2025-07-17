package model.items;

public class SimpleItem extends Item {
    private final int sellPrice;
    private final int energy;
    private final String type;

    public SimpleItem(String name, char displayChar, int sellPrice,int energy , String type) {
        super(name, displayChar);
        this.sellPrice = sellPrice;
        this.energy = energy;
        this.type = type;
    }

    @Override
    public void interact() {
    }

    @Override
    public int getSellPrice() {
        return sellPrice;
    }

    @Override
    public boolean isEdible() {
        return false;
    }
}
