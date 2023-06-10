package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    TextButton mainButton;
    TextButton settingsButton;
    TextButton exitButton;

    /**
     * The MainMenuScreen is created.
     *
     * @param game The game for the Main Menu.
     */
    public MainMenuScreen(final Ararita game) {

        /*
            First initializations.
         */

        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        /*
            Creating the three main buttons.
         */

        mainButton = game.createMainButtonXCentered("Play", Gdx.graphics.getHeight() * 0.63f, stage);
        settingsButton = game.createMainButtonXCentered("Settings", Gdx.graphics.getHeight() * 0.44f, stage);
        exitButton = game.createMainButtonXCentered("Exit", Gdx.graphics.getHeight() * 0.26f, stage);

        /*
            Adding the title.
         */

        game.createTitleCentered("ARARITA", Gdx.graphics.getHeight() * 0.86f, Color.WHITE, stage);

        /*
            Adding the buttons' listeners.
         */

        mainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.newPlayer) {
                    game.setScreen(new TutorialScreen(game));
                } else {
                    game.stopAudio();
                    game.setScreen(new CityScreen(game));
                }
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.dispose();
                Gdx.app.exit();
            }
        });

        game.playAudio(game.mainTheme);
        game.updateVolume();
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
        skin.dispose();
    }
}
