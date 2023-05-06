package com.ararita.game;

import com.ararita.game.items.Inventory;
import com.ararita.game.items.Item;
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

public class ShopScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    Inventory inventory;

    Label title;
    Label.LabelStyle titleStyle;
    TextButton exitButton;

    Array<String> allItemsArray;
    SelectBox<String> buySelectBox;

    Texture backgroundTexture;
    Sprite backgroundSprite;

    public ShopScreen(final Ararita game) {
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
            allItemsArray = new Array<>();
            Global.getAllItems().stream().sorted((o1, o2) -> {
                if(o1.getPrice() == o2.getPrice()) {
                    return 0;
                }
                else if (o1.getPrice() > o2.getPrice()) {
                    return 1;
                }
                return -1;
            }).forEach((item) -> allItemsArray.add(item.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
            Setting the background texture.
         */

        backgroundTexture = new Texture(Gdx.files.local("assets/Backgrounds/city.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize((int) (Gdx.graphics.getWidth() * 1.1), (int) (Gdx.graphics.getHeight() * 1.1));

        /*
            Setting the title.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("SHOP", titleStyle);
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
            Adding the Select Box for the items to buy and its listener.
         */

        buySelectBox = new SelectBox<>(game.selectBoxStyle);
        buySelectBox.setItems(allItemsArray);
        buySelectBox.setWidth(300);
        buySelectBox.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4, Gdx.graphics.getHeight() - 350);
        buySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO
            }
        });


        /*
            Adding all actors.
         */

        stage.addActor(title);
        stage.addActor(exitButton);
        stage.addActor(buySelectBox);
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
    }
}
