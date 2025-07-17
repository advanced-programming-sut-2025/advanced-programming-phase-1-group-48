package model.Tools;

import model.Player.Player;
import model.game.Tile;
import model.items.Item;

public abstract class Tool extends Item {
    protected int level;
    protected int baseEnergyCost;

    public Tool(String name, char displayChar,int level, int baseEnergyCost) {
        super(name, displayChar);
        this.level = level;
        this.baseEnergyCost = baseEnergyCost;
    }

    public int getLevel() { return level; }

    public abstract boolean use(Player player,int x, int y);

    public abstract boolean canUseOn(int x, int y );

    public abstract Tool upgrade(); // returns upgraded version if possible
}
