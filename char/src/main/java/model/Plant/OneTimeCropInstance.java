package model.Plant;


import model.Plant.CropType;
import model.Player.Player;
import model.game.Game;
import model.game.GameManager;
import model.items.Item;
import model.items.ItemFactory;

public class OneTimeCropInstance implements PlantInstance {
    private final CropType type;
    private int age = 0;
    private boolean wateredToday = false;
    private boolean harvested = false;
    private FertilizerType fertilizer;
    private int daysWithoutWater = 0;



    public OneTimeCropInstance(CropType type) {
        this.type = type;
    }
    public void applyFertilizer(FertilizerType type) {
        this.fertilizer = type;
    }
    @Override
    public void growOneDay() {
        if (fertilizer == FertilizerType.DELUXE_RETAINING_SOIL) {
            wateredToday = true;
        }
        if (!wateredToday) {
            daysWithoutWater++;
            // manteqh 2 rooz ab nakhordan
            if(daysWithoutWater ==2) {
                age = Integer.MAX_VALUE;
            }// می‌میرد
        } else {
            age++;
            if (fertilizer == FertilizerType.SPEED_GRO) {
                age++; // رشد سریع‌تر
            }
            wateredToday = false;
            daysWithoutWater=0;

        }
    }

    @Override public void resetWaterFlag() { wateredToday = false; }
    @Override public void water()       { wateredToday = true; }

    @Override
    public boolean isReadyToHarvest() {
        return !harvested && age >= type.getTotalHarvestTime();
    }

    @Override
    public Item harvest() {
        Game game = GameManager.getCurrentGame();
        Player player = game.getCurrentPlayerForPlay();
        if (!isReadyToHarvest()) return null;
        harvested = true;
        return ItemFactory.createItem(type.getName(), player.getInventory());
    }

    public CropType getType() {
        return type;
    }
}
