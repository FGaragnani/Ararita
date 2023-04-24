package com.global.battlers;

public interface Battler {

    int maxHP();
    int maxMP();
    void charLevel();
    void levelUp();
    int EXPForLevel(int level);
    double critChance();

}
