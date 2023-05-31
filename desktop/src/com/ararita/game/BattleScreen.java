package com.ararita.game;

import com.ararita.game.battlers.Enemy;
import com.ararita.game.battlers.PC;
import com.ararita.game.items.ConsumableItem;
import com.ararita.game.items.Inventory;
import com.ararita.game.items.Item;
import com.ararita.game.spells.Spell;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.rafaskoberg.gdx.typinglabel.TypingListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    Label.LabelStyle labelStyle;

    ProgressBar firstBar;
    ProgressBar secondBar;
    ProgressBar thirdBar;
    ProgressBar fourthBar;

    TextButton.TextButtonStyle textButtonStyle;
    TextButton attackButton;
    TextButton castButton;
    TextButton itemButton;
    TextButton runButton;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    Texture handTexture;
    Image handImage;

    Dialog runDialog;
    Dialog itemDialog;
    SelectBox<String> itemDialogSelectBox;
    Dialog castDialog;
    SelectBox<String> castDialogSelectBox;
    Label castDialogLabel;
    Dialog levelUpDialog;
    Dialog dropDialog;

    Enemy enemy;
    List<PC> party;
    int currentBattler;
    Inventory inventory;
    Array<String> itemsSelectBox;
    Array<String> castsSelectBox;

    public BattleScreen(final Ararita game, final GlobalBattle battle) {

         /*
            First initialization.
         */

        this.game = game;
        this.battle = battle;
        battle.sortBattleOrder();
        this.stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        enemy = battle.getEnemy();
        party = battle.getCharacters();
        currentBattler = 0;
        try {
            inventory = new Inventory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = game.normalFont;

        /*
            Initalizing the audio.
         */

        game.playAudio(game.battleTheme);

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundBattle + "/" + (new Random().nextInt(4) + 1) + ".png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Setting the enemy image.
         */

        enemyTexture = new Texture(Gdx.files.local(game.enemyPath + enemy.getName() + ".png"));
        enemyImage = new Image(new TextureRegionDrawable(enemyTexture));
        enemyImage.setScale((Gdx.graphics.getWidth() / 274f));
        enemyImage.setPosition((Gdx.graphics.getWidth() - enemyImage.getWidth()) / 4 - (Gdx.graphics.getWidth() / 19.2f), Gdx.graphics.getHeight() * 0.3f);

        /*
            Setting the label style.
         */

        labelStyle = new Skin(Gdx.files.internal(game.stylesPath)).get("default", Label.LabelStyle.class);
        labelColor = new Pixmap(Gdx.graphics.getWidth(), (int) (Gdx.graphics.getHeight() / 10.8), Pixmap.Format.RGB888);
        labelColor.setColor(Color.BLACK);
        labelColor.fill();
        labelStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(labelColor)));
        labelMain = new TypingLabel("", labelStyle);

        /*
            Setting the characters' images.
         */

        charSheet = new Texture(Gdx.files.internal(game.spritesPath));
        tmp = TextureRegion.split(charSheet, charSheet.getWidth() / (game.spriteFrameCols * 6), charSheet.getHeight());
        firstCharImage = new Image();
        secondCharImage = new Image();
        thirdCharImage = new Image();
        fourthCharImage = new Image();
        float charImageScale = (Gdx.graphics.getWidth() / 225.88f);
        firstCharImage.setScale(charImageScale);
        secondCharImage.setScale(charImageScale);
        thirdCharImage.setScale(charImageScale);
        fourthCharImage.setScale(charImageScale);

        updateImages();

        firstCharImage.setPosition((Gdx.graphics.getWidth() - firstCharImage.getWidth()) * 3 / 4 - (Gdx.graphics.getWidth() / 19.2f), Gdx.graphics.getHeight() * 0.444f);
        secondCharImage.setPosition((Gdx.graphics.getWidth() - secondCharImage.getWidth()) * 3 / 4, Gdx.graphics.getHeight() * 0.352f);
        thirdCharImage.setPosition((Gdx.graphics.getWidth() - thirdCharImage.getWidth()) * 3 / 4 + (Gdx.graphics.getWidth() / 19.2f), Gdx.graphics.getHeight() * 0.259f);
        fourthCharImage.setPosition((Gdx.graphics.getWidth() - fourthCharImage.getWidth()) * 3 / 4 + (Gdx.graphics.getWidth() / 9.6f), Gdx.graphics.getHeight() / 6f);

        /*
            Adding the hand cursor.
         */

        handTexture = new Texture(Gdx.files.local(game.handPath));
        handImage = new Image(new TextureRegionDrawable(handTexture));
        handImage.setScale(Gdx.graphics.getWidth() / 960f);
        handImage.setSize(handTexture.getWidth(), handTexture.getHeight());

        /*
            Setting the four main buttons.
         */

        attackButton = new TextButton("Attack", textButtonStyle);
        castButton = new TextButton("Cast", textButtonStyle);
        itemButton = new TextButton("Items", textButtonStyle);
        runButton = new TextButton("Run", textButtonStyle);

        attackButton.setWidth(Gdx.graphics.getWidth() / 4f);
        castButton.setWidth(Gdx.graphics.getWidth() / 4f);
        itemButton.setWidth(Gdx.graphics.getWidth() / 4f);
        runButton.setWidth(Gdx.graphics.getWidth() / 4f);

        float buttonHeight = Gdx.graphics.getHeight() / 10.8f;
        attackButton.setHeight(buttonHeight);
        castButton.setHeight(buttonHeight);
        itemButton.setHeight(buttonHeight);
        runButton.setHeight(buttonHeight);

        attackButton.setPosition(0, 0);
        castButton.setPosition(Gdx.graphics.getWidth() / 4f, 0);
        itemButton.setPosition(Gdx.graphics.getWidth() / 2f, 0);
        runButton.setPosition(Gdx.graphics.getWidth() * 3 / 4f, 0);

        /*
            Adding all listeners.
         */

        runButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (labelMain.hasEnded()) {
                    runDialog.show(stage);
                }
            }
        });

        attackButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (labelMain.hasEnded()) {
                    int currHPEnemy = battle.getEnemy().getCurrHP();
                    battle.attack(battle.getBattlers().get(currentBattler), battle.getEnemy());
                    updateAttack(currHPEnemy - battle.getEnemy().getCurrHP(), 0);
                }
            }
        });

        castButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (labelMain.hasEnded()) {
                    updateCastDialog(false);
                    castDialog.show(stage);
                }
            }
        });

        itemButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (labelMain.hasEnded()) {
                    updateItemsDialog();
                    itemDialog.show(stage);
                }
            }
        });

        /*
            Creating all the dialogs.
         */

        runDialog = new Dialog("", game.skin) {
            @Override
            public void result(Object confirm) {
                if (confirm.equals(true) && labelMain.hasEnded()) {
                    game.stopAudio();
                    dispose();
                    game.playAudio(game.cityTheme);
                    game.setScreen(new CityScreen(game));
                }
                hide();
            }
        };
        runDialog.setResizable(false);
        runDialog.text(" Do you want to run away?\n All used items will remain lost! \n ", game.labelStyle);
        runDialog.button("Yes", true, game.textButtonStyle);
        runDialog.button("No", false, game.textButtonStyle);
        runDialog.setPosition(0, 0);

        itemDialogSelectBox = new SelectBox<>(game.selectBoxStyle);
        itemDialogSelectBox.setWidth(game.width400);
        itemsSelectBox = new Array<>();
        inventory.getItems().entrySet().stream().filter((entry) -> (entry.getKey() instanceof ConsumableItem)).forEach((entry) -> itemsSelectBox.add(entry.getValue().toString() + " " + entry.getKey().getName()));
        if (itemsSelectBox.isEmpty()) {
            itemsSelectBox.add("No consumables...");
        }
        itemDialogSelectBox.setItems(itemsSelectBox);

        itemDialog = new Dialog("", game.skin) {
            @Override
            public void result(Object confirm) {
                if ((boolean) confirm && !itemDialogSelectBox.getSelected().equals("No consumables...")) {
                    int index = itemDialogSelectBox.getSelectedIndex();
                    ConsumableItem toUse = (ConsumableItem) new ArrayList<>(inventory.getItems().entrySet()).get(index).getKey();
                    try {
                        inventory.use((PC) battle.getBattlers().get(currentBattler), toUse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    updateItem(toUse);
                }
                hide();
            }
        };
        itemDialog.text("\t   Use which item?\t  ", game.labelStyle);
        itemDialog.getContentTable().addActor(itemDialogSelectBox);
        itemDialog.getContentTable().padBottom(Gdx.graphics.getHeight() / 10.8f);
        itemDialog.button("Use", true, textButtonStyle);
        itemDialog.button("Back", false, textButtonStyle);
        itemDialog.getButtonTable().padTop(Gdx.graphics.getHeight() / 36f);
        itemDialog.setPosition(0, 0);

        castDialogSelectBox = new SelectBox<>(game.selectBoxStyle);
        castDialogSelectBox.setWidth(game.width400);
        castDialogSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCastDialog(true);
            }
        });

        castDialogLabel = new Label("", game.labelStyle);

        castDialog = new Dialog("", game.skin) {
            @Override
            public void result(Object confirm) {
                if ((boolean) confirm && !castDialogSelectBox.getSelected().equals("No spells...")) {
                    try {
                        Spell toCast = Global.getSpell(castDialogSelectBox.getSelected());
                        if (((PC) battle.getBattlers().get(currentBattler)).canCast(toCast)) {
                            int oldEnemyHP = enemy.getCurrHP();
                            battle.cast((PC) battle.getBattlers().get(currentBattler), enemy, toCast);
                            updateCast(toCast, oldEnemyHP - enemy.getCurrHP());
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                hide();
            }
        };
        castDialog.text("\t   Use which spell?\t  ", game.labelStyle);
        castDialog.getContentTable().row();
        castDialog.getContentTable().add(castDialogSelectBox);
        castDialog.getContentTable().row();
        castDialog.getContentTable().add(castDialogLabel);
        castDialog.button("Cast", true, textButtonStyle);
        castDialog.button("Back", false, textButtonStyle);
        castDialog.getButtonTable().padTop(Gdx.graphics.getHeight() / 36f);
        castDialog.setPosition(0, 0);

        levelUpDialog = new Dialog("", game.skin) {

            public void result(Object confirm) {
                hide();
                game.stopAudio();
                dispose();
                game.playAudio(game.cityTheme);
                game.setScreen(new CityScreen(game));
            }
        };
        levelUpDialog.button("OK", true, textButtonStyle);
        levelUpDialog.setPosition(0, 0);

        dropDialog = new Dialog("", game.skin) {

            public void result(Object confirm) {
                StringBuilder text = new StringBuilder();
                int level;
                int EXP = enemy.givenEXP();
                for (PC character : party) {
                    try {
                        level = character.getLevel();
                        character.gainEXP(Math.max(EXP / party.size(), 1));
                        if (level != character.getLevel()) {
                            text.append(character.getName()).append(" levelled up!\n");
                        }
                        character.healAll();
                        character.update();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (text.isEmpty()) {
                    levelUpDialog.text(" No character \n levelled up. ");
                } else {
                    levelUpDialog.text(text.toString());
                }
                hide();
                levelUpDialog.show(stage);
            }
        };
        dropDialog.button("OK", true, textButtonStyle);
        dropDialog.setPosition(0, 0);


        /*
            Adding all actors to the stage.
         */

        stage.addActor(enemyImage);
        stage.addActor(firstCharImage);
        stage.addActor(secondCharImage);
        stage.addActor(thirdCharImage);
        stage.addActor(fourthCharImage);
        stage.addActor(handImage);
        stage.addActor(attackButton);
        stage.addActor(castButton);
        stage.addActor(itemButton);
        stage.addActor(runButton);

        /*
            Setting everything else.
         */

        setProgressBars();
        updateHandImage();
        if (battle.getBattlers().get(0) instanceof Enemy) {
            updateLabel("turn", 1, 0);
        } else {
            updateLabel("turn", 0, 0);
        }
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
        handTexture.dispose();
    }

    /**
     * This method is called when an attack is being made.
     *
     * @param damageDone The damage inflicted during the attack.
     * @param attacked The index in battlers of the attacked.
     */
    public void updateAttack(int damageDone, int attacked) {
        updateLabel("attack", damageDone, attacked);
    }

    /**
     * A turn is used to consume an item.
     *
     * @param itemUsed The item that has been used.
     */
    public void updateItem(ConsumableItem itemUsed) {
        updateLabel("item", Global.getAllItems().indexOf(itemUsed), 0);
    }

    /**
     * This method is called when a certain battler ends its turn.
     */
    public void updateTurn() {
        updateCurrentBattler();
        updateHandImage();
        if (battle.isLost()) {
            battleLost();
            return;
        } else if (battle.isWon()) {
            battleWon();
            return;
        }
        if (battle.getBattlers().get(currentBattler) instanceof Enemy) {
            updateLabel("turn", 1, 0);
        } else {
            updateLabel("turn", 0, 0);
        }
    }

    /**
     * Every character's progress bar is updated.
     */
    public void updateProgressBars() {
        firstBar.setValue(battle.getCharacters().get(0).getCurrHP());
        if (party.size() >= 2) {
            secondBar.setValue(battle.getCharacters().get(1).getCurrHP());
        }
        if (party.size() >= 3) {
            thirdBar.setValue(battle.getCharacters().get(2).getCurrHP());
        }
        if (party.size() >= 4) {
            fourthBar.setValue(battle.getCharacters().get(3).getCurrHP());
        }
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
        progressBarStyle.knobBefore.setMinHeight(14);
        firstBar = new ProgressBar(0, party.get(0).maxHP(), 1, false, progressBarStyle);
        firstBar.setWidth(100);
        firstBar.setPosition(firstCharImage.getX() + 20, firstCharImage.getY() + firstCharImage.getHeight() + Gdx.graphics.getHeight() / 8f);
        firstBar.setValue(firstBar.getMaxValue());
        stage.addActor(firstBar);
        if (party.size() >= 2) {
            secondBar = new ProgressBar(0, party.get(1).maxHP(), 1, false, progressBarStyle);
            secondBar.setWidth(100);
            secondBar.setPosition(secondCharImage.getX() + 20, secondCharImage.getY() + secondCharImage.getHeight() + Gdx.graphics.getHeight() / 8f);
            secondBar.setValue(secondBar.getMaxValue());
            stage.addActor(secondBar);
        }
        if (party.size() >= 3) {
            thirdBar = new ProgressBar(0, party.get(2).maxHP(), 1, false, progressBarStyle);
            thirdBar.setWidth(100);
            thirdBar.setPosition(thirdCharImage.getX() + 20, thirdCharImage.getY() + thirdCharImage.getHeight() + Gdx.graphics.getHeight() / 8f);
            thirdBar.setValue(thirdBar.getMaxValue());
            stage.addActor(thirdBar);
        }
        if (party.size() >= 4) {
            fourthBar = new ProgressBar(0, party.get(3).maxHP(), 1, false, progressBarStyle);
            fourthBar.setWidth(100);
            fourthBar.setPosition(fourthCharImage.getX() + 20, fourthCharImage.getY() + fourthCharImage.getHeight() + Gdx.graphics.getHeight() / 8f);
            fourthBar.setValue(fourthBar.getMaxValue());
            stage.addActor(fourthBar);
        }
    }

    /**
     * The hand icon is moved to point to the current battler.
     */
    public void updateHandImage() {
        if (battle.getBattlers().get(currentBattler) instanceof Enemy) {
            handImage.setVisible(false);
            return;
        } else {
            handImage.setVisible(true);
        }
        int toUse = battle.getBattlers().stream().filter((battler) -> (battler instanceof PC)).toList().indexOf(battle.getBattlers().get(currentBattler));
        handImage.setPosition((Gdx.graphics.getWidth() - firstCharImage.getWidth()) * 3 / 4 + ((Gdx.graphics.getWidth() / 19.2f) * (toUse - 1)) - (Gdx.graphics.getWidth() / 38.4f), Gdx.graphics.getHeight() * 0.49f - ((Gdx.graphics.getHeight() / 10.8f) * toUse));
    }

    /**
     * The typing label is updated.
     *
     * @param type What is being done; can be:
     * <p>
     * - turn, while waiting for a character to act;
     * <p>
     * - attack, for an attack done;
     * <p>
     * - item, for using an item;
     * <p>
     * - cast, for casting a spell;
     * <p>
     * - win, for when the battle is won;
     * <p>
     * - lost, for when the battle is lost.
     * @param info A parameter used by some events. It's:
     * <p>
     * - for turn, 1 if it's the first turn of the battle is an enemy's, not 1 elsewhere;
     * <p>
     * - for attack, the damage done;
     * <p>
     * - for item, the index in Global.getAllItems() of the used item;
     * <p>
     * - for cast, the index of the spell cast;
     * <p>
     * - for win, how much EXP every character gets;
     * <p>
     * - for lost, unused.
     * @param attacked A parameter used by some events. It's:
     * <p>
     * - for turn, unused;
     * <p>
     * - for attack, the index of the attacked character;
     * <p>
     * - for item, unused;
     * <p>
     * - for cast, the damage done by the spell;
     * <p>
     * - for win, unused;
     * <p>
     * - for lost, unused.
     */
    public void updateLabel(String type, int info, int attacked) {
        if (labelMain != null) {
            labelMain.setText("");
            stage.getActors().removeValue(labelMain, true);
        }
        switch (type) {
            case "turn" -> {
                if (info == 0) {
                    labelMain = new TypingLabel(" It's " + battle.getBattlers().get(currentBattler).getName() + "'s " + "turn.", labelStyle);
                } else if (info == 1) {
                    StringBuilder text = new StringBuilder(" It is the enemy's turn.");
                    switch (enemy.getStatusEffect()) {
                        case "Paralysis" -> text.append(" The enemy is paralysed.");
                        case "Blindness" -> text.append(" The enemy has been blinded.");
                        case "Burn" -> text.append(" The enemy is burned.");
                        case "Poison" -> text.append(" The enemy is poisoned.");
                    }
                    labelMain = new TypingLabel(text.toString(), labelStyle);
                    labelMain.setTypingListener(new TypingListener() {
                        @Override
                        public void event(String event) {

                        }

                        @Override
                        public void end() {
                            try {
                                Thread.sleep(400);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            int alivePCs = (int) battle.getBattlers().stream().filter((battler) -> (battler instanceof PC) && (!battler.isDead())).count();
                            PC attackedPC = battle.getCharacters().stream().filter((PC) -> (!PC.isDead())).toList().get((int) Math.round(Global.getRandomZeroOne() * (alivePCs - 1)));
                            int attacked = battle.getCharacters().indexOf(attackedPC);
                            int attackedCurrHP = attackedPC.getCurrHP();
                            battle.attack(enemy, battle.getCharacters().get(attacked));
                            updateAttack(attackedCurrHP - battle.getCharacters().get(attacked).getCurrHP(), attacked);
                        }

                        @Override
                        public String replaceVariable(String variable) {
                            return null;
                        }

                        @Override
                        public void onChar(Character ch) {

                        }
                    });
                }
            }
            case "attack" -> {
                if (battle.getBattlers().get(currentBattler) instanceof Enemy) {
                    labelMain = new TypingLabel(" The enemy attacks " + party.get(attacked).getName() + ", dealing " + info + " damage!", labelStyle);
                } else {
                    labelMain = new TypingLabel(" " + battle.getBattlers().get(currentBattler).getName() + " attacks the " + "enemy, dealing it " + info + " damage!", labelStyle);
                }
                labelMain.setTypingListener(new TypingListener() {
                    @Override
                    public void event(String event) {

                    }

                    @Override
                    public void end() {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updateProgressBars();
                        updateTurn();
                    }

                    @Override
                    public String replaceVariable(String variable) {
                        return null;
                    }

                    @Override
                    public void onChar(Character ch) {

                    }
                });
            }
            case "item" -> {
                String usedName = Global.getAllItems().get(info).getName();
                labelMain = new TypingLabel(" " + battle.getBattlers().get(currentBattler).getName() + " uses a " + usedName + "!", labelStyle);
                labelMain.setTypingListener(new TypingListener() {
                    @Override
                    public void event(String event) {

                    }

                    @Override
                    public void end() {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updateProgressBars();
                        updateTurn();
                    }

                    @Override
                    public String replaceVariable(String variable) {
                        return null;
                    }

                    @Override
                    public void onChar(Character ch) {

                    }
                });
            }
            case "win" -> {
                game.playAudio(game.fanfareTheme);
                labelMain = new TypingLabel(" You win! You gain " + enemy.getMoney() + "G and each character gains " + info + " " + "EXP!", labelStyle);
                labelMain.setTypingListener(new TypingListener() {
                    @Override
                    public void event(String event) {

                    }

                    @Override
                    public void end() {
                        StringBuilder text = new StringBuilder("\t   You gained:\t  \n");
                        for (Map.Entry<Item, Double> toDrop : enemy.getToDrop().entrySet()) {
                            if (Global.getRandomZeroOne() <= toDrop.getValue()) {
                                try {
                                    inventory.add(toDrop.getKey());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                text.append("- ").append(toDrop.getKey().getName()).append("\n");
                            }
                        }
                        if (text.toString().equals("\t   You gained:\t  \n")) {
                            dropDialog.text("The enemy didn't \n drop anything. ");
                        } else {
                            dropDialog.text(text.toString());
                        }
                        dropDialog.show(stage);
                    }

                    @Override
                    public String replaceVariable(String variable) {
                        return null;
                    }

                    @Override
                    public void onChar(Character ch) {

                    }
                });
            }
            case "lose" -> {
                labelMain = new TypingLabel(" You lost...", labelStyle);
                labelMain.setTypingListener(new TypingListener() {
                    @Override
                    public void event(String event) {

                    }

                    @Override
                    public void end() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        game.stopAudio();
                        dispose();
                        game.playAudio(game.cityTheme);
                        game.setScreen(new CityScreen(game));
                    }

                    @Override
                    public String replaceVariable(String variable) {
                        return null;
                    }

                    @Override
                    public void onChar(Character ch) {

                    }
                });
            }
            case "cast" -> {
                labelMain = new TypingLabel(" " + battle.getBattlers().get(currentBattler).getName() + " casts " + ((PC) battle.getBattlers().get(currentBattler)).getSpells().get(attacked).getName() + ", inflicting " + info + " damage!", labelStyle);
                labelMain.setTypingListener(new TypingListener() {
                    @Override
                    public void event(String event) {

                    }

                    @Override
                    public void end() {
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updateProgressBars();
                        updateTurn();
                    }

                    @Override
                    public String replaceVariable(String variable) {
                        return null;
                    }

                    @Override
                    public void onChar(Character ch) {

                    }
                });
            }
        }
        assert labelMain != null;
        labelMain.setPosition((Gdx.graphics.getWidth() - labelMain.getWidth()) / 2.0f, Gdx.graphics.getHeight() * 0.935f);
        labelMain.setFontScale(game.descScaleX * 1.5f, game.descScaleY * 1.4f);
        stage.addActor(labelMain);
    }

    /**
     * The current battler's index is updated.
     */
    public void updateCurrentBattler() {
        if (battle.isBattleFinished()) {
            return;
        }
        currentBattler++;
        currentBattler %= battle.getBattlers().size();
        while (battle.getBattlers().get(currentBattler).isDead()) {
            currentBattler++;
            currentBattler %= battle.getBattlers().size();
        }
    }

    /**
     * Manages the case in which the battle is lost.
     */
    public void battleLost() {
        updateLabel("lose", 0, 0);
    }

    /**
     * Manages the case in which the battle is won.
     */
    public void battleWon() {
        game.stopAudio();
        hideGUI();
        int EXP = enemy.givenEXP();
        try {
            inventory.addMoney(enemy.getMoney());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateLabel("win", Math.max(EXP / party.size(), 1), 0);
    }

    /**
     * The SelectBox inside the Item Dialog is updated.
     */
    public void updateItemsDialog() {
        itemsSelectBox = new Array<>();
        inventory.getItems().entrySet().stream().filter((entry) -> (entry.getKey() instanceof ConsumableItem)).forEach((entry) -> itemsSelectBox.add(entry.getValue().toString() + " " + entry.getKey().getName()));
        if (itemsSelectBox.isEmpty()) {
            itemsSelectBox.add("No consumables...");
        }
        itemDialogSelectBox.setItems(itemsSelectBox);
    }

    /**
     * The SelectBox inside the Cast Dialog is updated.
     */
    public void updateCastDialog(boolean onlyLabel) {
        if (!onlyLabel) {
            castsSelectBox = new Array<>();
            ((PC) battle.getBattlers().get(currentBattler)).getSpells().forEach(spell -> castsSelectBox.add(spell.getName()));
            if (castsSelectBox.isEmpty()) {
                castsSelectBox.add("No spells...");
            }
            castDialogSelectBox.setItems(castsSelectBox);
        }
        if (!castDialogSelectBox.getSelected().equals("No spells...")) {
            try {
                castDialogLabel.setText("MP: " + ((PC) battle.getBattlers().get(currentBattler)).getCurrMP() + "/" + ((PC) battle.getBattlers().get(currentBattler)).getMP() + ". To cast: " + Global.getSpell(castDialogSelectBox.getSelected()).getMPCost());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            castDialogLabel.setText("");
        }
    }

    /**
     * Manages the cast of a spell.
     *
     * @param spell The spell to cast.
     */
    public void updateCast(Spell spell, int damage) {
        updateLabel("cast", damage, ((PC) battle.getBattlers().get(currentBattler)).getSpells().indexOf(spell));
    }

    /**
     * Hides the hand icon and the Progress Bars.
     */
    public void hideGUI() {
        handImage.setVisible(false);
        firstBar.setVisible(false);
        if (party.size() >= 2) {
            secondBar.setVisible(false);
        }
        if (party.size() >= 3) {
            thirdBar.setVisible(false);
        }
        if (party.size() >= 4) {
            fourthBar.setVisible(false);
        }
        enemyImage.setVisible(false);
    }
}
