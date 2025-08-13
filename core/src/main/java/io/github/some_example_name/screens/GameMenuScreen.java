package io.github.some_example_name.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.some_example_name.Main;

public class GameMenuScreen extends ScreenAdapter {
    private final Main game;
    private Stage stage;
    private Skin skin;

    public GameMenuScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));
        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label title = new Label("منوی بازی", skin);
        title.setFontScale(2f);

        TextButton btnNewGame   = new TextButton("شروع بازی جدید", skin);
        TextButton btnFarmType  = new TextButton("انتخاب نوع مزرعه", skin);
        TextButton btnLoadGame  = new TextButton("لود بازی", skin);
        TextButton btnNextTurn  = new TextButton("تغییر نوبت", skin);
        TextButton btnShowError = new TextButton("نمایش خطاها", skin);
        TextButton btnExit      = new TextButton("خروج", skin);

        final Label lblError = new Label("", skin);
        lblError.setColor(Color.RED);

        // چینش (اعداد فاصله طبق خواسته تو)
        table.add(title).padBottom(40).row();
        table.add(btnNewGame).width(300).pad(5).row();    // 5
        table.add(btnFarmType).width(300).pad(10).row();  // 10
        table.add(btnLoadGame).width(300).pad(15).row();  // 15
        table.add(btnExit).width(300).pad(5).row();       // 5
        table.add(btnNextTurn).width(300).pad(10).row();  // 10
        table.add(btnShowError).width(300).pad(10).row(); // 10
        table.add(lblError).padTop(20);

        // لیسنرها
        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("شروع بازی جدید کلیک شد");
                // TODO: رفتن به صفحه بازی
            }
        });

        btnFarmType.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Dialog farmDialog = new Dialog("انتخاب مزرعه", skin);
                farmDialog.text("نوع مزرعه رو انتخاب کن:");
                farmDialog.button("مزرعه استاندارد");
                farmDialog.button("مزرعه رودخانه‌ای");
                farmDialog.button("مزرعه کوهستانی");
                farmDialog.show(stage);
            }
        });

        btnLoadGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("لود بازی کلیک شد");
                // TODO: لود فایل ذخیره
            }
        });

        btnNextTurn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("تغییر نوبت کلیک شد");
                // TODO: تغییر نوبت بازیکن
            }
        });

        btnShowError.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lblError.setText("⚠ خطای تستی: داده‌ها بارگذاری نشدند!");
            }
        });

        btnExit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.6f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
