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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
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
    SelectBox<String> statSelectBox;
    SelectBox<String> proficiencySelectBox;
    SelectBox<String> spellTypeSelectBox;
    TextButton statPlus;
    TextButton statMinus;
    TextButton proficiencyPlus;
    TextButton proficiencyMinus;
    TextButton spellTypesPlus;
    TextButton spellTypesMinus;

    TextButton confirmButton;
    TextButton exitButton;

    Texture coinTexture;
    Image coinImage;
    Label currentMoney;
    Image currentMoneyImage;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    List<Integer> statsList;
    Map<String, Integer> proficiencies;
    Set<String> spellTypes;
    double increaseEXP = 1.5;
    double exponentEXP = 1.5;

    Dialog nameLengthDialog;
    Dialog nameExistsDialog;
    Dialog classCreationDialog;
    Dialog moneyDialog;

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
        statsList = new ArrayList<>();
        statsList.addAll(List.of(0, 0, 0, 0, 0, 0));
        Array<String> proficiencyList = new Array<>();
        try {
            Global.getListJSON(Global.globalSets, "weaponTypesSet").forEach(weaponType -> proficiencyList.add((String) weaponType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        proficiencies = new HashMap<>();
        Array<String> spellLists = new Array<>();
        try {
            Global.getListJSON(Global.globalSets, "spellTypesSet").forEach(spellType -> spellLists.add((String) spellType));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                try {
                    if (Global.isPresentInJSONList(Global.globalSets, classNameField.getText(), "classNamesSet")) {
                        nameExistsDialog.show(stage);
                    } else if (classNameField.getText().length() < 1 || classNameField.getText().length() > 12) {
                        nameLengthDialog.show(stage);
                    } else if (getClassCost() > Global.getMoney()) {
                        moneyDialog.show(stage);
                    } else {
                        classCreationDialog.show(stage);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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

        /*
            Creating the EXP Slider.
         */

        expSlider = new Slider(1.6f, 2.8f, 0.01f, false, game.sliderStyle);
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
            Setting the style for all plus and minuses button.
         */

        TextButton.TextButtonStyle plusMinusStyle = skin.get("default", TextButton.TextButtonStyle.class);
        plusMinusStyle.font = game.normalFont;

        /*
            Creating the Stat Select Box and its buttons.
         */

        statSelectBox = new SelectBox<>(game.selectBoxStyle);
        statSelectBox.setItems("Strength", "Intelligence", "Vigor", "Agility", "Spirit", "Arcane");
        statSelectBox.setWidth(300);
        statSelectBox.setPosition(Gdx.graphics.getWidth() - 460, Gdx.graphics.getHeight() - 300);
        statPlus = new TextButton("+", plusMinusStyle);
        statPlus.setSize(80, 90);
        statPlus.setPosition(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 310);
        statMinus = new TextButton("-", skin.get("default", TextButton.TextButtonStyle.class));
        statMinus.setPosition(Gdx.graphics.getWidth() - 600, Gdx.graphics.getHeight() - 310);
        statMinus.setSize(80, 90);
        statPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (getRemainingPoints() > 0) {
                    int index = statSelectBox.getSelectedIndex();
                    int listElement = statsList.get(index);
                    statsList.set(index, listElement + 1);
                    updateStats();
                }
            }
        });

        statMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int index = statSelectBox.getSelectedIndex();
                if (statsList.get(index) > 0) {
                    statsList.set(index, statsList.get(index) - 1);
                    updateStats();
                }
            }
        });

        /*
            Creating the Proficiency Select Box and buttons.
         */

        proficiencySelectBox = new SelectBox<>(game.selectBoxStyle);
        proficiencySelectBox.setItems(proficiencyList);
        proficiencySelectBox.setWidth(300);
        proficiencySelectBox.setPosition(Gdx.graphics.getWidth() - 460, Gdx.graphics.getHeight() - 460);
        proficiencyPlus = new TextButton("+", plusMinusStyle);
        proficiencyPlus.setSize(80, 90);
        proficiencyPlus.setPosition(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 470);
        proficiencyMinus = new TextButton("-", skin.get("default", TextButton.TextButtonStyle.class));
        proficiencyMinus.setPosition(Gdx.graphics.getWidth() - 600, Gdx.graphics.getHeight() - 470);
        proficiencyMinus.setSize(80, 90);

        proficiencyPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!proficiencies.containsKey(proficiencySelectBox.getSelected())) {
                    proficiencies.put(proficiencySelectBox.getSelected(), 1);
                } else {
                    int currProficiency = proficiencies.get(proficiencySelectBox.getSelected());
                    if (currProficiency < 3) {
                        proficiencies.put(proficiencySelectBox.getSelected(), currProficiency + 1);
                    }
                }
                updateStats();
                updateCost();
            }
        });

        proficiencyMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (proficiencies.containsKey(proficiencySelectBox.getSelected())) {
                    proficiencies.put(proficiencySelectBox.getSelected(), proficiencies.get(proficiencySelectBox.getSelected()) - 1);
                    if (proficiencies.get(proficiencySelectBox.getSelected()) == 0) {
                        proficiencies.remove(proficiencySelectBox.getSelected());
                    }
                    updateStats();
                    updateCost();
                }
            }
        });

        /*
            Creating the SpellType Select Box and its two buttons.
         */

        spellTypeSelectBox = new SelectBox<>(game.selectBoxStyle);
        spellTypeSelectBox.setItems(spellLists);
        spellTypeSelectBox.setWidth(300);
        spellTypeSelectBox.setPosition(Gdx.graphics.getWidth() - 460, Gdx.graphics.getHeight() - 620);
        spellTypesPlus = new TextButton("+", plusMinusStyle);
        spellTypesPlus.setSize(80, 90);
        spellTypesPlus.setPosition(Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 630);
        spellTypesMinus = new TextButton("-", skin.get("default", TextButton.TextButtonStyle.class));
        spellTypesMinus.setPosition(Gdx.graphics.getWidth() - 600, Gdx.graphics.getHeight() - 630);
        spellTypesMinus.setSize(80, 90);

        spellTypesPlus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                spellTypes.add(spellTypeSelectBox.getSelected());
                updateCost();
                updateStats();
            }
        });

        spellTypesMinus.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                spellTypes.remove(spellTypeSelectBox.getSelected());
                updateCost();
                updateStats();
            }
        });

        /*
            Creating the four dialogs.
         */

        classCreationDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                if (confirm.equals(true)) {
                    try {
                        new AbstractBattler(statsList.get(0), statsList.get(1), statsList.get(2), statsList.get(3), statsList.get(4), statsList.get(5), classNameField.getText(), game.baseEXP, increaseEXP, exponentEXP, proficiencies, spellTypes, true) {
                            @Override
                            public int maxMP() {
                                return 0;
                            }

                            @Override
                            public int maxHP() {
                                return 0;
                            }

                            @Override
                            public void levelUp() {

                            }
                        };
                        Global.setMoney(Global.getMoney() - getClassCost());
                        dispose();
                        game.setScreen(new CityScreen(game));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                hide();
            }
        };
        classCreationDialog.setResizable(false);
        classCreationDialog.text(" Do you want to create \n the " + classNameField.getText() + " class?\n", game.labelStyle);
        classCreationDialog.button("Yes", true, game.textButtonStyle);
        classCreationDialog.button("No", false, game.textButtonStyle);
        classCreationDialog.setPosition(0, 0);

        nameExistsDialog = new Dialog("", skin) {

            public void result(Object confirm) {
                hide();
            }
        };
        nameExistsDialog.setResizable(false);
        nameExistsDialog.text(" The class' name given is already used.\n Choose another!\n", game.labelStyle);
        nameExistsDialog.button("Ok!", true, game.textButtonStyle);
        nameExistsDialog.setPosition(0, 0);

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
        moneyDialog.text(" You don't have enough money\n to create this new class\n", game.labelStyle);
        moneyDialog.button("Ok!", true, game.textButtonStyle);
        moneyDialog.setPosition(0, 0);

        /*
            Adding the coin icon.
         */

        coinTexture = new Texture(Gdx.files.local(game.coinPath));
        coinImage = new Image();
        coinImage.setDrawable(new TextureRegionDrawable(coinTexture));
        coinImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        coinImage.setPosition(((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2) + costLabel.getText().length() + 250, confirmButton.getY() + 125);
        updateCost();

        /*
            Adding the current money label and image.
         */

        try {
            currentMoney = new Label("Money: " + Global.getMoney(), stats.getStyle());
        } catch (IOException e) {
            currentMoney = new Label("Money: ?", stats.getStyle());
        }
        currentMoney.setColor(Color.BLACK);
        currentMoney.setPosition(1450, 950);
        currentMoneyImage = new Image(new TextureRegionDrawable(coinTexture));
        currentMoneyImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        currentMoneyImage.setPosition(1400 + (currentMoney.getText().length() * 10) + 160, 935);

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
        stage.addActor(statSelectBox);
        stage.addActor(statPlus);
        stage.addActor(statMinus);
        stage.addActor(proficiencySelectBox);
        stage.addActor(proficiencyPlus);
        stage.addActor(proficiencyMinus);
        stage.addActor(spellTypeSelectBox);
        stage.addActor(spellTypesPlus);
        stage.addActor(spellTypesMinus);
        stage.addActor(coinImage);
        stage.addActor(currentMoney);
        stage.addActor(currentMoneyImage);
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
        int otherLines = 0;
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
                text.append(" +".repeat(o));
                text.append("\n");
            });
            otherLines += proficiencies.size();
        }
        if (!spellTypes.isEmpty()) {
            text.append("Learnable spell types:\n");
            spellTypes.forEach((str) -> text.append(" - ").append(str).append("\n"));
            otherLines += spellTypes.size();
        }
        stats.setText(text);
        stats.setPosition(300, Gdx.graphics.getHeight() - 400 - (18 * otherLines));
    }

    /**
     * The cost label is updated.
     */
    public void updateCost() {
        costLabel.setText("Class cost: " + getClassCost());
        costLabel.setX((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2);
        coinImage.setPosition(((Gdx.graphics.getWidth() - confirmButton.getWidth()) / 2) + (costLabel.getText().length() * 5) + 200, confirmButton.getY() + 125);
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

    /**
     * The EXP label and the EXP values are updated.
     */
    public void updateEXP() {
        float expSum = expSlider.getValue();
        increaseEXP = expSum / 2;
        exponentEXP = 3 - increaseEXP;
        if (expSum >= 2.5) {
            expGrowthLabel.setText("EXP Growth: Slow");
        } else if (expSum <= 2.05) {
            expGrowthLabel.setText("EXP Growth: Fast");
        } else {
            expGrowthLabel.setText("EXP Growth: Medium");
        }
        expGrowthLabel.setPosition(expSlider.getX() + 20, Gdx.graphics.getHeight() - 500);
    }
}
