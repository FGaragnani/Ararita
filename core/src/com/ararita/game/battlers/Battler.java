package com.ararita.game.battlers;

import java.nio.file.Path;

public interface Battler {
    void getPhysicalDamage(int attack);
    void getMagicalDamage(int attack);
    double critChance();
    boolean isDead();
    int hasPhysicalAttackPower();
    int hasMagicalAttackPower();
    int hasPhysicalDefense();
    int hasMagicalDefense();
    int hasAttackSpeed();
    boolean canAttack();
}
