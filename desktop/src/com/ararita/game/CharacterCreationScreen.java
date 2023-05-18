package com.ararita.game;

import com.ararita.game.battlers.PC;
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
import java.nio.file.Path;

public class CharacterCreationScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    Label stats;
    float statX;
    float statY;

    TextField charNameField;
    SelectBox<String> charClassSelectBox;
    SelectBox<String> charImageSelectBox;

    TextButton confirmButton;
    TextButton exitButton;

    Dialog classCreationDialog;
    Dialog nameExistsDialog;
    Dialog nameLengthDialog;

    Image spriteImage;
    Animation<TextureRegion> charAnimation;
    Texture charSheet;
    TextureRegion[][] tmp;
    float stateTime;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    boolean newPlayer;

    public CharacterCreationScreen(final Ararita game, boolean newPlayer) throws IOException {
        /*
            First initializations.
         */

        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.newPlayer = newPlayer;

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundPaper));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Creating the Image and the texture.
         */

        charSheet = new Texture(Gdx.files.internal(game.spritesPath));
        tmp = TextureRegion.split(charSheet, charSheet.getWidth() / (game.spriteFrameCols * 6), charSheet.getHeight());
        spriteImage = new Image();
        spriteImage.setPosition((Gdx.graphics.getWidth() - spriteImage.getWidth()) * 3 / 4, (Gdx.graphics.getHeight() - spriteImage.getHeight()) / 2);
        spriteImage.setScale(Gdx.graphics.getWidth() / 174.5f);

        /*
            Creating the three dialogs.
            One will pop up before creating a new class.
            The other two will pop up for invalid inputs.
         */

        classCreationDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                if (confirm.equals(true)) {
                    game.setScreen(new ClassCreationScreen(game));
                }
                hide();
            }
        };
        classCreationDialog.setResizable(false);
        classCreationDialog.text(" Do you want to open the Class Creation Menu?\n", game.labelStyle);
        classCreationDialog.button("Yes", true, game.textButtonStyle);
        classCreationDialog.button("No", false, game.textButtonStyle);
        classCreationDialog.setPosition(0, 0);

        nameExistsDialog = new Dialog("", skin) {

            public void result(Object confirm) {
                hide();
            }
        };
        nameExistsDialog.setResizable(false);
        nameExistsDialog.text(" The character's name given is already used by another character.\n Choose another!\n", game.labelStyle);
        nameExistsDialog.button("Ok!", true, game.textButtonStyle);
        nameExistsDialog.setPosition(0, 0);

        nameLengthDialog = new Dialog("", skin) {

            public void result(Object confirm) {
                hide();
            }
        };
        nameLengthDialog.setResizable(false);
        nameLengthDialog.text(" The name must be at least 1 and max 12 characters long.\n Choose another!\n", game.labelStyle);
        nameLengthDialog.button("Ok!", true, game.textButtonStyle);
        nameLengthDialog.setPosition(0, 0);

        /*
            Setting the title.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("CHARACTER CREATION", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() * 0.86f);

        /*
            Creating the TextField - for the character's name.
         */

        TextField.TextFieldStyle textFieldStyle = game.textFieldStyle;
        charNameField = new TextField("Character Name", textFieldStyle);
        charNameField.setWidth(game.width400);
        charNameField.setPosition((Gdx.graphics.getWidth() - charNameField.getWidth()) / 2, Gdx.graphics.getHeight() * 0.676f);

        /*
            Creating the SelectBox - for selecting the character's class.
            Creating its listener.
         */

        JSONObject jsonGlobal = Global.getJSON(Global.globalSets);

        charClassSelectBox = new SelectBox<>(game.selectBoxStyle);
        Array<String> classArray = new Array<>();
        jsonGlobal.getJSONArray("classNamesSet").toList().forEach((str) -> classArray.add(str.toString()));
        if (!newPlayer) {
            classArray.add("Create new...");
        }
        charClassSelectBox.setItems(classArray);
        charClassSelectBox.setWidth(game.width400);
        charClassSelectBox.setPosition((Gdx.graphics.getWidth() - charClassSelectBox.getWidth()) / 2, Gdx.graphics.getHeight() * 0.54f);

        charClassSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (charClassSelectBox.getSelected().equals("Create new...")) {
                    classCreationDialog.show(stage);
                } else {
                    statUpdate();
                }
            }
        });

        /*
            Creating the Image Select Box and its listener.
         */

        charImageSelectBox = new SelectBox<>(game.selectBoxStyle);
        Array<String> imageArray = new Array<>();
        game.spriteNames.forEach(imageArray::add);
        charImageSelectBox.setItems(imageArray);
        charImageSelectBox.setWidth(400);
        charImageSelectBox.setPosition((Gdx.graphics.getWidth() - charImageSelectBox.getWidth()) / 2, Gdx.graphics.getHeight() * 0.444f);
        charImageSelectBox.setSelected("Fighter");
        changeSprite(charImageSelectBox.getSelected());
        charImageSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeSprite(charImageSelectBox.getSelected());
            }
        });

        /*
            Creating the button for confirmation.
            Creating its Listener.
         */

        confirmButton = new TextButton("Confirm", game.textButtonStyle);
        confirmButton.setPosition((Gdx.graphics.getWidth() - (confirmButton.getWidth())) / 2, Gdx.graphics.getHeight() * 0.21f);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if (Global.isPresentInJSONList(Global.globalSets, charNameField.getText(), "party") || Global.isPresentInJSONList(Global.globalSets, charNameField.getText(), "otherCharacters")) {
                        nameExistsDialog.show(stage);
                    } else if (charNameField.getText().length() < 1 || charNameField.getText().length() > 12) {
                        nameLengthDialog.show(stage);
                    } else if (!charClassSelectBox.getSelected().equals("Create new...")) {
                        PC toAdd = new PC(charNameField.getText(), charClassSelectBox.getSelected(), false);
                        toAdd.setImage(charImageSelectBox.getSelected());
                        Global.addCharacter(toAdd);
                        if (newPlayer) {
                            game.newPlayer = false;
                            game.settingsUpdate();
                        }
                        dispose();
                        game.setScreen(new CityScreen(game));
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
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() * 0.074f);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                game.setScreen(new CityScreen(game));
            }
        });

        /*
            Creating the class Stats label.
         */

        stats = new Label("", game.labelStyle);
        stats.setFontScale(game.statScaleX, game.statScaleY);
        stats.setColor(Color.BLACK);
        statX = Gdx.graphics.getWidth() / 6.4f;
        statY = Gdx.graphics.getHeight() * 0.5f;
        stats.setPosition(statX, statY);
        statUpdate();

        stage.addActor(title);
        stage.addActor(charNameField);
        stage.addActor(charClassSelectBox);
        stage.addActor(charImageSelectBox);
        stage.addActor(confirmButton);
        if (!newPlayer) {
            stage.addActor(exitButton);
        }
        stage.addActor(stats);
        stage.addActor(spriteImage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stateTime += Gdx.graphics.getDeltaTime();

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        TextureRegion currentFrame = charAnimation.getKeyFrame(stateTime, true);
        spriteImage.setDrawable(new TextureRegionDrawable(currentFrame));
        spriteImage.setSize(currentFrame.getRegionWidth(), currentFrame.getRegionHeight());

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
        charSheet.dispose();
        backgroundTexture.dispose();
    }

    /**
     * Updates the stat Label text.
     * Getting the class name from the SelectBox, it updates the label with
     * the class' info.
     */
    public void statUpdate() {
        int otherLines = 0;
        StringBuilder text = new StringBuilder();
        try {
            JSONObject jsonClass = Global.getJSON(Path.of(Global.getJSONFile(Global.classSets, charClassSelectBox.getSelected()).path()));
            text.append("Strength: ").append(jsonClass.getInt("strength")).append("\n");
            text.append("Intelligence: ").append(jsonClass.getInt("intelligence")).append("\n");
            text.append("Agility: ").append(jsonClass.getInt("agility")).append("\n");
            text.append("Vigor: ").append(jsonClass.getInt("vigor")).append("\n");
            text.append("Spirit: ").append(jsonClass.getInt("spirit")).append("\n");
            text.append("Arcane: ").append(jsonClass.getInt("arcane")).append("\n");
            text.append("Proficiencies: \n");
            jsonClass.getJSONObject("proficiencies").toMap().forEach((s, o) -> {
                text.append("\t").append(s).append(":");
                text.append(" +".repeat((int) o));
                text.append("\n");
            });
            otherLines += jsonClass.getJSONObject("proficiencies").toMap().size();
            text.append("Learnable spell types:\n");
            jsonClass.getJSONArray("spellTypes").toList().forEach((str) -> text.append("\t- ").append(str.toString()).append("\n"));
            otherLines += jsonClass.getJSONArray("spellTypes").toList().size();
            stats.setText(text.toString());
            stats.setPosition(statX, statY - (game.otherLinesFactor * (otherLines - 1)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Modifies charAnimation according to the spriteName given.
     *
     * @param spriteName The name of the sprite (which is in game.spriteNames) to change the animation to.
     */
    public void changeSprite(String spriteName) {
        int listPosition = game.spriteNames.indexOf(spriteName) * 3;
        TextureRegion[] walkFrames = new TextureRegion[game.spriteFrameCols];
        int index = 0;
        for (int i = listPosition; i < game.spriteFrameCols + listPosition; i++) {
            walkFrames[index++] = tmp[0][i];
        }
        charAnimation = new Animation<>(0.200f, walkFrames);
        stateTime = 0f;
    }
}
