package com.ararita.game;

import com.ararita.game.spells.Spell;
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
import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

public class SpellCreationScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    Label stats;

    TextField spellNameField;
    SelectBox<String> spellTypeSelectBox;
    Label spellTypeLabel;
    SelectBox<String> spellPowerSelectBox;
    Label spellPowerLabel;
    Label costLabel;

    Texture coinTexture;
    Image coinImage;
    Label currentMoney;
    Image currentMoneyImage;

    TextButton confirmButton;
    TextButton exitButton;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    int spellBasePower;
    Array<String> spellPowerList;
    Map<String, Double> statusEffects;

    public SpellCreationScreen(final Ararita game) {
        /*
            First initialization.
         */

        this.game = game;
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        Array<String> spellLists = new Array<>();
        try {
            Global.getListJSON(Global.globalSets, "spellTypesSet").forEach(spellType -> spellLists.add((String) spellType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        spellPowerList = new Array<>();
        spellPowerList.addAll("I", "II", "III", "IV", "V");

        statusEffects = new HashMap<>();

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
        title = new Label("SPELL CREATION", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() - 150);

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
            Setting the stats Label.
         */

        stats = new Label("", game.labelStyle);
        stats.setFontScale(2.8f, 3.8f);
        stats.setColor(Color.BLACK);
        stats.setPosition(250, Gdx.graphics.getHeight() - 300);

        /*
            Creating the TextField - for the spell's name.
         */

        spellNameField = new TextField("Spell Name", game.textFieldStyle);
        spellNameField.setWidth(400);
        spellNameField.setPosition((Gdx.graphics.getWidth() - spellNameField.getWidth()) / 2, Gdx.graphics.getHeight() - 300);

        /*
            Creating the SpellType Select Box and its label.
         */

        spellTypeSelectBox = new SelectBox<>(game.selectBoxStyle);
        spellTypeSelectBox.setItems(spellLists);
        spellTypeSelectBox.setWidth(200);
        spellTypeSelectBox.setPosition((Gdx.graphics.getWidth() - spellTypeSelectBox.getWidth()) / 2 + 95, Gdx.graphics.getHeight() - 420);
        spellTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateStats();
            }
        });
        spellTypeLabel = new Label("Spell Type: ", game.labelStyle);
        spellTypeLabel.setFontScale(3f, 4.2f);
        spellTypeLabel.setColor(Color.BLACK);
        spellTypeLabel.setPosition((Gdx.graphics.getWidth() - spellTypeSelectBox.getWidth()) / 2 - 92, Gdx.graphics.getHeight() - 402);

        /*
            Creating the Spell Power Select Box, its label and its listener.
         */

        spellPowerSelectBox = new SelectBox<>(game.selectBoxStyle);
        spellPowerSelectBox.setItems(spellPowerList);
        spellPowerSelectBox.setWidth(200);
        spellPowerSelectBox.setPosition((Gdx.graphics.getWidth() - spellTypeSelectBox.getWidth()) / 2 + 95, Gdx.graphics.getHeight() - 548);
        spellPowerSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateStats();
                updateCost();
            }
        });
        spellPowerLabel = new Label("Spell Power: ", game.labelStyle);
        spellPowerLabel.setFontScale(3f, 4.2f);
        spellPowerLabel.setColor(Color.BLACK);
        spellPowerLabel.setPosition((Gdx.graphics.getWidth() - spellTypeSelectBox.getWidth()) / 2 - 100, Gdx.graphics.getHeight() - 530);

        /*
            Creating the coin icon and the cost label.
         */

        costLabel = new Label("", stats.getStyle());
        costLabel.setFontScale(2.8f, 3.8f);
        costLabel.setColor(Color.BLACK);
        costLabel.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, confirmButton.getY() + 155);
        coinTexture = new Texture(Gdx.files.local("Icons/coin.png"));
        coinImage = new Image();
        coinImage.setDrawable(new TextureRegionDrawable(coinTexture));
        coinImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        coinImage.setPosition(((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2) + costLabel.getText().length() + 250, confirmButton.getY() + 125);


        /*
            Initializing the different values.
         */

        updateStats();
        updateCost();

        /*
            Adding all actors.
         */

        stage.addActor(confirmButton);
        stage.addActor(exitButton);
        stage.addActor(title);
        stage.addActor(spellNameField);
        stage.addActor(spellTypeSelectBox);
        stage.addActor(spellTypeLabel);
        stage.addActor(spellPowerSelectBox);
        stage.addActor(spellPowerLabel);
        stage.addActor(stats);
        stage.addActor(costLabel);
        stage.addActor(coinImage);
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
        coinTexture.dispose();
    }

    /**
     * Determines the MP Cost of the creating spell.
     *
     * @return The MP Cost of the spell.
     */
    public int MPCost() {
        int baseCost = (int) Math.pow(10, spellBasePower);
        int statusEffectsSize = statusEffects.size();
        baseCost += statusEffects.entrySet().stream().flatMapToDouble((entry) -> DoubleStream.of(entry.getValue() * 200000 * Math.pow(10, statusEffectsSize))).sum();
        return baseCost;
    }

    /**
     * The stats label is updated.
     */
    public void updateStats() {
        int otherLines = 0;
        spellBasePower = spellPowerList.indexOf(spellPowerSelectBox.getSelected(), false) + 1;
        StringBuilder text = new StringBuilder();
        text.append("Spell Type: ").append(spellTypeSelectBox.getSelected()).append("\n");
        text.append("Spell Base Power: ").append(spellPowerSelectBox.getSelected()).append("\n");
        text.append("MP Cost: ").append(MPCost()).append("\n");
        if (!statusEffects.isEmpty()) {
            text.append("Status Effects:\n");
            statusEffects.forEach((key, value) -> text.append(" ").append(key).append(": ").append(value).append("\n"));
            otherLines += statusEffects.size();
        }
        stats.setText(text);
        stats.setPosition(250, Gdx.graphics.getHeight() - 300 - (17 * otherLines));
    }

    public void updateCost() {
        try {
            spellBasePower = spellPowerList.indexOf(spellPowerSelectBox.getSelected(), false) + 1;
            Spell toCreate = new Spell("", MPCost(), spellTypeSelectBox.getSelected(), spellBasePower, statusEffects, false);
            costLabel.setText("Spell cost: " + toCreate.moneyCost());
            costLabel.setX((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2);
            coinImage.setPosition(((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2) + (costLabel.getText().length() * 5) + 200, confirmButton.getY() + 125);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
