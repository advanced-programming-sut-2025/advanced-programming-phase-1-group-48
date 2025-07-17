package model.Weather;


import model.Animal.Animal;
import model.Player.EnergySystem;
import model.cook.Buff;
import model.enums.Season;

import model.Player.Player;
import model.game.*;
import model.shop.ShopManager;

import java.util.Collection;
import java.util.Map;

public class DateAndTime {
    private static DateAndTime instance;
    private static int totalDays = 0;
    private static final String[] WEEK_DAYS = {
            "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
    };
    private static int addDays = 1;



    private static int hour = 9;
    private static int day = 1;
    private static int year =0;
    private static int hoursToAdd = 0;
    private static int daysToAdd = 0;
    private static final int DAYS_PER_SEASON = 28;
    public static Season currentSeason = Season.SPRING;

    private DateAndTime() {}

    public static Season getSeasonType() {
        return instance.currentSeason;
    }

    public static DateAndTime getInstance() {
        if (instance == null) {
            instance = new DateAndTime();
        }
        return instance;
    }

    public static void cheatHours(int hoursToAdd){
        System.out.println("it is here?");
        hoursToAdd = hoursToAdd;
        int daysToAdd = hoursToAdd / 13;
        advanceDay(daysToAdd);
        int hoursForAdd = hoursToAdd % 13;
        int currentHour = hour + hoursForAdd;
        if(currentHour >= 22){
            advanceDay(1);
            currentHour = currentHour - 22;
            hour = currentHour + 9;
        } else {
            hour = currentHour;
        }
    }

    public static void cheatDays(int daysToAdd){
        int seasonToAdd = daysToAdd / DAYS_PER_SEASON;
        for(int i = 0; i < seasonToAdd ; i++){
            nextSeason();
        }
        int remainingDaysToAdd = daysToAdd % DAYS_PER_SEASON;
        int currentDay = day + remainingDaysToAdd;
        if(currentDay >= DAYS_PER_SEASON){
            nextSeason();
            currentDay = currentDay - DAYS_PER_SEASON;
            day = currentDay ;
            totalDays = day - 1;
            startDay();
        } else {
            day = currentDay;
            totalDays = day - 1;
            startDay();
        }
    }

    public static void advanceHour(int hours) {
        hour += hours;
        startHour(hour);

        while (hour >= 22) {
            endOfDay();
            hour = 9;
            advanceDay(1);
            startDay();
        }
    }

    public static void advanceDay(int days) {
        addDays=day;
        day+=days;
        totalDays+=days;
        Weather.getInstance().advanceDayAndWeather();
        if (day > DAYS_PER_SEASON) {
            day = 1;
            nextSeason();
        }
    }

    public static Season getCurrentSeason() {
        return currentSeason;
    }

    public static void displayDayOfWeek() {
        int idx = totalDays % WEEK_DAYS.length;
        System.out.printf("Day of week: %s%n", WEEK_DAYS[idx]);
    }

    public void displayDate() {
        System.out.printf("Current date: Year %d, Day %d of %s%n", year, day, currentSeason);
    }

    public static int getTotalDays() {
        return totalDays;
    }

    public static int getHour() {
        return hour;
    }

    public static int getDay() {
        return day;
    }

    private static void nextSeason() {
        int idx = currentSeason.ordinal();
        Season[] seasons = Season.values();
        currentSeason = seasons[(idx + 1) % seasons.length];
        if (currentSeason == Season.SPRING) {
            year++;
        }
    }

    public static void startHour(int currentHour) {
        Game game = GameManager.getCurrentGame();
        Player player= game.getCurrentPlayerForPlay();
        Buff buff = player.getActiveBuff();
        if (buff != null && buff.isExpired(currentHour)) {
            if (buff.getType() == Buff.Type.ENERGY_BOOST) {
                player.setEnergy(player.getMaxEnergy() - buff.getAmount());

                if (player.getEnergy() > player.getMaxEnergy()) {
                    player.setEnergy(player.getMaxEnergy());
                }
            }
            player.setActiveBuff(null);
        }
    }

    public static void startDay() {
        Game game = GameManager.getCurrentGame();
        Collection<Player> players = game.getPlayers();
        for (Player player : players) {
            player.resetDailyEnergy();
        }
        Game.getShopManager().resetDailyStock();
        for (Animal a : Player.getBroughtAnimal().values()) {
            a.produceToday();
            a.setCount(addDays);
        }
        addDays=1;
    }

    public static void endOfDay() {
        WorldMap worldMap = GameManager.getCurrentGame().getWorldMap();
        Game game = GameManager.getCurrentGame();
        worldMap.sendPlayersToHome(game.getPlayers());
        Map<String, Animal> animals = Player.getBroughtAnimal();
        for (Animal a : animals.values()) {
            a.endOfDayUpdate();
        }
    }

}
