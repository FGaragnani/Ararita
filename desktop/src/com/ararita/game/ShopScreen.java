package com.ararita.game;

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
    TextButton buyButton;
    TextField numberBuyTextField;
    Label toBuyLabel;

    Label stats;
    Label costLabel;

    SelectBox<String> sellSelectBox;
    Label sellLabel;
    Image coinImageSell;
    Label sellStats;
    TextField numberSellTextField;
    TextButton sellButton;

    Label toSellLabel;

    Texture coinTexture;
    Image coinImage;
    Label currentMoney;
    Image currentMoneyImage;

    Dialog moneyDialog;
    Dialog noInventorySpaceDialog;
    Dialog noItemDialog;

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
            Global.getAllItems().stream().filter((entry) -> entry.getPrice() < 1000).sorted((o1, o2) -> {
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

        backgroundTexture = new Texture(Gdx.files.local(game.backgroundPaper));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(1920, 1080);

        /*
            Setting the title.
         */

        titleStyle = skin.get("default", Label.LabelStyle.class);
        titleStyle.font = game.titleFont;
        title = new Label("SHOP", titleStyle);
        title.setColor(Color.BLACK);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2, Gdx.graphics.getHeight() * 0.86f);

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
            Adding the Select Box for the items to buy and its listener.
         */

        buySelectBox = new SelectBox<>(game.selectBoxStyle);
        buySelectBox.setItems(allItemsArray);
        buySelectBox.setWidth(game.width400);
        buySelectBox.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.676f);
        buySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCost();
                updateStats();
                updateBuy();
            }
        });

        /*
            Setting the stats Label.
         */

        stats = new Label("", game.labelStyle);
        stats.setFontScale(game.descScaleX, game.descScaleY);
        stats.setColor(Color.BLACK);
        stats.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.6f);

        /*
            Creating the cost label.
         */

        costLabel = new Label("", stats.getStyle());
        costLabel.setFontScale(game.statScaleX, game.statScaleY);
        costLabel.setColor(Color.BLACK);
        costLabel.setPosition((Gdx.graphics.getWidth() - (buySelectBox.getWidth())) / 2 - (0.2f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.75f);

        /*
            Adding the coin icon.
         */

        coinTexture = new Texture(game.coinPath);
        coinImage = new Image();
        coinImage.setDrawable(new TextureRegionDrawable(coinTexture));
        coinImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        coinImage.setPosition(((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 3) + (Gdx.graphics.getWidth() / 45f), Gdx.graphics.getHeight() * 0.722f);

        /*
            Setting the number to buy TextField, its label and its listener.
         */

        numberBuyTextField = new TextField("1", game.textFieldStyle);
        numberBuyTextField.setWidth(game.width200 / 2);
        numberBuyTextField.setTextFieldFilter(new DigitFilter());
        numberBuyTextField.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (0.08f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.583f);
        toBuyLabel = new Label("", costLabel.getStyle());
        toBuyLabel.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (0.156f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.565f);
        toBuyLabel.setColor(Color.BLACK);
        numberBuyTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCost();
            }
        });

        /*
            Setting the Buy Button and its listener.
         */

        buyButton = new TextButton("Buy", skin.get("default", TextButton.TextButtonStyle.class));
        buyButton.getLabel().setStyle(stats.getStyle());
        buyButton.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (0.08f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.648f);
        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    int howMany = 0;
                    try {
                        howMany = Integer.parseInt(numberBuyTextField.getText());
                    } catch (NumberFormatException ignored) {
                    }
                    if (howMany == 0) {
                        return;
                    }
                    Item toBuy = Global.getItem(buySelectBox.getSelected());
                    if (inventory.canBuy(toBuy, howMany)) {
                        inventory.buy(toBuy, howMany);
                        updateBuy();
                        updateSellItems();
                        updateSellLabels();
                    } else if (howMany + inventory.inventorySize() > inventory.MAX_INVENTORY_SPACE) {
                        noInventorySpaceDialog.show(stage);
                    } else {
                        moneyDialog.show(stage);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        /*
            Adding the current money label and image.
         */

        currentMoney = new Label("Money: " + inventory.getMoney(), stats.getStyle());
        currentMoney.setColor(Color.BLACK);
        currentMoney.setPosition(0.755f * Gdx.graphics.getWidth(), 0.88f * Gdx.graphics.getHeight());
        currentMoneyImage = new Image(new TextureRegionDrawable(coinTexture));
        currentMoneyImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        currentMoneyImage.setPosition((0.81f * Gdx.graphics.getWidth()) + (currentMoney.getText().length() * (Gdx.graphics.getWidth() / 192f)), 0.866f * Gdx.graphics.getHeight());

        /*
            Creating the Sell Select Box and its listener.
         */

        sellSelectBox = new SelectBox<>(game.selectBoxStyle);
        sellSelectBox.setWidth(game.width400);
        sellSelectBox.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) * 3 / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.676f);
        sellSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSellLabels();
            }
        });

        /*
            Creating the sell label and its coin image.
         */

        sellLabel = new Label("", stats.getStyle());
        sellLabel.setFontScale(game.statScaleX, game.statScaleY);
        sellLabel.setColor(Color.BLACK);
        sellLabel.setPosition((Gdx.graphics.getWidth() - (buySelectBox.getWidth())) / 2 + (0.1875f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 3 / 4f);
        coinImageSell = new Image();
        coinImageSell.setDrawable(new TextureRegionDrawable(coinTexture));
        coinImageSell.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        coinImageSell.setPosition(((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 3) + (0.41f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.722f);

        /*
            Setting the sell stats Label.
         */

        sellStats = new Label("", game.labelStyle);
        sellStats.setFontScale(game.descScaleX, game.descScaleY);
        sellStats.setColor(Color.BLACK);

        /*
            Setting the "how many" and "you have" label for selling, and the sell text field.
         */

        toSellLabel = new Label("", game.labelStyle);
        toSellLabel.setColor(Color.BLACK);
        numberSellTextField = new TextField("1", game.textFieldStyle);
        numberSellTextField.setWidth(game.width200 / 2);
        numberSellTextField.setTextFieldFilter(new DigitFilter());
        numberSellTextField.setPosition(Gdx.graphics.getWidth() * 0.88f, Gdx.graphics.getHeight() * 0.68f);
        numberSellTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSellLabels();
            }
        });

        /*
            Setting the Sell Button.
         */

        sellButton = new TextButton("Sell", skin.get("default", TextButton.TextButtonStyle.class));
        sellButton.getLabel().setStyle(stats.getStyle());
        sellButton.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) * 3 / 4 - (0.08f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.648f);
        sellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (sellSelectBox.getSelected().equals("No items...")) {
                    noItemDialog.show(stage);
                    return;
                }
                int toSell = 0;
                Item itemToSell;
                try {
                    itemToSell = Global.getItem(sellSelectBox.getSelected());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    toSell = Math.min(Integer.parseInt(numberSellTextField.getText()), inventory.getItems().get(itemToSell));
                } catch (NumberFormatException ignored) {
                }
                if (toSell != 0) {
                    try {
                        inventory.sell(itemToSell, toSell);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    updateSellItems();
                    updateSellLabels();
                    updateBuy();
                }
            }
        });

        /*
            Creating all the dialogs.
         */

        moneyDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        moneyDialog.setResizable(false);
        moneyDialog.text(" You don't have enough money\n to buy the desired number of items\n", game.labelStyle);
        moneyDialog.button("Ok!", true, game.textButtonStyle);
        moneyDialog.setPosition(0, 0);

        noInventorySpaceDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        noInventorySpaceDialog.setResizable(false);
        noInventorySpaceDialog.text(" You don't have enough space\n in your inventory!\n", game.labelStyle);
        noInventorySpaceDialog.button("Ok!", true, game.textButtonStyle);
        noInventorySpaceDialog.setPosition(0, 0);

        noItemDialog = new Dialog("", skin) {
            public void result(Object confirm) {
                hide();
            }
        };
        noItemDialog.setResizable(false);
        noItemDialog.text(" You don't have items\n in your inventory to sell!\n", game.labelStyle);
        noItemDialog.button("Ok!", true, game.textButtonStyle);
        noItemDialog.setPosition(0, 0);

        /*
            Adding all actors.
         */

        stage.addActor(title);
        stage.addActor(exitButton);
        stage.addActor(buySelectBox);
        stage.addActor(coinImage);
        stage.addActor(costLabel);
        stage.addActor(stats);
        stage.addActor(currentMoney);
        stage.addActor(currentMoneyImage);
        stage.addActor(buyButton);
        stage.addActor(numberBuyTextField);
        stage.addActor(toBuyLabel);
        stage.addActor(sellSelectBox);
        stage.addActor(sellLabel);
        stage.addActor(coinImageSell);
        stage.addActor(sellStats);
        stage.addActor(toSellLabel);
        stage.addActor(numberSellTextField);
        stage.addActor(sellButton);

        /*
            Setting the default values.
         */

        updateSellItems();
        updateSellLabels();
        updateCost();
        updateStats();
        updateBuy();
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
        skin.dispose();
    }

    /**
     * The cost label is updated.
     */
    public void updateCost() {
        int howMany;
        try {
            howMany = Integer.parseInt(numberBuyTextField.getText());
        } catch (NumberFormatException e) {
            howMany = 0;
        }
        try {
            costLabel.setText("Price: " + (Global.getItem(buySelectBox.getSelected()).getPrice() * howMany));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The stats label is updated.
     */
    public void updateStats() {
        int otherLines = 0;
        StringBuilder text = new StringBuilder();
        try {
            Item toDescribe = Global.getItem(buySelectBox.getSelected());
            text.append("Item type: ").append(toDescribe.getType()).append("\n");
            if (toDescribe instanceof Weapon) {
                text.append("Weapon Type: ").append(((Weapon) toDescribe).getWeaponType()).append("\n");
                text.append("Stats:\n");
                otherLines += 2;
                for (Map.Entry<String, Integer> entry : ((Weapon) toDescribe).getAttributesAffection().entrySet()) {
                    text.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    otherLines++;
                }
            }
            text.append("Description:\n ").append(toDescribe.getDescription());
            otherLines += toDescribe.getDescription().codePoints().filter((ch) -> (ch == '\n')).count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stats.setText(text);
        stats.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.58f - (game.otherLinesFactor * otherLines));
    }

    /**
     * Both the How Many label and the current money label are updated.
     */
    public void updateBuy() {
        currentMoney.setText("Money: " + inventory.getMoney());
        try {
            Item toBuy = Global.getItem(buySelectBox.getSelected());
            toBuyLabel.setText("How many:\n\n\nYou have: " + (inventory.getItems().containsKey(toBuy) ? inventory.getItems().get(toBuy) : "0"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The sell Select Box items are updated.
     */
    public void updateSellItems() {
        if (!inventory.getItems().isEmpty()) {
            Array<String> itemsInInventory = new Array<>();
            inventory.getItems().forEach((key, value) -> itemsInInventory.add(key.getName()));
            sellSelectBox.setItems(itemsInInventory);
        } else {
            sellSelectBox.setItems("No items...");
        }
    }

    /**
     * Updating the sell Label, the coin image visibility and the sell stats label.
     */
    public void updateSellLabels() {
        int howMany = 0;
        if (!sellSelectBox.getSelected().equals("No items...")) {
            try {
                howMany = Math.min(Integer.parseInt(numberSellTextField.getText()), inventory.getItems().get(Global.getItem(sellSelectBox.getSelected())));
            } catch (NumberFormatException ignored) {
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                sellLabel.setText("Gain: " + (int) (Global.getItem(sellSelectBox.getSelected()).getPrice() * howMany * inventory.RESELL_MULTIPLIER));
                coinImageSell.setVisible(true);
                int otherLines = 0;
                StringBuilder text = new StringBuilder();
                Item toDescribe = Global.getItem(sellSelectBox.getSelected());
                text.append("Item type: ").append(toDescribe.getType()).append("\n");
                if (toDescribe instanceof Weapon) {
                    text.append("Weapon Type: ").append(((Weapon) toDescribe).getWeaponType()).append("\n");
                    text.append("Stats:\n");
                    otherLines += 2;
                    for (Map.Entry<String, Integer> entry : ((Weapon) toDescribe).getAttributesAffection().entrySet()) {
                        text.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                        otherLines++;
                    }
                }
                text.append("Description:\n ").append(toDescribe.getDescription());
                otherLines += toDescribe.getDescription().codePoints().filter((ch) -> (ch == '\n')).count();
                sellStats.setText(text);
                sellStats.setPosition((Gdx.graphics.getWidth() - buySelectBox.getWidth()) * 3 / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.58f - (game.otherLinesFactor * otherLines));
                toSellLabel.setText("How many:\n\n\nYou have: " + (inventory.getItems().get(toDescribe)));
                toSellLabel.setPosition((Gdx.graphics.getWidth() - sellSelectBox.getWidth()) * 3 / 4 + (0.21f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.66f);
                coinImageSell.setVisible(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            sellLabel.setText("");
            coinImageSell.setVisible(false);
            sellStats.setText("");
            toSellLabel.setText("How many: ");
            toSellLabel.setPosition((Gdx.graphics.getWidth() - sellSelectBox.getWidth()) * 3 / 4 + (0.21f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.71f);
        }
    }
}
