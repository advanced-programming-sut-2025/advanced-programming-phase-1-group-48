package model.Tools;


import model.enums.TileType;
import model.game.Game;
import model.game.GameManager;
import model.game.Tile;
import model.Player.Player;
import model.game.WorldMap;
import model.items.ItemFactory;

public class Scythe extends Tool {
    public Scythe(String name, char displayChar, int level, int baseEnergyCost) {
        super(name, displayChar, level, baseEnergyCost);
    }

    @Override
    public boolean use(Player player,int x , int y) {
        int energyCost = 4;
        if (player.getEnergy() < energyCost) return false;
        player.decreaseEnergy(energyCost);
        Game game= GameManager.getCurrentGame();
        WorldMap worldmap = game.getWorldMap();

        if (worldmap.getTileAt(x, y).getType() == TileType.FORAGING) {
            worldmap.replaceTileTypeIfMatch(x,y,TileType.FORAGING,TileType.EMPTY);
            System.out.println("Weed was cut with a Scythe");
        }else {System.out.println("You can now use the Scythe");}
        return true;
    }

    @Override
    public boolean canUseOn(int x, int y) {
        return false;
    }

    @Override
    public Tool upgrade() {
        return null;
    }

    @Override
    public void interact() {

    }

    @Override
    public int getSellPrice() {
        return 0;
    }

    @Override
    public boolean isEdible() {
        return false;
    }
}
