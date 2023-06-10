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
            Global.getAllItems().stream().filter(entry -> entry.getPrice() < 1000).sorted((o1, o2) -> {
                if (o1.getPrice() == o2.getPrice()) {
                    return 0;
                } else if (o1.getPrice() > o2.getPrice()) {
                    return 1;
                }
                return -1;
            }).forEach(item -> allItemsArray.add(item.getName()));
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

        game.createTitleCentered("SHOP", Gdx.graphics.getHeight() * 0.86f, Color.BLACK, stage);

        /*
            Creating the Exit Button.
         */

        exitButton = game.createMainButtonXCentered("Exit", Gdx.graphics.getHeight() * 0.074f, stage);
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

        buySelectBox = game.createSelectBox(game.width400, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.676f, stage);
        buySelectBox.setItems(allItemsArray);
        buySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCost();
                updateStats();
                updateBuy();
            }
        });

        /*
            Setting the stats and cost Label.
         */

        stats = game.createStatLabel("", Color.BLACK, (Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.6f, stage);
        costLabel = game.createStatLabel("", Color.BLACK, (Gdx.graphics.getWidth() - (buySelectBox.getWidth())) / 2 - (0.2f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.75f, stage);

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

        numberBuyTextField = game.createTextField("1", game.width200 / 2f, textField -> (Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (0.08f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.583f, stage);
        numberBuyTextField.setTextFieldFilter(new DigitFilter());
        toBuyLabel = game.createStatLabel("", Color.BLACK, (Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (0.156f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.565f, stage);
        numberBuyTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateCost();
            }
        });

        /*
            Setting the Buy Button and its listener.
         */

        buyButton = game.createNormalButton("Buy", textButton -> (Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 4 - (0.08f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.648f, stage);
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

        currentMoney = game.createStatLabel("Money: " + inventory.getMoney(), Color.BLACK, 0.755f * Gdx.graphics.getWidth(), 0.88f * Gdx.graphics.getHeight(), stage);
        currentMoneyImage = new Image(new TextureRegionDrawable(coinTexture));
        currentMoneyImage.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        currentMoneyImage.setPosition((0.81f * Gdx.graphics.getWidth()) + (currentMoney.getText().length() * (Gdx.graphics.getWidth() / 192f)), 0.866f * Gdx.graphics.getHeight());

        /*
            Creating the Sell Select Box and its listener.
         */

        sellSelectBox = game.createSelectBox(game.width400, stringSelectBox -> (Gdx.graphics.getWidth() - stringSelectBox.getWidth()) * 3 / 4 - (Gdx.graphics.getWidth() / 96f), Gdx.graphics.getHeight() * 0.676f, stage);
        sellSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSellLabels();
            }
        });

        /*
            Creating the sell label and its coin image.
         */

        sellLabel = game.createStatLabel("", Color.BLACK, (Gdx.graphics.getWidth() - (buySelectBox.getWidth())) / 2 + (0.1875f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 3 / 4f, stage);
        coinImageSell = new Image();
        coinImageSell.setDrawable(new TextureRegionDrawable(coinTexture));
        coinImageSell.setSize(coinTexture.getWidth(), coinTexture.getHeight());
        coinImageSell.setPosition(((Gdx.graphics.getWidth() - buySelectBox.getWidth()) / 3) + (0.41f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.722f);

        /*
            Setting the sell stats Label.
         */

        sellStats = game.createLabel("", 0, 0, game.descScaleX, game.descScaleY, Color.BLACK, stage);

        /*
            Setting the "how many" and "you have" label for selling, and the sell text field.
         */

        toSellLabel = game.createLabel("", 0, 0, stage);
        toSellLabel.setColor(Color.BLACK);
        numberSellTextField = game.createTextField("1", game.width200 / 2f, textField -> Gdx.graphics.getWidth() * 0.88f, Gdx.graphics.getHeight() * 0.68f, stage);
        numberSellTextField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateSellLabels();
            }
        });

        /*
            Setting the Sell Button.
         */

        sellButton = game.createNormalButton("Sell", textButton -> (Gdx.graphics.getWidth() - buySelectBox.getWidth()) * 3 / 4 - (0.08f * Gdx.graphics.getWidth()), Gdx.graphics.getHeight() * 0.648f, stage);
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
            @Override
            public void result(Object confirm) {
                hide();
            }
        };
        moneyDialog.setResizable(false);
        moneyDialog.text(" You don't have enough money\n to buy the desired number of items\n", game.labelStyle);
        moneyDialog.button("Ok!", true, game.textButtonStyle);
        moneyDialog.setPosition(0, 0);

        noInventorySpaceDialog = new Dialog("", skin) {
            @Override
            public void result(Object confirm) {
                hide();
            }
        };
        noInventorySpaceDialog.setResizable(false);
        noInventorySpaceDialog.text(" You don't have enough space\n in your inventory!\n", game.labelStyle);
        noInventorySpaceDialog.button("Ok!", true, game.textButtonStyle);
        noInventorySpaceDialog.setPosition(0, 0);

        noItemDialog = new Dialog("", skin) {
            @Override
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

        stage.addActor(coinImage);
        stage.addActor(currentMoneyImage);
        stage.addActor(coinImageSell);

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
            otherLines += toDescribe.getDescription().codePoints().filter(ch -> (ch == '\n')).count();
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
                otherLines += toDescribe.getDescription().codePoints().filter(ch -> (ch == '\n')).count();
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
