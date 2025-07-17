package model.Plant;

class Crop {
    String name;
    String source;
    String stages;
    Integer totalHarvestTime;
    boolean oneTime;
    Integer regrowthTime;      // null if one-time
    int baseSellPrice;
    boolean isEdible;
    Integer energy;            // null if not edible
    Integer baseHealth;        // null if not edible
    String season;
    boolean canBecomeGiant;



    public Crop(String name, String source, String stages,
                Integer totalHarvestTime, boolean oneTime,
                Integer regrowthTime, int baseSellPrice,
                boolean isEdible, Integer energy, Integer baseHealth,
                String season, boolean canBecomeGiant) {
        this.name = name;
        this.source = source;
        this.stages = stages;
        this.totalHarvestTime = totalHarvestTime;
        this.oneTime = oneTime;
        this.regrowthTime = regrowthTime;
        this.baseSellPrice = baseSellPrice;
        this.isEdible = isEdible;
        this.energy = energy;
        this.baseHealth = baseHealth;
        this.season = season;
        this.canBecomeGiant = canBecomeGiant;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(name).append("\n");
        sb.append("Source: ").append(source).append("\n");
        sb.append("Stages: ").append(stages).append("\n");
        sb.append("Total Harvest Time: ").append(totalHarvestTime).append("\n");
        sb.append("One Time: ").append(oneTime).append("\n");
        sb.append("Regrowth Time: ").append(regrowthTime != null ? regrowthTime : "-").append("\n");
        sb.append("Base Sell Price: ").append(baseSellPrice).append("\n");
        sb.append("Is Edible: ").append(isEdible).append("\n");
        sb.append("Base Energy: ").append(energy != null ? energy : "-").append("\n");
        sb.append("Base Health: ").append(baseHealth != null ? baseHealth : "-").append("\n");
        sb.append("Season: ").append(season).append("\n");
        sb.append("Can Become Giant: ").append(canBecomeGiant);
        return sb.toString();
    }
}



