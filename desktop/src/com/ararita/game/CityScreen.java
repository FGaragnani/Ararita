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

        charCreateButton = game.createNormalButton(" Recruit new \n character ", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) / 4f, Gdx.graphics.getHeight() * 0.676f, stage);
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

        classCreateButton = game.createNormalButton(" Create new \n class ", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) / 4, Gdx.graphics.getHeight() * 0.463f, stage);
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

        spellCreateButton = game.createNormalButton("   Organize   \n spells ", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) / 4, Gdx.graphics.getHeight() * 0.25f, stage);
        spellCreateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new SpellScreen(game));
            }
        });

        /*
            Initialize the Shop Button and its listener.
         */

        shopButton = game.createNormalButton(" Enter the \n shop ", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) * 3 / 4, Gdx.graphics.getHeight() * 0.676f, stage);
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

        partyManageButton = game.createNormalButton(" Manage \n the party ", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) * 3 / 4, Gdx.graphics.getHeight() * 0.463f, stage);
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

        mainMenuButton = game.createNormalButton(" Back to \n Main Menu ", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) * 3 / 4, Gdx.graphics.getHeight() / 4f, stage);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.stopAudio();
                game.playAudio(game.mainTheme);
                dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        /*
            Creating the Battle Button.
         */

        battleButton = game.createMainButtonXCentered("Battle!", Gdx.graphics.getHeight() * 0.45f, stage);
        battleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new BattleSelectScreen(game));
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
