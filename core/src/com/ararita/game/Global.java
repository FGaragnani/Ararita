package com.global;

import com.global.battlers.AbstractBattler;
import com.global.battlers.PC;
import com.global.items.Item;
import com.global.spells.Spell;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Global {

    final public static int MAX_PARTY_MEMBERS = 4;

    final static Path globalSets = Path.of("src/main/java/com/global/global.json");
    final static Path classSets = Path.of("src/main/java/com/global/classes");
    final static Path characterSets = Path.of("src/main/java/com/global/characters");
    final static Path spellSets = Path.of("src/main/java/com/global/spells/data");

    /**
     * A new element is added in a global manager's array; note: the name MUST BE unique.
     * @param name The name of the class to add.
     * @param key The key to access the array.
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
     * @param key To identify the needed int.
     * @return The needed int.
     * @throws IOException If the file cannot be read.
     */
    public static int getIntFromGlobalArray(String key) throws IOException{
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        return jsonGlobal.getInt(key);
    }

    /**
     * Adds a new class as a separate file.
     * @param abstractBattler The abstract battler onto which create the JSON file.
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
     * @param battler The Playing Character to add.
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addCharacter(PC battler) throws IOException {
        File charFile = new File(characterSets + "/" + battler.getName() + ".json");
        if (!charFile.exists()) {
            charFile.createNewFile();
            FileWriter fileWriter = new FileWriter(charFile);
            fileWriter.write(new JSONObject(battler).toString(4));
            fileWriter.close();
        }
        if (getArrayLengthJSONGlobal("party") >= MAX_PARTY_MEMBERS) {
            addToOtherCharacters(battler.getName());
        } else {
            addToParty(battler.getName());
        }
    }

    /**
     * A character is added in the global manager to the reserve; note: a character's name MUST BE unique.
     * @param charName The name of the character to add.
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
     * @param charName The name of the character.
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
     * @param charName The name of the character to remove.
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
     * @param character The character to update.
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void updateCharacter(PC character) throws IOException {
        File charFile = new File(characterSets + "/" + character.getName() + ".json");
        if (!charFile.exists()) {
            charFile.createNewFile();
            FileWriter fileWriter = new FileWriter(charFile);
            fileWriter.write(new JSONObject(character).toString(4));
            fileWriter.close();
        } else {
            FileWriter fileWriter = new FileWriter(charFile);
            fileWriter.write(new JSONObject(character).toString(4));
            fileWriter.close();
        }
    }

    /**
     * Adds a spell; the method creates a JSON file to store info about it.
     * @param spell The spell to save.
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addSpell(Spell spell) throws IOException {
        File spellFile = new File(spellSets + "/" + spell.getName() + ".json");
        if(!spellFile.exists()){
            spellFile.createNewFile();
            FileWriter fileWriter = new FileWriter(spellFile);
            fileWriter.write(new JSONObject(spell).toString(4));
            fileWriter.close();
        }
        addInGlobalArray(spell.getName(), "spellNamesSet");
    }

    /**
     * Checks the presence of an element in an array in the global manager.
     * @param identifier The id of the element.
     * @param key The name of the array.
     * @return True, if the element does indeed exist.
     * @param <T> The type of the identifier.
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
     * @param className The name of the class (unique).
     * @param identifier The id to get the needed element.
     * @return The element to get.
     * @param <T> The type of the element to get.
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
     * @param className The name of the class (unique).
     * @param identifier The id to get the needed element.
     * @return The double required.
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
     * @param identifier The id to search for the element.
     * @param key To refer to the JSON array.
     * @return An int referring to the index of the identifier in the array.
     * @param <T> For the identifier type.
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
     * @param key To refer to the array.
     * @return An int referring to the array's length.
     * @throws IOException If the file cannot be opened or read.
     */
    public static int getArrayLengthJSONGlobal(String key) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        return jsonGlobal.getJSONArray(key).length();
    }

    /**
     * Returns a List copied from a JSON Array of a class.
     * @param className The name of the class.
     * @param identifier The identifier to get the array.
     * @return A List of T elements, a copy of the array.
     * @param <T> The type of the elements in the JSON array.
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
     * @param identifier The id to get the map.
     * @return A Map of (String, Integer) entries.
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
     * @param className The name of the class.
     * @param identifier The id to get the map.
     * @return A Map of (String, Integer) entries.
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
     * Determines if an item is affordable for the party.
     * @param item The item to consider.
     * @return True, if the item could be bought.
     * @throws IOException If the file cannot be opened or read.
     */
    public static boolean canBuy(Item item) throws IOException{
        return (getIntFromGlobalArray("money") >= item.getPrice());
    }

    /**
     * Adds an item onto the inventory in global manager.
     * If the item is already present, the number count of the item is incremented; else,
     * the item is added.
     * @param item The item to add to the inventory.
     * @param count How many items to be added.
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void addItem(Item item, int count) throws IOException{
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        if(jsonGlobal.getJSONObject("inventory").has(item.getName())){
            jsonGlobal.getJSONObject("inventory").put(item.getName(),
                    jsonGlobal.getJSONObject("inventory").getInt(item.getName()) + count);
        } else {
            jsonGlobal.getJSONObject("inventory").put(item.getName(), count);
        }
        FileWriter fileWriter = new FileWriter(globalSets.toFile());
        fileWriter.write(jsonGlobal.toString(4));
        fileWriter.close();
    }

    /**
     * Removes one item from the inventory. If the item is not present, nothing is done.
     * @param item The item to be removed.
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void removeItem(Item item) throws IOException{
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        if(!jsonGlobal.getJSONObject("inventory").has(item.getName())){
            return;
        } else if(jsonGlobal.getJSONObject("inventory").getInt(item.getName()) <= 1) {
            jsonGlobal.getJSONObject("inventory").remove(item.getName());
        } else {
            jsonGlobal.getJSONObject("inventory").put(item.getName(),
                    jsonGlobal.getJSONObject("inventory").getInt(item.getName()) - 1);
        }
        FileWriter fileWriter = new FileWriter(globalSets.toFile());
        fileWriter.write(jsonGlobal.toString(4));
        fileWriter.close();
    }
}