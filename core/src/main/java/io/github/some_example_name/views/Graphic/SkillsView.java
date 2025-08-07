package io.github.some_example_name.views.Graphic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import io.github.some_example_name.model.Player.Player;
import io.github.some_example_name.model.Player.Skill;

import java.util.Map;

public class SkillsView extends Group {
    private final Player player;
    private final Skin skin;
    private final Table skillsTable;
    private final ScrollPane scrollPane;

    public SkillsView(Player player, Skin skin) {
        this.player = player;
        this.skin = skin;

        skillsTable = new Table(skin);
        skillsTable.align(Align.topLeft).pad(10);
        updateSkillsTable();

        scrollPane = new ScrollPane(skillsTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setScrollbarsOnTop(true);

        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.align(Align.top);
        mainTable.add(scrollPane).expand().fill().pad(10);

        this.addActor(mainTable);
    }

    private void updateSkillsTable() {
        skillsTable.clear();
        int col = 0;
        int maxCols = 4;

        for (Map.Entry<String, Skill> entry : player.getSkills().entrySet()) {
            Skill skill = entry.getValue();

            // Load skill icon
            Texture texture = new Texture("Skill/" + skill.getName().toLowerCase() + ".png");
            Image skillImage = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
            skillImage.setScaling(Scaling.fit);
            skillImage.setSize(64, 64);
            skillImage.setAlign(Align.center);

            // Level and XP label
            Label infoLabel = new Label("Lv " + skill.getLevel() + " | XP " + skill.getXp(), skin);
            infoLabel.setAlignment(Align.center);

            // Stack for skill image and label
            Table container = new Table();
            container.add(skillImage).size(64, 64).row();
            container.add(infoLabel).padTop(4);

            // Tooltip with detailed info
//            String tooltipText = getSkillDescription(skill.getName());
//            Tooltip<Label> tooltip = new Tooltip<>(new Label(tooltipText, skin));
//            container.addListener(tooltip);
//
//            skillsTable.add(container).pad(8);
//            col++;
//            if (col % maxCols == 0) skillsTable.row();
        }
    }

    private String getSkillDescription(String skillName) {
        return switch (skillName.toLowerCase()) {
            case "farming" -> "🌾 Farming: Increase crop yield and efficiency.\nLevel 1: Basic farming.\nLevel 2: +10% speed.\nLevel 3: Less water needed.";
            case "mining" -> "⛏ Mining: Break rocks faster.\nLevel 1: Basic mining.\nLevel 2: +10% mining speed.\nLevel 3: Chance for rare ores.";
            case "foraging" -> "🍃 Foraging: Gather resources.\nLevel 1: Basic foraging.\nLevel 2: Find more items.\nLevel 3: Higher quality resources.";
            case "fishing" -> "🎣 Fishing: Catch fish efficiently.\nLevel 1: Basic fishing.\nLevel 2: +10% catch speed.\nLevel 3: Rare fish appear.";
            default -> "Skill: " + skillName + "\nLevel up to unlock bonuses!";
        };
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void refresh() {
        updateSkillsTable();
    }
}
