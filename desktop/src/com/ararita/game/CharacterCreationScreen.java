package com.ararita.game;

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
import com.sun.tools.javac.util.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.function.BiConsumer;

public class CharacterCreationScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    Label stats;

    TextField charNameField;
    TextField.TextFieldStyle textFieldStyle;
    SelectBox<String> charClassSelectBox;
    SelectBox.SelectBoxStyle selectBoxStyle;

    TextButton confirmButton;
    TextButton exitButton;

    Dialog classCreationDialog;
    Dialog nameExistsDialog;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    boolean newPlayer;

    public CharacterCreationScreen(final Ararita game, boolean newPlayer) throws IOException {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.newPlayer = newPlayer;

        this.skin = game.skin;
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        backgroundTexture = new Texture(Gdx.files.local("assets/background.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        classCreationDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                if (confirm.equals("true")) {
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

        JSONObject jsonGlobal = Global.getJSON(Global.globalSets);

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("CHARACTER CREATION", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() - 150);

        textFieldStyle = skin.get("default", TextField.TextFieldStyle.class);
        textFieldStyle.font = game.normalFont;
        charNameField = new TextField("Character Name", textFieldStyle);
        charNameField.setWidth(400);
        charNameField.setPosition((Gdx.graphics.getWidth() - charNameField.getWidth()) / 2 - 300, Gdx.graphics.getHeight() - 350);

        selectBoxStyle = skin.get("default", SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = game.normalFont;
        selectBoxStyle.listStyle.font = game.normalFont;
        selectBoxStyle.listStyle.selection.setTopHeight(10);
        charClassSelectBox = new SelectBox<>(selectBoxStyle);
        Array<String> classArray = new Array<>();
        jsonGlobal.getJSONArray("classNamesSet").toList().forEach((str) -> classArray.add(str.toString()));
        classArray.add("Create new...");
        charClassSelectBox.setItems(classArray);
        charClassSelectBox.setWidth(400);
        charClassSelectBox.setPosition((Gdx.graphics.getWidth() - charClassSelectBox.getWidth()) / 2 - 300, Gdx.graphics.getHeight() - 500);
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

        confirmButton = new TextButton("Confirm", game.textButtonStyle);
        confirmButton.setPosition((Gdx.graphics.getWidth() - (confirmButton.getWidth())) / 2, Gdx.graphics.getHeight() - 850);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    if (Global.isPresentInJSONList(Global.globalSets, charNameField.getText(), "party") || Global.isPresentInJSONList(Global.globalSets, charNameField.getText(), "otherCharacters")) {
                        nameExistsDialog.show(stage);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        exitButton = new TextButton("Exit", game.textButtonStyle);
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() - 1000);

        if (newPlayer) {
            exitButton.setVisible(false);
        }

        stats = new Label("", game.labelStyle);
        stats.setFontScale(2.7f, 3.65f);
        stats.setColor(Color.BLACK);
        stats.setPosition(1100, 600);
        statUpdate();

        stage.addActor(title);
        stage.addActor(charNameField);
        stage.addActor(charClassSelectBox);
        stage.addActor(confirmButton);
        stage.addActor(exitButton);
        stage.addActor(stats);
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
    }

    public void statUpdate(){
        StringBuilder text = new StringBuilder();
        try {
            JSONObject jsonClass = Global.getJSON(Global.getJSONFilePath(Global.classSets, charClassSelectBox.getSelected()));
            text.append("Strength: ").append(jsonClass.getInt("strength")).append("\n");
            text.append("Intelligence: ").append(jsonClass.getInt("intelligence")).append("\n");
            text.append("Agility: ").append(jsonClass.getInt("agility")).append("\n");
            text.append("Vigor: ").append(jsonClass.getInt("vigor")).append("\n");
            text.append("Spirit: ").append(jsonClass.getInt("spirit")).append("\n");
            text.append("Arcane: ").append(jsonClass.getInt("arcane")).append("\n");
            text.append("\n").append("Proficiencies: \n");
            jsonClass.getJSONObject("proficiencies").toMap().forEach(new BiConsumer<String, Object>() {
                @Override
                public void accept(String s, Object o) {
                    text.append("\t").append(s).append(":");
                    if((int) o >= 0){
                        text.append(" +".repeat((int) o));
                    } else {
                        text.append(" -".repeat((int) o));
                    }
                    text.append("\n");
                }
            });
            text.append("Learnable spells: ");
            text.append(jsonClass.getJSONArray("spellTypes").toList());
            text.append("\n");
            stats.setText(text.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
