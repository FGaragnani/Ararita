package com.ararita.game;

import com.ararita.game.items.ConsumableItem;
import com.ararita.game.items.Inventory;
import com.ararita.game.items.Item;
import com.ararita.game.items.Weapon;
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
import java.util.Map;

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

    Label stats;
    Label costLabel;

    Texture coinTexture;
    Image coinImage;
    Label currentMoney;
    Image currentMoneyImage;

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
            Global.getAllItems().stream().filter((item) -> item.getPrice() < 1000).sorted((o1, o2) -> {
                if (o1.getPrice() == o2.getPrice()) {
                    return 0;
                } else if (o1.getPrice() > o2.getPrice()) {
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
        buySelectBox.setWidth(400);
        buySelectBox.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - 20, Gdx.graphics.getHeight() - 350);
        buySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCost();
                updateStats();
            }
        });

        /*
            Setting the stats Label.
         */

        stats = new Label("", game.labelStyle);
        stats.setFontScale(3f, 4f);
        stats.setColor(Color.BLACK);
        stats.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - 20, Gdx.graphics.getHeight() - 430);

        /*
            Creating the cost label.
         */

        costLabel = new Label("", stats.getStyle());
        costLabel.setFontScale(2.8f, 3.8f);
        costLabel.setColor(Color.BLACK);
        costLabel.setPosition((Gdx.graphics.getWidth() - (buySelectBox.getWidth())) / 2 - 400, Gdx.graphics.getHeight() - 270);

        /*
            Adding the coin icon.
         */

        coinTexture = new Texture(Gdx.files.local("Icons/coin.png"));
        coinImage = new Image();
        coinImage.setDrawable(new TextureRegionDrawable(coinTexture));
        coinImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        coinImage.setPosition(((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 3) - 10, Gdx.graphics.getHeight() - 300);


        /*
            Adding all actors.
         */

        stage.addActor(title);
        stage.addActor(exitButton);
        stage.addActor(buySelectBox);
        stage.addActor(coinImage);
        stage.addActor(costLabel);
        stage.addActor(stats);

        /*
            Setting the default values.
         */

        updateCost();
        updateStats();
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
        coinTexture.dispose();
    }

    /**
     * The cost label is updated.
     */
    public void updateCost() {
        try {
            costLabel.setText("Price: " + Global.getItem(buySelectBox.getSelected()).getPrice());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The stats label is updated.
     */
    public void updateStats(){
        int otherLines = 0;
        StringBuilder text = new StringBuilder();
        try {
            Item toDescribe = Global.getItem(buySelectBox.getSelected());
            text.append("Item type: ").append(toDescribe.getType()).append("\n");
            if(toDescribe instanceof Weapon){
                text.append("Weapon Type: ").append(((Weapon) toDescribe).getWeaponType()).append("\n");
                text.append("Stats:\n");
                otherLines += 2;
                for(Map.Entry<String, Integer> entry : ((Weapon) toDescribe).getAttributesAffection().entrySet()){
                    text.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    otherLines++;
                }
            }
            text.append("Description:\n ").append(toDescribe.getDescription());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stats.setText(text);
        stats.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - 20,
                Gdx.graphics.getHeight() - 430 - (17 * otherLines));
    }

}
