package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;

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
    Label volumeLabel;
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
        skin = new Skin(Gdx.files.internal(game.stylesPath));

        /*
            Creating the two sliders.
         */

        volumeSlider = game.createSlider(0, 100, 1, false, game.width300, game.volume, Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.722f, stage);
        soundEffectsSlider = game.createSlider(0, 100, 1, false, game.width300, game.soundEffects, Gdx.graphics.getWidth() * 0.37f, Gdx.graphics.getHeight() * 0.537f, stage);

        /*
            Creating the SoundEffects and Volume labels.
         */

        soundEffectLabel = game.createLabel("Sound Effects: " + (float) game.soundEffects, soundEffectsSlider.getX() + (Gdx.graphics.getWidth() / 5.9f), soundEffectsSlider.getY() + (Gdx.graphics.getHeight() / 72f), stage);
        volumeLabel = game.createLabel("Volume: " + (float) game.volume, volumeSlider.getX() + (Gdx.graphics.getWidth() / 5.9f), volumeSlider.getY() + (Gdx.graphics.getHeight() / 72f), stage);

        /*
            Creating the two main buttons.
         */

        backButton = game.createMainButtonXCentered("Back",Gdx.graphics.getHeight() * 0.12f, stage);
        deleteButton = game.createMainButtonXCentered("Erase Data",Gdx.graphics.getHeight() * 0.33f, stage);

        /*
            Setting the title.
         */

        game.createTitleCentered("SETTINGS", Gdx.graphics.getHeight() * 0.86f, Color.WHITE, stage);

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
                        Global.setMoney(0);
                        Global.emptyClass();
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
        confirmDeleteDialog.text("""
                 Do you want to delete all your save files?\s
                 These include classes, spells and characters!\s
                """, game.labelStyle);
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
                game.soundEffects = (int) soundEffectsSlider.getValue();
            }
        });

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                volumeLabel.setText("Volume: " + volumeSlider.getValue());
                game.volume = (int) volumeSlider.getValue();
                game.updateVolume();
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

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
