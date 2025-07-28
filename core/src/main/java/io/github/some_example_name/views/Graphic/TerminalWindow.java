package io.github.some_example_name.views.Graphic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import io.github.some_example_name.controllers.GameController;
import io.github.some_example_name.controllers.PlayerController;
import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.model.cook.FoodRecipe;
import io.github.some_example_name.model.items.ItemFactory;

import java.util.Arrays;

public class TerminalWindow extends Group {
    private final Skin skin;
    private final TextField commandField;
    private final Label outputLabel;
    private final Table container;
    private final Stage stage;
    private Player player;
    private final BitmapFont bitmapFont;
    private final Label.LabelStyle labelStyle;
    private final TextField.TextFieldStyle textFieldStyle;

    public TerminalWindow(Skin skin, Stage stage , GameController controller) {
        this.skin = skin;
        this.stage = stage;
        this.player =controller.getPlayerController().getPlayer();

        bitmapFont = new BitmapFont();
        labelStyle = new Label.LabelStyle();
        labelStyle.font = bitmapFont;
        textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = bitmapFont;
        textFieldStyle.fontColor = skin.getColor("white"); // یا هر رنگی که مناسب است
//        textFieldStyle.cursor = skin.getDrawable("cursor");
//        textFieldStyle.selection = skin.getDrawable("selection");
//        textFieldStyle.background = skin.getDrawable("textfield");

        container = new Table(skin);
        container.setBackground("window");

        container.align(Align.topLeft);
        container.setSize(600, 600);
        container.setPosition((Gdx.graphics.getWidth() - 600) / 2f, -200);

        Label titleLabel = new Label("Terminal", labelStyle);
        commandField = new TextField("", textFieldStyle);
        outputLabel = new Label("", labelStyle);
        outputLabel.setWrap(true);

        commandField.setMessageText("Enter command...");
        commandField.setTextFieldListener((field, c) -> {
            if (c == '\r' || c == '\n') {
                handleCommand(field.getText());
                field.setText("");
            }
        });

        container.add(titleLabel).left().pad(10).row();
        container.add(commandField).width(580).pad(10).row();
        container.add(outputLabel).width(580).pad(10).row();

        this.setSize(600, 200);
        this.addActor(container);
    }

    public void toggle() {
        if (this.isVisible()) {
            container.addAction(Actions.sequence(
                Actions.moveTo(container.getX(), -container.getHeight(), 0.3f),
                Actions.run(() -> setVisible(false))
            ));
        } else {
            setVisible(true);
            container.addAction(Actions.moveTo(container.getX(), 20, 0.3f));
            stage.setKeyboardFocus(commandField); // تمرکز روی فیلد ورودی
        }
    }

    private void handleCommand(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String[] parts = text.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "help":
                outputLabel.setText("Available commands: help, clear, gold, add <itemName>");
                break;

            case "clear":
                outputLabel.setText("");
                break;

            case "gold":
                outputLabel.setText("Gold: 99999 (cheat not applied yet)");
                break;

            case "add":
                if (parts.length < 2) {
                    outputLabel.setText("Usage: add <itemName>");
                } else {
                    String itemName = parts[1].toLowerCase();

                    ItemFactory.createItem(itemName, player.getInventory());

                    outputLabel.setText("Added item: " + itemName);
                }
                break;
            case "recipe":
                if (parts.length < 2) {
                    outputLabel.setText("Please specify the recipe name.");
                } else {
                    String foodName = String.join("_", Arrays.copyOfRange(parts, 1, parts.length)).toUpperCase();
                    try {
                        FoodRecipe recipe = FoodRecipe.valueOf(foodName);
                        player.learnRecipe(recipe);
                        outputLabel.setText("Learned Recipe: " + recipe.getName());
                    } catch (IllegalArgumentException e) {
                        outputLabel.setText("No such recipe: " + foodName.replace("_", " "));
                    }
                }
                break;

            default:
                outputLabel.setText("Unknown command: " + text);
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
