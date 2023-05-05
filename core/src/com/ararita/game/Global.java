package com.ararita.game;

import com.ararita.game.battlers.AbstractBattler;
import com.ararita.game.battlers.PC;
import com.ararita.game.battlers.Enemy;
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
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Global {

    final public static int MAX_PARTY_MEMBERS = 4;
    final public static int MAX_WEAPON_EQUIPPED = 1;
    final public static int MAX_INVENTORY_SPACE = 200;
    final public static double RESELL_MULTIPLIER = 0.75;
    final public static int MAX_SPELLS_LEARNT = 20;
    final public static double WEAKNESS_MULTIPLIER = 1.5;
    final public static int INITIAL_ATTRIBUTES_POINT = 15;

    final public static double BURN_CURE = 0.5;
    final public static double BLINDNESS_INEFFICIENCY = 0.5;
    final public static double BURN_DAMAGE = 0.06;
    final public static double POISON_DAMAGE = 0.02;

    final public static Path globalSets = Path.of(Paths.get(".").normalize().toAbsolutePath().toString(), "core/src" + "/com/ararita/game" + "/global.json");
    final public static Path classSets = Path.of(Paths.get(".").normalize().toAbsolutePath().toString(), "core/src" + "/com" + "/ararita/game/classes");
    final public static Path characterSets = Path.of(Paths.get(".").normalize().toAbsolutePath().toString(), "core" + "/src/com/ararita/game/characters");
    final public static Path spellSets = Path.of(Paths.get(".").normalize().toAbsolutePath().toString(), "core/src" + "/com/ararita/game/spells/data");
    final public static Path itemSets = Path.of(Paths.get(".").normalize().toAbsolutePath().toString(), "core/src/com" + "/ararita/game/items/data");
    final public static Path enemySets = Path.of(Paths.get(".").normalize().toAbsolutePath().toString(), "core/src" + "/com" + "/ararita/game/enemies/data");

    /**
     * A path to a JSON file is created from its root and its name.
     *
     * @param generalPath The path from the root to the folder.
     * @param fileName The name of the JSON file.
     *
     * @return The path of the JSON file.
     */
    public static Path getJSONFilePath(Path generalPath, String fileName) {
        return Path.of(generalPath.toString() + "/" + fileName + ".json");
    }

    /**
     * All files are deleted from a specific folder.
     *
     * @param dirPath The path of the folder.
     */
    public static void deleteAllFolders(Path dirPath) {
        if (dirPath.toFile().isDirectory()) {
            for (File f : Objects.requireNonNull(dirPath.toFile().listFiles())) {
                f.delete();
            }
        }
    }

    /**
     * Chooses a random double in the interval [0, 1].
     *
     * @return The random double.
     */
    public static double getRandomZeroOne() {
        RandomGenerator rng = RandomGenerator.getDefault();
        return rng.nextDouble(0, 1);
    }

    /**
     * A new element is added in a JSON array; note: the name MUST BE unique.
     *
     * @param filePath The path of the file.
     * @param name The name to add to the array.
     * @param key The key to access the array.
     *
     * @throws IOException If it can't open or write onto the file.
     */
    public static void addInJSONArray(Path filePath, String name, String key) throws IOException {
        if (!isPresentInJSONList(filePath, name, key)) {
            JSONObject jsonGlobal = getJSON(filePath);
            jsonGlobal.getJSONArray(key).put(name);
            writeJSON(filePath, jsonGlobal);
        }
    }

    /**
     * A JSONObject is written onto a file.
     *
     * @param pathToWrite The path of the file to write onto.
     * @param jsonObject The JSONObject to write on the file.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void writeJSON(Path pathToWrite, JSONObject jsonObject) throws IOException {
        if (pathToWrite.toFile().exists()) {
            FileWriter fileWriter = new FileWriter(pathToWrite.toFile());
            fileWriter.write(jsonObject.toString(4));
            fileWriter.close();
        }
    }

    /**
     * A JSONObject is read from a file.
     *
     * @param filePath The path of the file storing the JSON.
     *
     * @return The JSONObject read.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static JSONObject getJSON(Path filePath) throws IOException {
        String content = new String(Files.readAllBytes(filePath));
        return new JSONObject(content);
    }

    /**
     * The method scans a Map (String, T) from a JSON file.
     *
     * @param filePath The JSON filepath.
     * @param key The name of the map.
     * @param <T> The type of the values in the map.
     *
     * @return The map itself.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static <T> Map<String, T> getMapJSON(Path filePath, String key) throws IOException {
        JSONObject jsonFile = getJSON(filePath);
        Map<String, T> toRet = new HashMap<>();
        jsonFile.getJSONObject(key).toMap().forEach((key1, value) -> toRet.put(key1, (T) value));
        return toRet;
    }

    /**
     * The method scans for a Map (String, Double) in a JSON file. This method MUST be used for double values since
     * the JSON library scans for BigDecimal values.
     *
     * @param filePath The JSON filepath.
     * @param key The name of the map.
     *
     * @return The map.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static Map<String, Double> getDoubleMapJSON(Path filePath, String key) throws IOException {
        Map<String, BigDecimal> bigDecimalMap = getMapJSON(filePath, key);
        Map<String, Double> toRet = new HashMap<>();
        bigDecimalMap.forEach((key1, value) -> toRet.put(key1, value.doubleValue()));
        return toRet;
    }

    /**
     * Adds a new class as a separate file.
     *
     * @param abstractBattler The abstract battler onto which create the JSON file.
     *
     * @throws IOException If the file cannot be opened.
     */
    public static void addClass(AbstractBattler abstractBattler) throws IOException {
        File classFile = getJSONFilePath(classSets, abstractBattler.getCharClass()).toFile();
        if (classFile.createNewFile()) {
            writeJSON(classFile.toPath(), new JSONObject(abstractBattler));
        }
        addInJSONArray(globalSets, abstractBattler.getCharClass(), "classNamesSet");
    }

    /**
     * Adds a new character, and creates the appropriate file; if the party is full, it is added to the reserve.
     *
     * @param battler The Playing Character to add.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addCharacter(PC battler) throws IOException {
        File charFile = getJSONFilePath(characterSets, battler.getName()).toFile();
        if (charFile.createNewFile()) {
            JSONObject toWrite = new JSONObject(battler);
            toWrite.put("image", battler.getImage());
            writeJSON(charFile.toPath(), toWrite);
            if (getArrayLengthJSON(globalSets, "party") >= MAX_PARTY_MEMBERS) {
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
        if (isPresentInJSONList(globalSets, charName, "party")) {
            addInJSONArray(globalSets, charName, "otherCharacters");
            JSONObject jsonGlobal = getJSON(globalSets);
            jsonGlobal.getJSONArray("party").remove(getListJSON(globalSets, "party").indexOf(charName));
            writeJSON(globalSets, jsonGlobal);
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
        if (getArrayLengthJSON(globalSets, "party") >= MAX_PARTY_MEMBERS || isPresentInJSONList(globalSets, charName, "party")) {
            return;
        }
        addInJSONArray(globalSets, charName, "party");
        JSONObject globalJson = getJSON(globalSets);
        globalJson.getJSONArray("otherCharacters").remove(getListJSON(globalSets, "otherCharacters").indexOf(charName));
        writeJSON(globalSets, globalJson);
    }

    /**
     * A character is removed from the party in the global manager.
     *
     * @param charName The name of the character to remove.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void removeFromParty(String charName) throws IOException {
        if (isPresentInJSONList(globalSets, charName, "party")) {
            JSONObject jsonGlobal = getJSON(globalSets);
            jsonGlobal.getJSONArray("party").remove(getListJSON(globalSets, "party").indexOf(charName));
            writeJSON(globalSets, jsonGlobal);
            addInJSONArray(globalSets, charName, "otherCharacters");
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
        File charFile = getJSONFilePath(characterSets, character.getName()).toFile();
        charFile.createNewFile();
        JSONObject toWrite = new JSONObject(character);
        toWrite.remove("weapons");
        toWrite.remove("spells");
        toWrite.put("weapons", character.getWeapons().stream().map(Item::getName).collect(Collectors.toList()));
        toWrite.put("spells", character.getSpells().stream().map(Spell::getName).collect(Collectors.toList()));
        writeJSON(charFile.toPath(), toWrite);
    }

    /**
     * Returns a PC from a saved file given its name.
     *
     * @param charName The name of the character.
     *
     * @return The constructed character from file.
     *
     * @throws IOException If the files cannot be opened or read.
     */
    public static PC getCharacter(String charName) throws IOException {
        if (isPresentInJSONList(globalSets, charName, "otherCharacters") || isPresentInJSONList(globalSets, charName, "party")) {
            Path charFile = getJSONFilePath(characterSets, charName);
            JSONObject jsonGlobal = getJSON(charFile);
            List<String> weapons = getListJSON(charFile, "weapons");
            List<String> spells = getListJSON(charFile, "spells");
            return new PC(jsonGlobal.getInt("strength"), jsonGlobal.getInt("intelligence"), jsonGlobal.getInt("vigor"), jsonGlobal.getInt("agility"), jsonGlobal.getInt("spirit"), jsonGlobal.getInt("arcane"), jsonGlobal.getString("charClass"), charName, jsonGlobal.getInt("currHP"), jsonGlobal.getInt("currMP"), jsonGlobal.getInt("level"), jsonGlobal.getInt("EXP"), weapons.stream().map((name) -> {
                try {
                    return Global.getWeapon(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()), spells.stream().map((name) -> {
                try {
                    return Global.getSpell(name);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
        } else {
            throw new IOException("The character is non-existent in the global manager.");
        }
    }

    /**
     * Adds a spell; the method creates a JSON file to store info about it.
     *
     * @param spell The spell to save.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void addSpell(Spell spell) throws IOException {
        File spellFile = getJSONFilePath(spellSets, spell.getName()).toFile();
        if (spellFile.createNewFile()) {
            writeJSON(spellFile.toPath(), new JSONObject(spell));
        }
        addInJSONArray(globalSets, spell.getName(), "spellNamesSet");
    }

    /**
     * Returns a Spell from a file.
     *
     * @param name The name of the spell.
     *
     * @return The constructed spell.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static Spell getSpell(String name) throws IOException {
        Path spellFile = getJSONFilePath(spellSets, name);
        JSONObject jsonGlobal = getJSON(spellFile);
        Map<String, Double> statusEffects = getDoubleMapJSON(spellFile, "statusEffects");
        return new Spell(jsonGlobal.getString("name"), jsonGlobal.getInt("MPCost"), jsonGlobal.getString("type"), jsonGlobal.getInt("basePower"), statusEffects, true);
    }

    /**
     * The spells are deleted, both in the global manager and in their files.
     * Useful for tests.
     *
     * @throws IOException If the files cannot be read or written upon.
     */
    public static void emptySpell() throws IOException {
        JSONObject jsonGlobal = getJSON(globalSets);
        jsonGlobal.remove("spellNamesSet");
        jsonGlobal.put("spellNamesSet", new ArrayList<>());
        writeJSON(globalSets, jsonGlobal);
        deleteAllFolders(spellSets);
    }

    /**
     * Checks the presence of an element in a JSON array.
     *
     * @param filePath The path of the JSON file.
     * @param identifier The id of the element.
     * @param key The name of the array.
     * @param <T> The type of the identifier.
     *
     * @return True, if the element does indeed exist.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static <T> boolean isPresentInJSONList(Path filePath, T identifier, String key) throws IOException {
        return getListJSON(filePath, key).contains(identifier);
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
        Path specificClassSet = getJSONFilePath(classSets, className);
        JSONObject jsonClass = getJSON(specificClassSet);
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
     * Returns the length of a JSON array.
     *
     * @param filePath The path of the file.
     * @param key To refer to the array.
     *
     * @return An int referring to the array's length.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static int getArrayLengthJSON(Path filePath, String key) throws IOException {
        return getListJSON(filePath, key).size();
    }

    /**
     * A list is read from a file containing a JSON.
     *
     * @param filePath The path of the file.
     * @param identifier The name of the array stored.
     * @param <T> The type contained in the array.
     *
     * @return A new List of T.
     *
     * @throws IOException If the file cannot be read.
     */
    public static <T> List<T> getListJSON(Path filePath, String identifier) throws IOException {
        return (List<T>) getJSON(filePath).getJSONArray(identifier).toList();
    }

    /**
     * A List of PC is returned, getting the names from a JSON file.
     *
     * @param filePath The path containing the JSON file.
     * @param identifier The name of the names array.
     *
     * @return The List of PCs.
     *
     * @throws IOException If the files cannot be read.
     */
    public static List<PC> getListPCJSON(Path filePath, String identifier) throws IOException {
        List<String> charNames = getListJSON(filePath, identifier);
        List<PC> toRet = new ArrayList<>();
        charNames.forEach((str) -> {
            try {
                toRet.add(getCharacter(str));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return toRet;
    }

    /**
     * The party - as a List of PCs - is determined.
     *
     * @return The party as a List of PCs.
     *
     * @throws IOException If the files cannot be read.
     */
    public static List<PC> getParty() throws IOException {
        return getListPCJSON(globalSets, "party");
    }

    /**
     * The other characters not in the party - as a List of PCs - are given.
     *
     * @return The List of PCs of the characters NOT in the party.
     *
     * @throws IOException If the files cannot be read.
     */
    public static List<PC> getOtherCharacters() throws IOException {
        return getListPCJSON(globalSets, "otherCharacters");
    }

    /**
     * A list containing all the created characters is returned.
     *
     * @return A PC List containing all the created characters.
     *
     * @throws IOException If the files cannot be read.
     */
    public static List<PC> getAllCharacters() throws IOException {
        List<PC> allChars = getParty();
        allChars.addAll(getOtherCharacters());
        return allChars;
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
        Path specificClassSet = getJSONFilePath(classSets, className);
        return getListJSON(specificClassSet, identifier);
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
        Path specificClassSet = getJSONFilePath(classSets, className);
        return getMapJSON(specificClassSet, identifier);
    }

    /**
     * Returns the inventory from the global manager.
     *
     * @return The inventory as a map of strings and integers.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static Map<String, Integer> getInventory() throws IOException {
        return getMapJSON(globalSets, "inventory");
    }

    /**
     * Completely empties the inventory in the global manager.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void emptyInventory() throws IOException {
        JSONObject jsonGlobal = getJSON(globalSets);
        jsonGlobal.remove("inventory");
        jsonGlobal.put("inventory", new HashMap<>());
        writeJSON(globalSets, jsonGlobal);
    }

    /**
     * The characters data is wiped from memory. Useful for testing.
     *
     * @throws IOException If the files cannot be deleted.
     */
    public static void emptyCharacters() throws IOException {
        JSONObject jsonGlobal = getJSON(globalSets);
        jsonGlobal.remove("party");
        jsonGlobal.put("party", new ArrayList<>());
        jsonGlobal.remove("otherCharacters");
        jsonGlobal.put("otherCharacters", new ArrayList<>());
        writeJSON(globalSets, jsonGlobal);
        deleteAllFolders(characterSets);
    }

    /**
     * Returns the amount of money stored in the global manager.
     *
     * @return The amount of money.
     */
    public static int getMoney() throws IOException {
        return getJSON(globalSets).getInt("money");
    }

    /**
     * Sets the amount of money to the global handler.
     *
     * @param amount The amount of money to add.
     *
     * @throws IOException If the file cannot be opened or written upon.
     */
    public static void setMoney(int amount) throws IOException {
        JSONObject jsonGlobal = getJSON(globalSets);
        jsonGlobal.put("money", amount);
        writeJSON(globalSets, jsonGlobal);
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
        if (!getInventory().containsKey(item.getName())) {
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
        File specificItemSet = getJSONFilePath(itemSets, consumableItem.getName()).toFile();
        if (specificItemSet.createNewFile()) {
            writeJSON(specificItemSet.toPath(), new JSONObject(consumableItem));
        }
    }

    /**
     * Gets a ConsumableItem parsed from its file.
     *
     * @param name The name of the item.
     *
     * @return The ConsumableItem requested.
     *
     * @throws IOException If the file cannot be opened.
     */
    public static ConsumableItem getConsumableItem(String name) throws IOException {
        Path specificConsumable = getJSONFilePath(itemSets, name);
        JSONObject itemJSON = getJSON(specificConsumable);
        Map<String, Integer> effect = new HashMap<>();
        JSONObject effectJSON = itemJSON.getJSONObject("effect");
        effectJSON.toMap().forEach((key, value) -> effect.put(key, (Integer) value));
        return new ConsumableItem(name, itemJSON.getInt("price"), itemJSON.getString("description"), effect);
    }

    /**
     * A weapon is stored onto his own JSON file.
     *
     * @param weapon The weapon to store.
     *
     * @throws IOException If the file cannot be created or written upon.
     */
    public static void addWeapon(Weapon weapon) throws IOException {
        File specificWeapon = getJSONFilePath(itemSets, weapon.getName()).toFile();
        if (specificWeapon.createNewFile()) {
            writeJSON(specificWeapon.toPath(), new JSONObject(weapon));
        }
    }

    /**
     * Returns a Weapon from its file.
     *
     * @param weaponName The name of the weapon.
     *
     * @return The constructed weapon.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static Weapon getWeapon(String weaponName) throws IOException {
        Path specificWeapon = getJSONFilePath(itemSets, weaponName);
        JSONObject itemJSON = getJSON(specificWeapon);
        Map<String, Integer> attributes = new HashMap<>();
        JSONObject effectJSON = itemJSON.getJSONObject("attributesAffection");
        effectJSON.toMap().forEach((key, value) -> attributes.put(key, (Integer) value));
        return new Weapon(weaponName, itemJSON.getInt("price"), itemJSON.getString("description"), attributes, itemJSON.getString("weaponType"));
    }

    /**
     * Determines if the inventory is full in the global manager.
     *
     * @return True, if the inventory is full.
     *
     * @throws IOException If the file cannot be read.
     */
    public static boolean isInventoryFull() throws IOException {
        return getInventory().entrySet().stream().flatMapToInt((entry) -> IntStream.of(entry.getValue())).sum() >= MAX_INVENTORY_SPACE;
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
        JSONObject jsonGlobal = getJSON(globalSets);
        if (jsonGlobal.getJSONObject("inventory").has(item.getName())) {
            jsonGlobal.getJSONObject("inventory").put(item.getName(), jsonGlobal.getJSONObject("inventory").getInt(item.getName()) + count);
        } else {
            jsonGlobal.getJSONObject("inventory").put(item.getName(), count);
        }
        writeJSON(globalSets, jsonGlobal);
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
        JSONObject jsonGlobal = getJSON(globalSets);
        if (!jsonGlobal.getJSONObject("inventory").has(item.getName())) {
            return;
        } else if (jsonGlobal.getJSONObject("inventory").getInt(item.getName()) <= 1) {
            jsonGlobal.getJSONObject("inventory").remove(item.getName());
        } else {
            jsonGlobal.getJSONObject("inventory").put(item.getName(), jsonGlobal.getJSONObject("inventory").getInt(item.getName()) - 1);
        }
        writeJSON(globalSets, jsonGlobal);
    }

    /**
     * Returns an item, given his name - reading it from its JSON file.
     * Specifically, the return is either a ConsumableItem or a Weapon.
     *
     * @param name The name of the item to find.
     *
     * @return The item itself.
     *
     * @throws IOException If the file cannot be read.
     */
    public static Item getItem(String name) throws IOException {
        Path itemPath = getJSONFilePath(itemSets, name);
        JSONObject itemJSON = getJSON(itemPath);
        if (itemJSON.getString("type").equals("Consumable")) {
            return getConsumableItem(name);
        } else {
            return getWeapon(name);
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
        File charFile = getJSONFilePath(characterSets, charName).toFile();
        if (charFile.exists()) {
            JSONObject jsonGlobal = getJSON(charFile.toPath());
            if (jsonGlobal.getJSONArray("weapons").toList().contains(weapon.getName()) && !isInventoryFull()) {
                addItem(weapon, 1);
                jsonGlobal.getJSONArray("weapons").remove(jsonGlobal.getJSONArray("weapons").toList().indexOf(weapon.getName()));
                writeJSON(charFile.toPath(), jsonGlobal);
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
        File charFile = getJSONFilePath(characterSets, charName).toFile();
        if (charFile.exists()) {
            JSONObject jsonGlobal = getJSON(charFile.toPath());
            if (getInventory().containsKey(weapon.getName()) && getArrayLengthJSON(charFile.toPath(), "weapons") < MAX_WEAPON_EQUIPPED) {
                removeItem(weapon);
                writeJSON(charFile.toPath(), jsonGlobal);
                addInJSONArray(charFile.toPath(), weapon.getName(), "weapons");
            }
        }
    }

    /**
     * A spell is added onto the learnt spells' array in its JSON file.
     *
     * @param charName The name of the character learning the spell.
     * @param spell The spell the character will learn.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void learnSpell(String charName, Spell spell) throws IOException {
        if (getCharacter(charName).canLearn(spell)) {
            File charFile = getJSONFilePath(characterSets, charName).toFile();
            if (charFile.exists()) {
                JSONObject jsonGlobal = getJSON(charFile.toPath());
                jsonGlobal.getJSONArray("spells").put(spell.getName());
                writeJSON(charFile.toPath(), jsonGlobal);
            }
        }
    }

    /**
     * A spell is erased from the JSON file of a character.
     *
     * @param charName The name of the character forgetting a certain spell.
     * @param spell The spell the character is forgetting.
     *
     * @throws IOException If the file cannot be read or written upon.
     */
    public static void forgetSpell(String charName, Spell spell) throws IOException {
        if (getCharacter(charName).getSpells().contains(spell)) {
            File charFile = getJSONFilePath(characterSets, charName).toFile();
            if (charFile.exists()) {
                JSONObject jsonGlobal = getJSON(charFile.toPath());
                jsonGlobal.getJSONArray("spells").remove(jsonGlobal.getJSONArray("spells").toList().indexOf(spell.getName()));
                writeJSON(charFile.toPath(), jsonGlobal);
            }
        }
    }

    /**
     * An Enemy file is stored in memory.
     *
     * @param enemy The enemy to store.
     *
     * @throws IOException If the file cannot be created or written upon.
     */
    public static void addEnemy(Enemy enemy) throws IOException {
        File specificEnemy = getJSONFilePath(enemySets, enemy.getName()).toFile();
        if (specificEnemy.createNewFile()) {
            JSONObject enemyJSON = new JSONObject(enemy);
            enemyJSON.remove("toDrop");
            Map<String, Double> toDrop = new HashMap<>();
            enemy.getToDrop().forEach((key, value) -> toDrop.put(key.getName(), value));
            enemyJSON.put("toDrop", toDrop);
            writeJSON(specificEnemy.toPath(), enemyJSON);
        }
    }

    /**
     * An Enemy is retrieved from memory.
     *
     * @param name The name of the enemy.
     *
     * @return The Enemy scanned from its file.
     *
     * @throws IOException If the file cannot be opened or read.
     */
    public static Enemy getEnemy(String name) throws IOException {
        File specificEnemy = getJSONFilePath(enemySets, name).toFile();
        JSONObject jsonObject = getJSON(specificEnemy.toPath());
        Map<Item, Double> toDrop = new HashMap<>();
        getDoubleMapJSON(specificEnemy.toPath(), "toDrop").forEach((key, value) -> {
            try {
                toDrop.put(Global.getItem(key), value);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        List<String> weakTo = getListJSON(specificEnemy.toPath(), "weakTo");
        return new Enemy(jsonObject.getString("name"), jsonObject.getInt("attack"), jsonObject.getInt("defense"), jsonObject.getInt("magicDefense"), jsonObject.getInt("speed"), jsonObject.getInt("currHP"), jsonObject.getInt("money"), toDrop, weakTo);
    }
}