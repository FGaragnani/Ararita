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

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;

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
        enemies.addAll("Ararita", "Eye", "Goblin", "Lizard-Man", "Skeleton", "Slime", "Spider", "Turtle", "Wasp", "Wyvern");
        enemies.sort((o1, o2) -> {
            try {
                return Integer.compare(Global.getEnemy(o1).getLevel(), Global.getEnemy(o2).getLevel());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundPaper));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Setting the title.
         */

        game.createTitleCentered("ENEMY SELECTION", Gdx.graphics.getHeight() * 0.86f, Color.BLACK, stage);

        /*
            Creating the statistics label.
         */

        statsLabel = game.createStatLabel("", Color.BLACK, Gdx.graphics.getWidth() / 6f, Gdx.graphics.getHeight() * 0.537f, stage);

        /*
            Creating the two main buttons.
         */

        confirmButton = game.createMainButtonXCentered("Battle!", Gdx.graphics.getHeight() * 0.213f, stage);
        exitButton = game.createMainButtonXCentered("Exit", Gdx.graphics.getHeight() * 0.074f, stage);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    game.stopAudio();
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

        enemySelectBox = game.createSelectBox(game.width400, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) / 2, Gdx.graphics.getHeight() * 0.537f, stage);
        enemySelectBox.setItems(enemies);
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

        enemyTexture = new Texture(Gdx.files.local(game.enemyPath + enemySelectBox.getSelected() + ".png"));
        enemyImage = new Image(new TextureRegionDrawable(enemyTexture));
        enemyImage.setScale((Gdx.graphics.getWidth() / 192f));
        enemyImage.setPosition((Gdx.graphics.getWidth() - enemyImage.getWidth()) * 3 / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.398f);

        /*
            Adding all actors.
         */

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
    public void updateTexture() {
        enemyTexture = new Texture(Gdx.files.local(game.enemyPath + enemySelectBox.getSelected() + ".png"));
        stage.getActors().removeValue(enemyImage, true);
        enemyImage = new Image(new TextureRegionDrawable(enemyTexture));
        enemyImage.setScale(10);
        enemyImage.setPosition((Gdx.graphics.getWidth() - enemyImage.getWidth()) * 3 / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.398f);
        stage.addActor(enemyImage);
    }
}
