package model.enums;

public enum SimpleItemInfo {
    RING("ring",'s',6,538, true, "fruit"),
    FLOWER("flower",'s',6,538, true, "fruit"),
    APRICOT("Apricot", 'A', 59, 38, true, "fruit"),
    CHERRY("Cherry", 'C', 80, 38, true, "fruit"),
    BANANA("Banana", 'B', 150, 75, true, "fruit"),
    MANGO("Mango", 'M', 130, 100, true, "fruit"),
    ORANGE("Orange", 'O', 100, 38, true, "fruit"),
    PEACH("Peach", 'P', 140, 38, true, "fruit"),
    APPLE("Apple", 'A', 100, 38, true, "fruit"),
    POMEGRANATE("Pomegranate", 'G', 140, 38, true, "fruit"),
    //حلقه ازدواج
    WEDDING_RING("Wedding Ring", 'W', 10000, 0, false, "ring"),

    CHERRY_BOMB("Cherry Bomb", 'C', 50, 0, false, "bomb"),
    BOMB("Bomb", 'B', 50, 0, false, "bomb"),
    MEGA_BOMB("Mega Bomb", 'M', 50, 0, false, "bomb"),

    SPRINKLER("Sprinkler", 'S', 0, 0, false, "machine"),
    QUALITY_SPRINKLER("Quality Sprinkler", 'Q', 0, 0, false, "machine"),
    IRIDIUM_SPRINKLER("Iridium Sprinkler", 'I', 0, 0, false, "machine"),

    CHARCOAL_KILN("Charcoal Klin", 'K', 0, 0, false, "machine"),
    FURNACE("Furnace", 'F', 0, 0, false, "machine"),
    SCARECROW("Scarecrow", 'S', 0, 0, false, "decoration"),
    DELUXE_SCARECROW("Deluxe Scarecrow", 'D', 0, 0, false, "decoration"),

    BEE_HOUSE("Bee House", 'H', 0, 0, false, "machine"),
    CHEESE_PRESS("Cheese Press", 'C', 0, 0, false, "machine"),
    KEG("Keg", 'K', 0, 0, false, "machine"),
    LOOM("Loom", 'L', 0, 0, false, "machine"),
    MAYONNAISE_MACHINE("Mayonnaise Machine", 'M', 0, 0, false, "machine"),
    OIL_MAKER("Oil Maker", 'O', 0, 0, false, "machine"),
    PRESERVES_JAR("Preserves Jar", 'P', 0, 0, false, "machine"),

    DEHYDRATOR("Dehydrator", 'D', 0, 0, false, "machine"),
    GRASS_STARTER("Grass Starter", 'G', 0, 0, false, "seed"),
    FISH_SMOKER("Fish Smoker", 'F', 0, 0, false, "machine"),

    MYSTIC_TREE_SEED("Mystic Tree Seed", 'T', 100, 0, false, "seed"),

    OAK_RESIN("Oak Resin", 'R', 150, 0, false, "resin"),
    MAPLE_SYRUP("Maple Syrup", 'S', 200, 0, false, "syrup"),
    PINE_TAR("Pine Tar", 'T', 100, 0, false, "tar"),
    SAP("Sap", 's', 2, -2, true, "sap"),
    COMMON_MUSHROOM("Common Mushroom", 'm', 40, 38, true, "fungus"),
    MYSTIC_SYRUP("Mystic Syrup", 'Y', 1000, 500, true, "syrup");



    private final String name;
    private final char displayChar;
    private final int sellPrice;
    private final int energy;
    private final boolean isEdible;
    private final String type;

    SimpleItemInfo(String name, char displayChar, int sellPrice, int energy, boolean isEdible, String type) {
        this.name = name;
        this.displayChar = displayChar;
        this.sellPrice = sellPrice;
        this.energy = energy;
        this.isEdible = isEdible;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public char getDisplayChar() {
        return displayChar;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isEdible() {
        return isEdible;
    }

    public String getType() {
        return type;
    }
}
