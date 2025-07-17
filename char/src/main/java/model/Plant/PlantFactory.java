package model.Plant;

import model.Plant.CropType;

public class PlantFactory {
    public static PlantInstance createPlant(CropType type) {
        if (!type.isOneTime()) {
            return new RegrowableCropInstance(type);
        }

        return new OneTimeCropInstance(type);
    }

    public static PlantInstance creatTree (TreeType type){
        return  new Tree (type);
    }
}
