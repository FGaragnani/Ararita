package com.ararita.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.json.JSONObject;

import java.util.Map;

/**
 * Handles the multiple screens in the game.
 */
public class Ararita extends Game {

    public SpriteBatch batch;
    public BitmapFont titleFont;
    public BitmapFont bigFont;
    public BitmapFont normalFont;

    public void create() {
        batch = new SpriteBatch();

        normalFont = new BitmapFont(Gdx.files.internal("mainFontWhite.fnt"));
        normalFont.getData().setScale(2.6f, 3.5f);

        titleFont = new BitmapFont(Gdx.files.internal("mainFontWhite.fnt"));
        titleFont.getData().setScale(9f, 13f);

        bigFont = new BitmapFont(Gdx.files.internal("mainFontWhite.fnt"));
        bigFont.getData().setScale(7f, 10f);

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render();
    }

    public void dispose() {
        batch.dispose();
        normalFont.dispose();
        titleFont.dispose();
    }

}
