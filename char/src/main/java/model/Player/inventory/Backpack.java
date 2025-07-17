package model.Player.inventory;

import model.Result;
import model.enums.BackpackType;

public class Backpack {
    private BackpackType type;

    public Backpack(BackpackType type) {
        this.type = type;
    }

    public int getCapacity() {
        return type.getCapacity();
    }

    public boolean isUnlimited() {
        return type.isUnlimited();
    }

    public BackpackType getType() {
        return type;
    }

    public Result upgrade(BackpackType newType) {
        if (newType.getCapacity() > type.getCapacity()) {
            this.type = newType;
            return Result.success("Backpack upgraded successfully!");
        } else {
            return Result.failure("You already have an equal or better backpack.");
        }
    }

}