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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.util.*;
import java.util.List;
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
    Label expGrowthLabel;

    TextField classNameField;
    Slider expSlider;

    TextButton confirmButton;
    TextButton exitButton;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    List<Integer> statsList = List.of(0, 0, 0, 0, 0, 0);
    Map<String, Integer> proficiencies;
    Set<String> spellTypes;
    double increaseEXP = 1.5;
    double exponentEXP = 1.5;

    public ClassCreationScreen(final Ararita game) {
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
            Creating the Class Name Field.
         */

        classNameField = new TextField("Class Name", game.textFieldStyle);
        classNameField.setWidth(400);
        classNameField.setPosition((Gdx.graphics.getWidth() - classNameField.getWidth()) / 2, Gdx.graphics.getHeight() - 320);

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

        /*
            Creating the cost label.
         */

        costLabel = new Label("", stats.getStyle());
        costLabel.setFontScale(2.8f, 3.8f);
        costLabel.setColor(Color.BLACK);
        costLabel.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, confirmButton.getY() + 155);
        updateCost();

        /*
            Creating the EXP Slider.
         */

        expSlider = new Slider(1, 3, 0.01f, false, game.sliderStyle);
        expSlider.setWidth(300);
        expSlider.setValue(3);
        expSlider.setPosition((Gdx.graphics.getWidth() - expSlider.getWidth()) / 2, Gdx.graphics.getHeight() - 450);
        expSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateEXP();
                updateCost();
            }
        });

        /*
            Creating the EXP label.
         */

        expGrowthLabel = new Label("", stats.getStyle());
        expGrowthLabel.setFontScale(2.8f, 3.8f);
        expGrowthLabel.setColor(Color.BLACK);
        expGrowthLabel.setPosition((Gdx.graphics.getWidth() - (expGrowthLabel.getWidth())) / 2, Gdx.graphics.getHeight() - 500);
        updateEXP();

        /*
            Adding all actors.
         */

        stage.addActor(title);
        stage.addActor(stats);
        stage.addActor(confirmButton);
        stage.addActor(exitButton);
        stage.addActor(costLabel);
        stage.addActor(classNameField);
        stage.addActor(expSlider);
        stage.addActor(expGrowthLabel);
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
        skin.dispose();
        backgroundTexture.dispose();
    }

    /**
     * Determines how many attribute points are left to add.
     *
     * @return The attribute points still to set.
     */
    public int getRemainingPoints() {
        return Global.INITIAL_ATTRIBUTES_POINT - statsList.stream().flatMapToInt(IntStream::of).sum();
    }

    /**
     * The stats label is updated.
     */
    public void updateStats() {
        StringBuilder text = new StringBuilder();
        text.append("Attributes Points: ").append(getRemainingPoints()).append("\n\n");
        text.append(" Strength: ").append(statsList.get(0)).append("\n");
        text.append(" Intelligence: ").append(statsList.get(1)).append("\n");
        text.append(" Vigor: ").append(statsList.get(2)).append("\n");
        text.append(" Agility: ").append(statsList.get(3)).append("\n");
        text.append(" Spirit: ").append(statsList.get(4)).append("\n");
        text.append(" Arcane: ").append(statsList.get(5)).append("\n");
        if (!proficiencies.isEmpty()) {
            text.append("Proficiencies:\n");
            proficiencies.forEach((s, o) -> {
                text.append(" ").append(s).append(":");
                if (o >= 0) {
                    text.append(" +".repeat(o));
                } else {
                    text.append(" -".repeat(o));
                }
                text.append("\n");
            });
        }
        if (!spellTypes.isEmpty()) {
            text.append("Learnable spell types:\n");
            spellTypes.forEach((str) -> text.append(" - ").append(str).append("\n"));
            text.append("\n");
        }
        stats.setText(text);
        stats.setPosition(300, Gdx.graphics.getHeight() - 400);
    }

    /**
     * The cost label is updated.
     */
    public void updateCost() {
        costLabel.setText("Class cost: " + getClassCost());
    }

    /**
     * Creates a character from the screen data and calculates its cost.
     *
     * @return The class' cost.
     */
    public int getClassCost() {
        try {
            PC toBuy = new PC(statsList.get(0), statsList.get(1), statsList.get(2), statsList.get(3), statsList.get(4), statsList.get(5), "", "", 0, 0, 0, 0, game.baseEXP, increaseEXP, exponentEXP, proficiencies, spellTypes, new ArrayList<>(), new ArrayList<>());
            return toBuy.classCost();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateEXP(){
        float expSum = expSlider.getValue();
        if(expSum == 3){
            increaseEXP = 1.5f;
            exponentEXP = 1.5f;
        } else {
            increaseEXP = game.getRandom(expSum / 2, 1.5f);
            exponentEXP = 1.5f - increaseEXP;
        }
        if(expSum >= 2.333){
            expGrowthLabel.setText("EXP Growth: Slow");
        } else if(expSum <= 1.666){
            expGrowthLabel.setText("EXP Growth: Fast");
        } else {
            expGrowthLabel.setText("EXP Growth: Medium");
        }
        expGrowthLabel.setPosition(expSlider.getX() + 20, Gdx.graphics.getHeight() - 500);
    }

}
