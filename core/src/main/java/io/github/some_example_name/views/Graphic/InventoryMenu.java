package io.github.some_example_name.views.Graphic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.Group;
import io.github.some_example_name.model.Player.Player;

public class InventoryMenu extends Group {
    private final Skin skin;
    private final Player player;

    private final Table rootTable;
    private final Table contentTable;
    private final TextButton btnInventory, btnSkills, btnSocial, btnMap;

    private final InventoryView inventoryView;
    private final SkillsView skillsView;
    private final SocialView socialView;
    private final MapView mapView;

    public InventoryMenu(Player player, Skin skin) {
        this.player = player;
        this.skin = skin;

        rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.align(Align.top);

        // 🔹 Header Buttons
        btnInventory = new TextButton("Inventory", skin);
        btnSkills = new TextButton("Skills", skin);
        btnSocial = new TextButton("Social", skin);
        btnMap = new TextButton("Map", skin);

        Table tabTable = new Table();
        tabTable.add(btnInventory).pad(5);
        tabTable.add(btnSkills).pad(5);
        tabTable.add(btnSocial).pad(5);
        tabTable.add(btnMap).pad(5);

        // 🔹 Content Table
        contentTable = new Table();
        contentTable.align(Align.top);

        // 🔹 Create Views
        inventoryView = new InventoryView(player, skin);
        skillsView = new SkillsView(player, skin);
        socialView = new SocialView(player, skin);
        mapView = new MapView(player, skin);

        showInventoryView();

        // 🔹 Button Listeners
        btnInventory.addListener(e -> {
            if (!btnInventory.isChecked()) showInventoryView();
            return true;
        });

        btnSkills.addListener(e -> {
            if (!btnSkills.isChecked()) showSkillsView();
            return true;
        });

        btnSocial.addListener(e -> {
            if (!btnSocial.isChecked()) showSocialView();
            return true;
        });

        btnMap.addListener(e -> {
            if (!btnMap.isChecked()) showMapView();
            return true;
        });

        // 🔹 Assemble
        rootTable.add(tabTable).padTop(10).row();
        rootTable.add(contentTable).expand().fill().pad(10);

        this.addActor(rootTable);
        rootTable.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void clearAndSetView(Actor view) {
        contentTable.clearChildren();
        contentTable.add(view).expand().fill();
    }

    private void showInventoryView() {
        inventoryView.refresh();
        clearAndSetView(inventoryView);
    }

    private void showSkillsView() {
        clearAndSetView(skillsView);
    }

    private void showSocialView() {
        clearAndSetView(socialView);
    }

    private void showMapView() {
        clearAndSetView(mapView);
    }
}
