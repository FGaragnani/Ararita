package com.ararita.game;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class GlobalTest {
    @AfterEach
    void tearDown() {
        try {
            Global.setMoney(0);
            Global.emptyInventory();
            Global.emptyCharacters();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
        try {
            Global.setMoney(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getMoney() {
        try {
            assertEquals(0, Global.getMoney());
            Global.setMoney(1000);
            assertEquals(1000, Global.getMoney());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void setMoney() {
        try {
            Global.setMoney(200);
            assertEquals(Global.getMoney(), 200);
            Global.setMoney(-10);
            assertEquals(Global.getMoney(), -10);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getRandomZeroOne() {
        for(int i = 0; i < 10; i++){
            assertTrue(Global.getRandomZeroOne() <= 1 && Global.getRandomZeroOne() >= 0);
        }
    }

    @Test
    void addCharacter() {
    }

    @Test
    void addToParty() {
    }

    @Test
    void removeFromParty() {
    }

    @Test
    void updateCharacter() {
    }

    @Test
    void getCharacter() {
    }

    @Test
    void addSpell() {
    }

    @Test
    void getSpell() {
    }

    @Test
    void isPresentInJSONGlobal() {
    }

    @Test
    void getFromJSONClass() {
    }

    @Test
    void getInventory() {
    }

    @Test
    void emptyInventory() {
    }

    @Test
    void emptyCharacters() {
    }

    @Test
    void canBuy() {
    }

    @Test
    void sell() {
    }

    @Test
    void buy() {
    }

    @Test
    void addConsumableItem() {
    }

    @Test
    void addWeapon() {
    }

    @Test
    void getWeapon() {
    }

    @Test
    void isInventoryFull() {
    }

    @Test
    void addItem() {
    }

    @Test
    void removeItem() {
    }

    @Test
    void getItem() {
    }

    @Test
    void unequip() {
    }

    @Test
    void equip() {
    }

    @Test
    void learnSpell() {
    }

    @Test
    void forgetSpell() {
    }

    @Test
    void addEnemy() {
    }

    @Test
    void getEnemy() {
    }
}