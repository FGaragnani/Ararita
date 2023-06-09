package com.ararita.game;

import com.ararita.game.battlers.PC;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class SpellManagerScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    Label deleteLabel;
    SelectBox<String> deleteCharSelectBox;
    SelectBox<String> deleteSpellSelectBox;
    Label deleteStats;
    Array<String> allCharacters;
    Array<String> charSpells;
    TextButton deleteButton;

    Label learnLabel;

    TextButton exitButton;

    Dialog deleteDialog;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    TextButton.TextButtonStyle textButtonStyle;

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

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = game.normalFont;

        allCharacters = new Array<>();
        try {
            Global.getAllCharacters().stream().filter(PC -> !PC.getSpells().isEmpty()).forEach(PC -> allCharacters.add(PC.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

        deleteLabel = new Label("Forget known spells", game.labelStyle);
        deleteLabel.setColor(Color.BLACK);
        deleteLabel.setFontScale(game.descScaleX, game.descScaleY);
        deleteLabel.setPosition((Gdx.graphics.getWidth() - deleteLabel.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.75f);

        deleteCharSelectBox = new SelectBox<>(game.selectBoxStyle);
        deleteCharSelectBox.setWidth(game.width400);
        deleteCharSelectBox.setItems(allCharacters);
        deleteCharSelectBox.setPosition((Gdx.graphics.getWidth() - deleteCharSelectBox.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.65f);
        deleteCharSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateDeleteSpellItems();
                updateDeleteStats();
            }
        });

        deleteSpellSelectBox = new SelectBox<>(game.selectBoxStyle);
        deleteSpellSelectBox.setWidth(game.width400);
        deleteSpellSelectBox.setPosition((Gdx.graphics.getWidth() - deleteSpellSelectBox.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.55f);
        deleteSpellSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateDeleteStats();
            }
        });

        deleteStats = new Label("", game.labelStyle);
        deleteStats.setFontScale(game.statScaleX, game.statScaleY);
        deleteStats.setColor(Color.BLACK);
        deleteStats.setPosition((Gdx.graphics.getWidth() - deleteLabel.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.45f);

        deleteButton = new TextButton("Forget", textButtonStyle);
        deleteButton.setPosition((Gdx.graphics.getWidth() - deleteLabel.getWidth()) / 5f, Gdx.graphics.getHeight() * 0.1f);
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                deleteDialog.show(stage);
            }
        });

        deleteDialog = new Dialog("", skin) {

            @Override
            public void result(Object confirm) {
                if ((boolean) confirm) {
                    try {
                        PC toForget = Global.getCharacter(deleteCharSelectBox.getSelected());
                        toForget.forgetSpell(Global.getSpell(deleteSpellSelectBox.getSelected()));
                        updateDeleteCharItems();
                        updateDeleteSpellItems();
                        updateDeleteStats();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                hide();
            }
        };
        deleteDialog.setResizable(false);
        deleteDialog.text(" Do you want your character \n to forget the spell? ", game.labelStyle);
        deleteDialog.button("Yes", true, game.textButtonStyle);
        deleteDialog.button("No", false, game.textButtonStyle);
        deleteDialog.setPosition(0, 0);

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
        stage.addActor(deleteCharSelectBox);
        stage.addActor(deleteSpellSelectBox);
        stage.addActor(deleteStats);
        stage.addActor(deleteButton);
        stage.addActor(learnLabel);
        stage.addActor(exitButton);

        /*
            Setting every initial values.
         */

        updateDeleteSpellItems();
        updateDeleteStats();
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

    /**
     * The characters inside the 'delete' select box are updated.
     */
    public void updateDeleteCharItems() {
        allCharacters = new Array<>();
        try {
            Global.getAllCharacters().stream().filter(PC -> !PC.getSpells().isEmpty()).forEach(PC -> allCharacters.add(PC.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        deleteCharSelectBox.setItems(allCharacters);
    }

    /**
     * The items in the 'delete' spell select box are updated.
     */
    public void updateDeleteSpellItems() {
        try {
            PC chosen = Global.getCharacter(deleteCharSelectBox.getSelected());
            charSpells = new Array<>();
            chosen.getSpells().forEach(spell -> charSpells.add(spell.getName()));
            deleteSpellSelectBox.setItems(charSpells);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDeleteStats() {

        if (deleteSpellSelectBox.getItems().isEmpty()) {
            return;
        }

        int otherLines = 0;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Spell toDescribe = Global.getSpell(deleteSpellSelectBox.getSelected());
            stringBuilder.append("Power: ").append(toDescribe.getBasePower()).append("\n");
            stringBuilder.append("Type: ").append(toDescribe.getType()).append("\n");
            stringBuilder.append("MP: ").append(toDescribe.getMPCost()).append("\n");
            if (!toDescribe.getStatusEffects().isEmpty()) {
                stringBuilder.append("Status Effects:\n");
                otherLines++;
                for (Map.Entry<String, Double> entry : toDescribe.getStatusEffects().entrySet()) {
                    otherLines++;
                    stringBuilder.append(" - ").append(entry.getKey()).append(": ").append(BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP)).append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        deleteStats.setText(stringBuilder.toString());
        deleteStats.setPosition((Gdx.graphics.getWidth() - deleteLabel.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.45f - (otherLines * game.otherLinesFactor));
    }
}
