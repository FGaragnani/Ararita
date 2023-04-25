package com.ararita.game.battlers;

import com.ararita.game.Global;
import com.ararita.game.items.Weapon;
import com.ararita.game.spells.Spell;

import java.io.IOException;
import java.util.*;

import static java.lang.Math.ceil;

public abstract class AbstractBattler implements Battler {

    /*

    There are six main attributes that a Battler must have:

        1. Strength, which mainly affects physical damage;
        2. Intelligence, which mainly affects magical damage and the number of spells that can be cast;
        3. Vigor, which affects physical defence and toughness;
        4. Agility, which affects speed and critical chance;
        5. Spirit, which amplify status spells' effects and affinity to healing and object using;
        6. Arcane, which affects affinity to status imposing and magical defence.

    The stats have an initial value - which is determined by the character class - and are upgraded
    during level-ups.

     */

    int strength;
    int intelligence;
    int vigor;
    int agility;
    int spirit;
    int arcane;

    /*

    HP (Health Points) and MP (Mana Points) are determined from the six main stats.
    Different classes may have different formulas for determining them.
    currHP and currMP must be kept between the max value and 0.

     */

    int HP;
    int MP;


    /*

    charClass must be a String which also appears in Global.classNamesSet.

     */

    String charClass;

    /*

    The long-valued EXP is the total EXP ever gathered.
    The character level may be deduced by it.
    The formula for the EXP is always the same,
    but some values will change.

     */

    int EXP;
    private final int baseEXP;
    private final double increaseEXP;
    private final double exponentEXP;

    int level;

    List<Spell> spells;
    List<Weapon> weapons;

    /*

    This Map contains a list of proficiencies; will be consulted for every attack done.

     */

    Map<String, Integer> proficiencies;
    Set<String> spellTypes;

    /**
     * Creates a new AbstractBattler; note: this will be used to create a new class.
     * @throws IOException If the files cannot be read or written upon.
     */
    public AbstractBattler(int strength, int intelligence, int vigor, int agility, int spirit, int arcane, String charClass, int baseEXP, double increaseEXP, double exponentEXP, Map<String, Integer> proficiencies, Set<String> spellTypes) throws IOException {
        this.strength = strength;
        this.intelligence = intelligence;
        this.vigor = vigor;
        this.agility = agility;
        this.spirit = spirit;
        this.arcane = arcane;
        this.charClass = charClass;
        this.EXP = 0;
        this.HP = maxHP();
        this.MP = maxMP();
        this.spells = null;
        this.baseEXP = baseEXP;
        this.increaseEXP = increaseEXP;
        this.exponentEXP = exponentEXP;
        charLevel();
        this.spells = new ArrayList<>();
        this.weapons = new ArrayList<>(6);
        this.proficiencies = proficiencies;
        this.spellTypes = new HashSet<>();
        if (spellTypes != null && !spellTypes.isEmpty()) {
            this.spellTypes = new HashSet<>();
            this.spellTypes.addAll(spellTypes);
        }
        Global.addClass(this);
    }

    /**
     * Checks if the max HP and the max MP are up-to-date.
     */
    public void check() {
        this.HP = maxHP();
        this.MP = maxMP();
    }

    /**
     * Updates gradually the character's level.
     */
    @Override
    public void charLevel() {
        for (int i = 1; ; i++) {
            if (this.getEXP() < EXPForLevel(i)) {
                this.setLevel(i);
                break;
            }
        }
    }

    /**
     * Returns the needed EXP to reach a certain level.
     * @param level The level to reach.
     * @return The EXP needed to reach it.
     */
    @Override
    public int EXPForLevel(int level) {
        if (level == 0) {
            return 0;
        }
        int toRet;
        toRet = (int) ceil(increaseEXP * (baseEXP * level + (Math.pow(level, exponentEXP))));
        return toRet + EXPForLevel(level - 1);
    }

    /**
     * Gives EXP to the character.
     * @param gainedEXP The amount of EXP to give.
     */
    public void gainEXP(int gainedEXP) {
        int currLevel = getLevel();
        setEXP(getEXP() + gainedEXP);
        charLevel();
        for (int i = currLevel; i < getLevel(); i++) {
            levelUp();
        }
        check();
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getVigor() {
        return vigor;
    }

    public void setVigor(int vigor) {
        this.vigor = vigor;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getSpirit() {
        return spirit;
    }

    public void setSpirit(int spirit) {
        this.spirit = spirit;
    }

    public int getArcane() {
        return arcane;
    }

    public void setArcane(int arcane) {
        this.arcane = arcane;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getMP() {
        return MP;
    }

    public void setMP(int MP) {
        this.MP = MP;
    }

    public String getCharClass() {
        return charClass;
    }

    public void setCharClass(String charClass) {
        this.charClass = charClass;
    }

    public int getEXP() {
        return EXP;
    }

    public void setEXP(int EXP) {
        this.EXP = EXP;
    }

    public int getLevel() {
        return level;
    }

    public int getBaseEXP() {
        return baseEXP;
    }

    public double getIncreaseEXP() {
        return increaseEXP;
    }

    public double getExponentEXP() {
        return exponentEXP;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<Spell> getSpells() {
        return spells;
    }

    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    public Map<String, Integer> getProficiencies() {
        return proficiencies;
    }

    public void setProficiencies(Map<String, Integer> proficiencies) {
        this.proficiencies = proficiencies;
    }

    public Set<String> getSpellTypes() {
        return spellTypes;
    }

    public void setSpellTypes(Set<String> spellTypes) {
        this.spellTypes = spellTypes;
    }
}
