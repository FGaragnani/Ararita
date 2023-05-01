package com.ararita.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Handles the multiple screens in the game.
 */
public class Ararita extends Game {

    public SpriteBatch batch;
    public BitmapFont titleFont;
    public BitmapFont normalFont;

    public void create() {
        batch = new SpriteBatch();
        normalFont = new BitmapFont(Gdx.files.internal("mainFontWhite.fnt"));
        normalFont.getData().setScale(1.3f, 1.85f);
        titleFont = new BitmapFont(Gdx.files.internal("mainFontWhite.fnt"));
        titleFont.getData().setScale(3.5f, 5.1f);
        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
        batch.dispose();
        normalFont.dispose();
        titleFont.dispose();
    }


}
