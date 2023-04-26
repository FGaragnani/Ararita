package com.ararita.game.battlers;

public interface Battler {
    double critChance();
    boolean isDead();
    int hasPhysicalAttackPower();
    int hasMagicalAttackPower();
    int hasPhysicalDefense();
    int hasMagicalDefense();
    int hasAttackSpeed();
}
