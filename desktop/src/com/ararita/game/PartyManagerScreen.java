package com.ararita.game;

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
import org.json.JSONObject;

import java.io.IOException;

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

    Image spriteImageParty;
    Image spriteImageReserve;
    Animation<TextureRegion> partyAnimation;
    Animation<TextureRegion> reserveAnimation;
    Texture charSheet;
    TextureRegion[][] tmpParty;
    TextureRegion[][] tmpReserve;
    TextureRegion currentFrame;
    float statePartyTime;
    float stateReserveTime;

    TextButton exitButton;

    Dialog oneCharacterInParty;
    Dialog noCharactersInReserve;
    Dialog maxPartyCharacters;

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

        charSheet = new Texture(Gdx.files.internal("General/msprites.png"));
        tmpParty = TextureRegion.split(charSheet, charSheet.getWidth() / (game.spriteFrameCols * 6), charSheet.getHeight());
        tmpReserve = TextureRegion.split(charSheet, charSheet.getWidth() / (game.spriteFrameCols * 6), charSheet.getHeight());
        spriteImageParty = new Image();
        spriteImageParty.setScale(7);
        spriteImageReserve = new Image();
        spriteImageReserve.setPosition(Gdx.graphics.getWidth() - 120, Gdx.graphics.getHeight() - 200);
        spriteImageReserve.setScale(7);

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
        title = new Label("PARTY MANAGER", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() - 150);

        /*
            Creating the Exit Button.
         */

        exitButton = new TextButton("Exit", game.textButtonStyle);
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() - 1100);
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
        partyCharactersSelectBox.setWidth(500);
        partyCharactersSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - 40, Gdx.graphics.getHeight() - 380);
        partyCharactersSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCharacters();
                updatePartyStats();
            }
        });
        partyLabel = new Label("Party:", skin.get("default", Label.LabelStyle.class));
        partyLabel.setFontScale(2.8f, 3.8f);
        partyLabel.setColor(Color.BLACK);
        partyLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - 140, Gdx.graphics.getHeight() - 400);
        spriteImageParty.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() - 310);

        /*
            Adding the other characters Select Box and its label.
         */

        otherCharactersSelectBox = new SelectBox<>(game.selectBoxStyle);
        otherCharactersSelectBox.setWidth(500);
        otherCharactersSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6 + 80, Gdx.graphics.getHeight() - 380);
        otherCharactersLabel = new Label("Reserve:", skin.get("default", Label.LabelStyle.class));
        otherCharactersLabel.setFontScale(2.8f, 3.8f);
        otherCharactersLabel.setColor(Color.BLACK);
        otherCharactersLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6 - 50, Gdx.graphics.getHeight() - 400);
        spriteImageReserve.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6 + 110, Gdx.graphics.getHeight() - 310);

        /*
            Creating the two transfer buttons.
         */

        partyToReserveButton = new TextButton("Party -> Reserve", skin.get("default", TextButton.TextButtonStyle.class));
        partyToReserveButton.getLabel().setStyle(partyLabel.getStyle());
        partyToReserveButton.getLabel().setFontScale(2.2f, 3.2f);
        partyToReserveButton.setWidth(250);
        partyToReserveButton.setPosition((Gdx.graphics.getWidth() - partyToReserveButton.getWidth()) / 2.0f, Gdx.graphics.getHeight() - 325);
        reserveToPartyButton = new TextButton("Party <- Reserve", skin.get("default", TextButton.TextButtonStyle.class));
        reserveToPartyButton.getLabel().setStyle(partyLabel.getStyle());
        reserveToPartyButton.getLabel().setFontScale(2.2f, 3.2f);
        reserveToPartyButton.setWidth(250);
        reserveToPartyButton.setPosition((Gdx.graphics.getWidth() - partyToReserveButton.getWidth()) / 2.0f, Gdx.graphics.getHeight() - 375 - partyToReserveButton.getHeight());

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
                }
            }
        });

        /*
            Setting the two stats labels.
         */

        partyStats = new Label("", game.labelStyle);
        partyStats.setFontScale(2.5f, 3.4f);
        partyStats.setColor(Color.BLACK);
        reserveStats = new Label("", game.labelStyle);
        reserveStats.setFontScale(2.5f, 3.4f);
        reserveStats.setColor(Color.BLACK);

        /*
            Setting the inventory select box.
         */

        weaponsInInventorySelectBox = new SelectBox<>(game.selectBoxStyle);
        weaponsInInventorySelectBox.setWidth(400);
        weaponsInInventorySelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() - 800);
        equipButton = new TextButton("Equip", skin.get("default", TextButton.TextButtonStyle.class));
        equipButton.getLabel().setStyle(partyLabel.getStyle());
        equipButton.getLabel().setFontScale(2.2f, 3f);
        equipButton.setWidth(140);
        equipButton.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() - 700);
        inventoryLabel = new Label("Inventory:", skin.get("default", Label.LabelStyle.class));
        inventoryLabel.setFontScale(2.6f, 3.7f);
        inventoryLabel.setColor(Color.BLACK);
        inventoryLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - 150, Gdx.graphics.getHeight() - 565);

        /*
            Setting the weapons SelectBox.
         */

        weaponsEquippedSelectBox = new SelectBox<>(game.selectBoxStyle);
        weaponsEquippedSelectBox.setWidth(400);
        weaponsEquippedSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() - 550);
        unEquipButton = new TextButton("Unequip", skin.get("default", TextButton.TextButtonStyle.class));
        unEquipButton.getLabel().setStyle(partyLabel.getStyle());
        unEquipButton.getLabel().setFontScale(2.2f, 3f);
        unEquipButton.setWidth(140);
        unEquipButton.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 + 255, Gdx.graphics.getHeight() - 700);

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

        /*
            Setting the initial values.
         */

        updateCharacters();
        updatePartyStats();
        updateWeapons();
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

    public void changeSprite(String spriteName, boolean inParty) {
        int listPosition = game.spriteNames.indexOf(spriteName) * 3;
        TextureRegion[] walkFrames = new TextureRegion[game.spriteFrameCols];
        int index = 0;
        if (inParty) {
            for (int i = listPosition; i < game.spriteFrameCols + listPosition; i++) {
                walkFrames[index++] = tmpParty[0][i];
            }
            partyAnimation = new Animation<>(0.200f, walkFrames);
            statePartyTime = 0f;
        } else {
            for (int i = listPosition; i < game.spriteFrameCols + listPosition; i++) {
                walkFrames[index++] = tmpReserve[0][i];
            }
            reserveAnimation = new Animation<>(0.200f, walkFrames);
            stateReserveTime = 0f;
        }
    }

    public void updatePartyStats() {
        int otherLines = 0;
        StringBuilder partyText = new StringBuilder();
        try {
            int partyIndex = partyCharactersSelectBox.getSelectedIndex();
            JSONObject jsonChar = Global.getJSON(Global.getJSONFilePath(Global.characterSets, Global.getParty().get(partyIndex).getName()));
            partyText.append("LVL: ").append(jsonChar.getInt("level")).append("\n");
            partyText.append("STR: ").append(jsonChar.getInt("strength")).append("\n");
            partyText.append("INT: ").append(jsonChar.getInt("intelligence")).append("\n");
            partyText.append("AGI: ").append(jsonChar.getInt("agility")).append("\n");
            partyText.append("VIG: ").append(jsonChar.getInt("vigor")).append("\n");
            partyText.append("SPI: ").append(jsonChar.getInt("spirit")).append("\n");
            partyText.append("ARC: ").append(jsonChar.getInt("arcane")).append("\n");
            partyText.append("Proficiencies: \n");
            jsonChar.getJSONObject("proficiencies").toMap().forEach((s, o) -> {
                partyText.append(" ").append(s).append(":");
                if ((int) o >= 0) {
                    partyText.append(" +".repeat((int) o));
                } else {
                    partyText.append(" -".repeat((int) o));
                }
                partyText.append("\n");
            });
            otherLines += jsonChar.getJSONObject("proficiencies").toMap().size();
            partyText.append("Learnable spell types:\n");
            jsonChar.getJSONArray("spellTypes").toList().forEach((str) -> partyText.append(" - ").append(str.toString()).append("\n"));
            otherLines += jsonChar.getJSONArray("spellTypes").toList().size();
            partyStats.setText(partyText.toString());
            partyStats.setPosition(660, Gdx.graphics.getHeight() - 580 - (18 * (otherLines - 1)));
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
            JSONObject jsonChar = Global.getJSON(Global.getJSONFilePath(Global.characterSets, Global.getOtherCharacters().get(reserveIndex).getName()));
            reserveText.append("LVL: ").append(jsonChar.getInt("level")).append("\n");
            reserveText.append("STR: ").append(jsonChar.getInt("strength")).append("\n");
            reserveText.append("INT: ").append(jsonChar.getInt("intelligence")).append("\n");
            reserveText.append("AGI: ").append(jsonChar.getInt("agility")).append("\n");
            reserveText.append("VIG: ").append(jsonChar.getInt("vigor")).append("\n");
            reserveText.append("SPI: ").append(jsonChar.getInt("spirit")).append("\n");
            reserveText.append("ARC: ").append(jsonChar.getInt("arcane")).append("\n");
            reserveText.append("Proficiencies: \n");
            jsonChar.getJSONObject("proficiencies").toMap().forEach((s, o) -> {
                reserveText.append(" ").append(s).append(":");
                if ((int) o >= 0) {
                    reserveText.append(" +".repeat((int) o));
                } else {
                    reserveText.append(" -".repeat((int) o));
                }
                reserveText.append("\n");
            });
            otherLines += jsonChar.getJSONObject("proficiencies").toMap().size();
            reserveText.append("Learnable spell types:\n");
            jsonChar.getJSONArray("spellTypes").toList().forEach((str) -> reserveText.append(" - ").append(str.toString()).append("\n"));
            otherLines += jsonChar.getJSONArray("spellTypes").toList().size();
            reserveStats.setText(reserveText.toString());
            reserveStats.setPosition(1600, Gdx.graphics.getHeight() - 580 - (18 * (otherLines - 1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
}
