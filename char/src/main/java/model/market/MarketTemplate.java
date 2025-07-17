package model.market;

import model.enums.TileType;
import model.game.Position;

import java.util.Map;

public interface MarketTemplate {
    TileType[][] generateLayout();
    int getWidth();
    int getHeight();
    String getName();
    Map<Position, String> getShopNames(); // <Position, ShopName>
    //Map<Position, String> getShopNames();
}
