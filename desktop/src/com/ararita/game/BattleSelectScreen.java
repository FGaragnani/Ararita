package com.ararita.game;

import com.ararita.game.battlers.Enemy;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

public class BattleSelectScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    TextButton confirmButton;
    TextButton exitButton;

    SelectBox<String> enemySelectBox;
    Array<String> enemies;
    Label statsLabel;

    Texture enemyTexture;
    Image enemyImage;

    Label title;
    Label.LabelStyle titleStyle;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    public BattleSelectScreen(final Ararita game) {

        /*
            First initialization.
         */

        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        enemies = new Array<>();
        for (File f : Objects.requireNonNull(Global.enemySets.toFile().listFiles())) {
            enemies.add(f.getName().substring(0, f.getName().length() - 5));
        }


        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local("assets/Backgrounds/paperbg.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize((int) (Gdx.graphics.getWidth() * 1.1), (int) (Gdx.graphics.getHeight() * 1.1));

        /*
            Setting the title.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("ENEMY SELECTION", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() - 150);

        /*
            Creating the statistics label.
         */

        statsLabel = new Label("", skin.get("default", Label.LabelStyle.class));
        statsLabel.setFontScale(2.8f, 3.8f);
        statsLabel.setColor(Color.BLACK);
        statsLabel.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 6, Gdx.graphics.getHeight() - 500);

        /*
            Creating the two main buttons.
         */

        confirmButton = new TextButton("Battle!", game.textButtonStyle);
        confirmButton.setPosition((Gdx.graphics.getWidth() - (confirmButton.getWidth())) / 2, Gdx.graphics.getHeight() - 850);
        exitButton = new TextButton("Exit", game.textButtonStyle);
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() - 1000);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    game.audio.stop();
                    dispose();
                    GlobalBattle battle = new GlobalBattle(Global.getEnemy(enemySelectBox.getSelected()));
                    game.setScreen(new BattleScreen(game, battle));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new CityScreen(game));
            }
        });

        /*
            Creating the Select Box.
         */

        enemySelectBox = new SelectBox<>(game.selectBoxStyle);
        enemySelectBox.setItems(enemies);
        enemySelectBox.setWidth(400);
        enemySelectBox.setPosition((Gdx.graphics.getWidth() - enemySelectBox.getWidth()) / 2, Gdx.graphics.getHeight() - 500);
        enemySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateStats();
                updateTexture();
            }
        });

        /*
            Setting the enemy texture and image.
         */

        enemyTexture = new Texture(Gdx.files.local("Enemies/" + enemySelectBox.getSelected() + ".png"));
        enemyImage = new Image(new TextureRegionDrawable(enemyTexture));
        enemyImage.setScale(10);
        enemyImage.setPosition((Gdx.graphics.getWidth() - enemyImage.getWidth()) * 3 / 4 - 20, Gdx.graphics.getHeight() - 650);

        /*
            Adding all actors.
         */

        stage.addActor(title);
        stage.addActor(confirmButton);
        stage.addActor(exitButton);
        stage.addActor(enemySelectBox);
        stage.addActor(statsLabel);
        stage.addActor(enemyImage);

        /*
            Initializing values.
         */

        updateStats();
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

    /**
     * The enemy stats label is updated.
     */
    public void updateStats() {
        StringBuilder text = new StringBuilder();
        Enemy enemy;
        try {
            enemy = new Enemy(enemySelectBox.getSelected());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        text.append("Level: ").append(enemy.getLevel()).append("\n");
        text.append("Weak to: \n");
        enemy.getWeakTo().forEach((string) -> text.append(" - ").append(string).append("\n"));
        text.append("\nMay drop: \n");
        enemy.getToDrop().entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).forEach((entry) -> text.append(" - ").append(entry.getKey().getName()).append("\n"));
        statsLabel.setText(text);
    }

    /**
     * The enemy image is updated.
     */
    public void updateTexture(){
        enemyTexture = new Texture(Gdx.files.local("Enemies/" + enemySelectBox.getSelected() + ".png"));
        enemyImage.setDrawable(new TextureRegionDrawable(enemyTexture));
    }

}
