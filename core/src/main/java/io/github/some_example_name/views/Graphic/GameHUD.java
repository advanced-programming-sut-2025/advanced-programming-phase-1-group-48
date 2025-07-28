package io.github.some_example_name.views.Graphic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import io.github.some_example_name.controllers.GameController;
import io.github.some_example_name.model.Weather.DateAndTime;
import io.github.some_example_name.model.Weather.Weather;
import io.github.some_example_name.model.enums.Season;
import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.model.enums.WeatherType;
import java.util.function.Consumer;

public class GameHUD extends Actor {

    public static Consumer<String> showMessage;

    private static final float TIME_SPEED = 5.0f;
    private float timer = 0f;

    private DateAndTime dateAndTime;
    private Player player;

    private BitmapFont font;
    private Texture hudBackground;
    private Texture weatherIcon;
    private Texture seasonIcon;

    private String messageText = null;
    private float messageTimer = 0f;
    private static final float MESSAGE_DURATION = 3f;

    public GameHUD(GameController controller) {
        this.font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.getData().setScale(1.2f);
        this.dateAndTime = DateAndTime.getInstance();
        this.player = controller.getPlayerController().getPlayer();

        hudBackground = new Texture("hud/hud_background.png");
        updateIcons();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timer += delta;

        if (timer >= TIME_SPEED) {
            DateAndTime.advanceHour(1);
            timer = 0;
            updateIcons();
        }

        if (messageTimer > 0) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                messageText = null; // پاک شدن پیام
            }
        }
    }


    private void updateIcons() {
        Season season = dateAndTime.getCurrentSeason();
        switch (season) {
            case SPRING:
                seasonIcon = new Texture("hud/spring_icon.png");
                break;
            case SUMMER:
                seasonIcon = new Texture("hud/summer_icon.png");
                break;
            case FALL:
                seasonIcon = new Texture("hud/fall_icon.png");
                break;
            case WINTER:
                seasonIcon = new Texture("hud/winter_icon.png");
                break;
        }

        WeatherType weather = Weather.getInstance().getToday();
        switch (weather) {
            case SUNNY:
                weatherIcon = new Texture("hud/sunny_icon.png");
                break;
            case RAINY:
                weatherIcon = new Texture("hud/rain_icon.png");
                break;
            case SNOW:
                weatherIcon = new Texture("hud/snow_icon.png");
                break;
            case STORM:
                weatherIcon = new Texture("hud/storm_icon.png");
                break;
            default:
                weatherIcon = new Texture("hud/sunny_icon.png");
                break;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        float hudWidth = 200;    // عرض دلخواه HUD
        float hudHeight = 200;

        float x = getStage().getWidth() - hudWidth - 20;
        float y = getStage().getHeight() - hudHeight - 20;
        batch.draw(hudBackground, x, y, hudWidth, hudHeight);


        String dayOfWeek = getWeekDay();
        int day = DateAndTime.getDay();
        String season = DateAndTime.getCurrentSeason().name();
        font.draw(batch, dayOfWeek + " " + day + " " + season, x + 75, y + 180);


        int hour = DateAndTime.getHour();
        String hourFormatted = formatHour(hour);
        font.draw(batch, hourFormatted, x + 100, y + 100);
//        font.draw(batch, "Gold: " + gold, x + 20, y + 30);

        float seasonIconWidth = 30;
        float seasonIconHeight = 30;

        float seasonX = x + hudWidth - seasonIconWidth - 20;
        float seasonY = y + 120;

        float weatherX = x + hudWidth - seasonIconWidth - 85;
        float weatherY = y + hudHeight - seasonIconHeight - 50;

        if (weatherIcon != null) {
            batch.draw(weatherIcon, weatherX, weatherY, seasonIconWidth, seasonIconHeight);
        }
        if (seasonIcon != null) {
            batch.draw(seasonIcon, seasonX, seasonY, seasonIconWidth, seasonIconHeight);
        }

        if (messageText != null) {
            font.setColor(Color.RED);
            font.draw(batch, messageText,x - 900, y + 200); // موقعیت دلخواه (مثلاً پایین صفحه)
            font.setColor(Color.BLACK); // بازگشت رنگ فونت
        }

    }

    private String getWeekDay() {
        int idx = DateAndTime.getTotalDays() % 7;
        return DateAndTime.getWeekDays(idx).substring(0, 3);
    }

    private String formatHour(int hour) {
        int h = hour;
        String ampm = "AM";
        if (h >= 12) {
            ampm = "PM";
            if (h > 12) h -= 12;
        }
        if (h == 0) h = 12;
        return String.format("%d:00 %s", h, ampm);
    }

    public void showMessage(String msg) {
        this.messageText = msg;
        this.messageTimer = MESSAGE_DURATION;
    }

}
