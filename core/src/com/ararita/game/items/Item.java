package com.ararita.game.items;

import java.util.function.Function;

public abstract class Item {

    String name;
    int price;
    String type;
    String description;

    /**
     * Item's constructor.
     *
     * @param name The name of the item.
     * @param price The price of the item.
     */
    public Item(String name, int price, String type, String description) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
