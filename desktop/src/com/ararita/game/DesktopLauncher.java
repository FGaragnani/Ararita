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
		config.setWindowIcon("Ararita.jpg");

		//new Lwjgl3Application(new AraritaGame(), config);
	}
}
