package com.global.items;

import com.global.battlers.PC;

import java.util.function.Consumer;

public class ConsumableItem extends Item implements Consumable{

    Consumer<PC> consumer;

    /**
     * ConsumableItem's constructor. Creates a new consumable item giving what the item will do.
     * @param consumer What will happen if a character consumes the item.
     */
    public ConsumableItem(Consumer<PC> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void use(PC characterUsing) {
        consumer.accept(characterUsing);
    }

}
