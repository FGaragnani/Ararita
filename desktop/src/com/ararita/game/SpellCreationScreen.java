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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.DoubleStream;

public class SpellCreationScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label stats;

    TextField spellNameField;
    SelectBox<String> spellTypeSelectBox;
    SelectBox<String> spellPowerSelectBox;
    Label costLabel;

    SelectBox<String> statusEffectSelectBox;
    Slider probabilitySlider;
    Label probabilityLabel;
    TextButton statusPlus;
    TextButton statusMinus;

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
    Array<String> statusEffectsList;
    int cost;

    SelectBox<String> characterSelectBox;
    java.util.List<PC> currentBattlers;

    Dialog nameLengthDialog;
    Dialog nameExistsDialog;
    Dialog noCharDialog;
    Dialog spellCreationDialog;
    Dialog moneyDialog;

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

        statusEffectsList = new Array<>();
        try {
            Global.getListJSON(Global.globalSets, "statusEffectsSet").forEach((str) -> statusEffectsList.add((String) str));
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
            Setting the title.
         */

        game.createTitleCentered("SPELL CREATION", Gdx.graphics.getHeight() * 0.86f, Color.BLACK, stage);

         /*
            Creating the button for confirmation.
            Creating its Listener.
         */

        confirmButton = game.createMainButtonXCentered("Confirm", Gdx.graphics.getHeight() * 0.21f, stage);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if (characterSelectBox.getSelected().equals("No character...")) {
                        noCharDialog.show(stage);
                    } else if (Global.getMoney() < cost) {
                        moneyDialog.show(stage);
                    } else if (Global.isPresentInJSONList(Global.globalSets, spellNameField.getText(), "spellNamesSet")) {
                        nameExistsDialog.show(stage);
                    } else if (spellNameField.getText().length() < 1 || spellNameField.getText().length() > 12) {
                        nameLengthDialog.show(stage);
                    } else {
                        spellCreationDialog.show(stage);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

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
            Setting the stats Label.
         */

        stats = game.createStatLabel("", Color.BLACK, Gdx.graphics.getWidth() * 0.13f, Gdx.graphics.getHeight() * 0.72f, stage);

        /*
            Creating the TextField - for the spell's name.
         */

        spellNameField = game.createTextField("Spell Name", game.width400, textField -> (Gdx.graphics.getWidth() - textField.getWidth()) / 2, Gdx.graphics.getHeight() * 0.72f, stage);

        /*
            Creating the SpellType Select Box and its label.
         */

        spellTypeSelectBox = game.createSelectBox(game.width200, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) / 2 + (Gdx.graphics.getWidth() / 20.2f), Gdx.graphics.getHeight() * 0.61f, stage);
        spellTypeSelectBox.setItems(spellLists);
        spellTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (statusEffects.containsKey(spellTypeSelectBox.getSelected())) {
                    probabilitySlider.setValue(BigDecimal.valueOf(statusEffects.get(spellTypeSelectBox.getSelected())).setScale(2, RoundingMode.HALF_UP).toBigInteger().floatValue());
                }
                updateStats();
                updateCharacters();
            }
        });
        game.createLabelVoid("Spell Type: ", (Gdx.graphics.getWidth() - spellTypeSelectBox.getWidth()) / 2 - (Gdx.graphics.getWidth() / 20.87f), Gdx.graphics.getHeight() * 0.628f, game.descScaleX, game.descScaleY, Color.BLACK, stage);

        /*
            Creating the Spell Power Select Box, its label and its listener.
         */

        spellPowerSelectBox = game.createSelectBox(game.width200, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) / 2 + (Gdx.graphics.getWidth() / 20.2f), Gdx.graphics.getHeight() * 0.49f, stage);
        spellPowerSelectBox.setItems(spellPowerList);
        spellPowerSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateStats();
                updateCost();
            }
        });
        game.createLabelVoid("Spell Power: ", (Gdx.graphics.getWidth() - spellTypeSelectBox.getWidth()) / 2 - (Gdx.graphics.getWidth() / 19.2f), Gdx.graphics.getHeight() * 0.5f, game.descScaleX, game.descScaleY, Color.BLACK, stage);

        /*
            Creating the coin icon and the cost label.
         */

        costLabel = game.createStatLabel("", Color.BLACK, 0, 0, stage);
        coinTexture = new Texture(Gdx.files.local(game.coinPath));
        coinImage = new Image();
        coinImage.setDrawable(new TextureRegionDrawable(coinTexture));
        coinImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        coinImage.setPosition(((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2) + costLabel.getText().length() + (Gdx.graphics.getWidth() / 7.68f), confirmButton.getY() + (Gdx.graphics.getHeight() * 0.11f));

        /*
            Adding the current money label and image.
         */

        try {
            currentMoney = new Label("Money: " + Global.getMoney(), stats.getStyle());
        } catch (IOException e) {
            currentMoney = new Label("Money: ?", stats.getStyle());
        }
        currentMoney.setColor(Color.BLACK);
        currentMoney.setPosition(0.75f * Gdx.graphics.getWidth(), 0.88f * Gdx.graphics.getHeight());
        currentMoneyImage = new Image(new TextureRegionDrawable(coinTexture));
        currentMoneyImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        currentMoneyImage.setPosition(Gdx.graphics.getWidth() * 0.81f + (currentMoney.getText().length() * (Gdx.graphics.getWidth() / 192f)), 0.86f * Gdx.graphics.getHeight());

        /*
            Creating the Status Effect Select Box.
         */

        statusEffectSelectBox = game.createSelectBox((game.width300 + game.width200) / 2f, stringSelectBox -> Gdx.graphics.getWidth() * 0.76f, Gdx.graphics.getHeight() * 0.72f, stage);
        statusEffectSelectBox.setItems(statusEffectsList);
        statusEffectSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (statusEffects.containsKey(statusEffectSelectBox.getSelected())) {
                    probabilitySlider.setValue(BigDecimal.valueOf(statusEffects.get(statusEffectSelectBox.getSelected())).setScale(2, RoundingMode.HALF_UP).floatValue());
                }
            }
        });

        /*
            Creating the slider's label and the slider.
         */

        probabilityLabel = game.createLabel("Probability: 0.25%", Gdx.graphics.getWidth() * 0.76f, Gdx.graphics.getHeight() * 0.574f, game.descScaleX, game.descScaleY, Color.BLACK, stage);
        probabilitySlider = game.createSlider(0.01f, 0.5f, 0.01f, false, game.width300, 0.25f, Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.63f, stage);
        probabilitySlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                probabilityLabel.setText("Probability: " + BigDecimal.valueOf(probabilitySlider.getValue()).setScale(2, RoundingMode.HALF_UP) + " %");
                if (statusEffects.containsKey(statusEffectSelectBox.getSelected())) {
                    statusEffects.put(statusEffectSelectBox.getSelected(), (double) probabilitySlider.getValue());
                    updateStats();
                    updateCost();
                }
            }
        });

        /*
            Adding the plus and minus buttons, and their listeners.
         */

        TextButton.TextButtonStyle plusMinusStyle = skin.get("default", TextButton.TextButtonStyle.class);
        plusMinusStyle.font = game.normalFont;

        statusPlus = new TextButton("+", plusMinusStyle);
        statusPlus.setSize(Gdx.graphics.getWidth() / 24f, Gdx.graphics.getHeight() / 12f);
        statusPlus.setPosition(Gdx.graphics.getWidth() * 0.92f, Gdx.graphics.getHeight() * 0.71f);
        statusMinus = new TextButton("-", plusMinusStyle);
        statusMinus.setPosition(Gdx.graphics.getWidth() * 0.6875f, Gdx.graphics.getHeight() * 0.71f);
        statusMinus.setSize(Gdx.graphics.getWidth() / 24f, Gdx.graphics.getHeight() / 12f);

        statusPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                statusEffects.put(statusEffectSelectBox.getSelected(), (double) probabilitySlider.getValue());
                updateStats();
                updateCost();
            }
        });

        statusMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                statusEffects.remove(statusEffectSelectBox.getSelected());
                updateStats();
                updateCost();
            }
        });

        /*
            Creating the Character Select Box.
         */

        characterSelectBox = game.createSelectBox(game.width200 + game.width300, stringSelectBox -> Gdx.graphics.getWidth() * 0.69f, Gdx.graphics.getHeight() * 0.444f, stage);

        /*
            Creating the five dialogs.
         */

        spellCreationDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                if ((boolean) confirm) {
                    try {
                        Global.setMoney(Global.getMoney() - cost);
                        PC toLearn = Global.getAllCharacters().stream().filter((pc) -> {
                            try {
                                return (pc.canLearn(new Spell("", MPCost(), spellTypeSelectBox.getSelected(), spellBasePower, statusEffects, false)));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList().get(characterSelectBox.getSelectedIndex());
                        toLearn.learnSpell(new Spell(spellNameField.getText(), MPCost(), spellTypeSelectBox.getSelected(), spellBasePower, statusEffects, true));
                        dispose();
                        game.setScreen(new CityScreen(game));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    remove();
                }
            }
        };
        spellCreationDialog.setResizable(false);
        spellCreationDialog.text(" Do you want to create \n the new spell?\n", game.labelStyle);
        spellCreationDialog.button("Yes", true, game.textButtonStyle);
        spellCreationDialog.button("No", false, game.textButtonStyle);
        spellCreationDialog.setPosition(0, 0);

        nameExistsDialog = new Dialog("", skin) {

            public void result(Object confirm) {
                hide();
            }
        };
        nameExistsDialog.setResizable(false);
        nameExistsDialog.text(" The spell's name given is already used.\n Choose another!\n", game.labelStyle);
        nameExistsDialog.button("Ok!", true, game.textButtonStyle);
        nameExistsDialog.setPosition(0, 0);

        noCharDialog = new Dialog("", skin) {

            public void result(Object confirm) {
                hide();
            }
        };
        noCharDialog.setResizable(false);
        noCharDialog.text(" There isn't a character that can\n learn spells of this type.\n", game.labelStyle);
        noCharDialog.button("Ok!", true, game.textButtonStyle);
        noCharDialog.setPosition(0, 0);

        nameLengthDialog = new Dialog("", skin) {

            public void result(Object confirm) {
                hide();
            }
        };
        nameLengthDialog.setResizable(false);
        nameLengthDialog.text(" The name must be at least 1 and \n max 12 characters long. Choose another!\n", game.labelStyle);
        nameLengthDialog.button("Ok!", true, game.textButtonStyle);
        nameLengthDialog.setPosition(0, 0);

        moneyDialog = new Dialog("", skin) {

            public void result(Object confirm) {
                hide();
            }
        };
        moneyDialog.setResizable(false);
        moneyDialog.text(" You don't have enough money\n to create this new spell\n", game.labelStyle);
        moneyDialog.button("Ok!", true, game.textButtonStyle);
        moneyDialog.setPosition(0, 0);

        /*
            Initializing the different values.
         */

        updateStats();
        updateCost();

        /*
            Adding all actors.
         */

        stage.addActor(coinImage);
        stage.addActor(currentMoney);
        stage.addActor(currentMoneyImage);
        stage.addActor(statusPlus);
        stage.addActor(statusMinus);

        updateCharacters();
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
        baseCost += statusEffects.entrySet().stream().flatMapToDouble((entry) -> DoubleStream.of(entry.getValue() * 200 * Math.pow(10, statusEffectsSize - 1))).sum();
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
            statusEffects.forEach((key, value) -> text.append("\t").append(key).append(": ").append(BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP)).append(" %\n"));
            otherLines += statusEffects.size();
        }
        stats.setText(text);
        stats.setPosition(Gdx.graphics.getWidth() * 0.13f, Gdx.graphics.getHeight() * 0.72f - (game.otherLinesFactor * otherLines));
    }

    /**
     * The cost label is updated.
     */
    public void updateCost() {
        try {
            spellBasePower = spellPowerList.indexOf(spellPowerSelectBox.getSelected(), false) + 1;
            Spell toCreate = new Spell("", MPCost(), spellTypeSelectBox.getSelected(), spellBasePower, statusEffects, false);
            costLabel.setText("Spell cost: " + toCreate.moneyCost());
            costLabel.setX((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2);
            costLabel.setY(confirmButton.getY() + (Gdx.graphics.getHeight() * 0.165f));
            coinImage.setPosition(((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2) + (costLabel.getText().length() * (Gdx.graphics.getWidth() / 350f)) + (Gdx.graphics.getWidth() / 8f), confirmButton.getY() + (Gdx.graphics.getHeight() * 0.135f));
            cost = toCreate.moneyCost();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The characters displayed in the select box are updated.
     */
    public void updateCharacters() {
        String spellType = spellTypeSelectBox.getSelected();
        java.util.List<PC> allCharacters;
        try {
            allCharacters = Global.getAllCharacters();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentBattlers = allCharacters;
        java.util.List<String> charLearnable = allCharacters.stream().filter((pc) -> {
            try {
                return (pc.canLearn(new Spell("", MPCost(), spellType, spellBasePower, statusEffects, false)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).map((pc) -> (pc.getName() + ", " + pc.getCharClass())).toList();
        if (!charLearnable.isEmpty()) {
            Array<String> charLearnableNamesArray = new Array<>();
            charLearnable.forEach(charLearnableNamesArray::add);
            characterSelectBox.setItems(charLearnableNamesArray);
        } else {
            characterSelectBox.setItems("No character...");
        }
    }
}
