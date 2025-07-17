package controllers;

import model.Player.Player;
import model.Player.inventory.Inventory;
import model.Player.inventory.Refrigerator;
import model.Result;
import model.cook.Buff;
import model.cook.Food;
import model.cook.FoodRecipe;
import model.cook.Ingredient;
import model.game.Game;
import model.game.GameManager;
import model.game.WorldMap;
import model.items.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CookController {
    public static String handleRefrigeratorCommand(Player player, String action, String itemName) {
        Game game = GameManager.getCurrentGame();
        WorldMap worldmap = game.getWorldMap();
        if (worldmap.isInPlayerCabin(player)){
            return "You must be at home to use the refrigerator.";
        }

        Inventory inventory = player.getInventory();
        Refrigerator refrigerator = player.getRefrigerator();

        if (action.equalsIgnoreCase("put")) {
            if (!inventory.hasItem(itemName)) {
                return "You don't have " + itemName + " in your inventory.";
            }
            Item item = Inventory.itemInstances.get(itemName.toLowerCase());
            if (item == null || !item.isEdible()) {
                return itemName + " is not edible and cannot be put in the refrigerator.";
            }
            refrigerator.addItem(item);
            inventory.removeItem(itemName, 1);
            return itemName + " has been placed in the refrigerator.";
        } else if (action.equalsIgnoreCase("pick")) {
            if (!refrigerator.hasItem(itemName)) {
                return "No such item in the refrigerator.";
            }
            Item item = Inventory.itemInstances.get(itemName.toLowerCase());
            Result result = inventory.addItem(item);
            if (!result.success()) {
                return "Cannot pick item in your inventory.";
            }
            refrigerator.removeItem(itemName,1);
            return "Picked " + itemName + " from the refrigerator.";
        } else {
            return "Invalid action. Use 'put' or 'pick'.";
        }
    }

    public static String showLearnedRecipes(Player player) {
        if (player.getLearnedRecipes().isEmpty()) return "You haven't learned any recipes yet.";
        return player.getLearnedRecipes().stream()
                .map(FoodRecipe::getName)
                .collect(Collectors.joining(", "));
    }

    public static String prepareFood(Player player, String recipeName) {
        FoodRecipe recipe;
        try {
            recipe = FoodRecipe.valueOf(recipeName.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return "No such recipe exists.";
        }

        if (!player.knowsRecipe(recipe)) {
            return "You haven't learned this recipe yet.";
        }

        Map<Ingredient, Integer> ingredients = recipe.getIngredients();
        Map<Ingredient, Integer> fromInventory = new HashMap<>();
        Map<Ingredient, Integer> fromFridge = new HashMap<>();

        for (Map.Entry<Ingredient, Integer> entry : ingredients.entrySet()) {
            Ingredient ing = entry.getKey();
            int required = entry.getValue();
            int inInv = player.getInventory().getItemInstance(ing.name());
            int inFridge = player.getRefrigerator().getItemCount(ing.name());

            if (inInv + inFridge < required) {
                return "Not enough ingredients to cook " + recipe.getName();
            }

            // Decide from where to take
            int fromInv = Math.min(required, inInv);
            int fromRef = required - fromInv;

            if (fromInv > 0) fromInventory.put(ing, fromInv);
            if (fromRef > 0) fromFridge.put(ing, fromRef);
        }

        int energy = player.getEnergy();


        if (energy < 3) {
            return "Not enough energy to cook.";
        }
        player.decreaseEnergy(energy);

        Food food = new Food(recipe);
        Result result = player.getInventory().addItem(food);
        if (!result.success()) {
            return "Inventory is full.";
        }

        for (Map.Entry<Ingredient, Integer> e : fromInventory.entrySet()) {
            player.getInventory().removeItem(e.getKey().name(), e.getValue());
        }
        for (Map.Entry<Ingredient, Integer> e : fromFridge.entrySet()) {
            player.getRefrigerator().removeItem(e.getKey().name(), e.getValue());
        }

        return "Cooked " + recipe.getName() + " successfully.";
    }

    public static String eat(Player player,String foodName) {
        Game game = GameManager.getCurrentGame();
        long currentHour = game.getCurrentHour();
        String key = foodName.toLowerCase();
        Inventory inv = player.getInventory();

        if (!inv.hasItem(key)) return "You don't have " + foodName + " in your inventory.";
        Item item = Inventory.itemInstances.get(key);
        if (!(item instanceof Food)) return "This item is not edible.";

        Food food = (Food) item;
        inv.removeItem(key, 1);


        player.increaseEnergy(food.getEnergyRestoration());
        String newBuffStr = food.getBuff();
        if (newBuffStr != null) {
            Buff newBuff = Buff.parseBuff(newBuffStr, currentHour);
            player.setActiveBuff(newBuff);
            if (newBuff.getType() == Buff.Type.ENERGY_BOOST){
                player.setEnergy(player.getMaxEnergy() + newBuff.getAmount());
            }
            return  ("You gained a buff: " + newBuff.getType() + " for " + newBuff.getDurationInHours() + " hours.");
        } else {
            player.setActiveBuff(null);
            return "You ate " + food.getName() + ". Energy restored: " + food.getEnergyRestoration();

        }

    }
}
