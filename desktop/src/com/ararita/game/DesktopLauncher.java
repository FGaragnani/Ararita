package com.ararita.game;

import com.ararita.game.battlers.AbstractBattler;
import com.ararita.game.battlers.PC;
import com.ararita.game.items.ConsumableItem;
import com.ararita.game.items.Item;
import com.ararita.game.items.Weapon;
import com.ararita.game.spells.Spell;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ararita.game.AraritaGame;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) throws IOException {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Ararita");

		/*
		A new PC is created, is added either to the party or to 'other characters', and his file is created.
		An error occurs if its class doesn't exist.
		*/

		/*
		PC test0 = new PC("Carletto Giochetto", "Knight");
		PC test1 = new PC("Ruggero Ruggeri", "Black Mage");
		test1.gainEXP(1000);
		test1.equip(Global.getWeapon("Wooden Sword"));
		Global.updateCharacter(test1);
		PC test2 = Global.getCharacter("Ruggero Ruggeri");
		test1.unequip(Global.getWeapon("Wooden Sword"));

		test1.learnSpell(new Spell("Fireball", 10, "Fire", 1, new HashMap<>()));
		*/

		Global.getCharacter("Ruggero Ruggeri").forgetSpell(Global.getSpell("Fireball"));


		/*

		The character gains EXP and its stats are updated.

		test1.gainEXP(1000);
		Global.updateCharacter(test1);

		Two spells are created; they are immediately saved in the global manager.

		Map<String, Double> statusEffect = new HashMap<String, Double>();
		statusEffect.put("Burn", 0.1);
		Spell test2 = new Spell("Fire I", 10, "Fire", 1, new HashMap<>());
		Spell test3 = new Spell("Fire II", 30, "Fire", 2, statusEffect);
		*/

		/*

		Creates a new item; the item.json file is hence created.



		Map<String, Integer> effect = new HashMap<>();
		effect.put("Arcane", 1);
		Item test4 = new ConsumableItem("Grim Soul", 1000, "Permanently boost a character's arcane.",
				effect);

		ConsumableItem test5 = (ConsumableItem) Global.getItem("Potion");


		 */

		/*

		Map<String, Integer> attributes = new HashMap<>();
		attributes.put("Strength", 40); attributes.put("Agility", -2);
		Item test6 = new Weapon("Phinia", 1000, "A sword that was thought to have been lost in time.",
				attributes, "Sword");

		Global.setMoney(1000);

		if(Global.canBuy(test6)){
			Global.buy(test6);
			Global.sell(test6);
		}

		Global.setMoney(0);

		*/

		//new Lwjgl3Application(new AraritaGame(), config);
	}
}
