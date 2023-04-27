package com.ararita.game.battlers;

import com.ararita.game.Global;
import com.ararita.game.items.Weapon;
import com.ararita.game.spells.Spell;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class PC extends AbstractBattler implements Battler {

    final static double HP_VIGOR_EFFECTIVENESS = 7;
    final static double HP_SECOND_STAT_EFFECTIVENESS = 2.5;
    final static double HP_THIRD_STAT_EFFECTIVENESS = 1.5;
    final static double HP_LEVEL_EFFECTIVENESS = 2.5;
    final static double MP_INTELLIGENCE_EFFECTIVENESS = 5;
    final static double MP_SECOND_STAT_EFFECTIVENESS = 3;
    final static double MP_THIRD_STAT_EFFECTIVENESS = 2;
    final static double MP_LEVEL_EFFECTIVENESS = 1.25;

    final static double MAIN_STAT_INCREASE = 0.15;
    final static double MAIN_EQUAL_STAT_INCREASE = 0.15;
    final static double SECOND_STAT_INCREASE = 0.1;
    final static double SECOND_EQUAL_STAT_INCREASE = 0.075;
    final static double PERCENTAGE_INCREASE = 0.125;

    int currHP;
    int currMP;

    String name;

    /**
     * Initializes a new Playing Character.
     *
     * @param name The unique name of the character.
     * @param charClass The name of an existing class the character will have.
     */
    public PC(String name, String charClass) throws IOException {
        super(Global.getFromJSONClass(charClass, "strength"), Global.getFromJSONClass(charClass, "intelligence"), Global.getFromJSONClass(charClass, "vigor"), Global.getFromJSONClass(charClass, "agility"), Global.getFromJSONClass(charClass, "spirit"), Global.getFromJSONClass(charClass, "arcane"), charClass, Global.getFromJSONClass(charClass, "baseEXP"), Global.getDoubleFromJSONClass(charClass, "increaseEXP"), Global.getDoubleFromJSONClass(charClass, "exponentEXP"), Global.getMapJSONClass(charClass, "proficiencies"), new HashSet<>(Global.getArrayJSONClass(charClass, "spellTypes")));
        this.name = name;
        this.currHP = maxHP();
        this.currMP = maxMP();
        Global.addCharacter(this);
    }

    /**
     * A second constructor taking every parameter. Useful for Global.getCharacter().
     */
    public PC(int strength, int intelligence, int vigor, int agility, int spirit, int arcane, String charClass, String name, int currHP, int currMP, int level, int EXP, List<Weapon> weapons, List<Spell> spells) throws IOException {
        super(strength, intelligence, vigor, agility, spirit, arcane, charClass, Global.getFromJSONClass(charClass, "baseEXP"), Global.getDoubleFromJSONClass(charClass, "increaseEXP"), Global.getDoubleFromJSONClass(charClass, "exponentEXP"), Global.getMapJSONClass(charClass, "proficiencies"), new HashSet<>(Global.getArrayJSONClass(charClass, "spellTypes")));
        this.name = name;
        this.currHP = currHP;
        this.currMP = currMP;
        this.level = level;
        this.EXP = EXP;
        this.weapons.addAll(weapons);
        this.spells.addAll(spells);
        this.HP = maxHP();
        this.MP = maxMP();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tells if the character is dead.
     *
     * @return True, if the character is currently dead.
     */
    public boolean isDead() {
        return getCurrHP() == 0;
    }

    /**
     * Reduces the character's current HP.
     *
     * @param loss How much HP the character loses.
     */
    public void loseHP(int loss) {
        int oldHP = getCurrHP();
        setCurrHP(Math.max(0, getCurrHP() - loss));
    }

    /**
     * Reduces the character's current MP.
     *
     * @param loss How much MP the character loses.
     */
    public void loseMP(int loss) {
        int oldMP = getCurrMP();
        setCurrMP(Math.max(0, getCurrMP() - loss));
    }

    public int getCurrHP() {
        return currHP;
    }

    public void setCurrHP(int currHP) {
        this.currHP = Math.max(currHP, 0);
    }

    public int getCurrMP() {
        return currMP;
    }

    public void setCurrMP(int currMP) {
        this.currMP = currMP;
    }

    /**
     * Determines the character's maximum Health Points.
     *
     * @return The maximum Health Points.
     */
    public int maxHP() {
        int maxHP = 0;
        maxHP += Math.floor(Math.max(getVigor(), 1) * HP_VIGOR_EFFECTIVENESS);
        maxHP += Math.floor(Math.max(Math.max(getStrength(), getAgility()), 1) * HP_SECOND_STAT_EFFECTIVENESS);
        maxHP += Math.floor(Math.max(Math.min(getStrength(), getAgility()), 1) * HP_THIRD_STAT_EFFECTIVENESS);
        maxHP += Math.floor(getLevel() * HP_LEVEL_EFFECTIVENESS);
        return maxHP;
    }

    /**
     * Determines the character's maximum Mana Points.
     *
     * @return The maximum Mana Points.
     */
    public int maxMP() {
        int maxMP = 0;
        maxMP += Math.floor(Math.max(getIntelligence(), 1) * MP_INTELLIGENCE_EFFECTIVENESS);
        maxMP += Math.floor(Math.max(Math.max(getSpirit(), getArcane()), 1) * MP_SECOND_STAT_EFFECTIVENESS);
        maxMP += Math.floor(Math.max(Math.min(getSpirit(), getArcane()), 1) * MP_THIRD_STAT_EFFECTIVENESS);
        maxMP += Math.floor(getLevel() * MP_LEVEL_EFFECTIVENESS);
        return maxMP;
    }

    /**
     * The character is levelled up, and the stats are increased.
     * The algorithm runs as follows: the primary main stats are determined (Strength, Intelligence or both of them)
     * and increased;
     * then the secondary main stats are determined (Strength -> Vigor and/or Agility, Intelligence -> Spirit and/or
     * Arcane), and increased;
     * then the ternary main stats are determined and increased.
     * Finally, every stat may be increased following a 1:8 probability (see PERCENTAGE_INCREASE).
     */
    public void levelUp() {
        if (getIntelligence() > getStrength()) {
            setIntelligence((int) (getIntelligence() + Math.floor(Math.max(getIntelligence() * MAIN_STAT_INCREASE, 1))));
            if (getStrength() > getSpirit() && getStrength() > getArcane()) {
                setStrength((int) (getStrength() + Math.floor(Math.max(getStrength() * SECOND_STAT_INCREASE, 1))));
            } else if (getSpirit() > getArcane()) {
                setSpirit((int) (getSpirit() + Math.floor(Math.max(getSpirit() * SECOND_STAT_INCREASE, 1))));
            } else if (getArcane() > getSpirit()) {
                setArcane((int) (getArcane() + Math.floor(Math.max(getArcane() * SECOND_STAT_INCREASE, 1))));
            } else {
                setSpirit((int) (getSpirit() + Math.floor(Math.max(getSpirit() * SECOND_EQUAL_STAT_INCREASE, 1))));
                setArcane((int) (getArcane() + Math.floor(Math.max(getArcane() * SECOND_EQUAL_STAT_INCREASE, 1))));
            }
        } else if (getStrength() > getIntelligence()) {
            setStrength((int) (getStrength() + Math.floor(Math.max(getStrength() * MAIN_STAT_INCREASE, 1))));
            if (getIntelligence() > getAgility() && getIntelligence() > getVigor()) {
                setIntelligence((int) (getIntelligence() + Math.floor(Math.max(getIntelligence() * SECOND_STAT_INCREASE, 1))));
            } else if (getAgility() > getVigor()) {
                setAgility((int) (getAgility() + Math.floor(Math.max(getAgility() * SECOND_STAT_INCREASE, 1))));
            } else if (getVigor() > getAgility()) {
                setVigor((int) (getVigor() + Math.floor(Math.max(getVigor() * SECOND_STAT_INCREASE, 1))));
            } else {
                setVigor((int) (getVigor() + Math.floor(Math.max(getVigor() * SECOND_EQUAL_STAT_INCREASE, 1))));
                setAgility((int) (getAgility() + Math.floor(Math.max(getAgility() * SECOND_EQUAL_STAT_INCREASE, 1))));
            }
        } else {
            setStrength((int) (getStrength() + Math.floor(Math.max(getStrength() * MAIN_EQUAL_STAT_INCREASE, 1))));
            setIntelligence((int) (getIntelligence() + Math.floor(Math.max(getIntelligence() * MAIN_EQUAL_STAT_INCREASE, 1))));
            if (getVigor() > getAgility()) {
                setVigor((int) (getVigor() + Math.floor(Math.max(getVigor() * SECOND_STAT_INCREASE, 1))));
            } else if (getAgility() > getAgility()) {
                setAgility((int) (getAgility() + Math.floor(Math.max(getAgility() * SECOND_STAT_INCREASE, 1))));
            } else {
                setVigor((int) (getVigor() + Math.floor(Math.max(getVigor() * SECOND_EQUAL_STAT_INCREASE, 1))));
                setAgility((int) (getAgility() + Math.floor(Math.max(getAgility() * SECOND_EQUAL_STAT_INCREASE, 1))));
            }
            if (getSpirit() > getArcane()) {
                setSpirit((int) (getSpirit() + Math.floor(Math.max(getSpirit() * SECOND_STAT_INCREASE, 1))));
            } else if (getArcane() > getSpirit()) {
                setArcane((int) (getArcane() + Math.floor(Math.max(getArcane() * SECOND_STAT_INCREASE, 1))));
            } else {
                setSpirit((int) (getSpirit() + Math.floor(Math.max(getSpirit() * SECOND_EQUAL_STAT_INCREASE, 1))));
                setArcane((int) (getArcane() + Math.floor(Math.max(getArcane() * SECOND_EQUAL_STAT_INCREASE, 1))));
            }
        }

        if (Global.getRandomZeroOne() <= PERCENTAGE_INCREASE) {
            setStrength(getStrength() + 1);
        }
        if (Global.getRandomZeroOne() <= PERCENTAGE_INCREASE) {
            setIntelligence(getIntelligence() + 1);
        }
        if (Global.getRandomZeroOne() <= PERCENTAGE_INCREASE) {
            setVigor(getVigor() + 1);
        }
        if (Global.getRandomZeroOne() <= PERCENTAGE_INCREASE) {
            setAgility(getAgility() + 1);
        }
        if (Global.getRandomZeroOne() <= PERCENTAGE_INCREASE) {
            setSpirit(getSpirit() + 1);
        }
        if (Global.getRandomZeroOne() <= PERCENTAGE_INCREASE) {
            setArcane(getArcane() + 1);
        }

        setCurrHP(maxHP());
        setCurrMP(maxMP());
    }

    /**
     * The character suffers a certain amount of physical damage.
     *
     * @param attack The damage suffered.
     */
    @Override
    public void getPhysicalDamage(int attack) {
        int damage = Math.max(1, attack - hasPhysicalDefense());
        setCurrHP(getCurrHP() - damage);
    }

    /**
     * The character suffers some Magical damage.
     *
     * @param attack The magical damage suffered.
     */
    @Override
    public void getMagicalDamage(int attack) {
        int damage = Math.max(1, attack - hasPhysicalDefense());
        setCurrHP(getCurrHP() - damage);
    }

    /**
     * Determines the character's critical chance.
     *
     * @return The character's critical chance, topped at 50%.
     */
    public double critChance() {
        if (getAgility() >= 100) {
            return 0.5;
        }
        return getAgility() / 200.0;
    }

    /**
     * The character equips a certain weapon. It is achieved if and only if the weapon is inside the inventory and
     * the character doesn't have too many weapons equipped.
     *
     * @param weapon The weapon to equip.
     *
     * @throws IOException If the character file can't be read or written upon.
     */
    public void equip(Weapon weapon) throws IOException {
        if (Global.MAX_WEAPON_EQUIPPED > getWeapons().size() && Global.getInventory().containsKey(weapon.getName())) {
            weapons.add(weapon);
            Global.equip(getName(), weapon);
        }
    }

    /**
     * The character unequips a certain weapon. It is achieved if and only if the character indeed had the weapon
     * equipped and if the inventory is not full.
     *
     * @param weapon The weapon to unequip.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public void unequip(Weapon weapon) throws IOException {
        if (weapons.contains(weapon) && !Global.isInventoryFull()) {
            weapons.remove(weapon);
            Global.unequip(getName(), weapon);
        }
    }

    /**
     * Determines if the PC can learn a spell.
     *
     * @param spell The spell which could be learned.
     *
     * @return True, if the spell can indeed be read.
     */
    public boolean canLearn(Spell spell) {
        return spellTypes.contains(spell.getType()) && !spells.contains(spell) && spells.size() < Global.MAX_SPELLS_LEARNT;
    }

    /**
     * The character learns a new spell, if it can.
     *
     * @param spell The spell to learn.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public void learnSpell(Spell spell) throws IOException {
        if (canLearn(spell)) {
            spells.add(spell);
            Global.addSpell(spell);
            Global.learnSpell(getName(), spell);
        }
    }

    /**
     * A character forgets a spell, if it can.
     *
     * @param spell The spell to forget.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public void forgetSpell(Spell spell) throws IOException {
        if (spells.contains(spell)) {
            spells.remove(spell);
            Global.forgetSpell(getName(), spell);
        }
    }

    /**
     * The character 'Physical Attack' is calculated.
     *
     * @return The character's Physical Attack.
     */
    public int hasPhysicalAttackPower() {
        double multiplier = getWeapons().stream().filter((weapon) -> this.getProficiencies().containsKey(weapon.getWeaponType())).count() / 2.0;
        return (int) ((getStrength() + (int) Math.floor((getVigor() + getAgility()) / 6.0)) * (multiplier + 0.5));
    }

    /**
     * The character 'Magical Attack' is calculated.
     *
     * @return The character's Magical Attack.
     */
    @Override
    public int hasMagicalAttackPower() {
        double multiplier = getWeapons().stream().filter((weapon) -> this.getProficiencies().containsKey(weapon.getWeaponType())).count() / 2.0;
        return (int) ((getIntelligence() + (int) Math.floor((getSpirit() + getArcane()) / 6.0)) * (multiplier + 0.5));
    }

    /**
     * The character 'Physical Defense' is calculated.
     *
     * @return The character's Physical Defense.
     */
    public int hasPhysicalDefense() {
        return getVigor() + (int) Math.floor(getStrength() / 2.0);
    }

    /**
     * The character 'Magical Defense' is calculated.
     *
     * @return The character's Magical Defense.
     */
    @Override
    public int hasMagicalDefense() {
        return getIntelligence() + (int) Math.floor((getSpirit() + getArcane()) / 6.0);
    }

    /**
     * The character's Speed is calculated.
     *
     * @return The character's Speed.
     */
    @Override
    public int hasAttackSpeed() {
        return getAgility() * 2 - (int) Math.floor(getVigor() / 3.0);
    }

    /**
     * Determines if the character can indeed attack.
     *
     * @return True, if the character can attack.
     */
    @Override
    public boolean canAttack() {
        return !isDead();
    }

    /**
     * Determines if a character can cast a spell.
     *
     * @param spell The spell to cast.
     *
     * @return True, if it can cast the spell.
     */
    public boolean canCast(Spell spell) {
        return spells.contains(spell) && spell.getMPCost() >= getCurrMP();
    }

    /**
     * A spell is cast and its damage calculated.
     *
     * @param spell The spell to cast.
     *
     * @return The spell's damage.
     */
    public int cast(Spell spell) {
        if (!canCast(spell)) {
            return 0;
        }
        this.setCurrMP(getCurrMP() - spell.getMPCost());
        return (int) (hasMagicalAttackPower() * (spell.getBasePower() / 3.0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PC pc = (PC) o;
        return name.equals(pc.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
