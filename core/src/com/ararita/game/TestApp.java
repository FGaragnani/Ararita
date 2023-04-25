package com.global;

import com.global.battlers.AbstractBattler;
import com.global.battlers.PC;
import com.global.spells.Spell;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TestApp {

    public static void main(String[] args) throws IOException {
        //PC test1 = new PC("Carletto Giochetto", "Knight");
        //PC test2 = new PC("Domenico Modugno", "Knight");
        //test2.gainEXP(1000);

        Spell spell1 = new Spell("fireball", 10, "Fire", 1, new HashMap<>());
        Spell spell2 = new Spell("meteor", 40, "Fire", 3,
                new HashMap<>(Map.of("Burn", 0.1)));
    }



}
