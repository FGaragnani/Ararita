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

    Label title;
    Label.LabelStyle titleStyle;

    Slider volumeSlider;
    Slider soundEffectsSlider;

    Label.LabelStyle labelStyle;
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

        volumeSlider = new Slider(0, 100, 1, false, game.sliderStyle);
        soundEffectsSlider = new Slider(0, 100, 1, false, game.sliderStyle);
        volumeSlider.setWidth(game.width300);
        soundEffectsSlider.setWidth(game.width300);
        volumeSlider.setValue(game.volume);
        soundEffectsSlider.setValue(game.soundEffects);
        volumeSlider.setPosition(((Gdx.graphics.getWidth() - volumeSlider.getWidth()) / 2) - (Gdx.graphics.getWidth() / 19.2f), Gdx.graphics.getHeight() * 0.722f);
        soundEffectsSlider.setPosition(((Gdx.graphics.getWidth() - soundEffectsSlider.getWidth()) / 2) - (Gdx.graphics.getWidth() / 19.2f), Gdx.graphics.getHeight() * 0.537f);

        /*
            Creating the SoundEffects Label.
         */

        labelStyle = skin.get("default", Label.LabelStyle.class);
        labelStyle.font = game.normalFont;
        soundEffectLabel = new Label("Sound Effects: " + (float) game.soundEffects, labelStyle);
        soundEffectLabel.setPosition(soundEffectsSlider.getX() + (Gdx.graphics.getWidth() / 5.9f), soundEffectsSlider.getY() + (Gdx.graphics.getHeight() / 72f));

        /*
            Creating the Volume label.
         */

        volumeLabel = new Label("Volume: " + (float) game.volume, labelStyle);
        volumeLabel.setPosition(volumeSlider.getX() + (Gdx.graphics.getWidth() / 5.9f), volumeSlider.getY() + (Gdx.graphics.getHeight() / 72f));

        /*
            Creating the two main buttons.
         */

        backButton = new TextButton("Back", game.textButtonStyle);
        backButton.setPosition((Gdx.graphics.getWidth() - backButton.getWidth()) / 2, Gdx.graphics.getHeight() * 0.12f);
        deleteButton = new TextButton("Erase Data", game.textButtonStyle);
        deleteButton.setPosition((Gdx.graphics.getWidth() - deleteButton.getWidth()) / 2, Gdx.graphics.getHeight() * 0.33f);

        /*
            Setting the title.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("SETTINGS", titleStyle);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() * 0.86f);
        title.setColor(Color.WHITE);

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
                 Do you want to delete all your save files?
                 These include classes, spells and characters!
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

        stage.addActor(soundEffectLabel);
        stage.addActor(volumeLabel);
        stage.addActor(volumeSlider);
        stage.addActor(soundEffectsSlider);
        stage.addActor(backButton);
        stage.addActor(deleteButton);
        stage.addActor(title);
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
