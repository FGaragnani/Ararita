package com.ararita.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(60);
        config.setTitle("Ararita");
        config.setWindowIcon("Ararita.jpg");
        config.setWindowedMode(1920, 1080);

        new Lwjgl3Application(new Ararita(), config);
    }
}
