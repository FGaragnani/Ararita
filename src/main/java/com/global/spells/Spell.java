package com.global.spells;

public class Spell {

    String name;
    int MPCost;
    String type;
    int basePower;

    /**
     * Spell's constructor
     * @param name the unique name of the spell
     * @param MPCost how much MP is needed to cast the spell
     * @param type the spell type (see global.json spellTypesSet)
     * @param basePower the spell's base power; it will be an int from 1 to 10 (the actual damage will be calculated
     * in battle)
     */
    public Spell(String name, int MPCost, String type, int basePower) {
        this.name = name;
        this.MPCost = MPCost;
        this.type = type;
        this.basePower = basePower;
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
}
