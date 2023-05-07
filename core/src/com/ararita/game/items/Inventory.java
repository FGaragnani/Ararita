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
    final public int MAX_INVENTORY_SPACE;
    final double RESELL_MULTIPLIER;

    /**
     * Inventory constructor. All data is fetched from the global manager.
     *
     * @throws IOException If the file cannot be opened or read.
     */

    public Inventory() throws IOException {
        items = new HashMap<>();
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
        Global.writeJSON(Global.globalSets, jsonGlobal);
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
            int i = 0;
            while (i < n) {
                add(item);
                i++;
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
            int i = 0;
            while (i < n) {
                remove(item);
                i++;
            }
            setMoney(getMoney() - (int) (item.getPrice() * n * RESELL_MULTIPLIER));
            updateItems();
        }
    }

    /**
     * Determines if a certain item can be removed from the inventory.
     *
     * @param item The item to remove.
     *
     * @return True, if the item can indeed be removed.
     */
    public boolean canRemove(Item item) {
        return items.containsKey(item);
    }

    /**
     * Removes - if possible - an item from the inventory.
     *
     * @param item The item to remove.
     *
     * @throws IOException If the file cannot be written upon.
     */
    public void remove(Item item) throws IOException {
        if (canRemove(item)) {
            if (items.get(item) > 1) {
                items.put(item, items.get(item) - 1);
            } else {
                items.remove(item);
            }
            updateItems();
        }
    }

    /**
     * Determines if an item can be added to the inventory.
     *
     * @param item The item to add.
     *
     * @return True, if the item can be added.
     */
    public boolean canAdd(Item item) {
        return (inventorySize() + 1 <= MAX_INVENTORY_SPACE);
    }

    /**
     * An item is added to the inventory, if possible.
     *
     * @param item The item to add.
     *
     * @throws IOException If the file cannot be written upon.
     */
    public void add(Item item) throws IOException {
        if (canAdd(item)) {
            if (items.containsKey(item)) {
                items.put(item, items.get(item) + 1);
            } else {
                items.put(item, 1);
            }
            updateItems();
        }
    }
}
