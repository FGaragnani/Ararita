package com.ararita.game;

import com.ararita.game.battlers.PC;
import com.ararita.game.items.Inventory;
import com.ararita.game.items.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PartyManagerScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Inventory inventory;

    Label title;
    Label.LabelStyle titleStyle;

    SelectBox<String> partyCharactersSelectBox;
    Label partyLabel;
    SelectBox<String> otherCharactersSelectBox;
    Label otherCharactersLabel;
    TextButton partyToReserveButton;
    TextButton reserveToPartyButton;

    Label partyStats;
    Label reserveStats;

    SelectBox<String> weaponsInInventorySelectBox;
    TextButton equipButton;
    SelectBox<String> weaponsEquippedSelectBox;
    TextButton unEquipButton;
    Label inventoryLabel;
    Label statsInventoryWeapon;
    Label statsEquippedWeapon;

    Image spriteImageParty;
    Image spriteImageReserve;
    Animation<TextureRegion> partyAnimation;
    Animation<TextureRegion> reserveAnimation;
    Texture charSheet;
    TextureRegion[][] tmp;
    TextureRegion currentFrame;
    float statePartyTime;
    float stateReserveTime;

    TextButton exitButton;

    Dialog oneCharacterInParty;
    Dialog noCharactersInReserve;
    Dialog maxPartyCharacters;
    Dialog maxWeaponDialog;
    Dialog maxItemsDialog;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    public PartyManagerScreen(final Ararita game) {
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
            inventory = new Inventory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
            Initializing the animation textures.
         */

        charSheet = new Texture(Gdx.files.internal(game.spritesPath));
        tmp = TextureRegion.split(charSheet, charSheet.getWidth() / (game.spriteFrameCols * 6), charSheet.getHeight());
        spriteImageParty = new Image();
        spriteImageParty.setScale(Gdx.graphics.getWidth() / 274f);
        spriteImageReserve = new Image();
        spriteImageReserve.setScale(Gdx.graphics.getWidth() / 274f);

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundPaper));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Setting the title.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("PARTY MANAGER", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() * 0.861f);

        /*
            Creating the Exit Button.
         */

        exitButton = new TextButton("Exit", game.textButtonStyle);
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, 0);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new CityScreen(game));
            }
        });

        /*
            Setting the party SelectBox and Label.
         */

        partyCharactersSelectBox = new SelectBox<>(game.selectBoxStyle);
        partyCharactersSelectBox.setWidth(game.width200 + game.width300);
        partyCharactersSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - (0.02f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.648f);
        partyCharactersSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCharacters();
                updatePartyStats();
                updateWeapons();
            }
        });
        partyLabel = new Label("Party:", skin.get("default", Label.LabelStyle.class));
        partyLabel.setFontScale(game.statScaleX, game.statScaleY);
        partyLabel.setColor(Color.BLACK);
        partyLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - (0.073f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.63f);
        spriteImageParty.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() * 0.71f);

        /*
            Adding the other characters Select Box and its label.
         */

        otherCharactersSelectBox = new SelectBox<>(game.selectBoxStyle);
        otherCharactersSelectBox.setWidth(game.width200 + game.width300);
        otherCharactersSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6 + (0.042f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.648f);
        otherCharactersLabel = new Label("Reserve:", skin.get("default", Label.LabelStyle.class));
        otherCharactersLabel.setFontScale(game.statScaleX, game.statScaleY);
        otherCharactersLabel.setColor(Color.BLACK);
        otherCharactersLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6 - (Gdx.graphics.getWidth() * 0.026f), Gdx.graphics.getHeight() * 0.63f);
        spriteImageReserve.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6 + (Gdx.graphics.getWidth() / 17.45f), Gdx.graphics.getHeight() * 0.713f);

        /*
            Creating the two transfer buttons.
         */

        partyToReserveButton = new TextButton("Party -> Reserve", skin.get("default", TextButton.TextButtonStyle.class));
        partyToReserveButton.getLabel().setStyle(partyLabel.getStyle());
        partyToReserveButton.getLabel().setFontScale(game.statScaleX * 0.8f, game.statScaleY * 0.8f);
        partyToReserveButton.setWidth(game.width200 * 5 / 4);
        partyToReserveButton.setPosition((Gdx.graphics.getWidth() - partyToReserveButton.getWidth()) / 2.0f, Gdx.graphics.getHeight() * 0.7f);
        reserveToPartyButton = new TextButton("Party <- Reserve", skin.get("default", TextButton.TextButtonStyle.class));
        reserveToPartyButton.getLabel().setStyle(partyLabel.getStyle());
        reserveToPartyButton.getLabel().setFontScale(game.statScaleX * 0.8f, game.statScaleY * 0.8f);
        reserveToPartyButton.setWidth(game.width200 * 5 / 4);
        reserveToPartyButton.setPosition((Gdx.graphics.getWidth() - partyToReserveButton.getWidth()) / 2.0f, Gdx.graphics.getHeight() * 0.7f - partyToReserveButton.getHeight());

        partyToReserveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (partyCharactersSelectBox.getItems().size <= 1) {
                    oneCharacterInParty.show(stage);
                } else {
                    int index = partyCharactersSelectBox.getSelectedIndex();
                    try {
                        Global.addToOtherCharacters(Global.getParty().get(index).getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    updateCharacters();
                    updatePartyStats();
                    updateWeapons();
                }
            }
        });

        reserveToPartyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (otherCharactersSelectBox.getSelected().equals("No characters...")) {
                    noCharactersInReserve.show(stage);
                } else if (partyCharactersSelectBox.getItems().size >= Global.MAX_PARTY_MEMBERS) {
                    maxPartyCharacters.show(stage);
                } else {
                    int index = otherCharactersSelectBox.getSelectedIndex();
                    try {
                        Global.addToParty(Global.getOtherCharacters().get(index).getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    updateCharacters();
                    updatePartyStats();
                    updateWeapons();
                }
            }
        });

        /*
            Setting the two stats labels.
         */

        partyStats = new Label("", game.labelStyle);
        partyStats.setFontScale(game.statScaleX * 0.88f, game.statScaleY * 0.88f);
        partyStats.setColor(Color.BLACK);
        reserveStats = new Label("", game.labelStyle);
        reserveStats.setFontScale(game.statScaleX * 0.88f, game.statScaleY * 0.88f);
        reserveStats.setColor(Color.BLACK);

        /*
            Setting the inventory select box.
         */

        weaponsInInventorySelectBox = new SelectBox<>(game.selectBoxStyle);
        weaponsInInventorySelectBox.setWidth(game.width400);
        weaponsInInventorySelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() * 0.26f);
        equipButton = new TextButton("Equip", skin.get("default", TextButton.TextButtonStyle.class));
        equipButton.getLabel().setStyle(partyLabel.getStyle());
        equipButton.getLabel().setFontScale(game.statScaleX * 0.8f, game.statScaleY * 0.8f);
        equipButton.setWidth(game.width200 / 1.43f);
        equipButton.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() * 0.35f);
        inventoryLabel = new Label("Inventory:", skin.get("default", Label.LabelStyle.class));
        inventoryLabel.setFontScale(game.statScaleX, game.statScaleY);
        inventoryLabel.setColor(Color.BLACK);
        inventoryLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - (Gdx.graphics.getWidth() / 12.8f), Gdx.graphics.getHeight() * 0.477f);

        /*
            Setting the weapons SelectBox.
         */

        weaponsEquippedSelectBox = new SelectBox<>(game.selectBoxStyle);
        weaponsEquippedSelectBox.setWidth(game.width400);
        weaponsEquippedSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() * 0.49f);
        unEquipButton = new TextButton("Unequip", skin.get("default", TextButton.TextButtonStyle.class));
        unEquipButton.getLabel().setStyle(partyLabel.getStyle());
        unEquipButton.getLabel().setFontScale(game.statScaleX * 0.8f, game.statScaleY * 0.8f);
        unEquipButton.setWidth(game.width200 / 1.43f);
        unEquipButton.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 + (Gdx.graphics.getWidth() / 7.53f), Gdx.graphics.getHeight() * 0.35f);

        /*
            Setting the equipment labels.
         */

        statsEquippedWeapon = new Label("", game.labelStyle);
        statsEquippedWeapon.setFontScale(game.statScaleX, game.statScaleY);
        statsEquippedWeapon.setColor(Color.BLACK);
        statsInventoryWeapon = new Label("", game.labelStyle);
        statsInventoryWeapon.setFontScale(game.statScaleX, game.statScaleY);
        statsInventoryWeapon.setColor(Color.BLACK);

        /*
            Setting all dialogs.
         */

        oneCharacterInParty = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        oneCharacterInParty.setResizable(false);
        oneCharacterInParty.text(" You cannot leave \n your party empty! \n", game.labelStyle);
        oneCharacterInParty.button("Ok!", true, game.textButtonStyle);
        oneCharacterInParty.setPosition(0, 0);

        noCharactersInReserve = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        noCharactersInReserve.setResizable(false);
        noCharactersInReserve.text(" You don't have any \n character in reserve! \n", game.labelStyle);
        noCharactersInReserve.button("Ok!", true, game.textButtonStyle);
        noCharactersInReserve.setPosition(0, 0);

        maxPartyCharacters = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        maxPartyCharacters.setResizable(false);
        maxPartyCharacters.text(" You have reached the max \n number of characters in party! \n", game.labelStyle);
        maxPartyCharacters.button("Ok!", true, game.textButtonStyle);
        maxPartyCharacters.setPosition(0, 0);

        maxWeaponDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        maxWeaponDialog.setResizable(false);
        maxWeaponDialog.text(" You have reached the max \n number of equipable weapons! \n", game.labelStyle);
        maxWeaponDialog.button("Ok!", true, game.textButtonStyle);
        maxWeaponDialog.setPosition(0, 0);

        maxItemsDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        maxItemsDialog.setResizable(false);
        maxItemsDialog.text(" You have reached the max number \n of items in your inventory! \n", game.labelStyle);
        maxItemsDialog.button("Ok!", true, game.textButtonStyle);
        maxItemsDialog.setPosition(0, 0);

        /*
            Adding all stage actors.
         */

        stage.addActor(exitButton);
        stage.addActor(title);
        stage.addActor(partyCharactersSelectBox);
        stage.addActor(partyLabel);
        stage.addActor(otherCharactersSelectBox);
        stage.addActor(otherCharactersLabel);
        stage.addActor(partyToReserveButton);
        stage.addActor(reserveToPartyButton);
        stage.addActor(spriteImageParty);
        stage.addActor(spriteImageReserve);
        stage.addActor(partyStats);
        stage.addActor(reserveStats);
        stage.addActor(weaponsInInventorySelectBox);
        stage.addActor(equipButton);
        stage.addActor(weaponsEquippedSelectBox);
        stage.addActor(unEquipButton);
        stage.addActor(inventoryLabel);
        stage.addActor(statsInventoryWeapon);
        stage.addActor(statsEquippedWeapon);

        /*
            Setting the initial values.
         */

        updateCharacters();
        updatePartyStats();
        updateWeapons();
        updateStatsInventory();

        /*
            Adding every missing listener.
         */

        weaponsInInventorySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateStatsInventory();
            }
        });

        weaponsEquippedSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateStatsInventory();
            }
        });

        equipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    PC pcToEquip = Global.getParty().get(partyCharactersSelectBox.getSelectedIndex());
                    if (pcToEquip.getWeapons().size() >= Global.MAX_WEAPON_EQUIPPED) {
                        maxWeaponDialog.show(stage);
                    }
                    if (weaponsInInventorySelectBox.getSelected().equals("No weapons...")) {
                        return;
                    }
                    Weapon weaponToEquip = (Weapon) inventory.getItems().entrySet().stream().filter((entry -> entry.getKey() instanceof Weapon)).toList().get(weaponsInInventorySelectBox.getSelectedIndex()).getKey();
                    inventory.equip(pcToEquip, weaponToEquip);
                    updateWeapons();
                    updateStatsInventory();
                    updatePartyStats();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        unEquipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    PC pcToEquip = Global.getParty().get(partyCharactersSelectBox.getSelectedIndex());
                    if (inventory.getItems().size() >= inventory.MAX_INVENTORY_SPACE) {
                        maxItemsDialog.show(stage);
                        return;
                    }
                    if (weaponsEquippedSelectBox.getSelected().equals("No equipped weapons...")) {
                        return;
                    }
                    Weapon weaponToEquip = pcToEquip.getWeapons().get(weaponsEquippedSelectBox.getSelectedIndex());
                    inventory.unEquip(pcToEquip, weaponToEquip);
                    updateWeapons();
                    updateStatsInventory();
                    updatePartyStats();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        statePartyTime += Gdx.graphics.getDeltaTime();
        stateReserveTime += Gdx.graphics.getDeltaTime();

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        currentFrame = partyAnimation.getKeyFrame(statePartyTime, true);
        spriteImageParty.setDrawable(new TextureRegionDrawable(currentFrame));
        spriteImageParty.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        if (spriteImageReserve.isVisible()) {
            currentFrame = reserveAnimation.getKeyFrame(stateReserveTime, true);
            spriteImageReserve.setDrawable(new TextureRegionDrawable(currentFrame));
            spriteImageReserve.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
        }

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
        charSheet.dispose();
    }

    /**
     * The characters present in the Select Boxes are updated.
     */
    public void updateCharacters() {
        Array<String> party = new Array<>();
        Array<String> otherCharacters = new Array<>();
        try {
            Global.getParty().forEach((PC) -> party.add(PC.getName() + ", " + PC.getCharClass()));
            Global.getOtherCharacters().forEach((PC) -> otherCharacters.add(PC.getName() + ", " + PC.getCharClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        partyCharactersSelectBox.setItems(party);
        try {
            changeSprite(Global.getParty().get(partyCharactersSelectBox.getSelectedIndex()).getImage(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!otherCharacters.isEmpty()) {
            otherCharactersSelectBox.setItems(otherCharacters);
            try {
                changeSprite(Global.getOtherCharacters().get(otherCharactersSelectBox.getSelectedIndex()).getImage(), false);
                spriteImageReserve.setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            otherCharactersSelectBox.setItems("No characters...");
            spriteImageReserve.setVisible(false);
        }
    }

    /**
     * The sprites are updated.
     *
     * @param spriteName The string stating the name of the sprite - stored under 'Image' in the JSON.
     * @param inParty Tells if the sprite is representing the selected character inside the party or not.
     */
    public void changeSprite(String spriteName, boolean inParty) {
        int listPosition = game.spriteNames.indexOf(spriteName) * 3;
        TextureRegion[] walkFrames = new TextureRegion[game.spriteFrameCols];
        int index = 0;
        if (inParty) {
            for (int i = listPosition; i < game.spriteFrameCols + listPosition; i++) {
                walkFrames[index++] = tmp[0][i];
            }
            partyAnimation = new Animation<>(0.200f, walkFrames);
            statePartyTime = 0f;
        } else {
            for (int i = listPosition; i < game.spriteFrameCols + listPosition; i++) {
                walkFrames[index++] = tmp[0][i];
            }
            reserveAnimation = new Animation<>(0.200f, walkFrames);
            stateReserveTime = 0f;
        }
    }

    /**
     * The stats of the characters are updated.
     */
    public void updatePartyStats() {
        int otherLines = 0;
        StringBuilder partyText = new StringBuilder();
        try {
            int partyIndex = partyCharactersSelectBox.getSelectedIndex();
            PC partyChar = Global.getCharacter(Global.getParty().get(partyIndex).getName());
            partyText.append("LVL: ").append(partyChar.getLevel()).append("\n");
            partyText.append("NXT: ").append(BigDecimal.valueOf((double) (partyChar.getEXP() - partyChar.EXPForLevel(partyChar.getLevel())) / partyChar.EXPForLevel(partyChar.getLevel() + 1)).setScale(2, RoundingMode.HALF_UP).doubleValue()).append("%\n");
            partyText.append("STR: ").append(partyChar.getEquippedStat("Strength")).append("\n");
            partyText.append("INT: ").append(partyChar.getEquippedStat("Intelligence")).append("\n");
            partyText.append("AGI: ").append(partyChar.getEquippedStat("Agility")).append("\n");
            partyText.append("VIG: ").append(partyChar.getEquippedStat("Vigor")).append("\n");
            partyText.append("SPI: ").append(partyChar.getEquippedStat("Spirit")).append("\n");
            partyText.append("ARC: ").append(partyChar.getEquippedStat("Arcane")).append("\n");
            partyText.append("Proficiencies: \n");
            partyChar.getProficiencies().forEach((s, o) -> {
                partyText.append(" ").append(s).append(":");
                partyText.append(" +".repeat(o));
                partyText.append("\n");
            });
            otherLines += partyChar.getProficiencies().size();
            partyText.append("Learnable spell types:\n");
            partyChar.getSpellTypes().forEach((str) -> partyText.append(" - ").append(str).append("\n"));
            otherLines += partyChar.getSpellTypes().size();
            partyStats.setText(partyText.toString());
            partyStats.setPosition(Gdx.graphics.getWidth() / 2.9f, Gdx.graphics.getHeight() * 0.463f - (game.otherLinesFactor * (otherLines - 1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        otherLines = 0;
        StringBuilder reserveText = new StringBuilder();
        if (otherCharactersSelectBox.getSelected().equals("No characters...")) {
            reserveStats.setText("");
            return;
        }
        try {
            int reserveIndex = otherCharactersSelectBox.getSelectedIndex();
            PC reserveChar = Global.getCharacter(Global.getOtherCharacters().get(reserveIndex).getName());
            reserveText.append("LVL: ").append(reserveChar.getLevel()).append("\n");
            reserveText.append("NXT: ").append(BigDecimal.valueOf((double) (reserveChar.getEXP() - reserveChar.EXPForLevel(reserveChar.getLevel())) / reserveChar.EXPForLevel(reserveChar.getLevel() + 1)).setScale(2, RoundingMode.HALF_UP).doubleValue()).append("%\n");
            reserveText.append("STR: ").append(reserveChar.getEquippedStat("Strength")).append("\n");
            reserveText.append("INT: ").append(reserveChar.getEquippedStat("Intelligence")).append("\n");
            reserveText.append("AGI: ").append(reserveChar.getEquippedStat("Agility")).append("\n");
            reserveText.append("VIG: ").append(reserveChar.getEquippedStat("Vigor")).append("\n");
            reserveText.append("SPI: ").append(reserveChar.getEquippedStat("Spirit")).append("\n");
            reserveText.append("ARC: ").append(reserveChar.getEquippedStat("Arcane")).append("\n");
            reserveText.append("Proficiencies: \n");
            reserveChar.getProficiencies().forEach((s, o) -> {
                reserveText.append(" ").append(s).append(":");
                reserveText.append(" +".repeat(o));
                reserveText.append("\n");
            });
            otherLines += reserveChar.getProficiencies().size();
            reserveText.append("Learnable spell types:\n");
            reserveChar.getSpells().forEach((str) -> reserveText.append(" - ").append(str.toString()).append("\n"));
            otherLines += reserveChar.getSpells().size();
            reserveStats.setText(reserveText.toString());
            reserveStats.setPosition(Gdx.graphics.getWidth() / 1.2f, Gdx.graphics.getHeight() * 0.463f - (game.otherLinesFactor * (otherLines - 1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The weapons inside the Select Boxes are updated.
     */
    public void updateWeapons() {
        Array<String> inventoryWeapons = new Array<>();
        inventory.getItems().entrySet().stream().filter((entry) -> (entry.getKey() instanceof Weapon)).forEach((entry) -> inventoryWeapons.add(entry.getValue() + " " + entry.getKey().getName()));
        if (inventoryWeapons.isEmpty()) {
            weaponsInInventorySelectBox.setItems("No weapons...");
        } else {
            weaponsInInventorySelectBox.setItems(inventoryWeapons);
        }

        Array<String> equippedWeapons = new Array<>();
        try {
            Global.getParty().get(partyCharactersSelectBox.getSelectedIndex()).getWeapons().forEach((weapon) -> equippedWeapons.add(weapon.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (equippedWeapons.isEmpty()) {
            weaponsEquippedSelectBox.setItems("No equipped weapons...");
        } else {
            weaponsEquippedSelectBox.setItems(equippedWeapons);
        }
    }

    /**
     * The stats of the items are updated.
     */
    public void updateStatsInventory() {
        if (weaponsEquippedSelectBox.getItems().get(0).equals("No equipped weapons...")) {
            statsEquippedWeapon.setText("");
        } else {
            int otherLines = 0;
            StringBuilder equipText = new StringBuilder();
            Weapon toDescribe;
            try {
                List<Weapon> weaponList = Global.getParty().get(partyCharactersSelectBox.getSelectedIndex()).getWeapons();
                if (!weaponList.isEmpty()) {
                    toDescribe = Global.getWeapon(weaponList.get(weaponsEquippedSelectBox.getSelectedIndex()).getName());
                    equipText.append(toDescribe.getName()).append("\n");
                    equipText.append("Type: ").append(toDescribe.getWeaponType()).append("\n");
                    equipText.append("Stats:\n");
                    for (Map.Entry<String, Integer> entry : (toDescribe).getAttributesAffection().entrySet()) {
                        equipText.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                        otherLines++;
                    }
                    statsEquippedWeapon.setText(equipText);
                    statsEquippedWeapon.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - (Gdx.graphics.getWidth() / 38.4f), Gdx.graphics.getHeight() * 0.194f - (otherLines * game.otherLinesFactor));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (weaponsInInventorySelectBox.getItems().get(0).equals("No weapons...")) {
            statsInventoryWeapon.setText("");
        } else {
            int otherLines = 0;
            StringBuilder equipText = new StringBuilder();
            Weapon toDescribe;
            try {
                toDescribe = Global.getWeapon(new ArrayList<>(inventory.getItems().entrySet().stream().filter((entry) -> entry.getKey() instanceof Weapon).collect(Collectors.toList())).get(weaponsInInventorySelectBox.getSelectedIndex()).getKey().getName());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            equipText.append(toDescribe.getName()).append("\n");
            equipText.append("Type: ").append(toDescribe.getWeaponType()).append("\n");
            equipText.append("Stats:\n");
            for (Map.Entry<String, Integer> entry : (toDescribe).getAttributesAffection().entrySet()) {
                equipText.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                otherLines++;
            }
            statsInventoryWeapon.setText(equipText);
            statsInventoryWeapon.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 + (Gdx.graphics.getWidth() / 8.73f), Gdx.graphics.getHeight() * 0.194f - (otherLines * game.otherLinesFactor));
        }
    }
}
