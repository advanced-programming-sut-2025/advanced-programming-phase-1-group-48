package io.github.some_example_name.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;
import io.github.some_example_name.controllers.GameController;
import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.model.Tools.Tool;
import io.github.some_example_name.model.crafting.CraftingManager;
import io.github.some_example_name.model.enums.ToolActionState;
import io.github.some_example_name.views.Graphic.*;

public class GameView implements Screen, InputProcessor {
    private Stage stage;
    private GameController controller;
    private InventoryMenu inventoryMenu;
    private boolean isInventoryOpen = false;
    private Skin skin;
    private ToolInventoryView toolInventoryView;
    private boolean isToolInventoryOpen = false;
    private boolean isCookingMenuOpen = false;
    private CookingMenu cookingMenu;
    private TerminalWindow terminalWindow;
    private boolean isTerminalWindowOpen = false;
    private InventoryView inventoryView;
    private FridgeView fridgeView;
    private boolean isFridgeOpen = false;
    private CraftingView craftingView;
    private boolean isCraftingViewOpen = false;

    public GameView(GameController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        controller.setView(this);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(this);
        Player player = controller.getPlayerController().getPlayer();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
        GameHUD hud = new GameHUD(controller);
        GameHUD.showMessage = hud::showMessage;
        stage.addActor(hud);
        inventoryMenu = new InventoryMenu(player, skin);
        inventoryMenu.setVisible(false);
        inventoryMenu.setPosition((Gdx.graphics.getWidth() - inventoryMenu.getWidth()) / 2f, (Gdx.graphics.getHeight() - inventoryMenu.getHeight()) / 2f);
        stage.addActor(inventoryMenu);
        toolInventoryView = new ToolInventoryView(player, skin);
        toolInventoryView.setVisible(false);
        toolInventoryView.updateToolTable();
        stage.addActor(toolInventoryView);
        cookingMenu = new CookingMenu(player, skin);
        cookingMenu.setVisible(false);
        stage.addActor(cookingMenu);
        inventoryView = new InventoryView(player, skin);
        inventoryView.setVisible(false);
        inventoryView.setPosition((Gdx.graphics.getWidth() - inventoryView.getWidth()) / 2f, (Gdx.graphics.getHeight() - inventoryView.getHeight()) / 2f - 350);
        stage.addActor(inventoryView);
        fridgeView = new FridgeView(player, skin);
        fridgeView.setVisible(false);
        fridgeView.setPosition((Gdx.graphics.getWidth() - fridgeView.getWidth()) / 2f, (Gdx.graphics.getHeight() - fridgeView.getHeight()) / 2f - 300);
        stage.addActor(fridgeView);
        inventoryView.addFridgeDropTarget(fridgeView);
        fridgeView.setOnBackToCookingMenu(() -> {
            fridgeView.setVisible(false);
            inventoryView.setVisible(false);
            cookingMenu.setVisible(true);
            isFridgeOpen = false;
            isCookingMenuOpen = true;
        });
        cookingMenu.setOnFridgeIconClicked(() -> {
            cookingMenu.setVisible(false);
            fridgeView.update();
            inventoryView.refresh();
            fridgeView.setVisible(true);
            inventoryView.setVisible(true);
            isFridgeOpen = true;
            isCookingMenuOpen = false;
        });
        terminalWindow = new TerminalWindow(skin, stage, controller);
        terminalWindow.setVisible(false);
        stage.addActor(terminalWindow);
        CraftingManager.initializeRecipes();
        craftingView = new CraftingView(player, skin);
        craftingView.setVisible(false);
        craftingView.setPosition((Gdx.graphics.getWidth() - craftingView.getWidth()) / 2f, (Gdx.graphics.getHeight() - craftingView.getHeight()) / 2f - 300);
        stage.addActor(craftingView);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        controller.updateGame(delta);
        Player player = controller.getPlayerController().getPlayer();
        Tool equippedTool = player.getEquippedTool();
        if (equippedTool != null && player.getEquippedToolSprite() != null) {
            Sprite toolSprite = player.getEquippedToolSprite();
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            float angle = MathUtils.atan2(mouseY - player.getPosY(), mouseX - player.getPosX()) * MathUtils.radiansToDegrees;
            toolSprite.setRotation(angle);
            toolSprite.setPosition(player.getPosX()+30, player.getPosY()+30);
            if (player.getToolState() == ToolActionState.USING) {
                toolSprite.setScale(1.2f);
                toolSprite.setAlpha(0.8f);
            } else {
                toolSprite.setScale(1f);
                toolSprite.setAlpha(1f);
            }
            toolSprite.draw(Main.getBatch());
            toolSprite.setAlpha(1f);
        }
        player.updateTool(delta);
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) player.useTool();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.T) {
            isToolInventoryOpen = !isToolInventoryOpen;
            toolInventoryView.updateToolTable();
            toolInventoryView.setVisible(isToolInventoryOpen);
        }
        if (keycode == Input.Keys.ESCAPE) {
            isInventoryOpen = !isInventoryOpen;
            inventoryMenu.setVisible(isInventoryOpen);
        }
        if (keycode == Input.Keys.C) {
            isCookingMenuOpen = !isCookingMenuOpen;
            cookingMenu.updateRecipes();
            cookingMenu.setVisible(isCookingMenuOpen);
        }
        if (keycode == Input.Keys.R) {
            isTerminalWindowOpen = !isTerminalWindowOpen;
            terminalWindow.toggle();
        }
        if (keycode == Input.Keys.B) {
            isCraftingViewOpen = !isCraftingViewOpen;
            craftingView.updateRecipes();
            craftingView.setVisible(isCraftingViewOpen);
        }
        return false;
    }

    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
