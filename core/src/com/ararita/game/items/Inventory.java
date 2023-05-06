package com.ararita.game.items;

import com.ararita.game.Global;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class Inventory {

    Map<Item, Integer> items;
    int money;
    final int MAX_INVENTORY_SPACE;
    final double RESELL_MULTIPLIER;

    /**
     * Inventory constructor. All data is fetched from the global manager.
     *
     * @throws IOException If the file cannot be opened or read.
     */

    public Inventory() throws IOException {
        Global.getInventory().forEach((key, value) -> {
            try {
                items.put(Global.getItem(key), value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        money = Global.getMoney();
        MAX_INVENTORY_SPACE = Global.MAX_INVENTORY_SPACE;
        RESELL_MULTIPLIER = Global.RESELL_MULTIPLIER;
    }

    public Map<Item, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Item, Integer> items) throws IOException {
        this.items = items;
        updateItems();
    }

    /**
     * The items are overwritten in the global manager.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public void updateItems() throws IOException {
        JSONObject jsonGlobal = Global.getJSON(Global.globalSets);
        jsonGlobal.remove("inventory");
        Map<String, Integer> toPut = new HashMap<>();
        items.forEach((key, value) -> toPut.put(key.getName(), value));
        jsonGlobal.put("inventory", toPut);
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) throws IOException {
        this.money = money;
        Global.setMoney(money);
    }

    /**
     * Returns the size of the inventory, as a sum of its integer values.
     *
     * @return The inventory's size.
     */
    public int inventorySize() {
        return items.entrySet().stream().flatMapToInt((entry) -> IntStream.of((entry.getValue()))).sum();
    }

    /**
     * Determines if the user can buy N copies of the same item.
     *
     * @param item The item to buy.
     * @param n The number of items to buy.
     *
     * @return True, if those can be bought.
     */
    public boolean canBuy(Item item, int n) {
        return (inventorySize() + n <= MAX_INVENTORY_SPACE && ((item.getPrice() * n) <= getMoney()) && n >= 0);
    }

    /**
     * Buys N copies of the same item.
     *
     * @param item The item to buy.
     * @param n The number of items to buy.
     *
     * @throws IOException If the file cannot be written upon.
     */
    public void buy(Item item, int n) throws IOException {
        if (canBuy(item, n)) {
            if (items.containsKey(item)) {
                items.put(item, items.get(item) + n);
            } else {
                items.put(item, n);
            }
            setMoney(getMoney() - (item.getPrice() * n));
            updateItems();
        }
    }

    /**
     * Determines if you can sell N copies of the same item.
     *
     * @param item The item to sell.
     * @param n The number of items to sell.
     *
     * @return True, if those can be sold.
     */
    public boolean canSell(Item item, int n) {
        return items.get(item) >= n;
    }

    /**
     * N copies of the same item are sold.
     *
     * @param item The item to sell.
     * @param n How many items to sell.
     *
     * @throws IOException If the file cannot be written upon.
     */
    public void sell(Item item, int n) throws IOException {
        if (canSell(item, n)) {
            if (items.get(item) == n) {
                items.remove(item);
            } else {
                items.put(item, items.get(item) - n);
            }
            setMoney(getMoney() - (int) (item.getPrice() * n * RESELL_MULTIPLIER));
            updateItems();
        }
    }
}
