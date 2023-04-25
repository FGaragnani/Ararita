package com.ararita.game;

import com.ararita.game.battlers.AbstractBattler;
import com.ararita.game.battlers.PC;
import com.ararita.game.items.ConsumableItem;
import com.ararita.game.items.Item;
import com.ararita.game.items.Weapon;
import com.ararita.game.spells.Spell;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Global {

    final public static int MAX_PARTY_MEMBERS = 4;
    final public static int MAX_WEAPON_EQUIPPED = 1;
    final public static int MAX_INVENTORY_SPACE = 200;
    final public static double RESELL_MULTIPLIER = 0.75;

    final static Path globalSets = Path.of(Paths.get("..").normalize().toAbsolutePath().toString(), "core/src/com/ararita/game/global.json");
    final static Path classSets = Path.of(Paths.get("..").normalize().toAbsolutePath().toString(), "core/src/com" + "/ararita/game/classes");
    final static Path characterSets = Path.of(Paths.get("..").normalize().toAbsolutePath().toString(), "core/src/com/ararita/game/characters");
    final static Path spellSets = Path.of(Paths.get("..").normalize().toAbsolutePath().toString(), "core/src/com/ararita/game/spells/data");
    final static Path itemSets = Path.of(Paths.get("..").normalize().toAbsolutePath().toString(), "core/src/com" + "/ararita/game/items/data");

    /**
     * A new element is added in a global manager's array; note: the name MUST BE unique.
     *
     * @param name The name of the class to add.
     * @param key The key to access the array.
     *
     * @throws IOException If it can't open or write onto the file.
     */
    public static void addInGlobalArray(String name, String key) throws IOException {
        if (!isPresentInJSONGlobal(name, key)) {
            String content = new String(Files.readAllBytes(globalSets));
            JSONObject jsonGlobal = new JSONObject(content);
            jsonGlobal.getJSONArray(key).put(name);
            FileWriter fileWriter = new FileWriter(globalSets.toFile());
            fileWriter.write(jsonGlobal.toString(4));
            fileWriter.close();
        }
    }

    /**
     * Returns an int from the global JSON.
     *
     * @param key To identify the needed int.
     *
     * @return The needed int.
     *
     * @throws IOException If the file cannot be read.
     */
    public static int getIntFromGlobalArray(String key) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        return jsonGlobal.getInt(key);
    }

    /**
     * Adds a new class as a separate file.
     *
     * @param abstractBattler The abstract battler onto which create the JSON file.
     *
     * @throws IOException If the file cannot be opened.
     */
    public static void addClass(AbstractBattler abstractBattler) throws IOException {
        File classFile = new File(classSets + "/" + abstractBattler.getCharClass() + ".json");
        if (!classFile.exists()) {
            classFile.createNewFile();
            FileWriter fileWriter = new FileWriter(classFile);
            fileWriter.write(new JSONObject(abstractBattler).toString(4));
            fileWriter.close();
        }
        addInGlobalArray(abstractBattler.getCharClass(), "classNamesSet");
    }

    /**
     * Adds a new character, and creates the appropriate file; if the party is full, it is added to the reserve.
     *
     * @param battler The Playing Character to add.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addCharacter(PC battler) throws IOException {
        File charFile = new File(characterSets + "/" + battler.getName() + ".json");
        if (!charFile.exists()) {
            charFile.createNewFile();
            FileWriter fileWriter = new FileWriter(charFile);
            fileWriter.write(new JSONObject(battler).toString(4));
            fileWriter.close();
            if (getArrayLengthJSONGlobal("party") >= MAX_PARTY_MEMBERS) {
                addToOtherCharacters(battler.getName());
            } else {
                addToParty(battler.getName());
            }
        }
    }

    /**
     * A character is added in the global manager to the reserve; note: a character's name MUST BE unique.
     *
     * @param charName The name of the character to add.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addToOtherCharacters(String charName) throws IOException {
        if (!isPresentInJSONGlobal(charName, "party") && !isPresentInJSONGlobal(charName, "otherCharacters")) {
            String content = new String(Files.readAllBytes(globalSets));
            JSONObject jsonGlobal = new JSONObject(content);
            jsonGlobal.getJSONArray("otherCharacters").put(charName);
            FileWriter fileWriter = new FileWriter(globalSets.toFile());
            fileWriter.write(jsonGlobal.toString(4));
            fileWriter.close();
        }
    }

    /**
     * Adds a character to the party in the global manager; note: a character's name MUST BE unique.
     *
     * @param charName The name of the character.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addToParty(String charName) throws IOException {
        if (getArrayLengthJSONGlobal("party") >= MAX_PARTY_MEMBERS) {
            return;
        } else if (isPresentInJSONGlobal(charName, "party")) {
            return;
        } else if (!isPresentInJSONGlobal(charName, "otherCharacters")) {
            String content = new String(Files.readAllBytes(globalSets));
            JSONObject jsonGlobal = new JSONObject(content);
            jsonGlobal.getJSONArray("party").put(charName);
            FileWriter fileWriter = new FileWriter(globalSets.toFile());
            fileWriter.write(jsonGlobal.toString(4));
            fileWriter.close();
        } else {
            String content = new String(Files.readAllBytes(globalSets));
            JSONObject jsonGlobal = new JSONObject(content);
            jsonGlobal.getJSONArray("otherCharacters").remove(arrayIndexInJSONGlobal("otherCharacters", charName));
            jsonGlobal.getJSONArray("party").put(charName);
            FileWriter fileWriter = new FileWriter(globalSets.toFile());
            fileWriter.write(jsonGlobal.toString(4));
            fileWriter.close();
        }
    }

    /**
     * A character is removed from the party in the global manager.
     *
     * @param charName The name of the character to remove.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void removeFromParty(String charName) throws IOException {
        if (isPresentInJSONGlobal(charName, "party")) {
            String content = new String(Files.readAllBytes(globalSets));
            JSONObject jsonGlobal = new JSONObject(content);
            jsonGlobal.getJSONArray("party").remove(arrayIndexInJSONGlobal("party", charName));
            jsonGlobal.getJSONArray("otherCharacters").put(charName);
            FileWriter fileWriter = new FileWriter(globalSets.toFile());
            fileWriter.write(jsonGlobal.toString(4));
            fileWriter.close();
        }
    }

    /**
     * Updates an existing character; if the character doesn't exist, the function acts as addCharacter().
     *
     * @param character The character to update.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void updateCharacter(PC character) throws IOException {
        File charFile = new File(characterSets + "/" + character.getName() + ".json");
        if (!charFile.exists()) {
            charFile.createNewFile();
        }
        JSONObject toWrite = new JSONObject(character);
        toWrite.remove("weapons"); toWrite.remove("spells");
        toWrite.put("weapons", character.getWeapons().stream().map(Item::getName).collect(Collectors.toList()));
        toWrite.put("spells", character.getSpells().stream().map(Spell::getName).collect(Collectors.toList()));
        FileWriter fileWriter = new FileWriter(charFile);
        fileWriter.write(toWrite.toString(4));
        fileWriter.close();
    }

    public static PC getCharacter(String charName) throws IOException {
        if (isPresentInJSONGlobal(charName, "otherCharacters") || isPresentInJSONGlobal(charName, "party")) {
            File charFile = new File(characterSets + "/" + charName + ".json");
            if (charFile.exists()) {
                String content = new String(Files.readAllBytes(charFile.toPath()));
                JSONObject jsonGlobal = new JSONObject(content);
                List<Weapon> weapons = new ArrayList<>();
                List<Spell> spells = new ArrayList<>();
                for (Object name : jsonGlobal.getJSONArray("weapons")) {
                    weapons.add(getWeapon((String) name));
                }
                for (Object name : jsonGlobal.getJSONArray("spells")) {
                    spells.add(getSpell((String) name));
                }
                return new PC(jsonGlobal.getInt("strength"), jsonGlobal.getInt("intelligence"), jsonGlobal.getInt("vigor"), jsonGlobal.getInt("agility"), jsonGlobal.getInt("spirit"), jsonGlobal.getInt("arcane"), jsonGlobal.getString("charClass"), charName, jsonGlobal.getInt("currHP"), jsonGlobal.getInt("currMP"), jsonGlobal.getInt("level"), jsonGlobal.getInt("EXP"), weapons, spells);
            }
        }
        return null;
    }

    /**
     * Adds a spell; the method creates a JSON file to store info about it.
     *
     * @param spell The spell to save.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addSpell(Spell spell) throws IOException {
        File spellFile = new File(spellSets + "/" + spell.getName() + ".json");
        if (!spellFile.exists()) {
            spellFile.createNewFile();
            FileWriter fileWriter = new FileWriter(spellFile);
            fileWriter.write(new JSONObject(spell).toString(4));
            fileWriter.close();
        }
        addInGlobalArray(spell.getName(), "spellNamesSet");
    }

    public static Spell getSpell(String name) throws IOException {
        File spellFile = new File(spellSets + "/" + name + ".json");
        if (spellFile.exists()) {
            String content = new String(Files.readAllBytes(spellFile.toPath()));
            JSONObject jsonGlobal = new JSONObject(content);
            Map<String, Double> statusEffects = new HashMap<>();
            for (Map.Entry<String, Object> e : jsonGlobal.getJSONObject("statusEffects").toMap().entrySet()) {
                statusEffects.put(e.getKey(), (Double) e.getValue());
            }
            return new Spell(jsonGlobal.getString("name"), jsonGlobal.getInt("MPCost"), jsonGlobal.getString("type"), jsonGlobal.getInt("basePower"), statusEffects);
        }
        return null;
    }

    /**
     * Checks the presence of an element in an array in the global manager.
     *
     * @param identifier The id of the element.
     * @param key The name of the array.
     * @param <T> The type of the identifier.
     *
     * @return True, if the element does indeed exist.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static <T> boolean isPresentInJSONGlobal(T identifier, String key) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        for (Object o : jsonGlobal.getJSONArray(key)) {
            if (o.equals(identifier)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an element took from a class JSON.
     *
     * @param className The name of the class (unique).
     * @param identifier The id to get the needed element.
     * @param <T> The type of the element to get.
     *
     * @return The element to get.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static <T> T getFromJSONClass(String className, String identifier) throws IOException {
        Path specificClassSet = Path.of(classSets.toString(), "/", className + ".json");
        String content = new String(Files.readAllBytes(specificClassSet));
        JSONObject jsonClass = new JSONObject(content);
        return (T) jsonClass.get(identifier);
    }

    /**
     * Gets a double from a class JSON;
     * note: the getJSONClass wouldn't work with doubles, due to the JSON library usage of 'BigDecimal' class.
     *
     * @param className The name of the class (unique).
     * @param identifier The id to get the needed element.
     *
     * @return The double required.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static double getDoubleFromJSONClass(String className, String identifier) throws IOException {
        Object toRet = getFromJSONClass(className, identifier);
        if (toRet instanceof BigDecimal) {
            return ((BigDecimal) toRet).doubleValue();
        }
        return (double) toRet;
    }

    /**
     * Returns the index in which a certain identifier is in a JSON array in the global manager.
     *
     * @param identifier The id to search for the element.
     * @param key To refer to the JSON array.
     * @param <T> For the identifier type.
     *
     * @return An int referring to the index of the identifier in the array.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static <T> int arrayIndexInJSONGlobal(T identifier, String key) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        for (int i = 0; i < jsonGlobal.getJSONArray(key).length(); i++) {
            if (jsonGlobal.getJSONArray(key).get(i).equals(identifier)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the length of a JSON array in the global manager.
     *
     * @param key To refer to the array.
     *
     * @return An int referring to the array's length.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static int getArrayLengthJSONGlobal(String key) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        return jsonGlobal.getJSONArray(key).length();
    }

    public static int getArrayLengthJSONChar(String charName, String key) throws IOException{
        String content = new String(Files.readAllBytes(Path.of(characterSets + "/" + charName + ".json")));
        JSONObject jsonGlobal = new JSONObject(content);
        return jsonGlobal.getJSONArray(key).length();
    }

    /**
     * Returns a List copied from a JSON Array of a class.
     *
     * @param className The name of the class.
     * @param identifier The identifier to get the array.
     * @param <T> The type of the elements in the JSON array.
     *
     * @return A List of T elements, a copy of the array.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static <T> List<T> getArrayJSONClass(String className, String identifier) throws IOException {
        Path specificClassSet = Path.of(classSets.toString(), "/", className + ".json");
        String content = new String(Files.readAllBytes(specificClassSet));
        JSONObject jsonGlobal = new JSONObject(content);
        if (!jsonGlobal.has(identifier)) {
            return new ArrayList<>();
        }
        return (List<T>) jsonGlobal.getJSONArray(identifier).toList();
    }

    /**
     * Returns a Map from the global JSON.
     *
     * @param identifier The id to get the map.
     *
     * @return A Map of (String, Integer) entries.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static Map<String, Integer> getMapJSONGlobal(String identifier) throws IOException {
        Path globalSet = Path.of(globalSets.toString());
        String content = new String(Files.readAllBytes(globalSet));
        JSONObject jsonGlobal = new JSONObject(content);
        JSONObject jsonMap = jsonGlobal.getJSONObject(identifier);
        Map<String, Integer> toRet = new HashMap<>();
        for (Iterator<String> i = jsonMap.keys(); i.hasNext(); ) {
            String id = i.next();
            toRet.put(id, jsonMap.getInt(id));
        }
        return toRet;
    }

    /**
     * Returns a Map from a JSON file of a class.
     *
     * @param className The name of the class.
     * @param identifier The id to get the map.
     *
     * @return A Map of (String, Integer) entries.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static Map<String, Integer> getMapJSONClass(String className, String identifier) throws IOException {
        Path specificClassSet = Path.of(classSets.toString(), "/", className + ".json");
        String content = new String(Files.readAllBytes(specificClassSet));
        JSONObject jsonGlobal = new JSONObject(content);
        JSONObject jsonMap = jsonGlobal.getJSONObject(identifier);
        Map<String, Integer> toRet = new HashMap<>();
        for (Iterator<String> i = jsonMap.keys(); i.hasNext(); ) {
            String id = i.next();
            toRet.put(id, jsonMap.getInt(id));
        }
        return toRet;
    }

    /**
     * Returns the amount of money stored in the global manager.
     *
     * @return The amount of money.
     */
    public static int getMoney() throws IOException {
        return getIntFromGlobalArray("money");
    }

    /**
     * Sets the amount of money to the global handler.
     *
     * @param amount The amount of money to add.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void setMoney(int amount) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        jsonGlobal.put("money", amount);
        FileWriter fileWriter = new FileWriter(globalSets.toFile());
        fileWriter.write(jsonGlobal.toString(4));
        fileWriter.close();
    }

    /**
     * Determines if an item is affordable for the party and if there is enough space in the inventory.
     *
     * @param item The item to consider.
     *
     * @return True, if the item could be bought.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static boolean canBuy(Item item) throws IOException {
        return getMoney() >= item.getPrice() && !isInventoryFull();
    }

    /**
     * An item is sold. It is removed from the inventory and then adds its price to the money amount.
     * If the item is not present, nothing happens.
     *
     * @param item The item to be sold.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void sell(Item item) throws IOException {
        if (!getMapJSONGlobal("inventory").containsKey(item.getName())) {
            return;
        }
        removeItem(item);
        Global.setMoney(Global.getMoney() + (int) Math.floor(item.getPrice() * RESELL_MULTIPLIER));
    }

    /**
     * An item is bought, if it can be bought.
     *
     * @param item The item to buy.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void buy(Item item) throws IOException {
        if (canBuy(item)) {
            addItem(item, 1);
            setMoney(getMoney() - item.getPrice());
        }
    }

    /**
     * A ConsumableItem is stored as a JSON file.
     *
     * @param consumableItem The item to store.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addConsumableItem(ConsumableItem consumableItem) throws IOException {
        File specificItemSet = new File(itemSets + "/" + consumableItem.getName() + ".json");
        if (!specificItemSet.exists()) {
            specificItemSet.createNewFile();
            FileWriter fileWriter = new FileWriter(specificItemSet);
            fileWriter.write(new JSONObject(consumableItem).toString(4));
            fileWriter.close();
        }
    }

    /**
     * A weapon is stored onto his own JSON file.
     *
     * @param weapon The weapon to store.
     *
     * @throws IOException If the file cannot be created or written upon.
     */
    public static void addWeapon(Weapon weapon) throws IOException {
        File specificWeapon = new File(itemSets + "/" + weapon.getName() + ".json");
        if (!specificWeapon.exists()) {
            specificWeapon.createNewFile();
            FileWriter fileWriter = new FileWriter(specificWeapon);
            fileWriter.write(new JSONObject(weapon).toString(4));
            fileWriter.close();
        }
    }

    public static Weapon getWeapon(String weaponName) throws IOException {
        File specificWeapon = new File(itemSets + "/" + weaponName + ".json");
        if (specificWeapon.exists()) {
            String content = new String(Files.readAllBytes(specificWeapon.toPath()));
            JSONObject jsonGlobal = new JSONObject(content);
            Map<String, Integer> attributesAffection = new HashMap<>();
            for (Map.Entry<String, Object> e : jsonGlobal.getJSONObject("attributesAffection").toMap().entrySet()) {
                attributesAffection.put(e.getKey(), (Integer) e.getValue());
            }
            return new Weapon(weaponName, jsonGlobal.getInt("price"), jsonGlobal.getString("description"), attributesAffection, jsonGlobal.getString("weaponType"));
        }
        return null;
    }

    /**
     * Determines if the inventory is full in the global manager.
     *
     * @return True, if the inventory is full.
     *
     * @throws IOException If the file cannot be read.
     */
    public static boolean isInventoryFull() throws IOException {
        return getMapJSONGlobal("inventory").entrySet().stream().flatMapToInt((entry) -> IntStream.of(entry.getValue())).sum() >= MAX_INVENTORY_SPACE;
    }

    /**
     * Adds an item onto the inventory in global manager.
     * If the item is already present, the number count of the item is incremented; else,
     * the item is added.
     *
     * @param item The item to add to the inventory.
     * @param count How many items to be added.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void addItem(Item item, int count) throws IOException {
        if (isInventoryFull()) {
            return;
        }
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        if (jsonGlobal.getJSONObject("inventory").has(item.getName())) {
            jsonGlobal.getJSONObject("inventory").put(item.getName(), jsonGlobal.getJSONObject("inventory").getInt(item.getName()) + count);
        } else {
            jsonGlobal.getJSONObject("inventory").put(item.getName(), count);
        }
        FileWriter fileWriter = new FileWriter(globalSets.toFile());
        fileWriter.write(jsonGlobal.toString(4));
        fileWriter.close();
        if (item instanceof ConsumableItem) {
            addConsumableItem((ConsumableItem) item);
        } else {
            addWeapon((Weapon) item);
        }
    }

    /**
     * Removes one item from the inventory. If the item is not present, nothing is done.
     *
     * @param item The item to be removed.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void removeItem(Item item) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        if (!jsonGlobal.getJSONObject("inventory").has(item.getName())) {
            return;
        } else if (jsonGlobal.getJSONObject("inventory").getInt(item.getName()) <= 1) {
            jsonGlobal.getJSONObject("inventory").remove(item.getName());
        } else {
            jsonGlobal.getJSONObject("inventory").put(item.getName(), jsonGlobal.getJSONObject("inventory").getInt(item.getName()) - 1);
        }
        FileWriter fileWriter = new FileWriter(globalSets.toFile());
        fileWriter.write(jsonGlobal.toString(4));
        fileWriter.close();
    }

    /**
     * Returns an item, given his name - it reads it from its JSON file.
     *
     * @param name The name of the item to find.
     *
     * @return The item itself.
     *
     * @throws IOException If the file cannot be read.
     */
    public static Item getItem(String name) throws IOException {
        Path itemPath = Path.of(itemSets + "/", name + ".json");
        JSONObject itemJSON = new JSONObject(new String(Files.readAllBytes(itemPath)));
        if (itemJSON.getString("type").equals("Consumable")) {
            Map<String, Integer> effect = new HashMap<>();
            JSONObject effectJSON = itemJSON.getJSONObject("effect");
            effectJSON.toMap().forEach((key, value) -> effect.put(key, (Integer) value));
            return new ConsumableItem(itemJSON.getString("name"), itemJSON.getInt("price"), itemJSON.getString("description"), effect);
        } else {
            Map<String, Integer> attributes = new HashMap<>();
            JSONObject effectJSON = itemJSON.getJSONObject("attributesAffection");
            effectJSON.toMap().forEach((key, value) -> attributes.put(key, (Integer) value));
            return new Weapon(itemJSON.getString("name"), itemJSON.getInt("price"), itemJSON.getString("description"), attributes, itemJSON.getString("weaponType"));
        }
    }

    /**
     * A character unequips a certain weapon. If possible (i.g. the character has indeed the weapon and there is
     * enough space in the inventory), the equipment is hence put into the inventory.
     *
     * @param charName The name of the character.
     * @param weapon The weapon to remove.
     *
     * @throws IOException If the files cannot be read or written upon.
     */
    public static void unequip(String charName, Weapon weapon) throws IOException {
        File charFile = new File(characterSets + "/" + charName + ".json");
        if (charFile.exists()) {
            String content = new String(Files.readAllBytes(charFile.toPath()));
            JSONObject jsonGlobal = new JSONObject(content);
            if (jsonGlobal.getJSONArray("weapons").toList().contains(weapon.getName()) && !isInventoryFull()) {
                addItem(weapon, 1);
                jsonGlobal.getJSONArray("weapons").remove(jsonGlobal.getJSONArray("weapons").toList().indexOf(weapon.getName()));
                FileWriter fileWriter = new FileWriter(charFile);
                fileWriter.write(jsonGlobal.toString(4));
                fileWriter.close();
            }
        }
    }

    /**
     * A character is equipped with a weapon from the inventory.
     *
     * @param charName The character's name.
     * @param weapon The weapon to equip.
     *
     * @throws IOException If the files cannot be read or written upon.
     */
    public static void equip(String charName, Weapon weapon) throws IOException {
        File charFile = new File(characterSets + "/" + charName + ".json");
        if (charFile.exists()) {
            String content = new String(Files.readAllBytes(charFile.toPath()));
            JSONObject jsonGlobal = new JSONObject(content);
            if (getMapJSONGlobal("inventory").containsKey(weapon.getName()) && getArrayLengthJSONChar(charName,
                    "weapons") < MAX_WEAPON_EQUIPPED) {
                removeItem(weapon);
                jsonGlobal.getJSONArray("weapons").put(weapon.getName());
                FileWriter fileWriter = new FileWriter(charFile);
                fileWriter.write(jsonGlobal.toString(4));
                fileWriter.close();
            }
        }
    }
}
