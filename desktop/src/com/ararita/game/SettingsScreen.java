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

public class SettingsScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Dialog confirmDeleteDialog;

    Slider volumeSlider;
    Slider soundEffectsSlider;

    Label soundEffectLabel;
    TextButton deleteButton;
    TextButton backButton;

    public SettingsScreen(final Ararita game) {
        /*
            First initializations.
         */
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        skin = game.skin;

        /*
            Creating the two sliders.
         */

        volumeSlider = new Slider(0, 100, 1, false, game.sliderStyle);
        soundEffectsSlider = new Slider(0, 100, 1, false, game.sliderStyle);
        volumeSlider.setWidth(300);
        soundEffectsSlider.setWidth(300);
        volumeSlider.setValue(game.volume);
        soundEffectsSlider.setValue(game.soundEffects);
        volumeSlider.setPosition(((Gdx.graphics.getWidth() - volumeSlider.getWidth()) / 2) - 100, Gdx.graphics.getHeight() - 300);
        soundEffectsSlider.setPosition(((Gdx.graphics.getWidth() - soundEffectsSlider.getWidth()) / 2) - 100, Gdx.graphics.getHeight() - 500);

        /*
            Creating the SoundEffects Label.
         */
        soundEffectLabel = new Label("Sound Effects: " + (float) game.soundEffects, skin);
        soundEffectLabel.setPosition(soundEffectsSlider.getX() + 325, soundEffectsSlider.getY() + 15);

        /*
            Creating the two main buttons.
         */

        backButton = new TextButton("Back", game.textButtonStyle);
        backButton.setPosition((Gdx.graphics.getWidth() - backButton.getWidth()) / 2, Gdx.graphics.getHeight() - 950);
        deleteButton = new TextButton("Erase Data", game.textButtonStyle);
        deleteButton.setPosition((Gdx.graphics.getWidth() - deleteButton.getWidth()) / 2, Gdx.graphics.getHeight() - 720);

        /*
            Creating the dialog.
            This will show up before erasing the saved data.
         */

        confirmDeleteDialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                if ((boolean) object) {
                    try {
                        Global.emptyCharacters();
                        Global.emptyInventory();
                        Global.emptySpell();
                        game.newPlayer = true;
                        game.settingsUpdate();
                    } catch (IOException e) {
                        throw new RuntimeException("Deleting files is impossible!");
                    }
                }
                this.setVisible(false);
            }
        };
        confirmDeleteDialog.setResizable(false);
        confirmDeleteDialog.text(" Do you want to delete all your save files?\n These include classes, spells and " + "characters!\n", game.labelStyle);
        confirmDeleteDialog.button("Yes", true, game.textButtonStyle).button("No", false, game.textButtonStyle);

        /*
            Adding the listeners to the actors.
         */

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.volume = (int) volumeSlider.getValue();
                game.soundEffects = (int) soundEffectsSlider.getValue();
                game.settingsUpdate();
                dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                confirmDeleteDialog.setVisible(true);
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

        game.titleFont.draw(game.batch, "SETTINGS", 730, Gdx.graphics.getHeight() - 50);
        game.normalFont.draw(game.batch, "Volume: " + volumeSlider.getValue(), volumeSlider.getX() + 325, volumeSlider.getY() + 39);

        game.batch.end();

        stage.draw();
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
        stage.dispose();
    }
}
