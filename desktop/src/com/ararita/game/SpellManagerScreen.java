package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class SpellManagerScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    Label deleteLabel;

    Label learnLabel;

    TextButton exitButton;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    public SpellManagerScreen(final Ararita game) {

        /*
            First initialization.
         */

        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundPaper));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Title initialization.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("SPELL MANAGER", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() - 150);

        /*
            Creating the Exit Button.
         */

        exitButton = new TextButton("Exit", game.textButtonStyle);
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() * 0.074f);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new SpellScreen(game));
            }
        });

        /*
            Creating the 'delete' label and select boxes.
         */

        deleteLabel = new Label("Delete known spells", game.labelStyle);
        deleteLabel.setColor(Color.BLACK);
        deleteLabel.setFontScale(game.descScaleX, game.descScaleY);
        deleteLabel.setPosition((Gdx.graphics.getWidth() - deleteLabel.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.75f);

        /*
            Creating the 'learn' label and select boxes.
         */

        learnLabel = new Label("Learn created spells", game.labelStyle);
        learnLabel.setColor(Color.BLACK);
        learnLabel.setFontScale(game.descScaleX, game.descScaleY);
        learnLabel.setPosition((Gdx.graphics.getWidth() - learnLabel.getWidth()) * 0.85f, Gdx.graphics.getHeight() * 0.75f);

        /*
            Adding all actors.
         */

        stage.addActor(title);
        stage.addActor(deleteLabel);
        stage.addActor(learnLabel);
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
        backgroundSprite.draw(game.batch);
        game.batch.end();

        stage.draw();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
        backgroundTexture.dispose();
    }
}
