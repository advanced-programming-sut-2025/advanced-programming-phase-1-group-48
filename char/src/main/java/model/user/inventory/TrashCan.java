package model.user.inventory;

import model.enums.TrashCanType;

public class TrashCan {
    private TrashCanType type;

    public TrashCan(TrashCanType type) {
        this.type = type;
    }
    public void upgrade(TrashCanType newType) {
        if (newType.ordinal() > type.ordinal()) {
            this.type = newType;
        }
    }

    public TrashCanType getType() {
        return type;
    }
}
