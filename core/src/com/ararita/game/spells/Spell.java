package com.ararita.game.spells;

import com.ararita.game.Global;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Spell {

    final String name;
    final int MPCost;
    final String type;
    final int basePower;

    final Map<String, Double> statusEffects;

    /**
     * Spell's constructor; note that when the constructor will be called, the uniqueness of the name must already be
     * determined.
     *
     * @param name The unique name of the spell.
     * @param MPCost How much MP is needed to cast the spell.
     * @param type The spell type (see global.json spellTypesSet).
     * @param basePower The spell's base power; it will be an int from 1 to 5 (the actual damage will be calculated
     * in battle).
     * @param statusEffects A map of the status effect inflicting and its probability.
     */
    public Spell(String name, int MPCost, String type, int basePower, Map<String, Double> statusEffects, boolean toSave) throws IOException {
        this.name = name;
        this.MPCost = MPCost;
        this.type = type;
        this.basePower = basePower;
        this.statusEffects = statusEffects;
        if (toSave) {
            Global.addSpell(this);
        }
    }

    public String getName() {
        return name;
    }

    public int getMPCost() {
        return MPCost;
    }

    public String getType() {
        return type;
    }

    public int getBasePower() {
        return basePower;
    }

    public Map<String, Double> getStatusEffects() {
        return statusEffects;
    }

    public int moneyCost() {
        int baseCost = 10;
        baseCost += Math.floor(this.basePower * 10 * Math.pow(this.basePower, 3));
        int i = 1;
        for (Map.Entry<String, Double> statusEffect : statusEffects.entrySet()) {
            baseCost *= (1 + (statusEffect.getValue() * 10)) * i;
            if(baseCost > 10000000){
                return 10000000;
            }
            i++;
        }
        return baseCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Spell spell = (Spell) o;
        return Objects.equals(name, spell.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
