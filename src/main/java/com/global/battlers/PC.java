package com.global.battlers;

import com.global.Global;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.random.RandomGenerator;

public class PC extends AbstractBattler {

    final static double HP_VIGOR_EFFECTIVENESS = 7;
    final static double HP_SECOND_STAT_EFFECTIVENESS = 4.5;
    final static double HP_THIRD_STAT_EFFECTIVENESS = 2.5;
    final static double MP_INTELLIGENCE_EFFECTIVENESS = 8.5;
    final static double MP_SECOND_STAT_EFFECTIVENESS = 4;
    final static double MP_THIRD_STAT_EFFECTIVENESS = 1.25;

    final static double MAIN_STAT_INCREASE = 0.15;
    final static double MAIN_EQUAL_STAT_INCREASE = 0.15;
    final static double SECOND_STAT_INCREASE = 0.1;
    final static double SECOND_EQUAL_STAT_INCREASE = 0.075;
    final static double PERCENTAGE_INCREASE = 0.125;

    int currHP;
    int currMP;

    String name;

    /** Initializes a new Playing Character.
     * @param name refers to the unique name of the character
     * @param charClass refers to the existing class the character will have
     */
    public PC(String name, String charClass) throws IOException {
        super(Global.getFromJSONClass(charClass, "strength"), Global.getFromJSONClass(charClass, "intelligence"), Global.getFromJSONClass(charClass, "vigor"), Global.getFromJSONClass(charClass, "agility"), Global.getFromJSONClass(charClass, "spirit"), Global.getFromJSONClass(charClass, "arcane"), charClass, Global.getFromJSONClass(charClass, "baseEXP"), Global.getDoubleFromJSONClass(charClass, "increaseEXP"), Global.getDoubleFromJSONClass(charClass, "exponentEXP"), Global.getMapJSONClass(charClass, "proficiencies"), new HashSet<>(Global.getArrayJSONClass(charClass, "spellTypes")));
        this.name = name;
        Global.addCharacter(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tells if the character is dead.
     * @return true, if the character is dead.
     */
    public boolean isDead() {
        return getCurrHP() == 0;
    }

    public int loseHP(int loss) {
        int oldHP = getCurrHP();
        setCurrHP(Math.max(0, getCurrHP() - loss));
        return Math.min(oldHP, oldHP - loss);
    }

    public int loseMP(int loss) {
        int oldMP = getCurrMP();
        setCurrMP(Math.max(0, getCurrMP() - loss));
        return Math.min(oldMP, oldMP - loss);
    }

    public int getCurrHP() {
        return currHP;
    }

    public void setCurrHP(int currHP) {
        this.currHP = currHP;
    }

    public int getCurrMP() {
        return currMP;
    }

    public void setCurrMP(int currMP) {
        this.currMP = currMP;
    }

    @Override
    public int maxHP() {
        int maxHP = 0;
        maxHP += Math.floor(Math.max(getVigor(), 1) * HP_VIGOR_EFFECTIVENESS);
        maxHP += Math.floor(Math.max(Math.max(getStrength(), getAgility()), 1) * HP_SECOND_STAT_EFFECTIVENESS);
        maxHP += Math.floor(Math.max(Math.min(getStrength(), getAgility()), 1) * HP_THIRD_STAT_EFFECTIVENESS);
        return maxHP;
    }

    @Override
    public int maxMP() {
        int maxMP = 0;
        maxMP += Math.floor(Math.max(getIntelligence(), 1) * MP_INTELLIGENCE_EFFECTIVENESS);
        maxMP += Math.floor(Math.max(Math.max(getSpirit(), getArcane()), 1) * MP_SECOND_STAT_EFFECTIVENESS);
        maxMP += Math.floor(Math.max(Math.min(getSpirit(), getArcane()), 1) * MP_THIRD_STAT_EFFECTIVENESS);
        return maxMP;
    }

    @Override
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

        RandomGenerator rng = RandomGenerator.getDefault();

        if(rng.nextDouble(0, 1) <= PERCENTAGE_INCREASE) {
            setStrength(getStrength() + 1);
        }
        if(rng.nextDouble(0, 1) <= PERCENTAGE_INCREASE) {
            setIntelligence(getIntelligence() + 1);
        }
        if(rng.nextDouble(0, 1) <= PERCENTAGE_INCREASE) {
            setVigor(getVigor() + 1);
        }
        if(rng.nextDouble(0, 1) <= PERCENTAGE_INCREASE) {
            setAgility(getAgility() + 1);
        }
        if(rng.nextDouble(0, 1) <= PERCENTAGE_INCREASE) {
            setSpirit(getSpirit() + 1);
        }
        if(rng.nextDouble(0, 1) <= PERCENTAGE_INCREASE) {
            setArcane(getArcane() + 1);
        }
    }

    @Override
    public double critChance() {
        if (getAgility() >= 100) {
            return 0.5;
        }
        return getAgility() / 200.0;
    }
}
