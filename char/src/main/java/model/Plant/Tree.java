package model.Plant;


import model.enums.Season;
import model.game.Game;
import model.Player.Player;
import model.game.GameManager;
import model.items.Item;
import model.items.ItemFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Tree implements PlantInstance {
    private final TreeType type;
    private int age;
    private int daysSinceLastFruit;
    private boolean isBurned;

    public Tree(TreeType type) {
        this.type = type;
        this.age = 0;
        this.daysSinceLastFruit = 0;
        this.isBurned = false;
    }

    /** پیشروی یک روز: افزایش سن تا بلوغ و شمارش فاصلهٔ برداشت پس از بلوغ */
    @Override
    public void growOneDay() {
        if (age < type.getTotalTime()) {
            age++;
        } else if (!isBurned) {
            daysSinceLastFruit++;
        }
    }

    /** ریست کردن پرچم آب‌دهی (درخت نیاز به آب ندارد) */
    @Override
    public void resetWaterFlag() {
        // no-op برای درخت
    }

    /** آب دادن (معمولاً روی درخت اعمال نمی‌شود) */
    @Override
    public void water() {
        // no-op برای درخت
    }

    /** آیا بلوغ اولیه کامل شده و فاصلهٔ برداشت دوره‌ای سپری شده؟ */
    @Override
    public boolean isReadyToHarvest() {
        return !isBurned
                && age >= type.getTotalTime()
                && daysSinceLastFruit >= type.getFruitCycle();
    }

    /**
     * برداشت میوه یا ذغال:
     * - اگر سوخته باشد ذغال‌سنگ بده
     * - در غیر این صورت میوه بده و فاصلهٔ برداشت را ریست کن
     */
    @Override
    public Item harvest() {
        Game game = GameManager.getCurrentGame();
        Player player = game.getCurrentPlayerForPlay();
        if (isBurned) {
            Item coal = ItemFactory.createItem("coal", player.getInventory());

//            player.getInventory().add(coal);
            return coal;
        }
        if (isReadyToHarvest()) {
            daysSinceLastFruit = 0;
            Item fruit = ItemFactory.createItem(type.getFruit(), player.getInventory());
//            player.getInventory().add(fruit);
            return fruit;
        }
        return null;
    }

    /** علامت‌گذاری درخت به‌عنوان سوخته */
    public void strikeByLightning() {
        isBurned = true;
    }

    /** قطع درخت: تولید ۱–۲ دانه و حذف درخت از زمین */
    public void cutDown() {
        int count = new Random().nextInt(2) + 1;
        Game game = GameManager.getCurrentGame();
        Player player = game.getCurrentPlayerForPlay();
        for (int i = 0; i < count; i++) {
            ItemFactory.createItem(type.getName(), player.getInventory());
        }
    }

    // --- اختیاری: getter برای وضعیت سوختگی و بلوغ ---
    public boolean isBurned() {
        return isBurned;
    }

    public boolean isMature() {
        return age >= type.getTotalTime();
    }

    public TreeType getType() {
        return type;
    }
}
