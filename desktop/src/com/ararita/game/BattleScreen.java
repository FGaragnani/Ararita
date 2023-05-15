package com.ararita.game;

import com.ararita.game.battlers.Enemy;
import com.ararita.game.battlers.PC;
import com.ararita.game.items.ConsumableItem;
import com.ararita.game.items.Inventory;
import com.ararita.game.items.Item;
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
import java.util.stream.Collectors;

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

        game.playAudio("Music/BattleTheme.mp3");

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local("assets/Backgrounds/" + (new Random().nextInt(4) + 1) + ".png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize((int) (Gdx.graphics.getWidth() * 1.1), (int) (Gdx.graphics.getHeight() * 1.1));

        /*
            Setting the enemy image.
         */

        enemyTexture = new Texture(Gdx.files.local("Enemies/" + enemy.getName() + ".png"));
        enemyImage = new Image(new TextureRegionDrawable(enemyTexture));
        enemyImage.setScale(7);
        enemyImage.setPosition((Gdx.graphics.getWidth() - enemyImage.getWidth()) / 4 - 100, Gdx.graphics.getHeight() - 750);

        /*
            Setting the label style.
         */

        labelStyle = new Skin(Gdx.files.internal(game.stylesPath)).get("default", Label.LabelStyle.class);
        labelColor = new Pixmap(Gdx.graphics.getWidth(), 100, Pixmap.Format.RGB888);
        labelColor.setColor(Color.BLACK);
        labelColor.fill();
        labelStyle.background = new TextureRegionDrawable(new TextureRegion(new Texture(labelColor)));
        labelMain = new TypingLabel("", labelStyle);

        /*
            Setting the characters' images.
         */

        charSheet = new Texture(Gdx.files.internal("General/msprites.png"));
        tmp = TextureRegion.split(charSheet, charSheet.getWidth() / (game.spriteFrameCols * 6), charSheet.getHeight());
        firstCharImage = new Image();
        secondCharImage = new Image();
        thirdCharImage = new Image();
        fourthCharImage = new Image();
        firstCharImage.setScale(8.5f);
        secondCharImage.setScale(8.5f);
        thirdCharImage.setScale(8.5f);
        fourthCharImage.setScale(8.5f);

        updateImages();

        firstCharImage.setPosition((Gdx.graphics.getWidth() - firstCharImage.getWidth()) * 3 / 4 - 100, Gdx.graphics.getHeight() - 600);
        secondCharImage.setPosition((Gdx.graphics.getWidth() - secondCharImage.getWidth()) * 3 / 4, Gdx.graphics.getHeight() - 700);
        thirdCharImage.setPosition((Gdx.graphics.getWidth() - thirdCharImage.getWidth()) * 3 / 4 + 100, Gdx.graphics.getHeight() - 800);
        fourthCharImage.setPosition((Gdx.graphics.getWidth() - fourthCharImage.getWidth()) * 3 / 4 + 200, Gdx.graphics.getHeight() - 900);

        /*
            Adding the hand cursor.
         */

        handTexture = new Texture(Gdx.files.local("General/hand.png"));
        handImage = new Image(new TextureRegionDrawable(handTexture));
        handImage.setScale(2);
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

        attackButton.setHeight(100);
        castButton.setHeight(100);
        itemButton.setHeight(100);
        runButton.setHeight(100);

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
                runDialog.show(stage);
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
                    updateCastDialog();
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
        itemDialogSelectBox.setWidth(400);
        itemsSelectBox = new Array<>();
        inventory.getItems().entrySet().stream().filter((entry) -> (entry.getKey() instanceof ConsumableItem)).forEach((entry) -> itemsSelectBox.add(entry.getValue().toString() + " " + entry.getKey().getName()));
        if (itemsSelectBox.isEmpty()) {
            itemsSelectBox.add("No consumables...");
        }
        itemDialogSelectBox.setItems(itemsSelectBox);

        itemDialog = new Dialog("", game.skin) {
            public void result(Object confirm) {
                if ((boolean) confirm) {
                    if (!itemDialogSelectBox.getSelected().equals("No consumables...")) {
                        int index = itemDialogSelectBox.getSelectedIndex();
                        ConsumableItem toUse = (ConsumableItem) new ArrayList<>(inventory.getItems().entrySet()).get(index).getKey();
                        try {
                            inventory.use((PC) battle.getBattlers().get(currentBattler), toUse);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        updateItem(toUse);
                    }
                }
                hide();
            }
        };
        itemDialog.text("\t   Use which item?\t  ", game.labelStyle);
        itemDialog.getContentTable().addActor(itemDialogSelectBox);
        itemDialog.getContentTable().padBottom(100);
        itemDialog.button("Use", true, textButtonStyle);
        itemDialog.button("Back", false, textButtonStyle);
        itemDialog.getButtonTable().padTop(30);
        itemDialog.setPosition(0, 0);

        castDialogSelectBox = new SelectBox<>(game.selectBoxStyle);
        castDialogSelectBox.setWidth(400);
        castDialogSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCastDialog();
            }
        });

        castDialogLabel = new Label("", game.labelStyle);

        castDialog = new Dialog("", game.skin) {

            public void result(Object confirm) {
                if ((boolean) confirm) {
                    // TODO
                }
                hide();
            }
        };
        castDialog.text("\t   Use which cast?\t  ", game.labelStyle);
        castDialog.getContentTable().row();
        castDialog.getContentTable().add(castDialogSelectBox);
        castDialog.getContentTable().row();
        castDialog.getContentTable().add(castDialogLabel);
        castDialog.button("Cast", true, textButtonStyle);
        castDialog.button("Back", false, textButtonStyle);
        castDialog.getButtonTable().padTop(30);
        castDialog.setPosition(0, 0);

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
            int alivePCs = (int) battle.getBattlers().stream().filter((battler) -> (battler instanceof PC) && (!battler.isDead())).count();
            int attacked = (int) Math.round(Global.getRandomZeroOne() * (alivePCs - 1));
            int attackedCurrHP = battle.getCharacters().get(attacked).getCurrHP();
            battle.attack(enemy, battle.getCharacters().get(attacked));
            updateAttack(attackedCurrHP - battle.getCharacters().get(attacked).getCurrHP(), attacked);
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
        progressBarStyle.knobBefore.setMinHeight(15);
        firstBar = new ProgressBar(0, party.get(0).maxHP(), 1, false, progressBarStyle);
        firstBar.setWidth(100);
        firstBar.setPosition(firstCharImage.getX() + 20, firstCharImage.getY() + firstCharImage.getHeight() + 135);
        firstBar.setValue(firstBar.getMaxValue());
        stage.addActor(firstBar);
        if (party.size() >= 2) {
            secondBar = new ProgressBar(0, party.get(1).maxHP(), 1, false, progressBarStyle);
            secondBar.setWidth(100);
            secondBar.setPosition(secondCharImage.getX() + 20, secondCharImage.getY() + secondCharImage.getHeight() + 135);
            secondBar.setValue(secondBar.getMaxValue());
            stage.addActor(secondBar);
        }
        if (party.size() >= 3) {
            thirdBar = new ProgressBar(0, party.get(2).maxHP(), 1, false, progressBarStyle);
            thirdBar.setWidth(100);
            thirdBar.setPosition(thirdCharImage.getX() + 20, thirdCharImage.getY() + thirdCharImage.getHeight() + 135);
            thirdBar.setValue(thirdBar.getMaxValue());
            stage.addActor(thirdBar);
        }
        if (party.size() >= 4) {
            fourthBar = new ProgressBar(0, party.get(3).maxHP(), 1, false, progressBarStyle);
            fourthBar.setWidth(100);
            fourthBar.setPosition(fourthCharImage.getX() + 20, fourthCharImage.getY() + fourthCharImage.getHeight() + 135);
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
        int toUse = battle.getBattlers().stream().filter((battler) -> (battler instanceof PC)).collect(Collectors.toList()).indexOf(battle.getBattlers().get(currentBattler));
        handImage.setPosition((Gdx.graphics.getWidth() - firstCharImage.getWidth()) * 3 / 4 + (100 * (toUse - 1)) - 50, Gdx.graphics.getHeight() - 550 - (100 * toUse));
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
            case "turn":
                if (info == 0) {
                    labelMain = new TypingLabel(" It's " + battle.getBattlers().get(currentBattler).getName() + "'s " + "turn.", labelStyle);
                }
                if (info == 1) {
                    labelMain = new TypingLabel(" It is the enemy's turn.", labelStyle);
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
                            int attacked = (int) Math.round(Global.getRandomZeroOne() * (alivePCs - 1));
                            int attackedCurrHP = battle.getCharacters().get(attacked).getCurrHP();
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
                break;
            case "attack": {
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
                break;
            }
            case "item": {
                String usedName = Global.getAllItems().get(info).getName();
                labelMain = new TypingLabel(" " + battle.getBattlers().get(currentBattler).getName() + " uses a " + usedName + " " + "!", labelStyle);
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
                break;
            }
            case "win":
                labelMain = new TypingLabel(" You win! You gain " + enemy.getMoney() + "G and each character gains " + info + " " + "EXP!", labelStyle);
                labelMain.setTypingListener(new TypingListener() {
                    @Override
                    public void event(String event) {

                    }

                    @Override
                    public void end() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        game.stopAudio();
                        dispose();
                        game.playAudio("Music/CityTheme.mp3");
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
                break;
            case "lose":
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
                        game.playAudio("Music/CityTheme.mp3");
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
        assert labelMain != null;
        labelMain.setPosition((Gdx.graphics.getWidth() - labelMain.getWidth()) / 2.0f, Gdx.graphics.getHeight() - 70);
        labelMain.setFontScale(4.8f, 6);
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
        int EXP = enemy.givenEXP();
        try {
            inventory.addMoney(enemy.getMoney());
            for (Map.Entry<Item, Double> toDrop : enemy.getToDrop().entrySet()) {
                if (Global.getRandomZeroOne() <= toDrop.getValue()) {
                    inventory.add(toDrop.getKey());
                }
            }
            for (PC character : party) {
                character.gainEXP(EXP / party.size());
                character.healAll();
                character.update();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateLabel("win", EXP / party.size(), 0);
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
    public void updateCastDialog() {
        castsSelectBox = new Array<>();
        ((PC) battle.getBattlers().get(currentBattler)).getSpells().forEach((spell) -> castsSelectBox.add(spell.getName()));
        if (castsSelectBox.isEmpty()) {
            castsSelectBox.add("No spells...");
        }
        castDialogSelectBox.setItems(castsSelectBox);
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
}
