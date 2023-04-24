package com.global;

import com.global.battlers.AbstractBattler;
import com.global.battlers.PC;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Global {

    /** Specifies to which index each zone refers to */
    final public static int RIGHT_HAND = 0;
    final public static int LEFT_HAND = 1;
    final public static int HEAD = 2;
    final public static int TORSO = 3;
    final public static int LEGS = 4;
    final public static int ACCESSORY = 5;

    final public static int MAX_PARTY_MEMBERS = 4;

    final static Path globalSets = Path.of("src/main/java/com/global/global.json");
    final static Path classSets = Path.of("src/main/java/com/global/classes");
    final static Path characterSets = Path.of("src/main/java/com/global/characters");

    /**
     * A new class is added in the global manager; note: the name of the class MUST BE unique.
     * @param className the name of the class to add
     * @throws IOException due to 'nio' usage
     */
    public static void addClassName(String className) throws IOException {
        if (!isPresentInJSONGlobal(className, "classNamesSet")) {
            String content = new String(Files.readAllBytes(globalSets));
            JSONObject jsonGlobal = new JSONObject(content);
            jsonGlobal.getJSONArray("classNamesSet").put(className);
            FileWriter fileWriter = new FileWriter(globalSets.toFile());
            fileWriter.write(jsonGlobal.toString(4));
            fileWriter.close();
        }
    }

    /**
     * Adds a new class as a separate file
     * @param abstractBattler the abstract battler onto which create the JSON file
     * @throws IOException due to 'nio'usage
     */
    public static void addClass(AbstractBattler abstractBattler) throws IOException {
        File classFile = new File(classSets + "/" + abstractBattler.getCharClass() + ".json");
        if (!classFile.exists()) {
            classFile.createNewFile();
            FileWriter fileWriter = new FileWriter(classFile);
            fileWriter.write(new JSONObject(abstractBattler).toString(4));
            fileWriter.close();
            addClassName(abstractBattler.getCharClass());
        }
    }

    /**
     * Adds a new character, and creates the appropriate file; if the party is full, it is added to the reserve.
     * @param battler the battler to add
     * @throws IOException due to 'nio' usage
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
     * @param charName the name of the character to add
     * @throws IOException due to 'nio' usage
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
     * Adds a character to the party in the global manager; note: a character's name MUST BE unique
     * @param charName the name of the character
     * @throws IOException due to 'nio' usage
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
     * @param charName the name of the character to remove
     * @throws IOException due to 'nio' usage
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
     * Checks the presence of an element in an array in the global manager
     * @param identifier the id of the element
     * @param key the name of the array
     * @return true, if the element does indeed exist
     * @param <T> the type of the identifier
     * @throws IOException due to 'nio' usage
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
     * Gives an element took from a class JSON
     * @param className the name of the class (unique)
     * @param identifier the id to get the needed element
     * @return the element to get
     * @param <T> the type of the element to get
     * @throws IOException due to 'nio' usage
     */
    public static <T> T getFromJSONClass(String className, String identifier) throws IOException {
        Path specificClassSet = Path.of(classSets.toString(), "/", className + ".json");
        String content = new String(Files.readAllBytes(specificClassSet));
        JSONObject jsonClass = new JSONObject(content);
        return (T) jsonClass.get(identifier);
    }

    /**
     * Gets a double from a class JSON;
     * note: the getJSONClass wouldn't work with doubles, due to the JSON library usage of 'BigDecimal' class
     * @param className the name of the class (unique)
     * @param identifier the id to get the needed element
     * @return the double required
     * @throws IOException due to 'nio' usage
     */
    public static double getDoubleFromJSONClass(String className, String identifier) throws IOException {
        Object toRet = getFromJSONClass(className, identifier);
        if (toRet instanceof BigDecimal) {
            return ((BigDecimal) toRet).doubleValue();
        }
        return (double) toRet;
    }

    /**
     * Returns the index in which a certain identifier is in a JSON array in the global manager
     * @param identifier the id to search for the element
     * @param key to refer to the JSON array
     * @return an int referring to the index of the identifier in the array
     * @param <T> for the identifier type
     * @throws IOException due to 'nio' usage
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
     * Returns the length of a JSON array in the global manager
     * @param key to refer to the array
     * @return an int referring to the array's length
     * @throws IOException due to 'nio' usage
     */
    public static int getArrayLengthJSONGlobal(String key) throws IOException {
        String content = new String(Files.readAllBytes(globalSets));
        JSONObject jsonGlobal = new JSONObject(content);
        return jsonGlobal.getJSONArray(key).length();
    }

    /**
     * Returns a List copied from a JSON Array of a class
     * @param className the name of the class
     * @param identifier the identifier to get the array
     * @return a List of T, a copy of the array
     * @param <T> the type of the elements in the JSON array
     * @throws IOException due to 'nio' usage
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
     * Returns a Map from a JSON file of a class
     * @param className the name of the class
     * @param identifier the id to get the map
     * @return a Map of (String, Integer) entries
     * @throws IOException due to 'nio' usage
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
}
