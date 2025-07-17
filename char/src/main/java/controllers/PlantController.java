// فایل: PlantController.java

package controllers;

import model.Plant.*;
import model.Player.Player;
import model.Tools.Hoe;
import model.Weather.DateAndTime;
import model.enums.Season;
import model.enums.TileType;
import model.game.Game;
import model.game.GameManager;
import model.Plant.CropType;
import model.game.WorldMap;
import model.items.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static model.Player.Player.addPlantedInfo;
import static model.Player.Player.getPlantedAt;


public class PlantController {
    static Game game = GameManager.getCurrentGame();
    static Player player = game.getCurrentPlayerForPlay();

//    public  static void addPlantedInfo(int x, int y, PlantInstance plant) {
//        player.getPlantedList().add(new PlantedInfo(x, y, plant));
//    }
//
//    public static PlantInstance getPlantedAt(int x, int y) {
//        for (PlantedInfo info : getPlantedList()) {
//            if (info.getX() == x && info.getY() == y) {
//                return info.getPlant();
//            }
//        }
//        return null;
//    }
//
//    public static PlantInstance getPlantedName(int x, int y) {
//        for (PlantedInfo info : getPlantedList()) {
//            if (info.getX() == x && info.getY() == y) {
//                return info.getPlant();
//            }
//        }
//        return null;
//    }


    public PlantController(Game game) {
        this.game = game;
    }

    public static void getInfo(String inputName) {
        Stream.<Function<String, Optional<? extends PlantType>>>of(
                        CropType::fromName,
                        TreeType::fromName,
                        ForCropType::fromName,
                        ForTreeType::fromName
                )
                .map(f -> f.apply(inputName))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .ifPresentOrElse(
                        obj -> System.out.println(obj),
                        () -> System.out.println("Not Found: " + inputName)
                );
    }

    public static void plantSeed(String seedName, int x, int y) {
        Game game = GameManager.getCurrentGame();
        WorldMap worldmap = game.getWorldMap();
        Player player = game.getCurrentPlayerForPlay();
//Place is the method that show is x , y near playe
        if ((worldmap.getTileAt(x, y).getType() != TileType.TILLED_SOIL)) {
            System.out.println("You can't plant here.");
            return;
        }

        worldmap.replaceTileTypeIfMatch(x,y,TileType.TILLED_SOIL,TileType.CROP);
        player.getInventory().removeItem(seedName, 1);

        if (!player.getInventory().hasItem(seedName)) {
            System.out.println("You don't have " + seedName + " in your inventory.");
            return;
        }

        String cropNameToPlant = seedName;
        Season currentSeason = DateAndTime.getCurrentSeason();
        //ChangPlace is a method for change w to
        CropType cropType = CropType.valueOf(cropNameToPlant.toUpperCase().replace(" ", "_"));
        PlantInstance plant = PlantFactory.createPlant(cropType);
        //changePlace(x,y);

        if (plant == null) {
            System.out.println("Invalid seed or crop: " + cropNameToPlant);
            return;
        }
        player.getInventory().removeItem(seedName, 1);
        addPlantedInfo(x, y, plant);
        System.out.println("Planted " + cropNameToPlant + " at (" + x + "," + y + ")");
    }


    public static void showPlant(int x, int y) {
        if (getPlantedAt(x, y) == null) {
            System.out.println("no plant is here");
        } else {
            PlantInstance plant = getPlantedAt(x, y);
            if (plant instanceof OneTimeCropInstance crop) {
                String name = crop.getType().getName();
                getInfo(name);
            } else if (plant instanceof RegrowableCropInstance crop) {
                String name = crop.getType().getName();
                getInfo(name);
            } else if (plant instanceof Tree tree) {
                String name = tree.getType().getName();
                getInfo(name);
            }
        }

    }


    //
//
//
    public static void waterPlant(PlantInstance plant) {

        Game game = GameManager.getCurrentGame();
        Player player = game.getCurrentPlayerForPlay();
        plant.water();
    }

    //
    public static void fertilizeTile(String fertilizerName, int x, int y) {
        Game game = GameManager.getCurrentGame();
        Player player = game.getCurrentPlayerForPlay();


        // 4) چک موجود بودن کود
        if (!player.getInventory().hasItem(fertilizerName)) {
            System.out.println("You don't have " + fertilizerName + " in your inventory.");
            return;
        }

        FertilizerType type;
        try {
            type = FertilizerType.valueOf(fertilizerName
                    .trim()
                    .toUpperCase()
                    .replace("-", "_"));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid fertilizer type: " + fertilizerName);
            return;
        }
        PlantInstance plant = getPlantedAt(x, y);
        if (plant instanceof OneTimeCropInstance otc) {
            otc.applyFertilizer(type);
        } else if (plant instanceof RegrowableCropInstance rc) {
            rc.applyFertilizer(type);
        } else {
            System.out.println("You can only fertilize crops, not " + plant.getClass().getSimpleName());
            return;
        }
        player.getInventory().removeItem(fertilizerName, 1);

        System.out.println("Applied " + fertilizerName + " to crop at ("
                + x + "," + y + ").");
    }

    public static CropType handleMixedSeedPlanting(String mixSeedName) {
        Season currentSeason = DateAndTime.getCurrentSeason();
        Optional<MixSeedType> optionalMix = MixSeedType.fromName(mixSeedName);

        if (optionalMix.isPresent()) {
            MixSeedType mix = optionalMix.get();
            if (mix.getSeason() == currentSeason) {
                return mix.getRandomPlant();
            } else {
                System.out.println("Cannot plant " + mixSeedName + " in " + currentSeason + " season.");
            }
        } else {
            System.out.println("Invalid mixed seed name: " + mixSeedName);
        }

        return null; // ← در صورت نامعتبر بودن
    }

    public static PlantingBehavior detectBehaviorAt(int x, int y) {
        PlantInstance plant = getPlantedAt(x, y);  // یا از PlantController
        if (plant == null) {
            return PlantingBehavior.NONE;
        }
        if (plant instanceof Tree) {
            return PlantingBehavior.TREE;
        }
        if (plant instanceof OneTimeCropInstance) {
            return PlantingBehavior.ONE_TIME_CROP;
        }
        if (plant instanceof RegrowableCropInstance) {
            return PlantingBehavior.REGROWABLE_CROP;
        }
        return PlantingBehavior.NONE;
    }


    public static boolean testOneTimeCropLifecycle(OneTimeCropInstance crop) {
        while (!crop.isReadyToHarvest()) {
            crop.growOneDay();
            crop.resetWaterFlag();
            crop.water();
        }
        return crop.isReadyToHarvest();
    }


    public static boolean testRegrowableCropLifecycle(RegrowableCropInstance crop) {

        while (!crop.isReadyToHarvest()) {
            crop.growOneDay();
            crop.resetWaterFlag();
            crop.water();            // هر روز آب می‌دهیم
        }
        if (!crop.isReadyToHarvest()) return false;
        Item first = crop.harvest();
        if (first == null) return false;

        // * مورد دوم: صبر به اندازهٔ regrowthTime و برداشت دوم *
        int regrowth = crop.getType().getRegrowthTime();
        for (int i = 0; i < regrowth; i++) {
            crop.growOneDay();
            crop.resetWaterFlag();
            crop.water();
        }
        // تا وقتی دوباره آماده نشده روز اضافه رشد کن
        while (!crop.isReadyToHarvest()) {
            crop.growOneDay();
            crop.resetWaterFlag();
            crop.water();
        }
        Item second = crop.harvest();
        return second != null;
    }

    /// //////////////////TREE TEST HELPER
    public static boolean testPeriodicFruit(Tree tree) {
        // ۱– افزایش روزانه تا بلوغ
        while (!tree.isMature()) {
            tree.growOneDay();
        }
        // ۲– افزایش روزانه تا دوره برداشت
        int cycle = tree.getType().getFruitCycle();
        for (int i = 0; i < cycle; i++) {
            tree.growOneDay();
        }
        // اکنون آمادهٔ برداشت اول
        Item first = tree.harvest();
        if (first == null || !first.getName().equals(tree.getType().getFruit())) {
            return false;
        }
        // ۳– پس از برداشت اول، درخت حذف نمی‌شود و دوره دوباره شروع می‌شود
        for (int i = 0; i < cycle; i++) {
            tree.growOneDay();
        }
        Item second = tree.harvest();
        return second != null && second.getName().equals(tree.getType().getFruit());
    }

    public static boolean testLightningStrike(Tree tree) {
        tree.strikeByLightning();
        Item dropped = tree.harvest();
        return dropped != null && dropped.getName().equalsIgnoreCase("Coal");


    }

    public static void startForPlant() {
        Game game = GameManager.getCurrentGame();
        Player player = game.getCurrentPlayerForPlay();
        Iterator<PlantedInfo> it = player.getPlantedList().iterator();
        while (it.hasNext()) {
            PlantedInfo info = it.next();
            PlantInstance plant = info.getPlant();

            // 1) رشد یک روز
            plant.growOneDay();

            // 4) ریست پرچمِ آب
            plant.resetWaterFlag();
        }
    }
}
