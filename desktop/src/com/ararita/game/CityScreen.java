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

public class CityScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    TextButton.TextButtonStyle textButtonStyle;
    TextButton charCreateButton;
    TextButton mainMenuButton;

    public CityScreen(final Ararita game) {

        /*
            First initialization.
         */

        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = game.normalFont;

        /*
            Initialize Character Creation Button and its listener.
         */

        charCreateButton = new TextButton(" Recruit new \n character ", textButtonStyle);
        charCreateButton.setPosition((Gdx.graphics.getWidth() - charCreateButton.getWidth()) / 3, Gdx.graphics.getHeight() - 200);
        charCreateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    dispose();
                    game.setScreen(new CharacterCreationScreen(game, false));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /*
            Initialize the Main Menu button.
         */

        mainMenuButton = new TextButton(" Main \n Menu ", textButtonStyle);
        mainMenuButton.setPosition((Gdx.graphics.getWidth() - charCreateButton.getWidth()) * 2 / 3, Gdx.graphics.getHeight() - 800);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        /*
            Add all actors to the stage.
         */

        stage.addActor(charCreateButton);
        stage.addActor(mainMenuButton);
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

        //game.titleFont.draw(game.batch, "ARARITA", 730, Gdx.graphics.getHeight() - 50);

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
