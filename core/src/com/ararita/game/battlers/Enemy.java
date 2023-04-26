package com.ararita.game.battlers;

import com.ararita.game.Global;
import com.ararita.game.items.Item;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Enemy implements Battler {

    String name;
    int attack;
    int defense;
    int magicDefense;
    int speed;
    int currHP;
    int money;
    Map<Item, Double> toDrop;
    Optional<String> statusEffect;

    public Enemy(String name, int attack, int defense, int magicDefense, int speed, int currHP,
     int money, Map<Item, Double> toDrop) throws IOException {
        this.name = name;
        this.attack = attack;
        this.defense = defense;
        this.magicDefense = magicDefense;
        this.speed = speed;
        this.currHP = currHP;
        this.money = money;
        this.toDrop = toDrop;
        Global.addEnemy(this);
    }

    public Enemy(String name) throws IOException{
        Global.getEnemy(name);
    }

    public boolean isDead(){
        return getCurrHP() <= 0;
    }

    public int getPhysicalDamage(int attack){
        int damage = Math.max(1, attack - defense);
        setCurrHP(getCurrHP() - damage);
        return damage;
    }

    public int getMagicalDamage(int attack){
        int damage = Math.max(1, attack - magicDefense);
        setCurrHP(getCurrHP() - damage);
        return damage;
    }

    public int hasAttackPower(){
        return attack;
    }

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
        this.currHP = currHP;
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
        this.statusEffect = Optional.of(statusEffect);
    }

    public void removeStatusEffect(){
        this.statusEffect = Optional.empty();
    }

    public Optional<String> getStatusEffect() {
        return statusEffect;
    }
}
