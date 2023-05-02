package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class SettingsScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Dialog confirmDeleteDialog;

    Slider.SliderStyle sliderStyle;
    Slider volumeSlider;
    Slider soundEffectsSlider;

    Label soundEffectLabel;
    Label.LabelStyle labelStyle;
    TextButton.TextButtonStyle textButtonStyle;
    TextButton deleteButton;
    TextButton backButton;

    public SettingsScreen(final Ararita game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        this.skin = new Skin(Gdx.files.internal("Pixthulhu/pixthulhu-ui.json"));

        sliderStyle = skin.get("default-horizontal", Slider.SliderStyle.class);
        volumeSlider = new Slider(0, 100, 1, false, sliderStyle);
        soundEffectsSlider = new Slider(0, 100, 1, false, sliderStyle);
        volumeSlider.setWidth(300);
        soundEffectsSlider.setWidth(300);
        volumeSlider.setValue(game.volume);
        soundEffectsSlider.setValue(game.soundEffects);
        volumeSlider.setPosition(((Gdx.graphics.getWidth() - volumeSlider.getWidth()) / 2) - 100, Gdx.graphics.getHeight() - 300);
        soundEffectsSlider.setPosition(((Gdx.graphics.getWidth() - soundEffectsSlider.getWidth()) / 2) - 100, Gdx.graphics.getHeight() - 500);

        labelStyle = skin.get("default", Label.LabelStyle.class);
        labelStyle.font = game.normalFont;
        soundEffectLabel = new Label("Sound Effects: " + game.soundEffects, skin);
        soundEffectLabel.setPosition(soundEffectsSlider.getX() + 325, soundEffectsSlider.getY() + 15);

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = game.bigFont;
        backButton = new TextButton("Back", textButtonStyle);
        backButton.setPosition((Gdx.graphics.getWidth() - backButton.getWidth()) / 2, Gdx.graphics.getHeight() - 950);
        deleteButton = new TextButton("Erase Data", textButtonStyle);
        deleteButton.setPosition((Gdx.graphics.getWidth() - deleteButton.getWidth()) / 2, Gdx.graphics.getHeight() - 720);

        confirmDeleteDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                if (confirm.equals("true")) {
                    try {
                        Global.emptyCharacters();
                        Global.emptyInventory();
                        Global.emptySpell();
                        JSONObject jsonSettings = Global.getJSON(Gdx.files.local("assets/settings.json").file().toPath());
                        jsonSettings.put("New", true);
                        Global.writeJSON(Gdx.files.local("assets/settings.json").file().toPath(), jsonSettings);
                    } catch (IOException e) {
                        throw new RuntimeException("Deleting files is impossible!");
                    }
                }
                confirmDeleteDialog.setVisible(false);
            }
        };
        confirmDeleteDialog.setResizable(false);
        confirmDeleteDialog.text("Do you want to delete all your save files? These include classes, spells and characters!", labelStyle);
        confirmDeleteDialog.button("Yes", true, textButtonStyle);
        confirmDeleteDialog.button("No", false, textButtonStyle);
        confirmDeleteDialog.setPosition(0, 0);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    Global.writeJSON(Gdx.files.local("assets/settings.json").file().toPath(), new JSONObject(Map.of("Volume", volumeSlider.getValue(), "Sound Effects", soundEffectsSlider.getValue())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                game.volume = (int) volumeSlider.getValue();
                game.soundEffects = (int) soundEffectsSlider.getValue();
                dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                confirmDeleteDialog.show(stage);
            }
        });

        soundEffectsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                soundEffectLabel.setText("Sound Effects: " + soundEffectsSlider.getValue());
            }
        });

        stage.addActor(soundEffectLabel);
        stage.addActor(volumeSlider);
        stage.addActor(soundEffectsSlider);
        stage.addActor(backButton);
        stage.addActor(deleteButton);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        stage.draw();
        game.titleFont.draw(game.batch, "SETTINGS", 730, Gdx.graphics.getHeight() - 50);
        game.normalFont.draw(game.batch, "Volume: " + volumeSlider.getValue(), volumeSlider.getX() + 325, volumeSlider.getY() + 39);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
