package com.ararita.game;

import com.ararita.game.battlers.Enemy;
import com.ararita.game.battlers.PC;
import com.ararita.game.items.ConsumableItem;
import com.ararita.game.items.Item;
import com.ararita.game.items.Weapon;
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
            Global.addItem(Global.getItem("Bronze Sword"), 1);
            test.equip(Global.getWeapon("Bronze Sword"));
            test.update();
            assertEquals(Global.getCharacter("test").getWeapons(), List.of(Global.getWeapon("Bronze Sword")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addSpell() {
        try {
            Path spellFile = Path.of(Global.spellSets + "/Void Arrow.json");
            Spell voidArrow = new Spell("Void Arrow", 5, "Chaos", 1, new HashMap<>(), true);
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
            Spell voidArrow = new Spell("Void Arrow", 5, "Chaos", 1, new HashMap<>(), true);
            assertEquals(Global.getSpell("Void Arrow").getName(), voidArrow.getName());
            assertEquals(Global.getSpell(voidArrow.getName()).getBasePower(), 1);
            Spell fireball = new Spell("Fireball", 25, "Fire", 2, Map.of("Burn", 0.2), true);
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
            assertTrue(Global.getInventory().isEmpty());
            Global.addItem(ether, 2);
            Global.sell(ether);
            assertEquals(Global.getInventory().get("Ether"), 1);
            assertEquals(Math.floor(ether.getPrice() * Global.RESELL_MULTIPLIER), Global.getMoney());
            Global.sell(ether);
            assertTrue(Global.getInventory().isEmpty());
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
    void getWeapon() {
        try {
            Weapon weapon = Global.getWeapon("Wooden Sword");
            assertEquals(20, weapon.getPrice());
            assertEquals("Sword", weapon.getWeaponType());
            assertEquals(150, Global.getWeapon("Bronze Sword").getPrice());
            assertEquals(Map.of("Strength", 10), Global.getWeapon("Bronze Sword").getAttributesAffection());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            Global.addItem(Global.getConsumableItem("Ether"), 3);
            assertEquals(Global.getInventory().get("Ether"), 3);
            assertTrue(Global.getInventory().containsKey("Ether"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void removeItem() {
        try {
            Global.addItem(Global.getItem("Potion"), 2);
            Global.removeItem(Global.getItem("Potion"));
            assertTrue(Global.getInventory().containsKey("Potion"));
            assertEquals(Global.getInventory().get("Potion"), 1);
            Global.removeItem(Global.getItem("Potion"));
            assertTrue(Global.getInventory().isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getItem() {
        try {
            ConsumableItem item = Global.getConsumableItem("Potion");
            assertEquals(item.getEffect().get("HP"), 50);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void unequip() {
        try {
            PC test = new PC("test", "Black Mage");
            Global.addItem(Global.getItem("Silver Sword"), 3);
            test.equip(Global.getWeapon("Silver Sword"));
            test.update();
            assertTrue(Global.getCharacter(test.getName()).getWeapons().contains((Weapon) Global.getItem("Silver Sword")));
            test.unequip(Global.getWeapon("Silver Sword"));
            test.update();
            assertTrue(Global.getCharacter(test.getName()).getWeapons().isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void equip() {
        try {
            PC test = new PC("test", "Knight");
            assertFalse(test.getWeapons().contains(Global.getWeapon("Wooden Sword")));
            Global.addItem(Global.getItem("Wooden Sword"), 2);
            test.equip(Global.getWeapon("Wooden Sword"));
            assertTrue(test.getWeapons().contains(Global.getWeapon("Wooden Sword")));
            test.update();
            assertTrue(Global.getCharacter("test").getWeapons().contains(Global.getWeapon("Wooden Sword")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void learnSpell() {
        try {
            Spell fireball = new Spell("Fireball", 10, "Fire", 1, Map.of("Burn", 0.1), true);
            Spell holyLight = new Spell("Holy Light", 10, "Light", 1, new HashMap<>(), true);
            PC test = new PC("test", "Black Mage");
            test.learnSpell(fireball);
            test.update();
            assertTrue(test.getSpells().contains(Global.getSpell("Fireball")));
            assertEquals(Global.getCharacter("test").getSpells(), test.getSpells());
            assertEquals(Global.getCharacter("test").getSpells().get(0).getStatusEffects(), Map.of("Burn", 0.1));
            test.learnSpell(holyLight);
            assertFalse(test.getSpells().contains(Global.getSpell("Holy Light")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void forgetSpell() {
        try {
            Spell fireball = new Spell("Fireball", 10, "Fire", 1, Map.of("Burn", 0.1), true);
            Spell voidArrow = new Spell("Void Arrow", 10, "Chaos", 1, new HashMap<>(), true);
            PC test = new PC("test", "Black Mage");
            test.learnSpell(fireball);
            test.learnSpell(voidArrow);
            test.update();
            test.forgetSpell(fireball);
            test.update();
            assertFalse(test.getSpells().contains(fireball));
            test.forgetSpell(voidArrow);
            assertTrue(Global.getCharacter(test.getName()).getSpells().isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void addEnemy() {
        try {
            Enemy goblin = new Enemy("Goblin", 5, 3, 1, 6, 30, 10, Map.of(Global.getItem("Potion"), 0.1), List.of("Sword", "Fire"));
            assertTrue(Global.getJSONFilePath(Global.enemySets, goblin.getName()).toFile().exists());
            Enemy thief = new Enemy("Thief", 2, 2, 5, 10, 25, 20, Map.of(Global.getItem("Potion"), 0.07, Global.getItem("Ether"), 0.04), List.of("Wind", "Spear", "Gloves"));
            assertTrue(Global.getJSONFilePath(Global.enemySets, thief.getName()).toFile().exists());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getEnemy() {
        try {
            Enemy goblin = new Enemy("Goblin");
            assertTrue(goblin.getWeakTo().contains("Sword"));
            assertEquals(30, goblin.getCurrHP());
            assertTrue(Global.getEnemy("Thief").getToDrop().containsKey(Global.getItem("Ether")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void isPresentInJSONList() {
        try {
            assertTrue(Global.isPresentInJSONList(Global.globalSets, "Fire", "spellTypesSet"));
            assertTrue(Global.isPresentInJSONList(Global.globalSets, "Chaos", "spellTypesSet"));
            assertTrue(Global.isPresentInJSONList(Global.globalSets, "Light", "spellTypesSet"));
            assertTrue(Global.isPresentInJSONList(Global.globalSets, "Wind", "spellTypesSet"));
            assertTrue(Global.isPresentInJSONList(Global.globalSets, "Burn", "statusEffectsSet"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getConsumableItem() {
        try {
            ConsumableItem item = Global.getConsumableItem("Potion");
            assertEquals(50, item.getEffect().get("HP"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}