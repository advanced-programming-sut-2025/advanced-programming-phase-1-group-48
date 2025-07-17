package model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import model.enums.TileType;
import model.items.Item;

public class Tile {

    private  Position position;    // موقعیت تایل در نقشه منطقه
    private TileType type;              // نوع فعلی تایل
    private TileType defaultType;       // نوع اولیه تایل (قبل از اینکه مثلاً PLAYER بشه)
    private Item item;                  // آیتم روی تایل
    private boolean isWalkable;         // قابل عبور بودن یا نه
    ///private  Region region;// ریجن صاحب تایل
    private TileType originalType;

    @JsonIgnore
    private transient Region region;

    public Tile() {}

    public Tile(Position position, Region region) {
        this.position = position;
        this.region = region;
        this.type = TileType.EMPTY;
        this.defaultType = TileType.EMPTY;
        this.item = null;
        this.isWalkable = true;
    }

    public Position getPosition() {
        return position;
    }

//    public void setPosition(Position position) {
//        this.position = position;
//    }

    public int getRow() {
        return position.getRow();
    }

    public int getCol() {
        return position.getCol();
    }


    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        if (this.originalType == null) {
            this.originalType = type;
        }
        this.type = type;
        updateWalkableStatus();
    }

    public void restoreOriginalType() {
        this.type = originalType != null ? originalType : TileType.EMPTY;
        updateWalkableStatus();
    }


    public void resetToDefaultType() {
        this.type = this.defaultType;
        updateWalkableStatus();
    }

    public TileType getDefaultType() {
        return defaultType;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public Region getRegion() {
        return region;
    }

    private void updateWalkableStatus() {
        switch (this.type) {
            case ROCK, TREE, LAKE, PLAYER, GREENHOUSE, FARM_BORDER -> this.isWalkable = false;
            default -> this.isWalkable = true;
        }
    }

    @Override
    public String toString() {
        return "Tile{pos=" + position + ", type=" + type + ", item=" + item + "}";
    }
}
