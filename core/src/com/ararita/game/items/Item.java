package com.ararita.game.items;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return price == item.price && Objects.equals(name, item.name) && Objects.equals(type, item.type) && Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, type, description);
    }
}
