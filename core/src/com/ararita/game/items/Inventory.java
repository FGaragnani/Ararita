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

    public Inventory() throws IOException {
        Global.getMapJSON(Global.globalSets, "inventory").forEach((key, value) -> {
            try {
                items.put(Global.getItem(key), (int) value);
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

    public int inventorySize() {
        return items.entrySet().stream().flatMapToInt((entry) -> IntStream.of((entry.getValue()))).sum();
    }

    public boolean canBuy(Item item, int n) {
        return (inventorySize() + n <= MAX_INVENTORY_SPACE && ((item.getPrice() * n) <= getMoney()) && n >= 0);
    }

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

    public boolean canSell(Item item, int n) {
        return items.get(item) >= n;
    }

    public void sell(Item item, int n) throws IOException{
        if(canSell(item, n)){
            if(items.get(item) == n){
                items.remove(item);
            } else {
                items.put(item, items.get(item) - n);
            }
            setMoney(getMoney() - (int)(item.getPrice() * n * RESELL_MULTIPLIER));
            updateItems();
        }
    }

}
