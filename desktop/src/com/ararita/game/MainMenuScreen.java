package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;

public class MainMenuScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

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
        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal("Pixthulhu/pixthulhu-ui.json"));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        this.textButtonStyle = game.textButtonStyle;
        mainButton = new TextButton("Play", textButtonStyle);
        settingsButton = new TextButton("Settings", textButtonStyle);
        exitButton = new TextButton("Exit", textButtonStyle);
        mainButton.setPosition((Gdx.graphics.getWidth() - mainButton.getWidth()) / 2, Gdx.graphics.getHeight() - 400);
        settingsButton.setPosition((Gdx.graphics.getWidth() - settingsButton.getWidth()) / 2, Gdx.graphics.getHeight() - 600);
        exitButton.setPosition((Gdx.graphics.getWidth() - mainButton.getWidth()) / 2, Gdx.graphics.getHeight() - 800);

        mainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(game.newPlayer){
                    try {
                        game.setScreen(new CharacterCreationScreen(game, true));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
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
        game.titleFont.draw(game.batch, "ARARITA", 730, Gdx.graphics.getHeight() - 50);

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
        stage.dispose();
        skin.dispose();
    }
}
