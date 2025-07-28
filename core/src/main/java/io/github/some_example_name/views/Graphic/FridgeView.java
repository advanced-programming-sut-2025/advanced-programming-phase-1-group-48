package io.github.some_example_name.views.Graphic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.controllers.CookController;
import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.model.Player.inventory.Inventory;
import io.github.some_example_name.model.items.Item;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.Map;

public class FridgeView extends Group {
    private final Player player;
    private final Skin skin;
    private final Table itemTable;
    private final ScrollPane scrollPane;
    private Runnable onBackToCookingMenu;
    private final DragAndDrop dragAndDrop;
    private final Label.LabelStyle customLabelStyle;


    public FridgeView(Player player, Skin skin) {
        this.player = player;
        this.skin = skin;

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.2f);  // اندازه فونت (در صورت نیاز تغییر دهید)
        font.setColor(Color.BLACK);
        customLabelStyle = new Label.LabelStyle(font, Color.BLACK);

        this.setSize(1000, 500);
        this.dragAndDrop = new DragAndDrop();

        itemTable = new Table(skin);
        itemTable.align(Align.topLeft);

        // Background
        Texture backgroundTexture = new Texture("inventory/inventory_background.png");
        Image backgroundImage = new Image(new TextureRegionDrawable(new TextureRegion(backgroundTexture)));
        backgroundImage.setSize(1000, 100);
//        backgroundImage.setFillParent(true);


        // Icon for back
        Texture iconTex = new Texture("Cook/refrigerator.jpg");
        Image fridgeIcon = new Image(new TextureRegionDrawable(new TextureRegion(iconTex)));
        fridgeIcon.setSize(48, 48);
        fridgeIcon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                FridgeView.this.setVisible(false);
                if (onBackToCookingMenu != null) onBackToCookingMenu.run();
            }
        });

        // Top bar
        Table topBar = new Table(skin);
        topBar.add(fridgeIcon).size(48, 48).left().padRight(10);
        topBar.add(new Label("Refrigerator", customLabelStyle))  // ۳. استفاده از استایل دلخواه
            .left().padTop(10);
        topBar.align(Align.left);

        Stack stack = new Stack();
        stack.add(backgroundImage);
        stack.add(itemTable);

        scrollPane = new ScrollPane(stack, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.align(Align.top);
        mainTable.add(topBar).pad(10).left().row();
        mainTable.add(scrollPane).width(1000).height(100).padTop(5).row();


        this.addActor(mainTable);

        update();
    }

    public void update() {
        itemTable.clear();
        dragAndDrop.clear();

        Map<String, Integer> items = player.getRefrigerator().getItems();
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            String itemName = entry.getKey();
            int quantity = entry.getValue();

            Item item = Inventory.itemInstances.get(itemName);
            if (item == null) continue;

            Texture itemTexture = new Texture("item/" + item.getName() + ".png");
            Image itemImage = new Image(new TextureRegionDrawable(new TextureRegion(itemTexture)));
            itemImage.setScaling(Scaling.fit);
            itemImage.setSize(48, 48);

            Label qtyLabel = new Label("x" + quantity, customLabelStyle);
            Label nameLabel = new Label(item.getName(), customLabelStyle);

            Table row = new Table(skin);
            row.add(itemImage).size(48, 48).padRight(10);
            row.add(nameLabel).left().expandX();
            row.add(qtyLabel).right();

            // ✅ کلیک برای برداشتن آیتم از یخچال
            row.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String result = CookController.handleRefrigeratorCommand(player, "pick", item.getName());
                    System.out.println(result);
                    update();
                }
            });

            // ✅ Drag Source برای آیتم
            dragAndDrop.addSource(new DragAndDrop.Source(itemImage) {
                @Override
                public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    DragAndDrop.Payload payload = new DragAndDrop.Payload();
                    payload.setObject(item);
                    payload.setDragActor(new Image(new TextureRegionDrawable(new TextureRegion(itemTexture))));
                    return payload;
                }
            });

            itemTable.add(row).padBottom(5).row();
        }


        dragAndDrop.addTarget(new DragAndDrop.Target(this) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return payload.getObject() instanceof Item;
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Item draggedItem = (Item) payload.getObject();
                String result = CookController.handleRefrigeratorCommand(player, "put", draggedItem.getName());
                System.out.println(result);
                update();
            }
        });
    }

    public void setOnBackToCookingMenu(Runnable r) {
        this.onBackToCookingMenu = r;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
