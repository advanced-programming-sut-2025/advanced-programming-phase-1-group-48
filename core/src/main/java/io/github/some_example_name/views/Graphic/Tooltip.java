package io.github.some_example_name.views.Graphic;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Tooltip extends Container<Label> {
    public Tooltip(Skin skin) {
        super(new Label("", skin));
        this.setVisible(false);
        this.setBackground(skin.getDrawable("window"));
    }

    public void show(String text, float x, float y) {
        this.getActor().setText(text);
        this.setPosition(x, y);
        this.setVisible(true);
    }

    public void hide() {
        this.setVisible(false);
    }
}

