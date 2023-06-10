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
import java.util.List;
import java.util.Map;

public class SpellManagerScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    SelectBox<String> deleteCharSelectBox;
    SelectBox<String> deleteSpellSelectBox;
    Label deleteStats;
    Array<String> allDeleteCharacters;
    Array<String> charDeleteSpells;
    TextButton deleteButton;

    SelectBox<String> learnCharSelectBox;
    SelectBox<String> learnSpellsSelectBox;
    Label learnStats;
    Array<String> allLearnCharacters;
    Array<String> charLearnSpells;
    TextButton learnButton;

    TextButton exitButton;

    Dialog deleteDialog;
    Dialog learnDialog;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    List<PC> allCharacters;

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

        try {
            allCharacters = Global.getAllCharacters();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        allDeleteCharacters = new Array<>();
        allCharacters.stream().filter(PC -> !PC.getSpellTypes().isEmpty()).filter(PC::canLearnAnySpell).forEach(PC -> allDeleteCharacters.add(PC.getName()));

        allLearnCharacters = new Array<>();
        allCharacters.forEach(PC -> allLearnCharacters.add(PC.getName()));

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundPaper));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Title initialization.
         */

        game.createTitleCentered("SPELL MANAGER", Gdx.graphics.getHeight() * 0.86f, Color.BLACK, stage);

        /*
            Creating the Exit Button.
         */

        exitButton = game.createMainButtonXCentered("Exit", Gdx.graphics.getHeight() * 0.074f, stage);
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

        game.createLabelVoid("Forget known spells", Gdx.graphics.getWidth() * 0.12f, Gdx.graphics.getHeight() * 0.75f, game.descScaleX, game.descScaleY, Color.BLACK, stage);

        deleteCharSelectBox = game.createSelectBox(game.width400, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.65f, stage);
        if (!allDeleteCharacters.isEmpty()) {
            deleteCharSelectBox.setItems(allDeleteCharacters);
        } else {
            deleteCharSelectBox.setItems("No characters...");
        }
        deleteCharSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateDeleteSpellItems();
                updateDeleteStats();
            }
        });

        deleteSpellSelectBox = game.createSelectBox(game.width400, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.55f, stage);
        deleteSpellSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateDeleteStats();
            }
        });

        deleteStats = game.createStatLabel("", Color.BLACK, (Gdx.graphics.getWidth() - deleteSpellSelectBox.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.45f, stage);

        deleteButton = game.createNormalButton("Forget", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) / 5f, Gdx.graphics.getHeight() * 0.1f, stage);
        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!deleteSpellSelectBox.getSelected().equals("No spells...") && !deleteCharSelectBox.getSelected().equals("No characters...")) {
                    deleteDialog.show(stage);
                }
            }
        });

        deleteDialog = new Dialog("", skin) {

            @Override
            public void result(Object confirm) {
                if ((boolean) confirm) {
                    try {
                        if (!deleteCharSelectBox.getSelected().equals("No characters...")) {
                            PC toForget = Global.getCharacter(deleteCharSelectBox.getSelected());
                            toForget.forgetSpell(Global.getSpell(deleteSpellSelectBox.getSelected()));
                            updateDeleteCharItems();
                            updateDeleteSpellItems();
                            updateDeleteStats();
                            updateLearnCharItems();
                            updateLearnSpellItems();
                            updateLearnStats();
                        }
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

        game.createLabelVoid("Learn created spells", Gdx.graphics.getWidth() * 0.83f, Gdx.graphics.getHeight() * 0.75f, game.descScaleX, game.descScaleY, Color.BLACK, stage);

        learnCharSelectBox = game.createSelectBox(game.width400, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) * 0.85f, Gdx.graphics.getHeight() * 0.65f, stage);
        if (!allLearnCharacters.isEmpty()) {
            learnCharSelectBox.setItems(allLearnCharacters);
        } else {
            learnCharSelectBox.setItems("No characters...");
        }
        learnCharSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateLearnSpellItems();
            }
        });

        learnSpellsSelectBox = game.createSelectBox(game.width400, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) * 0.85f, Gdx.graphics.getHeight() * 0.55f, stage);
        learnSpellsSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateLearnStats();
            }
        });

        learnStats = game.createStatLabel("", Color.BLACK, (Gdx.graphics.getWidth() - deleteStats.getWidth()) * 0.85f, Gdx.graphics.getHeight() * 0.45f, stage);

        learnButton = game.createNormalButton("Learn", textButton -> (Gdx.graphics.getWidth() - textButton.getWidth()) * 0.85f, Gdx.graphics.getHeight() * 0.1f, stage);
        learnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!learnSpellsSelectBox.getSelected().equals("No spells...")) {
                    learnDialog.show(stage);
                }
            }
        });

        learnDialog = new Dialog("", skin) {

            @Override
            public void result(Object confirm) {
                if (!learnCharSelectBox.getSelected().equals("No characters...") && !learnSpellsSelectBox.getSelected().equals("No spells...")) {
                    if ((boolean) confirm) {
                        try {
                            PC chosen = Global.getCharacter(learnCharSelectBox.getSelected());
                            Spell toLearn = Global.getSpell(learnSpellsSelectBox.getSelected());
                            chosen.learnSpell(toLearn);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        updateLearnCharItems();
                        updateLearnSpellItems();
                        updateLearnStats();
                        updateDeleteCharItems();
                        updateDeleteSpellItems();
                        updateDeleteStats();
                    }
                }
                hide();
            }
        };
        learnDialog.setResizable(false);
        learnDialog.text(" Do you want your character \n to learn the spell? ", game.labelStyle);
        learnDialog.button("Yes", true, game.textButtonStyle);
        learnDialog.button("No", false, game.textButtonStyle);
        learnDialog.setPosition(0, 0);

        /*
            Setting every initial values.
         */

        updateDeleteSpellItems();
        updateDeleteStats();
        updateLearnSpellItems();
        updateLearnStats();
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
        allDeleteCharacters = new Array<>();
        try {
            Global.getAllCharacters().stream().filter(PC -> !PC.getSpells().isEmpty()).forEach(PC -> allDeleteCharacters.add(PC.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!allDeleteCharacters.isEmpty()) {
            deleteCharSelectBox.setItems(allDeleteCharacters);
        } else {
            deleteCharSelectBox.setItems("No characters...");
        }
    }

    /**
     * The items in the 'delete' spell select box are updated.
     */
    public void updateDeleteSpellItems() {
        try {
            if (!deleteCharSelectBox.getSelected().equals("No characters...")) {
                PC chosen = Global.getCharacter(deleteCharSelectBox.getSelected());
                charDeleteSpells = new Array<>();
                chosen.getSpells().forEach(spell -> charDeleteSpells.add(spell.getName()));
                if (!charDeleteSpells.isEmpty()) {
                    deleteSpellSelectBox.setItems(charDeleteSpells);
                } else {
                    deleteSpellSelectBox.setItems("No spells...");
                }
            } else {
                deleteSpellSelectBox.setItems("No spells...");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateDeleteStats() {

        if (deleteSpellSelectBox.getItems().isEmpty() || deleteSpellSelectBox.getSelected().equals("No spells...")) {
            deleteStats.setText("");
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
        deleteStats.setPosition((Gdx.graphics.getWidth() - deleteStats.getWidth()) * 0.15f, Gdx.graphics.getHeight() * 0.45f - (otherLines * game.otherLinesFactor));
    }

    public void updateLearnCharItems() {
        allLearnCharacters = new Array<>();
        try {
            Global.getAllCharacters().stream().filter(PC -> !PC.getSpellTypes().isEmpty()).filter(PC::canLearnAnySpell).forEach(PC -> allLearnCharacters.add(PC.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!allLearnCharacters.isEmpty()) {
            learnCharSelectBox.setItems(allLearnCharacters);
        } else {
            learnCharSelectBox.setItems("No characters...");
        }
    }

    public void updateLearnSpellItems() {
        if (learnCharSelectBox.getSelected().equals("No characters...")) {
            learnSpellsSelectBox.setItems("No spells...");
            return;
        }
        try {
            PC chosen = Global.getCharacter(learnCharSelectBox.getSelected());
            charLearnSpells = new Array<>();
            Global.getAllSpells().stream().filter(chosen::canLearn).forEach(spell -> charLearnSpells.add(spell.getName()));
            if (!charLearnSpells.isEmpty()) {
                learnSpellsSelectBox.setItems(charLearnSpells);
            } else {
                learnSpellsSelectBox.setItems("No spells...");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateLearnStats() {

        if (learnSpellsSelectBox.getItems().isEmpty() || learnSpellsSelectBox.getSelected().equals("No spells...")) {
            learnStats.setText("");
            return;
        }

        int otherLines = 0;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            Spell toDescribe = Global.getSpell(learnSpellsSelectBox.getSelected());
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

        learnStats.setText(stringBuilder.toString());
        learnStats.setPosition((Gdx.graphics.getWidth() - learnStats.getWidth()) * 0.85f, Gdx.graphics.getHeight() * 0.45f - (otherLines * game.otherLinesFactor));
    }
}
