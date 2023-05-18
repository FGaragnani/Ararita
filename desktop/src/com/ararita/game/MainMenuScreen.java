package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    TextButton.TextButtonStyle textButtonStyle;
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

        this.textButtonStyle = game.textButtonStyle;
        mainButton = new TextButton("Play", textButtonStyle);
        settingsButton = new TextButton("Settings", textButtonStyle);
        exitButton = new TextButton("Exit", textButtonStyle);
        mainButton.setPosition((Gdx.graphics.getWidth() - mainButton.getWidth()) / 2, Gdx.graphics.getHeight() * 0.63f);
        settingsButton.setPosition((Gdx.graphics.getWidth() - settingsButton.getWidth()) / 2, Gdx.graphics.getHeight() * 0.44f);
        exitButton.setPosition((Gdx.graphics.getWidth() - mainButton.getWidth()) / 2, Gdx.graphics.getHeight() * 0.26f);

        /*
            Adding the title.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("ARARITA", titleStyle);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() * 0.86f);
        title.setColor(Color.WHITE);

        /*
            Adding the buttons' listeners.
         */

        mainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (game.newPlayer) {
                    game.setScreen(new TutorialScreen(game));
                } else {
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
                Gdx.app.exit();
            }
        });

        stage.addActor(mainButton);
        stage.addActor(settingsButton);
        stage.addActor(exitButton);
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
        skin.dispose();
    }
}
