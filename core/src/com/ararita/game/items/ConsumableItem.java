package com.ararita.game.items;

import com.ararita.game.Global;
import com.ararita.game.battlers.PC;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public class ConsumableItem extends Item implements Consumable {

    Map<String, Integer> effect;

    /**
     * ConsumableItem's constructor. Creates a new consumable item giving what the item will do.
     * @param name The name of the item.
     * @param price The item's price.
     * @param effect What will happen if a character consumes the item (see this.use()).
     * @throws IOException If the files won't be opened or written upon.
     */
    public ConsumableItem(String name, int price, String description, Map<String, Integer> effect) throws IOException {
        super(name, price, "Consumable", description);
        this.effect = effect;
        Global.addConsumableItem(this);
    }

    /**
     * Applies the consumable effect onto a Playing Character. Its effect are denoted by a String (which will be an
     * attribute) and an Integer (meaning the increase of that attribute). For HP and MP, the current value must be
     * topped at maxHP() and maxMP().
     * @param characterUsing The character using the consumable item.
     */
    @Override
    public void use(PC characterUsing) {
        for(Map.Entry<String, Integer> entry : effect.entrySet()){
            switch (entry.getKey()) {
                case "HP":
                    characterUsing.setCurrHP(Math.min(characterUsing.maxHP(), characterUsing.getCurrHP() + entry.getValue()));
                    break;
                case "MP":
                    characterUsing.setCurrMP(Math.min(characterUsing.maxMP(), characterUsing.getCurrMP() + entry.getValue()));
                    break;
                case "Strength":
                    characterUsing.setStrength(characterUsing.getStrength() + entry.getValue());
                    break;
                case "Intelligence":
                    characterUsing.setIntelligence(characterUsing.getIntelligence() + entry.getValue());
                    break;
                case "Vigor":
                    characterUsing.setVigor(characterUsing.getVigor() + entry.getValue());
                    break;
                case "Agility":
                    characterUsing.setAgility(characterUsing.getAgility() + entry.getValue());
                    break;
                case "Spirit":
                    characterUsing.setSpirit(characterUsing.getSpirit() + entry.getValue());
                    break;
                case "Arcane":
                    characterUsing.setArcane(characterUsing.getArcane() + entry.getValue());
                    break;
                case "Life":
                    if (characterUsing.isDead()) {
                        characterUsing.setHP(Math.min(characterUsing.maxHP(), entry.getValue()));
                    }
            }
            characterUsing.check();
        }
    }

    public Map<String, Integer> getEffect() {
        return effect;
    }
}
