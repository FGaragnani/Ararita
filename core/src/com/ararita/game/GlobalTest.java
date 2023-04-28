package com.ararita.game;

import com.ararita.game.battlers.PC;
import com.ararita.game.items.Item;
import com.ararita.game.spells.Spell;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalTest {
    @AfterEach
    void tearDown() {
        try {
            Global.setMoney(0);
            Global.emptyInventory();
            Global.emptyCharacters();
            Global.emptySpell();
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
        for (int i = 0; i < 10; i++) {
            assertTrue(Global.getRandomZeroOne() <= 1 && Global.getRandomZeroOne() >= 0);
        }
    }

    @Test
    void addCharacter() {
        try {
            PC test = new PC("test", "Black Mage");
            assertTrue(Path.of(Global.characterSets + "/test.json").toFile().exists());
            PC prova = new PC("prova", "Knight");
            assertTrue(Path.of(Global.characterSets + "/prova.json").toFile().exists());
            assertEquals(test.getExponentEXP(), Global.getCharacter("test").getExponentEXP());
            assertEquals(prova.getProficiencies(), Global.getCharacter("prova").getProficiencies());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addToParty() {
        try {
            PC test = new PC("test", "Knight");
            Global.removeFromParty(test.getName());
            Global.addToParty("test");
            assertTrue(Global.getListJSON(Global.globalSets, "party").contains(test.getName()));
            PC prova = new PC("prova", "Black Mage");
            Global.removeFromParty("prova");
            Global.addToParty(prova.getName());
            assertTrue(Global.getListJSON(Global.globalSets, "party").containsAll(List.of(test.getName(), "prova")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void removeFromParty() {
        try {
            PC test = new PC("test", "Black Mage");
            Global.removeFromParty("test");
            assertTrue(Global.getListJSON(Global.globalSets, "party").isEmpty());
            Global.addToParty(test.getName());
            Global.removeFromParty("test");
            assertTrue(Global.getListJSON(Global.globalSets, "party").isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void updateCharacter() {
        try {
            PC test = new PC("test", "Knight");
            assertTrue(Global.getListJSON(Global.globalSets, "party").contains("test"));
            test.gainEXP(1000);
            assertNotEquals(1000, Global.getCharacter("test").getEXP());
            test.update();
            assertEquals(1000, Global.getCharacter("test").getEXP());
            test.gainEXP(200);
            Global.updateCharacter(test);
            assertEquals(1200, Global.getCharacter("test").getEXP());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getCharacter() {
        try {
            PC test = new PC("test", "Black Mage");
            assertEquals(test.getAgility(), Global.getCharacter("test").getAgility());
            test.gainEXP(1000);
            test.update();
            assertEquals(test.getLevel(), Global.getCharacter("test").getLevel());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addSpell() {
        try {
            Path spellFile = Path.of(Global.spellSets + "/Void Arrow.json");
            Spell voidArrow = new Spell("Void Arrow", 5, "Chaos", 1, new HashMap<>());
            assertTrue(spellFile.toFile().exists());
            assertTrue(Global.getListJSON(Global.globalSets, "spellNamesSet").contains("Void Arrow"));
            assertEquals(Global.getJSON(spellFile).getInt("MPCost"), voidArrow.getMPCost());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getSpell() {
        try {
            Path spellFile = Path.of(Global.spellSets + "/Void Arrow.json");
            Spell voidArrow = new Spell("Void Arrow", 5, "Chaos", 1, new HashMap<>());
            assertEquals(Global.getSpell("Void Arrow").getName(), voidArrow.getName());
            assertEquals(Global.getSpell(voidArrow.getName()).getBasePower(), 1);
            Spell fireball = new Spell("Fireball", 25, "Fire", 2, Map.of("Burn", 0.2));
            assertTrue(Global.getSpell(fireball.getName()).getStatusEffects().containsKey("Burn"));
            assertEquals(Global.getSpell("Fireball").getStatusEffects().get("Burn"), 0.2);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getFromJSONClass() {
        try {
            assertEquals((Integer) Global.getFromJSONClass("Knight", "agility"), 3);
            assertEquals((Integer) Global.getFromJSONClass("Black Mage", "intelligence"), 6);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getInventory() {
        try {
            Global.addItem(Global.getItem("Wooden Sword"), 3);
            assertTrue(Global.getInventory().containsKey("Wooden Sword"));
            assertEquals(3, (int) Global.getInventory().get("Wooden Sword"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void emptyInventory() {
        try {
            Global.addItem(Global.getItem("Wooden Sword"), 27);
            Global.addItem(Global.getItem("Supernal Ether"), 12);
            Global.emptyInventory();
            assertTrue(Global.getInventory().isEmpty());
            Global.addItem(Global.getItem("Potion"), 91);
            Global.emptyInventory();
            assertTrue(Global.getInventory().isEmpty());
            assertTrue(Path.of(Global.itemSets + "/Potion.json").toFile().exists());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void emptyCharacters() {
        try {
            PC test = new PC("test", "Knight");
            assertTrue(Path.of(Global.characterSets + "/test.json").toFile().exists());
            PC prova = new PC("prova", "Black Mage");
            Global.emptyCharacters();
            assertTrue(Global.getListJSON(Global.globalSets, "party").isEmpty());
            assertFalse(Path.of(Global.characterSets + prova.getName() + ".json").toFile().exists());
            assertFalse(Path.of(Global.characterSets + test.getName() + ".json").toFile().exists());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void canBuy() {
        try {
            Global.setMoney(3000);
            assertTrue(Global.canBuy(Global.getItem("Ether")));
            Global.setMoney(50);
            assertTrue(Global.canBuy(Global.getItem("Cosmic Ether")));
            Global.setMoney(0);
            assertFalse(Global.canBuy(Global.getItem("Ether")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void sell() {
        try {
            Item ether = Global.getItem("Ether");
            Global.sell(ether);
            assertEquals(0, Global.getMoney());
            assertTrue(Global.getMapJSONGlobal("inventory").isEmpty());
            Global.addItem(ether, 2);
            Global.sell(ether);
            assertEquals(Global.getMapJSONGlobal("inventory").get("Ether"), 1);
            assertEquals(Math.floor(ether.getPrice() * Global.RESELL_MULTIPLIER), Global.getMoney());
            Global.sell(ether);
            assertTrue(Global.getMapJSONGlobal("inventory").isEmpty());
            assertEquals(2 * Math.floor(ether.getPrice() * Global.RESELL_MULTIPLIER), Global.getMoney());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void buy() {
        try {
            Global.buy(Global.getItem("Ether"));
            assertFalse(Global.getInventory().containsKey("Ether"));
            Global.setMoney(200);
            Global.buy(Global.getItem("Ether"));
            assertTrue(Global.getInventory().containsKey("Ether"));
            assertEquals(1, (int) Global.getInventory().get("Ether"));
            assertEquals(Global.getMoney(), 200 - Global.getItem("Ether").getPrice());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            Item supernalEther = Global.getItem("Supernal Ether");
            Item grimSoul = Global.getItem("Grim Soul");
            for (int i = 0; i < Global.MAX_INVENTORY_SPACE - 1; i++) {
                if (i % 7 == 0) {
                    Global.addItem(supernalEther, 1);
                } else {
                    Global.addItem(grimSoul, 1);
                }
                assertFalse(Global.isInventoryFull());
            }
            Global.addItem(Global.getItem("Potion"), 1);
            assertTrue(Global.isInventoryFull());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Test
    void isPresentInJSONList() {
    }
}