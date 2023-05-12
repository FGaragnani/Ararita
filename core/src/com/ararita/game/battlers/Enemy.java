package com.ararita.game.battlers;

import com.ararita.game.Global;
import com.ararita.game.items.Item;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Enemy implements Battler {

    String name;
    int attack;
    int defense;
    int magicDefense;
    int speed;
    int currHP;
    int money;
    int level;
    Map<Item, Double> toDrop;
    String statusEffect;
    List<String> weakTo;

    /**
     * A new Enemy is created from scratch.
     *
     * @param name The unique name of the enemy.
     * @param attack The enemy's attack value.
     * @param defense The enemy's defense value.
     * @param magicDefense The enemy's magical defense value.
     * @param speed The enemy's speed value.
     * @param level The enemy's level (this is only descriptive).
     * @param currHP The enemy's HP.
     * @param money The enemy's drop money.
     * @param toDrop The enemy's drop items.
     * @param weakTo The enemy's weaknesses.
     *
     * @throws IOException If the file can't be read or written to.
     */
    public Enemy(String name, int attack, int defense, int magicDefense, int speed, int level, int currHP, int money, Map<Item, Double> toDrop, List<String> weakTo) throws IOException {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.magicDefense = magicDefense;
        this.speed = speed;
        this.level = level;
        this.currHP = currHP;
        this.money = money;
        this.toDrop = toDrop;
        this.weakTo = weakTo;
        this.statusEffect = "no";
        Global.addEnemy(this);
    }

    /**
     * Another constructor. A new enemy is created from its file.
     *
     * @param name The enemy's name.
     *
     * @throws IOException If the file can't be opened or read.
     */
    public Enemy(String name) throws IOException {
        Enemy toCopy = Global.getEnemy(name);
        this.name = name;
        this.attack = toCopy.getAttack();
        this.defense = toCopy.getDefense();
        this.magicDefense = toCopy.getMagicDefense();
        this.speed = toCopy.getSpeed();
        this.level = toCopy.getLevel();
        this.currHP = toCopy.getCurrHP();
        this.money = toCopy.getMoney();
        this.toDrop = toCopy.getToDrop();
        this.weakTo = toCopy.getWeakTo();
        this.statusEffect = "no";
    }

    /**
     * Determines if the enemy is dead.
     *
     * @return True, if he is dead.
     */
    public boolean isDead() {
        return getCurrHP() <= 0;
    }

    /**
     * The enemy suffers a physical damage.
     *
     * @param attack The damage suffered.
     */
    public void getPhysicalDamage(int attack) {
        int damage = Math.max(1, attack - defense);
        setCurrHP(getCurrHP() - damage);
    }

    /**
     * The enemy suffers magical damage.
     *
     * @param attack The magical damage suffered.
     */
    public void getMagicalDamage(int attack) {
        int damage = Math.max(1, attack - getMagicDefense());
        setCurrHP(getCurrHP() - damage);
    }

    /**
     * The enemy's 'Physical Attack' is calculated.
     *
     * @return The enemy's Physical Attack.
     */
    public int hasPhysicalAttackPower() {
        return attack;
    }

    /**
     * The enemy's 'Magical Attack' is calculated.
     *
     * @return The enemy's Magical Attack.
     */
    @Override
    public int hasMagicalAttackPower() {
        return attack;
    }

    /**
     * The enemy's 'Physical Defense' is calculated.
     *
     * @return The enemy's Physical Defense.
     */
    public int hasPhysicalDefense() {
        return defense;
    }

    /**
     * The enemy's 'Magical Defense' is calculated.
     *
     * @return The enemy's Magical Defense.
     */
    public int hasMagicalDefense() {
        return magicDefense;
    }

    /**
     * The enemy's 'Speed' is calculated.
     *
     * @return The enemy's Speed.
     */
    @Override
    public int hasAttackSpeed() {
        return speed;
    }

    /**
     * Determines if the enemy can attack.
     *
     * @return True, if the enemy can attack.
     */
    @Override
    public boolean canAttack() {

        if (getStatusEffect().equals("Blindness")) {
            return Global.getRandomZeroOne() < Global.BLINDNESS_INEFFICIENCY;
        }

        return !isDead() && !getStatusEffect().equals("Paralysis");
    }

    /**
     * The enemy suffers physical damage.
     *
     * @param damage The damage suffered.
     */
    public void sufferDamage(int damage) {
        setCurrHP(damage);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getMagicDefense() {
        return magicDefense;
    }

    public void setMagicDefense(int magicDefense) {
        this.magicDefense = magicDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getCurrHP() {
        return currHP;
    }

    public void setCurrHP(int currHP) {
        this.currHP = Math.max(currHP, 0);
    }

    public Map<Item, Double> getToDrop() {
        return toDrop;
    }

    public void setToDrop(Map<Item, Double> toDrop) {
        this.toDrop = toDrop;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    @Override
    public double critChance() {
        return 0.1;
    }

    public void setStatusEffect(String statusEffect) {
        this.statusEffect = statusEffect;
    }

    /**
     * The enemy's status effect is removed.
     */
    public void removeStatusEffect() {
        this.statusEffect = "no";
    }

    public String getStatusEffect() {
        return statusEffect;
    }

    public List<String> getWeakTo() {
        return weakTo;
    }

    public void setWeakTo(List<String> weakTo) {
        this.weakTo = weakTo;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
