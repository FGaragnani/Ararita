package com.ararita.game;

import com.ararita.game.battlers.Battler;
import com.ararita.game.battlers.Enemy;
import com.ararita.game.battlers.PC;
import com.ararita.game.items.Weapon;
import com.ararita.game.spells.Spell;

import java.io.IOException;
import java.util.*;
import java.util.random.RandomGenerator;

public class GlobalBattle {

    public List<Battler> battlers;

    public GlobalBattle(List<Battler> battlers) {
        this.battlers = battlers;
        sortBattleOrder();
    }

    public GlobalBattle(Enemy enemy, List<PC> party) {
        this.battlers = new ArrayList<>();
        this.battlers.addAll(party);
        this.battlers.add(enemy);
        sortBattleOrder();
    }

    public GlobalBattle(Enemy enemy) throws IOException {
        this.battlers = new ArrayList<>();
        for (Object o : Global.getListJSON(Global.globalSets, "party")) {
            this.battlers.add(Global.getCharacter((String) o));
        }
        this.battlers.add(enemy);
        sortBattleOrder();
    }

    public boolean isBattleFinished() {
        for (Battler battler : battlers) {
            if (battler instanceof Enemy) {
                if (battler.isDead()) {
                    return true;
                }
            } else {
                return battlers.stream().filter((b) -> b instanceof PC).allMatch(Battler::isDead);
            }
        }
        return battlers.stream().allMatch(Battler::isDead);
    }

    public void sortBattleOrder() {
        battlers.sort((o1, o2) -> o2.hasAttackSpeed() - o1.hasAttackSpeed());
    }

    public void attack(Battler attacker, Battler attacked) {
        if (!attacker.canAttack()) {
            return;
        }
        if (attacked instanceof PC) {
            attacked.getPhysicalDamage(attacker.hasPhysicalAttackPower());
        }
        if (attacker instanceof Enemy) {
            Enemy enemy = (Enemy) attacker;

            if (enemy.getStatusEffect().equals(Optional.of("Burn"))) {
                if (Global.getRandomZeroOne() < Global.BURN_CURE) {
                    enemy.removeStatusEffect();
                    return;
                } else {
                    enemy.sufferDamage(Math.max(1, (int) ((enemy).getCurrHP() * Global.BURN_DAMAGE)));
                }
            } else if (enemy.getStatusEffect().equals(Optional.of("Poison"))) {
                enemy.sufferDamage(Math.max(1, (int) ((enemy).getCurrHP() * Global.POISON_DAMAGE)));
            }
        }
        if (attacker instanceof PC && attacked instanceof Enemy) {
            double multiplier = 1;
            long count = ((PC) attacker).getWeapons().stream().map(Weapon::getWeaponType).filter((weaponType) -> ((Enemy) attacked).getWeakTo().contains(weaponType)).count();
            if (count > 0) {
                multiplier = Math.pow(Global.WEAKNESS_MULTIPLIER, count);
            }
            attacked.getPhysicalDamage((int) (attacker.hasPhysicalAttackPower() * multiplier));
        }
    }

    public void cast(PC attacker, Enemy attacked, Spell spell) {

        if (!attacker.canCast(spell)) {
            return;
        }

        double multiplier = 1;
        long count = ((PC) attacker).getWeapons().stream().map(Weapon::getWeaponType).filter((weaponType) -> ((Enemy) attacked).getWeakTo().contains(weaponType)).count();
        if (count > 0) {
            multiplier = Math.pow(Global.WEAKNESS_MULTIPLIER, count);
        }

        attacked.getMagicalDamage((int) (attacker.cast(spell) * multiplier));

        for (Map.Entry<String, Double> entry : spell.getStatusEffects().entrySet()) {
            if (Global.getRandomZeroOne() < entry.getValue()) {
                attacked.setStatusEffect(entry.getKey());
            }
        }
    }

    public List<Battler> getBattlers() {
        return battlers;
    }
}
