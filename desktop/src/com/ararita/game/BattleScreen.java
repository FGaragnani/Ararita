package com.ararita.game;

import com.ararita.game.battlers.Enemy;
import com.ararita.game.battlers.PC;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.List;
import java.util.Random;

public class BattleScreen implements Screen {

    final Ararita game;
    final GlobalBattle battle;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Texture enemyTexture;
    Image enemyImage;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    Enemy enemy;
    List<PC> party;

    public BattleScreen(final Ararita game, final GlobalBattle battle) {

         /*
            First initialization.
         */

        this.game = game;
        this.battle = battle;
        battle.sortBattleOrder();
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        enemy = battle.getEnemy();
        party = battle.getCharacters();

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local("assets/Backgrounds/" + (new Random().nextInt(4) + 1) + ".png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize((int) (Gdx.graphics.getWidth() * 1.1), (int) (Gdx.graphics.getHeight() * 1.1));

        /*
            Setting the enemy image.
         */

        enemyTexture = new Texture(Gdx.files.local("Enemies/" + enemy.getName() + ".png"));
        enemyImage = new Image(new TextureRegionDrawable(enemyTexture));
        enemyImage.setScale(7);
        enemyImage.setPosition((Gdx.graphics.getWidth() - enemyImage.getWidth()) / 4 - 100,
                Gdx.graphics.getHeight() - 750);

        /*
            Adding all actors to the stage.
         */

        stage.addActor(enemyImage);
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
        enemyTexture.dispose();
    }
}
