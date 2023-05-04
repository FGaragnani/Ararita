package com.ararita.game;

import com.ararita.game.battlers.AbstractBattler;
import com.ararita.game.battlers.PC;
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

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class ClassCreationScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    Label stats;
    Label costLabel;

    TextButton confirmButton;
    TextButton exitButton;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    List<Integer> statsList = List.of(0, 0, 0, 0, 0, 0);
    Map<String, Integer> proficiencies;
    Set<String> spellTypes;
    double increaseEXP = 1.5;
    double exponentEXP = 1.5;

    public ClassCreationScreen(final Ararita game){
        /*
            First initialization.
         */

        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);
        proficiencies = new HashMap<>();
        spellTypes = new HashSet<>();

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
        title = new Label("CLASS CREATION", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() - 150);

        /*
            Setting the stats Label.
         */

        stats = new Label("", game.labelStyle);
        stats.setFontScale(2.8f, 3.8f);
        stats.setColor(Color.BLACK);
        stats.setPosition(300, Gdx.graphics.getHeight() - 400);
        updateStats();

        /*
            Creating the button for confirmation.
            Creating its Listener.
         */

        confirmButton = new TextButton("Confirm", game.textButtonStyle);
        confirmButton.setPosition((Gdx.graphics.getWidth() - (confirmButton.getWidth())) / 2, Gdx.graphics.getHeight() - 850);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO
            }
        });

        /*
            Creating the Exit Button.
         */

        exitButton = new TextButton("Exit", game.textButtonStyle);
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() - 1000);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new CityScreen(game));
            }
        });

        costLabel = new Label("", stats.getStyle());
        costLabel.setColor(Color.BLACK);
        costLabel.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, confirmButton.getY() + 155);
        updateCost();

        /*
            Adding all actors.
         */

        stage.addActor(title);
        stage.addActor(stats);
        stage.addActor(confirmButton);
        stage.addActor(exitButton);
        stage.addActor(costLabel);

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
    }

    /**
     * Determines how many attribute points are left to add.
     *
     * @return The attribute points still to set.
     */
    public int getRemainingPoints(){
        return Global.INITIAL_ATTRIBUTES_POINT - statsList.stream().flatMapToInt(IntStream::of).sum();
    }

    /**
     * The stats label is updated.
     */
    public void updateStats(){
        StringBuilder text = new StringBuilder();
        text.append("Attributes Points: ").append(getRemainingPoints()).append("\n\n");
        text.append(" Strength: ").append(statsList.get(0)).append("\n");
        text.append(" Intelligence: ").append(statsList.get(1)).append("\n");
        text.append(" Vigor: ").append(statsList.get(2)).append("\n");
        text.append(" Agility: ").append(statsList.get(3)).append("\n");
        text.append(" Spirit: ").append(statsList.get(4)).append("\n");
        text.append(" Arcane: ").append(statsList.get(5)).append("\n");
        stats.setText(text);
    }

    /**
     * The cost label is updated.
     */
    public void updateCost(){
        costLabel.setText("Class cost: " + getClassCost());
    }

    /**
     * Creates a character from the screen data and calculates its cost.
     *
     * @return The class' cost.
     */
    public int getClassCost(){
        try {
            PC toBuy = new PC(statsList.get(0), statsList.get(1), statsList.get(2), statsList.get(3), statsList.get(4),
                    statsList.get(5), "", "", 0, 0, 0, 0, game.baseEXP, increaseEXP, exponentEXP, proficiencies,
                    spellTypes, new ArrayList<>(), new ArrayList<>());
            return toBuy.classCost();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
