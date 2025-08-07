//package model.Plant;
//
//import model.Plant.Enums.CropType;
//import model.Plant.Enums.FertilizerType;
//import model.Player.Player;
//import model.game.Game;
//import model.game.GameManager;
//import model.items.Item;
//import model.items.ItemFactory;
//
//public class RegrowableCropInstance implements PlantInstance {
//    private final CropType type;
//    private int age = 0;
//    private int daysSinceHarvest = 0;
//    private boolean wateredToday = false;
//    private FertilizerType fertilizer;
//    //فیلدی برای اینکه مشخص کند ایا اولین برداشت انجاک شده است یا خیر.
//
//
//    public RegrowableCropInstance(CropType type) {
//        this.type = type;
//    }
//
//    public void applyFertilizer(FertilizerType type) {
//        this.fertilizer = type;
//    }
//
//    @Override
//    public void growOneDay() {
//        if (!wateredToday && fertilizer != FertilizerType.DELUXE_RETAINING_SOIL && age < type.getTotalHarvestTime()) {
//            age = Integer.MAX_VALUE;
//        } else {
//            if (fertilizer == FertilizerType.SPEED_GRO && age + 2 <= type.getTotalHarvestTime()) {
//                age += 2;
//            } else {
//                age++;
//            }
//
//            if (age >= type.getTotalHarvestTime()) {
//                daysSinceHarvest++;
//            }
//
//            // اگر deluxe-retaining-soil نداریم، آبیاری برای روز بعد باید false بشه
//            wateredToday = FertilizerType.SPEED_GRO == fertilizer;
//        }
//    }
//
//
//    @Override public void resetWaterFlag() { wateredToday = false; }
//    @Override public void water()       { wateredToday = true; }
//
//    @Override
//    public boolean isReadyToHarvest() {
//        return age >= type.getTotalHarvestTime()
//                && daysSinceHarvest >= type.getRegrowthTime();
//    }
//
//    @Override
//    public Item harvest() {
//        if (!isReadyToHarvest()) return null;
//        daysSinceHarvest = 0;
//        Game game = GameManager.getCurrentGame();
//        Player player = game.getCurrentPlayerForPlay();
//        return ItemFactory.createItem(type.getName(),player.getInventory());
//    }
//
//    public CropType getType() {
//        return type;
//    }
//
//    @Override
//    public String toString() {
//        return "RegrowableCropInstance{" +
//                "type=" + type +
//                ", age=" + age +
//                ", daysSinceHarvest=" + daysSinceHarvest +
//                ", wateredToday=" + wateredToday +
//                ", fertilizer=" + fertilizer +
//                '}';
//    }
//}
