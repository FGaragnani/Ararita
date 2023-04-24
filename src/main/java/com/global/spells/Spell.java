package com.global.spells;

import com.global.Global;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Spell {

    String name;
    int MPCost;
    String type;
    int basePower;

    Map<String, Double> statusEffects;

    /**
     * Spell's constructor; note that when the constructor will be called, the uniqueness of the name must already be
     * determined.
     * @param name the unique name of the spell
     * @param MPCost how much MP is needed to cast the spell
     * @param type the spell type (see global.json spellTypesSet)
     * @param basePower the spell's base power; it will be an int from 1 to 10 (the actual damage will be calculated
     * in battle)
     */
    public Spell(String name, int MPCost, String type, int basePower,
                 Map<String, Double> statusEffects) throws IOException {
        this.name = name;
        this.MPCost = MPCost;
        this.type = type;
        this.basePower = basePower;
        this.statusEffects = statusEffects;
        Global.addSpell(this);
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
}
