package model.farm;

import model.enums.TileType;

public interface FarmTemplate {
    TileType[][] generateLayout();
    int getWidth();
    int getHeight();
    String getName();
}
