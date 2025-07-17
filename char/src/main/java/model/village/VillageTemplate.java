package model.village;

import model.enums.TileType;
import model.game.Position;

import java.util.Map;

public interface VillageTemplate {
    TileType[][] generateLayout();
    int getWidth();
    int getHeight();
    String getName();
    Map<Position, String> getNpcHomes(); // <Position, NPCName>
}

