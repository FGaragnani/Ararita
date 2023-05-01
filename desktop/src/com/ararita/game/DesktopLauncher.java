package com.ararita.game;

import com.ararita.game.items.Weapon;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.io.IOException;
import java.util.Map;

public class DesktopLauncher {
    public static void main(String[] arg) throws IOException {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Ararita");
        config.setWindowIcon("Ararita.jpg");
        config.setWindowedMode(800, 480);
        
        //new Lwjgl3Application(new AraritaGame(), config);
    }
}
