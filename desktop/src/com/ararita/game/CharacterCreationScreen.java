package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import org.json.JSONObject;

import java.io.IOException;

public class CharacterCreationScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    TextField charNameField;
    TextField.TextFieldStyle textFieldStyle;
    SelectBox<String> charClassSelectBox;
    SelectBox.SelectBoxStyle selectBoxStyle;

    TextButton confirmButton;
    TextButton exitButton;

    Dialog classCreationDialog;

    boolean newPlayer;

    public CharacterCreationScreen(final Ararita game, boolean newPlayer) throws IOException {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.newPlayer = newPlayer;

        this.skin = new Skin(Gdx.files.internal("Pixthulhu/pixthulhu-ui.json"));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        classCreationDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                if (confirm.equals("true")) {
                    game.setScreen(new ClassCreationScreen(game));
                }
                classCreationDialog.setVisible(false);
            }
        };
        classCreationDialog.setResizable(false);
        classCreationDialog.text(" Do you want to open the Class Creation Menu?\n", game.labelStyle);
        classCreationDialog.button("Yes", true, game.textButtonStyle);
        classCreationDialog.button("No", false, game.textButtonStyle);
        classCreationDialog.setPosition(0, 0);

        JSONObject jsonGlobal = Global.getJSON(Global.globalSets);

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("CHARACTER CREATION", titleStyle);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() - 150);

        textFieldStyle = skin.get("default", TextField.TextFieldStyle.class);
        textFieldStyle.font = game.normalFont;
        charNameField = new TextField("Character Name", textFieldStyle);
        charNameField.setWidth(400);
        charNameField.setPosition((Gdx.graphics.getWidth() - charNameField.getWidth()) / 2, Gdx.graphics.getHeight() - 350);

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
        charClassSelectBox.setPosition((Gdx.graphics.getWidth() - charClassSelectBox.getWidth()) / 2, Gdx.graphics.getHeight() - 500);
        charClassSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (charClassSelectBox.getSelected().equals("Create new...")) {
                    classCreationDialog.setVisible(true);
                    classCreationDialog.show(stage);
                }
            }
        });

        confirmButton = new TextButton("Confirm", game.textButtonStyle);
        confirmButton.setPosition((Gdx.graphics.getWidth() - (confirmButton.getWidth())) / 2, Gdx.graphics.getHeight() - 850);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                
            }
        });
        exitButton = new TextButton("Exit", game.textButtonStyle);
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() - 1000);

        if (newPlayer) {
            exitButton.setVisible(false);
        }

        stage.addActor(title);
        stage.addActor(charNameField);
        stage.addActor(charClassSelectBox);
        stage.addActor(confirmButton);
        stage.addActor(exitButton);
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

        stage.draw();

        game.batch.end();

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
    }
}
