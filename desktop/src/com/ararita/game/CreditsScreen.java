package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class CreditsScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label creditsLabel;
    TextButton backButton;

    public CreditsScreen(final Ararita game){
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
            Creating the Credits label.
         */

        creditsLabel = game.createLabel("Code used:\n" + "\tLibGDX\n" + "\tJSON utility library: org.JSON\n" + "Font " +
                        "used:\n" + "\tSol Schori by Noam Goldfarb\n" + "Assets by:\n" + "\tPixthulhu LibGDX's Skins " +
                        "by Raymond \"Raeleus\" Buckley\n" + "\t16-bit character sprites by Antifarea, comissioned by" +
                        " OpenGameArt\n" + "\tIcons by Dycha\n" + "\tEnemy sprites by Stephen \"Redshrike\" " +
                        "Challener, hosted by OpenGameArt",
                Gdx.graphics.getWidth() * 0.25f, Gdx.graphics.getHeight() * 0.42f, game.descScaleX, game.descScaleY,
                Color.WHITE, stage);

        /*
            Creating the Back button.
         */

        backButton = game.createMainButtonXCentered("Back", Gdx.graphics.getHeight() * 0.1f, stage);

        /*
            Creating the title.
         */

        game.createTitleCentered("CREDITS", Gdx.graphics.getHeight() * 0.86f, Color.WHITE, stage);

        /*
            Adding the listener.
         */

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new SettingsScreen(game));
            }
        });

    }
    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

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
