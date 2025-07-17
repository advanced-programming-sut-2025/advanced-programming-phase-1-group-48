package model.building;

import model.building.Buildings;
import model.game.Position;

public class Well extends Buildings {
    public Well(Position topLeft) {
        super("Well", topLeft, 1, 1);
    }

    @Override
    public void interact() {

    }

}
