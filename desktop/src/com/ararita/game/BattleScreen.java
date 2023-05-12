package com.ararita.game;

import com.ararita.game.battlers.Enemy;
import com.ararita.game.battlers.PC;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

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

    Texture charSheet;
    TextureRegion[][] tmp;
    Image firstCharImage;
    Image secondCharImage;
    Image thirdCharImage;
    Image fourthCharImage;

    Pixmap labelColor;
    TypingLabel labelMain;

    ProgressBar firstBar;
    ProgressBar secondBar;
    ProgressBar thirdBar;
    ProgressBar fourthBar;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    Enemy enemy;
    List<PC> party;
    int currentBattler;

    public BattleScreen(final Ararita game, final GlobalBattle battle) {

         /*
            First initialization.
         */

        this.game = game;
        this.battle = battle;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        enemy = battle.getEnemy();
        party = battle.getCharacters();
        currentBattler = 0;

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
        enemyImage.setPosition((Gdx.graphics.getWidth() - enemyImage.getWidth()) / 4 - 100, Gdx.graphics.getHeight() - 750);

        /*
            Setting the characters' images.
         */

        charSheet = new Texture(Gdx.files.internal("General/msprites.png"));
        tmp = TextureRegion.split(charSheet, charSheet.getWidth() / (game.spriteFrameCols * 6), charSheet.getHeight());
        firstCharImage = new Image();
        secondCharImage = new Image();
        thirdCharImage = new Image();
        fourthCharImage = new Image();
        firstCharImage.setScale(8.5f);
        secondCharImage.setScale(8.5f);
        thirdCharImage.setScale(8.5f);
        fourthCharImage.setScale(8.5f);

        updateImages();

        firstCharImage.setPosition((Gdx.graphics.getWidth() - firstCharImage.getWidth()) * 3 / 4 - 100, Gdx.graphics.getHeight() - 600);
        secondCharImage.setPosition((Gdx.graphics.getWidth() - secondCharImage.getWidth()) * 3 / 4, Gdx.graphics.getHeight() - 700);
        thirdCharImage.setPosition((Gdx.graphics.getWidth() - thirdCharImage.getWidth()) * 3 / 4 + 100, Gdx.graphics.getHeight() - 800);
        fourthCharImage.setPosition((Gdx.graphics.getWidth() - fourthCharImage.getWidth()) * 3 / 4 + 200, Gdx.graphics.getHeight() - 900);

        /*
            Setting the main label.
         */

        labelMain = new TypingLabel("", game.skin);
        labelColor = new Pixmap(Gdx.graphics.getWidth() / 2, 200, Pixmap.Format.RGB888);
        labelColor.setColor(Color.BLACK);
        labelColor.fill();
        labelMain.getStyle().background = new Image(new Texture(labelColor)).getDrawable();
        labelMain.setPosition(Gdx.graphics.getWidth() / 2.0f, Gdx.graphics.getHeight() - 100);

        /*
            Adding all actors to the stage.
         */

        stage.addActor(enemyImage);
        stage.addActor(firstCharImage);
        stage.addActor(secondCharImage);
        stage.addActor(thirdCharImage);
        stage.addActor(fourthCharImage);
        stage.addActor(labelMain);

        /*
            Setting everything else.
         */

        battle.sortBattleOrder();
        setProgressBars();
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
        charSheet.dispose();
        labelColor.dispose();
    }

    /**
     * The character images are set.
     */
    public void updateImages() {
        firstCharImage.setDrawable(new TextureRegionDrawable(tmp[0][game.spriteNames.indexOf(party.get(0).getImage()) * 3]));
        firstCharImage.setSize(tmp[0][0].getRegionWidth(), tmp[0][0].getRegionHeight());
        if (party.size() >= 2) {
            secondCharImage.setDrawable(new TextureRegionDrawable(tmp[0][game.spriteNames.indexOf(party.get(1).getImage()) * 3]));
            secondCharImage.setSize(tmp[0][0].getRegionWidth(), tmp[0][0].getRegionHeight());
        }
        if (party.size() >= 3) {
            thirdCharImage.setDrawable(new TextureRegionDrawable(tmp[0][game.spriteNames.indexOf(party.get(2).getImage()) * 3]));
            thirdCharImage.setSize(tmp[0][0].getRegionWidth(), tmp[0][0].getRegionHeight());
        }
        if (party.size() >= 4) {
            fourthCharImage.setDrawable(new TextureRegionDrawable(tmp[0][game.spriteNames.indexOf(party.get(3).getImage()) * 3]));
            fourthCharImage.setSize(tmp[0][0].getRegionWidth(), tmp[0][0].getRegionHeight());
        }
    }

    /**
     * The health progress bars are set.
     */
    public void setProgressBars() {
        ProgressBar.ProgressBarStyle progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        progressBarStyle.background.setMinHeight(20);
        firstBar = new ProgressBar(0, party.get(0).maxHP(), 1, false, progressBarStyle);
        firstBar.setWidth(100);
        firstBar.setPosition(firstCharImage.getX() + 20, firstCharImage.getY() + firstCharImage.getHeight() + 135);
        stage.addActor(firstBar);
        if (party.size() >= 2) {
            secondBar = new ProgressBar(0, party.get(1).maxHP(), 1, false, progressBarStyle);
            secondBar.setWidth(100);
            secondBar.setPosition(secondCharImage.getX() + 20, secondCharImage.getY() + secondCharImage.getHeight() + 135);
            stage.addActor(secondBar);
        }
        if (party.size() >= 3) {
            thirdBar = new ProgressBar(0, party.get(2).maxHP(), 1, false, progressBarStyle);
            thirdBar.setWidth(100);
            thirdBar.setPosition(thirdCharImage.getX() + 20, thirdCharImage.getY() + thirdCharImage.getHeight() + 135);
            stage.addActor(thirdBar);
        }
        if (party.size() >= 4) {
            fourthBar = new ProgressBar(0, party.get(3).maxHP(), 1, false, progressBarStyle);
            fourthBar.setWidth(100);
            fourthBar.setPosition(fourthCharImage.getX() + 20, fourthCharImage.getY() + fourthCharImage.getHeight() + 135);
            stage.addActor(fourthBar);
        }
    }
}
