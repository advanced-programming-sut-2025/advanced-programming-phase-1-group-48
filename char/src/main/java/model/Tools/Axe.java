package model.Tools;


import model.Player.Player;
import model.cook.Buff;
import model.enums.TileType;
import model.game.Game;
import model.game.GameManager;
import model.game.Tile;
import model.enums.AxeType;
import model.game.WorldMap;
import model.items.ItemFactory;

import static java.time.chrono.JapaneseEra.values;

public class Axe extends Tool {
    private AxeType type;

    public Axe(AxeType type) {
        super(type.getName(),type.getDisplayChar(), type.getLevel(), type.getBaseEnergyCost());
        this.type = type;
    }

    @Override
    public boolean use(Player player, int x , int y) {
        if (!canUseOn(x ,y)) return false;
        int energyCost = type.getBaseEnergyCost();
        if (player.getSkill("Foraging").getLevel() == 4) {
            energyCost = Math.max(0, energyCost - 1);
        }
        Game game = GameManager.getCurrentGame();
        Buff buff = player.getActiveBuff();

        if (buff != null && !buff.isExpired(game.getCurrentHour())
                && buff.getType() == Buff.Type.SKILL_ENERGY_REDUCTION
                && "Foraging".equals(buff.getTargetSkill())) {
            energyCost = Math.max(0, energyCost - 1);
        }

        if (player.getEnergy() < energyCost) return false;

        player.decreaseEnergy(energyCost);

        WorldMap worldmap = game.getWorldMap();

        if (worldmap.getTileAt(x, y).getType() == TileType.TREE) {
            ItemFactory.createItem("Wood",player.getInventory());
            ItemFactory.createItem("Sap",player.getInventory());
            worldmap.replaceTileTypeIfMatch(x,y,TileType.TREE,TileType.EMPTY);
            System.out.println("The branch has been cut");
        }
        else {System.out.println("You can now use the Axe");}
        return true;
    }

    @Override
    public boolean canUseOn(int x, int y) {
        return false;
    }

    @Override
    public Tool upgrade() {
        AxeType next = AxeType.getNext(this.type);
        return next != null ? new Axe(next) : null;
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

