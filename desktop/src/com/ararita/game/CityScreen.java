package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
    TextButton classCreateButton;
    TextButton spellCreateButton;
    TextButton shopButton;
    TextButton partyManageButton;
    TextButton mainMenuButton;
    TextButton battleButton;

    Texture backgroundTexture;
    Sprite backgroundSprite;

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
            Audio initialization.
         */

        game.playAudio(game.cityTheme);

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundCity));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Initialize the Character Creation Button and its listener.
         */

        charCreateButton = new TextButton(" Recruit new \n character ", textButtonStyle);
        charCreateButton.setPosition((Gdx.graphics.getWidth() - charCreateButton.getWidth()) / 4, Gdx.graphics.getHeight() * 0.676f);
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
            Initialize the Class Creation Button and its listener.
         */

        classCreateButton = new TextButton(" Create new \n class ", textButtonStyle);
        classCreateButton.setPosition((Gdx.graphics.getWidth() - classCreateButton.getWidth()) / 4, Gdx.graphics.getHeight() * 0.463f);
        classCreateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new ClassCreationScreen(game));
            }
        });

        /*
            Initialize the Spell Creation Button and its listener.
         */

        spellCreateButton = new TextButton(" Create new \n spell ", textButtonStyle);
        spellCreateButton.setPosition((Gdx.graphics.getWidth() - classCreateButton.getWidth()) / 4, Gdx.graphics.getHeight() * 0.25f);
        spellCreateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new SpellCreationScreen(game));
            }
        });

        /*
            Initialize the Shop Button and its listener.
         */

        shopButton = new TextButton(" Enter the \n shop ", textButtonStyle);
        shopButton.setPosition((Gdx.graphics.getWidth() - shopButton.getWidth()) * 3 / 4, Gdx.graphics.getHeight() * 0.676f);
        shopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new ShopScreen(game));
            }
        });

        /*
            Initialize the Party Manage Button and its listener.
         */

        partyManageButton = new TextButton(" Manage \n the party ", textButtonStyle);
        partyManageButton.setPosition((Gdx.graphics.getWidth() - partyManageButton.getWidth()) * 3 / 4, Gdx.graphics.getHeight() * 0.463f);
        partyManageButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new PartyManagerScreen(game));
            }
        });

        /*
            Initialize the Main Menu button and its listener.
         */

        mainMenuButton = new TextButton(" Back to \n Main Menu ", textButtonStyle);
        mainMenuButton.setPosition((Gdx.graphics.getWidth() - mainMenuButton.getWidth()) * 3 / 4, Gdx.graphics.getHeight() / 4f);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.stopAudio();
                dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        /*
            Creating the Battle Button.
         */

        battleButton = new TextButton("Battle!", game.textButtonStyle);
        battleButton.setPosition((Gdx.graphics.getWidth() - battleButton.getWidth()) / 2 + (Gdx.graphics.getWidth() / 192f), (Gdx.graphics.getHeight() - battleButton.getHeight()) / 2 + (Gdx.graphics.getHeight() / 108f));
        battleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new BattleSelectScreen(game));
            }
        });

        /*
            Add all actors to the stage.
         */

        stage.addActor(charCreateButton);
        stage.addActor(classCreateButton);
        stage.addActor(spellCreateButton);
        stage.addActor(shopButton);
        stage.addActor(partyManageButton);
        stage.addActor(mainMenuButton);
        stage.addActor(battleButton);
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
        backgroundTexture.dispose();
        skin.dispose();
    }
}
