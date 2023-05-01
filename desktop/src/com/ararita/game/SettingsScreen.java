package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class SettingsScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Slider.SliderStyle sliderStyle;
    Slider volumeSlider;
    Slider soundEffectsSlider;

    TextButton.TextButtonStyle textButtonStyle;
    TextButton backButton;

    public SettingsScreen(final Ararita game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        JsonReader jsonReader = new JsonReader();
        JsonValue jsonSettings = jsonReader.parse(Gdx.files.local("assets/settings.json"));

        this.skin = new Skin(Gdx.files.internal("Pixthulhu/pixthulhu-ui.json"));

        sliderStyle = skin.get("default-horizontal", Slider.SliderStyle.class);
        volumeSlider = new Slider(0, 100, 1, false, sliderStyle);
        soundEffectsSlider = new Slider(0, 100, 1, false, sliderStyle);
        volumeSlider.setWidth(300);
        soundEffectsSlider.setWidth(300);
        volumeSlider.setValue(jsonSettings.getInt("Volume"));
        soundEffectsSlider.setValue(jsonSettings.getInt("Sound Effects"));
        volumeSlider.setPosition(((Gdx.graphics.getWidth() - volumeSlider.getWidth()) / 2) - 100, Gdx.graphics.getHeight() - 300);
        soundEffectsSlider.setPosition(((Gdx.graphics.getWidth() - soundEffectsSlider.getWidth()) / 2) - 100, Gdx.graphics.getHeight() - 500);

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = game.bigFont;
        backButton = new TextButton("Back", textButtonStyle);
        backButton.setPosition((Gdx.graphics.getWidth() - backButton.getWidth()) / 2, Gdx.graphics.getHeight() - 900);

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    Global.writeJSON(Gdx.files.local("assets/settings.json").file().toPath(), new JSONObject(Map.of("Volume",
                            volumeSlider.getValue(), "Sound Effects", soundEffectsSlider.getValue())));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(volumeSlider);
        stage.addActor(soundEffectsSlider);
        stage.addActor(backButton);
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
        game.normalFont.draw(game.batch, "Sound Effects: " + soundEffectsSlider.getValue(), soundEffectsSlider.getX() + 325, soundEffectsSlider.getY() + 39);

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
