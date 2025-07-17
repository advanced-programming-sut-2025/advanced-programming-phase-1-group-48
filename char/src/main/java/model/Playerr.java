package model;

import model.Animal.Animal;
import model.NPC.NPC;
import model.NPC.Quest;
import model.Player.inventory.TrashCan;
import model.enums.BackpackType;
import model.enums.TrashCanType;
import model.farm.Farm;
import model.game.Position;
import model.intraction.Friendship;
import model.intraction.TradeRequest;
import model.items.Item;
import model.shop.Shop;
import model.Player.inventory.Backpack;
import model.Player.inventory.Inventory;
import model.user.EnergySystem;
import model.user.SkillSet;
import model.user.User;
import model.user.Wallet;

import java.util.ArrayList;
import java.util.HashMap;

public class Playerr {
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
    private ArrayList<Animal> animals;
    private ArrayList<SkillSet> skills;
    private ArrayList<Friendship> friendships;
    private ArrayList<TradeRequest> tradeRequests;
    private ArrayList<Quest> quests;
    private ArrayList<HashMap<NPC, Integer>> npcs;
    private ArrayList<HashMap<Playerr,Friendship>> friend;
    private Item equippedItem;

    public Playerr(Position position, Farm farm, EnergySystem energy, User user, ArrayList<Animal> animals,
                   ArrayList<SkillSet> skills, ArrayList<Friendship> friendships,
                   ArrayList<TradeRequest> tradeRequests, ArrayList<Quest> quests,
                   ArrayList<HashMap<NPC, Integer>> npcs, ArrayList<HashMap<Playerr,Friendship>> friend ) {
        this.position = position;
        this.farm = farm;
        this.energy = energy;
        this.user = user;
        this.animals = animals;
        this.skills = skills;
        this.backpack = new Backpack(BackpackType.SMALL);
        this.trashCan = new TrashCan(TrashCanType.BASIC);
//        ItemFactory.createItem("basic hoe", inventory);
//        ItemFactory.createItem("basic pickaxe", inventory);
//        ItemFactory.createItem("basic axe", inventory);
        this.friend = friend;
        this.friendships = friendships;
        this.tradeRequests = tradeRequests;
        this.quests = quests;
        this.npcs = npcs;
        this.shop = null;
        this.wallet = new Wallet();
        this.energySystem = new EnergySystem();
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
    public Shop getShop() {
        return shop;
    }
    public int getMoney() {
        return wallet.getGold();
    }
    public void decreaseMoney(int amount) {
        wallet.deduct(amount);
    }
    public void addMoney(int amount) {
        wallet.add(amount);
    }
    public int getEnergy() {
        return energySystem.getEnergy();
    }

    public void decreaseEnergy(int amount) {
        energySystem.decreaseEnergy(amount);
        if (energySystem.isPassedOut()) {

        }
    }
    public void setEnergy(int value) {
        energySystem.setEnergy(value);
    }
    public void setUltimate() {
        energySystem.setUnlimited(true);
    }

}
