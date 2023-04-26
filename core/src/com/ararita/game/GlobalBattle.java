package com.ararita.game;

import com.ararita.game.battlers.Battler;
import com.ararita.game.battlers.Enemy;

public class GlobalBattle {

    public static <T extends Battler> boolean canAttack(T battler){
        if(battler.isDead()){
            return false;
        }
        if(battler instanceof Enemy){
            if(((Enemy) battler).getStatusEffect().isPresent()){
                return !((Enemy) battler).getStatusEffect().get().equals("Paralysis");
            }
        }
        return true;
    }

}
