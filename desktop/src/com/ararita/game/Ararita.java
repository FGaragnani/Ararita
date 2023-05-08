package com.ararita.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

/**
 * Handles the multiple screens in the game.
 */
public class Ararita extends Game {

    public SpriteBatch batch;
    public BitmapFont titleFont;
    public BitmapFont bigFont;
    public BitmapFont normalFont;
    public BitmapFont mediumFont;

    public TextButton.TextButtonStyle textButtonStyle;
    public Label.LabelStyle labelStyle;
    public SplitPane.SplitPaneStyle splitPaneStyle;
    public Slider.SliderStyle sliderStyle;
    public TextField.TextFieldStyle textFieldStyle;
    public SelectBox.SelectBoxStyle selectBoxStyle;
    public ImageButton.ImageButtonStyle imageButtonStyle;

    public java.util.List<String> spriteNames;
    public final int spriteFrameCols = 3;
    public final int baseEXP = 100;

    public final String settingsPath = "assets/Settings/settings.json";
    public final String stylesPath = "Pixthulhu/pixthulhu-ui.json";

    Skin skin;

    int volume;
    int soundEffects;

    boolean newPlayer;

    public void create() {

        this.skin = new Skin(Gdx.files.internal(stylesPath));

        JsonValue jsonSettings = new JsonReader().parse(Gdx.files.local(settingsPath));
        volume = jsonSettings.getInt("Volume");
        soundEffects = jsonSettings.getInt("Sound Effects");
        newPlayer = jsonSettings.getBoolean("New");

        batch = new SpriteBatch();

        normalFont = new BitmapFont(Gdx.files.internal("Font/mainFontWhite.fnt"));
        normalFont.getData().setScale(2.7f, 3.65f);

        titleFont = new BitmapFont(Gdx.files.internal("Font/mainFontWhite.fnt"));
        titleFont.getData().setScale(9f, 13f);

        bigFont = new BitmapFont(Gdx.files.internal("Font/mainFontWhite.fnt"));
        bigFont.getData().setScale(7f, 10f);

        mediumFont = new BitmapFont(Gdx.files.internal("Font/mainFontWhite.fnt"));
        mediumFont.getData().setScale(4.8f, 6.75f);

        labelStyle = skin.get("default", Label.LabelStyle.class);
        labelStyle.font = this.normalFont;

        textButtonStyle = skin.get("default", TextButton.TextButtonStyle.class);
        textButtonStyle.font = this.bigFont;

        splitPaneStyle = skin.get("default-horizontal", SplitPane.SplitPaneStyle.class);
        sliderStyle = skin.get("default-horizontal", Slider.SliderStyle.class);

        textFieldStyle = skin.get("default", TextField.TextFieldStyle.class);
        textFieldStyle.font = this.normalFont;

        selectBoxStyle = skin.get("default", SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = this.normalFont;
        selectBoxStyle.listStyle.font = this.normalFont;
        selectBoxStyle.listStyle.selection.setTopHeight(10);

        spriteNames = new ArrayList<>();
        spriteNames.addAll(List.of("Fighter", "Magician", "Healer", "Ninja", "Archer", "Monk"));

        this.setScreen(new MainMenuScreen(this));
    }

    public void render() {
        super.render();
    }

    /**
     * Saves the local values into the JSON settings file.
     */
    public void settingsUpdate() {
        try {
            JSONObject jsonSettings = Global.getJSON(Path.of(settingsPath));
            jsonSettings.put("Volume", volume);
            jsonSettings.put("Sound Effects", soundEffects);
            jsonSettings.put("New", newPlayer);
            Global.writeJSON(Path.of(settingsPath), jsonSettings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dispose() {
        batch.dispose();
        normalFont.dispose();
        titleFont.dispose();
        bigFont.dispose();
    }

    /**
     * Determines a random float number between min and max.
     *
     * @param min The lower bound.
     * @param max The upper bound.
     *
     * @return The random float.
     */
    public float getRandom(float min, float max) {
        RandomGenerator rng = RandomGenerator.getDefault();
        return rng.nextFloat(min, max);
    }
}
