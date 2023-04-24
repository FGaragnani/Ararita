package com.global.spells;

public class Spell {

    String name;
    int MPCost;
    String type;
    int basePower;

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
