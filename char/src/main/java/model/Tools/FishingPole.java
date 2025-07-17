package model.Tools;


import model.Animal.Fish;
import model.Animal.FishCreator;
import model.Player.Player;
import model.Player.Skill;
import model.cook.Buff;
import model.game.Game;
import model.game.GameManager;
import model.game.Tile;
import model.enums.FishingPoleType;

public class FishingPole extends Tool {
    private FishingPoleType type;

    public FishingPole(FishingPoleType type) {
        super(type.getName(), type.getDisplayChar(), type.getLevel(), type.getBaseEnergyCost());
        this.type = type;
    }


    @Override
    public boolean use(Player player,int x, int y) {
        int energyCost = type.getBaseEnergyCost();
        Game game = GameManager.getCurrentGame();
        Buff buff = player.getActiveBuff();

        if (buff != null && !buff.isExpired(game.getCurrentHour())
                && buff.getType() == Buff.Type.SKILL_ENERGY_REDUCTION
                && "Fishing".equals(buff.getTargetSkill())) {
            energyCost = Math.max(0, energyCost - 1);
        }

        Skill fishing = player.getSkill("Fishing");
        if (fishing != null && fishing.getLevel() == 4) {
            energyCost = Math.max(0, energyCost - 1);
        }
        if (player.getEnergy() < energyCost) return false;

        player.decreaseEnergy(energyCost);
        return true;
    }

    @Override
    public boolean canUseOn(int x, int y ) {
        return false;
    }

    @Override
    public Tool upgrade() {
        switch (type) {
            case TRAINING -> { return new FishingPole(FishingPoleType.BAMBOO); }
            case BAMBOO -> { return new FishingPole(FishingPoleType.FIBERGLASS); }
            case FIBERGLASS -> { return new FishingPole(FishingPoleType.IRIDIUM); }
            default -> { return null; }
        }

    }

    public FishingPoleType getType() {
        return type;
    }

    public boolean canCatchFishType(Fish fish) {
        return type.canCatchAllFish();
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
