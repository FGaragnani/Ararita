package com.ararita.game.items;

import java.util.function.Function;

public abstract class Item {

    String name;
    int price;

    /**
     * Item's constructor.
     * @param name The name of the item.
     * @param price The price of the item.
     */
    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
