package com.ararita.game;

import com.ararita.game.battlers.Battler;
import com.ararita.game.battlers.Enemy;
import com.ararita.game.battlers.PC;
import com.ararita.game.items.Weapon;
import com.ararita.game.spells.Spell;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GlobalBattle {

    public final List<Battler> battlers;

    /**
     * A GlobalBattle constructor.
     *
     * @param battlers The list of the battlers in the battle.
     */
    public GlobalBattle(List<Battler> battlers) {
        this.battlers = battlers;
        sortBattleOrder();
    }

    /**
     * A GlobalBattle constructor.
     *
     * @param enemy The enemy to fight.
     * @param party The party of players.
     */
    public GlobalBattle(Enemy enemy, List<PC> party) {
        this.battlers = new ArrayList<>();
        this.battlers.addAll(party);
        this.battlers.add(enemy);
        sortBattleOrder();
    }

    /**
     * A GlobalBattle constructor. The other Battlers are implicitly the one in the 'Party' array in the global manager.
     *
     * @param enemy The enemy to fight.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public GlobalBattle(Enemy enemy) throws IOException {
        this.battlers = new ArrayList<>();
        for (Object o : Global.getListJSON(Global.globalSets, "party")) {
            this.battlers.add(Global.getCharacter((String) o));
        }
        this.battlers.add(enemy);
        sortBattleOrder();
    }

    /**
     * Determines if the battle has ended, which happens when the enemy died or when the party did.
     *
     * @return True, if the battle has ended.
     */
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

    /**
     * The battlers are sorted out of their speed.
     */
    public void sortBattleOrder() {
        battlers.sort((o1, o2) -> o2.hasAttackSpeed() - o1.hasAttackSpeed());
    }

    /**
     * An attack is performed.
     *
     * @param attacker The battler who attacks.
     * @param attacked The battler who is attacked.
     */
    public void attack(Battler attacker, Battler attacked) {

        double multiplier = 1;

        if (!attacker.canAttack()) {
            return;
        }
        if (attacked instanceof PC) {
            if(Global.getRandomZeroOne() <= attacker.critChance()){
                multiplier *= 1.5;
            }
            attacked.getPhysicalDamage((int) (attacker.hasPhysicalAttackPower() * multiplier));
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
            long count = ((PC) attacker).getWeapons().stream().map(Weapon::getWeaponType).filter((weaponType) -> ((Enemy) attacked).getWeakTo().contains(weaponType)).count();
            if (count > 0) {
                multiplier *= Math.pow(Global.WEAKNESS_MULTIPLIER, count);
            }
            if(Global.getRandomZeroOne() <= attacker.critChance()){
                multiplier *= 1.5;
            }
            attacked.getPhysicalDamage((int) (attacker.hasPhysicalAttackPower() * multiplier));
        }
    }

    /**
     * A spell is cast.
     *
     * @param attacker The one casting the spell.
     * @param attacked The enemy attacked.
     * @param spell The spell cast.
     */
    public void cast(PC attacker, Enemy attacked, Spell spell) {

        if (!attacker.canCast(spell)) {
            return;
        }

        double multiplier = 1;
        long count = (attacker.getWeapons().stream().map(Weapon::getWeaponType).filter((weaponType) -> attacked.getWeakTo().contains(weaponType)).count());
        if (count > 0) {
            multiplier = Math.pow(Global.WEAKNESS_MULTIPLIER, count);
        }

        double statEffect;
        if (Set.of("Light", "Water", "Wind").contains(spell.getType())) {
            statEffect = Math.sqrt(attacker.getSpirit() + attacker.getIntelligence()) / 5;
        } else {
            statEffect = Math.sqrt(attacker.getArcane() + attacker.getIntelligence()) / 5;
        }

        attacked.getMagicalDamage((int) (attacker.cast(spell) * (multiplier + statEffect)));

        for (Map.Entry<String, Double> entry : spell.getStatusEffects().entrySet()) {
            if (Global.getRandomZeroOne() < entry.getValue()) {
                attacked.setStatusEffect(entry.getKey());
            }
        }
    }

    /**
     * Determines if the battle is won.
     *
     * @return True, if the battle has ended and was won.
     */
    public boolean isWon() {
        return isBattleFinished() && battlers.stream().filter((b) -> b instanceof PC).anyMatch((pc) -> !pc.isDead());
    }

    /**
     * Determines if the battle is lost.
     *
     * @return True, if the battle has ended and was lost.
     */
    public boolean isLost() {
        return isBattleFinished() && battlers.stream().filter((b) -> b instanceof PC).allMatch(Battler::isDead);
    }

    public List<Battler> getBattlers() {
        return battlers;
    }

    public Enemy getEnemy(){
        try {
            return (Enemy) getBattlers().stream().filter((battler -> battler instanceof Enemy)).findFirst().orElseThrow((Supplier<Throwable>) () -> null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public List<PC> getCharacters(){
        return getBattlers().stream().filter((battler) -> battler instanceof PC).map((battler) -> (PC) battler).collect(Collectors.toList());
    }
}
