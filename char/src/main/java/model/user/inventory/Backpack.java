package model.user.inventory;

import model.enums.BackpackType;

public class Backpack {
    private BackpackType type;

    public Backpack(BackpackType type) {
        this.type = type;
    }

    public int getCapacity() {
        return type.getCapacity();
    }

    public void upgrade(BackpackType newType) {
        if (newType.getCapacity() > type.getCapacity()) {
            this.type = newType;
        }
    }
}
