package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import java.io.IOException;

public class PartyManagerScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Label title;
    Label.LabelStyle titleStyle;

    SelectBox<String> partyCharactersSelectBox;
    Label partyLabel;

    SelectBox<String> otherCharactersSelectBox;
    Label otherCharactersLabel;

    TextButton exitButton;

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
        exitButton.setPosition((Gdx.graphics.getWidth() - (exitButton.getWidth())) / 2, Gdx.graphics.getHeight() - 1000);
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
        partyCharactersSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6, Gdx.graphics.getHeight() - 300);
        partyLabel = new Label("Party:", skin.get("default", Label.LabelStyle.class));
        partyLabel.setFontScale(2.8f, 3.8f);
        partyLabel.setColor(Color.BLACK);
        partyLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) / 6 - 100, Gdx.graphics.getHeight() - 320);

        /*
            Adding the other characters Select Box and its label.
         */

        otherCharactersSelectBox = new SelectBox<>(game.selectBoxStyle);
        otherCharactersSelectBox.setWidth(500);
        otherCharactersSelectBox.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6, Gdx.graphics.getHeight() - 300);
        otherCharactersLabel = new Label("Reserve:", skin.get("default", Label.LabelStyle.class));
        otherCharactersLabel.setFontScale(2.8f, 3.8f);
        otherCharactersLabel.setColor(Color.BLACK);
        otherCharactersLabel.setPosition((Gdx.graphics.getWidth() - partyCharactersSelectBox.getWidth()) * 5 / 6 - 140, Gdx.graphics.getHeight() - 320);


        /*
            Adding all stage actors.
         */

        stage.addActor(exitButton);
        stage.addActor(title);
        stage.addActor(partyCharactersSelectBox);
        stage.addActor(partyLabel);
        stage.addActor(otherCharactersSelectBox);
        stage.addActor(otherCharactersLabel);

        /*
            Setting the initial values.
         */

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
        if (!otherCharacters.isEmpty()) {
            otherCharactersSelectBox.setItems(otherCharacters);
        } else {
            otherCharactersSelectBox.setItems("No characters...");
        }
    }
}
