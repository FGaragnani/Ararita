package com.ararita.game;

import com.ararita.game.battlers.AbstractBattler;
import com.ararita.game.battlers.PC;
import com.ararita.game.battlers.Enemy;
import com.ararita.game.items.ConsumableItem;
import com.ararita.game.items.Item;
import com.ararita.game.items.Weapon;
import com.ararita.game.spells.Spell;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    final public static Path globalSets = Path.of("Data/global.json");
    final public static Path classSets = Path.of("Data/classes");
    final public static Path characterSets = Paths.get("Data/characters");
    final public static Path spellSets = Paths.get("Data/spells");
    final public static Path itemSets = Paths.get("Data/items");
    final public static Path enemySets = Paths.get("Data/enemies");

    /**
     * A path to a JSON file is created from its root and its name.
     *
     * @param generalPath The path from the root to the folder.
     * @param fileName The name of the JSON file.
     *
     * @return The path of the JSON file.
     */
    public static FileHandle getJSONFile(Path generalPath, String fileName) {
        return Gdx.files.local(generalPath.toString() + "/" + fileName + ".json");
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
        return new Random().nextDouble();
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
        FileHandle file = Gdx.files.local(pathToWrite.toString());
        file.writeString(jsonObject.toString(4), false);
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
        return new JSONObject(Gdx.files.internal(filePath.toString()).readString());
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
        FileHandle classFile = getJSONFile(classSets, abstractBattler.getCharClass());
        if (!classFile.exists()) {
            writeJSON(Path.of(classFile.path()), new JSONObject(abstractBattler));
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
        FileHandle charFile = getJSONFile(characterSets, battler.getName());
        if (!charFile.exists()) {
            JSONObject toWrite = new JSONObject(battler);
            toWrite.put("image", battler.getImage());
            writeJSON(Path.of(charFile.path()), toWrite);
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
        addInJSONArray(globalSets, charName, "otherCharacters");
        JSONObject jsonGlobal = getJSON(globalSets);
        jsonGlobal.getJSONArray("party").remove(getListJSON(globalSets, "party").indexOf(charName));
        writeJSON(globalSets, jsonGlobal);
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
        FileHandle charFile = getJSONFile(characterSets, character.getName());
        JSONObject toWrite = new JSONObject(character);
        toWrite.remove("weapons");
        toWrite.remove("spells");
        toWrite.put("weapons", character.getWeapons().stream().map(Item::getName).collect(Collectors.toList()));
        toWrite.put("spells", character.getSpells().stream().map(Spell::getName).collect(Collectors.toList()));
        writeJSON(Path.of(charFile.path()), toWrite);
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
            Path charFile = Path.of(getJSONFile(characterSets, charName).path());
            JSONObject jsonGlobal = getJSON(charFile);
            List<String> weapons = getListJSON(charFile, "weapons");
            List<String> spells = getListJSON(charFile, "spells");
            PC toRet = new PC(jsonGlobal.getInt("strength"), jsonGlobal.getInt("intelligence"), jsonGlobal.getInt("vigor"), jsonGlobal.getInt("agility"), jsonGlobal.getInt("spirit"), jsonGlobal.getInt("arcane"), jsonGlobal.getString("charClass"), charName, jsonGlobal.getInt("currHP"), jsonGlobal.getInt("currMP"), jsonGlobal.getInt("level"), jsonGlobal.getInt("EXP"), weapons.stream().map((name) -> {
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
            toRet.setImage(jsonGlobal.getString("image"));
            return toRet;
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
        FileHandle spellFile = getJSONFile(spellSets, spell.getName());
        if (!spellFile.exists()) {
            writeJSON(Path.of(spellFile.path()), new JSONObject(spell));
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
        Path spellFile = Path.of(getJSONFile(spellSets, name).path());
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
        Path specificClassSet = Path.of(getJSONFile(classSets, className).path());
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
        Path specificClassSet = Path.of(getJSONFile(classSets, className).path());
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
        Path specificClassSet = Path.of(getJSONFile(classSets, className).path());
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
     * Determines if N items of the same the type can be bought.
     *
     * @param item The item to buy in batch.
     * @param n The number of the items to buy.
     *
     * @return True, if the items can indeed be bought.
     *
     * @throws IOException If the file cannot be read.
     */
    public static boolean canBuy(Item item, int n) throws IOException {
        return getMoney() >= (item.getPrice() * n) && (getInventory().entrySet().stream().flatMapToInt((entry) -> IntStream.of(entry.getValue())).sum() + n <= MAX_INVENTORY_SPACE);
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
        FileHandle specificItemSet = getJSONFile(itemSets, consumableItem.getName());
        if (!specificItemSet.exists()) {
            writeJSON(Path.of(specificItemSet.path()), new JSONObject(consumableItem));
        }
    }

    /**
     * A list of all the items is determined from all the files in the item directory.
     *
     * @return The list of all the items.
     */
    public static List<Item> getAllItems() {
        return Stream.of("Angelic Staff", "Apprentice Staff", "Bladed Gloves", "Bone Bow", "Bone Dagger", "Boxing " + "Gloves", "Bronze Sword", "Bulk Soul", "Cosmic Ether", "Curved Staff", "Death Dirk", "Dueling Gloves", "Eden Bow", "Elder Staff", "Ether", "Fist Soul", "Golden Sword", "Grim Dagger", "Grim Soul", "Guard" + " Spear", "Gust Soul", "Hunter Bow", "Iron Bow", "Iron Lance", "Iron Poniard", "Javelin", "Longbow", "Maior Potion", "Maxima Potion", "Midas' Spear", "Mind Soul", "Monk Gloves", "Phinia", "Potion", "Pure Soul", "Revitalizing", "Revitalizing Brew", "Revitalizing Leaf", "Silver Sword", "Small Dagger", "Supernal Ether", "Tiger Gloves", "Trident", "Willow Wand", "Wooden Sword").map((str) -> {
            try {
                return (Global.getItem(str));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
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
        Path specificConsumable = Path.of(getJSONFile(itemSets, name).path());
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
        FileHandle specificWeapon = getJSONFile(itemSets, weapon.getName());
        if (!specificWeapon.exists()) {
            writeJSON(Path.of(specificWeapon.path()), new JSONObject(weapon));
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
        Path specificWeapon = Path.of(getJSONFile(itemSets, weaponName).path());
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
        Path itemPath = Path.of(getJSONFile(itemSets, name).path());
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
        FileHandle charFile = getJSONFile(characterSets, charName);
        if (charFile.exists()) {
            JSONObject jsonGlobal = getJSON(Path.of(charFile.path()));
            if (jsonGlobal.getJSONArray("weapons").toList().contains(weapon.getName()) && !isInventoryFull()) {
                addItem(weapon, 1);
                jsonGlobal.getJSONArray("weapons").remove(jsonGlobal.getJSONArray("weapons").toList().indexOf(weapon.getName()));
                writeJSON(Path.of(charFile.path()), jsonGlobal);
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
        FileHandle charFile = getJSONFile(characterSets, charName);
        if (charFile.exists()) {
            JSONObject jsonGlobal = getJSON(Path.of(charFile.path()));
            if (getInventory().containsKey(weapon.getName()) && getArrayLengthJSON(Path.of(charFile.path()), "weapons") < MAX_WEAPON_EQUIPPED) {
                removeItem(weapon);
                writeJSON(Path.of(charFile.path()), jsonGlobal);
                addInJSONArray(Path.of(charFile.path()), weapon.getName(), "weapons");
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
            FileHandle charFile = getJSONFile(characterSets, charName);
            if (charFile.exists()) {
                JSONObject jsonGlobal = getJSON(Path.of(charFile.path()));
                jsonGlobal.getJSONArray("spells").put(spell.getName());
                writeJSON(Path.of(charFile.path()), jsonGlobal);
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
            FileHandle charFile = getJSONFile(characterSets, charName);
            if (charFile.exists()) {
                JSONObject jsonGlobal = getJSON(Path.of(charFile.path()));
                jsonGlobal.getJSONArray("spells").remove(jsonGlobal.getJSONArray("spells").toList().indexOf(spell.getName()));
                writeJSON(Path.of(charFile.path()), jsonGlobal);
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
        FileHandle specificEnemy = getJSONFile(enemySets, enemy.getName());
        if (!specificEnemy.exists()) {
            JSONObject enemyJSON = new JSONObject(enemy);
            enemyJSON.remove("toDrop");
            Map<String, Double> toDrop = new HashMap<>();
            enemy.getToDrop().forEach((key, value) -> toDrop.put(key.getName(), value));
            enemyJSON.put("toDrop", toDrop);
            writeJSON(Path.of(specificEnemy.path()), enemyJSON);
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
        FileHandle specificEnemy = getJSONFile(enemySets, name);
        JSONObject jsonObject = getJSON(Path.of(specificEnemy.path()));
        Map<Item, Double> toDrop = new HashMap<>();
        getDoubleMapJSON(Path.of(specificEnemy.path()), "toDrop").forEach((key, value) -> {
            try {
                toDrop.put(Global.getItem(key), value);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        List<String> weakTo = getListJSON(Path.of(specificEnemy.path()), "weakTo");
        return new Enemy(jsonObject.getString("name"), jsonObject.getInt("attack"), jsonObject.getInt("defense"), jsonObject.getInt("magicDefense"), jsonObject.getInt("speed"), jsonObject.getInt("level"), jsonObject.getInt("currHP"), jsonObject.getInt("money"), toDrop, weakTo);
    }

    /**
     * Classes - a part from the standard ones - are deleted.
     */
    public static void emptyClass() throws IOException {
        JSONObject jsonGlobal = getJSON(globalSets);
        jsonGlobal.remove("classNamesSet");
        jsonGlobal.put("classNamesSet", List.of("Knight", "Black Mage", "White Mage", "Ranger"));
        writeJSON(globalSets, jsonGlobal);
        if (classSets.toFile().isDirectory()) {
            for (File f : Objects.requireNonNull(classSets.toFile().listFiles())) {
                if (!jsonGlobal.getJSONArray("classNamesSet").toList().contains(f.getName().substring(0,
                        f.getName().length() - 5))) {
                    f.delete();
                }
            }
        }
    }
}