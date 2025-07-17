package model.Player;

import model.Animal.Animal;
import model.NPC.NPC;
import model.NPC.Quest;
import model.Plant.PlantInstance;
import model.Plant.PlantedInfo;
import model.Plant.Tree;
import model.Player.inventory.Refrigerator;
import model.Player.inventory.TrashCan;
import model.building.Buildings;
import model.cook.Buff;
import model.cook.FoodRecipe;
import model.enums.BackpackType;
import model.enums.TileType;
import model.enums.TrashCanType;
import model.enums.WateringCanType;
import model.farm.Farm;
import model.game.Position;
import model.intraction.Friendship;
import model.intraction.TradeRequest;
import model.items.Item;
import model.items.ItemFactory;
import model.shop.Shop;
import model.Player.inventory.Backpack;
import model.Player.inventory.Inventory;
import model.user.User;


import java.util.*;

public class Player {
    private Buff activeBuff;
    private boolean isMarried = false;
    private String spouseUsername = null;
    private int energyPenaltyDays = 0;
    private WateringCanType wateringCanType = WateringCanType.BASIC;
    private int currentWaterAmount = wateringCanType.getCapacity();


    private String gender;
    private String name;
    private EnergySystem energySystem;
    private Wallet wallet;
    private Shop shop;
    private Position position;
    private Farm farm;
    private EnergySystem energy;
    private User user;
    private Backpack backpack;
    private TrashCan trashCan;
    private Inventory inventory;
    private static Map<String, Animal> broughtAnimals = new HashMap<>();
    private Map<String, Skill> skills;
    private ArrayList<Animal> animals;
    private ArrayList<Friendship> friendships;
    private ArrayList<TradeRequest> tradeRequests;
    private ArrayList<Quest> quests;
    private ArrayList<HashMap<NPC, Integer>> npcs;
    private ArrayList<HashMap<Player, Friendship>> friend;
    private List<String> notifications = new ArrayList<>();
    private Set<FoodRecipe> learnedRecipes = new HashSet<>();
    private Item equippedItem;
    private static Map<Buildings, Integer> myBuildings = new HashMap<>();
    private Refrigerator refrigerator = new Refrigerator();
    private Set<String> learnedRecipesForHome = new HashSet<>();
    private static List<PlantedInfo> plantedList = new ArrayList<>();

    public static List<PlantedInfo> getPlantedList() {
        return plantedList;
    }

    public static void addPlantedInfo(int x, int y, PlantInstance plant) {
        plantedList.add(new PlantedInfo(x, y, plant));
    }

    public static PlantInstance getPlantedAt(int x, int y) {
        for (PlantedInfo info : getPlantedList()) {
            if (info.getX() == x && info.getY() == y) {
                return info.getPlant();
            }
        }
        return null;
    }

    public static Tree getPlantedTreeAt(int x, int y) {
        PlantInstance p = getPlantedAt(x,y);
        return (p instanceof Tree) ? (Tree)p : null;
    }

    public Player() {
    }

    public Player(String name, Position position, Farm farm, User user) {
        this.name = name;
        this.position = position;
        this.farm = farm;
        this.energy = energy;
        this.user = user;
        this.skills = new HashMap<>();
        skills.put("Farming", new Skill("Farming"));
        skills.put("Mining", new Skill("Mining"));
        skills.put("Foraging", new Skill("Foraging"));
        skills.put("Fishing", new Skill("Fishing"));
        this.gender = gender;
        this.backpack = new Backpack(BackpackType.SMALL);
        this.trashCan = new TrashCan(TrashCanType.BASIC);
        this.inventory = new Inventory(backpack, trashCan);
        ItemFactory.createItem("basic_hoe", inventory);
        ItemFactory.createItem("basic_pickaxe", inventory);
        ItemFactory.createItem("basic_axe", inventory);
        ItemFactory.createItem("Basic_Watering_Can", inventory);
        ItemFactory.createItem("Scythe", inventory);
        this.friend = friend;
        this.friendships = friendships;
        this.tradeRequests = tradeRequests;
        this.quests = quests;
        this.npcs = npcs;
        this.shop = null;
        this.wallet = new Wallet();

        this.energySystem = new EnergySystem();
        this.wallet = new Wallet();
        this.animals = new ArrayList<>();
        this.friendships = new ArrayList<>();
        this.tradeRequests = new ArrayList<>();
        this.quests = new ArrayList<>();
        this.npcs = new ArrayList<>();
    }

    public void setEquippedItem(Item item) {
        this.equippedItem = item;
    }

    public Item getEquippedItem() {
        return equippedItem;
    }


    public Inventory getInventory() {

        return inventory;

    }
    public void learnRecipeForHome(String recipeName) {
        learnedRecipesForHome.add(recipeName.toLowerCase());
    }

    public boolean hasLearnedRecipeForHome(String recipeName) {
        return learnedRecipesForHome.contains(recipeName.toLowerCase());
    }

    public Player getCurrentPlayer() {
        return this;
    }
    public Skill getSkill(String name) {
        if (!skills.containsKey(name)) {
            throw new IllegalArgumentException("Skill '" + name + "' does not exist.");
        }
        return skills.get(name);
    }

    public String getName() {
        return name;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public boolean hasLearnedRecipe(String itemName) {
        return learnedRecipes.contains(itemName.toLowerCase());
    }

    public void learnRecipe(FoodRecipe recipe) {
        learnedRecipes.add(recipe);
    }

    public ArrayList<Animal> getAnimals() {
        return animals;
    }


    public ArrayList<Friendship> getFriendships() {
        return friendships;
    }

    public ArrayList<TradeRequest> getTradeRequests() {
        return tradeRequests;
    }

    public ArrayList<Quest> getQuests() {
        return quests;
    }

    public ArrayList<HashMap<NPC, Integer>> getNpcs() {
        return npcs;
    }

    private TileType previousTileType = TileType.CABIN; // فرض اولیه

    public TileType getPreviousTileType() {
        return previousTileType;
    }

    public void setPreviousTileType(TileType type) {
        this.previousTileType = type;
    }


    public EnergySystem getEnergySystem() {
        return energySystem;
    }

    public int getEnergy() {
        return energySystem.getEnergy();
    }

    public void decreaseEnergy(int amount) {
        energySystem.decreaseEnergy(amount);
        if (energySystem.isPassedOut()) {
            // dont do the work
        }
    }

    public void setEnergy(int value) {
        energySystem.setEnergy(value);
    }

    public void increaseEnergy(int amount) {
        energySystem.increaseEnergy(amount);
    }


    public void setUltimate() {
        energySystem.setUnlimited(true);
    }



    public String getGender() {
        return gender;
    }


    public boolean isMarried() {
        return isMarried;
    }


    public void addNotification(String message) {
        notifications.add(message);
    }


    public List<String> getNotifications() {

        return new ArrayList<>(notifications);

    }


    public static Map<Buildings, Integer> getMyBuildings() {
        return myBuildings;
    }


    public static void addBuilding(Buildings building) {
        myBuildings.merge(building, 1, Integer::sum);
    }


    public Refrigerator getRefrigerator() {
        return refrigerator;
    }


    public static void addBroughtAnimal(String name, Animal animal) {
        broughtAnimals.put(name, animal);
    }


    public static Map<String, Animal> getBroughtAnimal() {
        return broughtAnimals;
    }


    public static Map<Buildings, Integer> getMyBuilding() {
        return myBuildings;
    }


    public void resetDailyEnergy() {
        energySystem.resetDailyEnergy();
    }


    public boolean knowsRecipe(FoodRecipe recipe) {
        return learnedRecipes.contains(recipe);
    }


    public Set<FoodRecipe> getLearnedRecipes() {
        return Collections.unmodifiableSet(learnedRecipes);
    }


    public void setActiveBuff(Buff buff) {
        this.activeBuff = buff;
    }


    public Buff getActiveBuff() {
        return activeBuff;
    }

    public int getMaxEnergy() {
        return energySystem.getMaxEnergy();
    }
    public void addMoney(int amount) {
        wallet.add(amount);
    }
    public void setMaxEnergy() {
        energySystem.setMaxEnergy(200);
    }
    public int getWaterAmount() {
        return currentWaterAmount;
    }
    public void upgradeBackpack(BackpackType newType) {
        backpack.upgrade(newType);//mary ***************
    }

    public int getMoney() {
        return wallet.getGold();
    }//mary ********

    public void decreaseMoney(int amount) {
        wallet.deduct(amount);
    }//mary **********

    public BackpackType getBackpack() {
        return backpack.getType();
    }

    public WateringCanType getWateringCanType() {
        return wateringCanType;
    }

    public void setWateringCanType(WateringCanType newType) {
        this.wateringCanType = newType;
        this.currentWaterAmount = newType.getCapacity();
    }

    public boolean decreaseWater(int amount) {
        if (currentWaterAmount >= amount) {
            currentWaterAmount -= amount;
            return true;
        }
        return false;
    }

    public void refillWater() {
        currentWaterAmount = wateringCanType.getCapacity();
    }

    public String getUsername() {
        return name;
    }

    public static void removePlantedInfo(int x, int y) {
        plantedList.removeIf(info -> info.getX() == x && info.getY() == y);
    }

    public static boolean removePlantedTreeAt(int x, int y) {
        PlantInstance p = getPlantedAt(x, y);
        if (p instanceof Tree) {
            return plantedList.removeIf(info ->
                    info.getX() == x &&
                            info.getY() == y &&
                            info.getPlant() instanceof Tree
            );
        }
        return false;
    }


}
