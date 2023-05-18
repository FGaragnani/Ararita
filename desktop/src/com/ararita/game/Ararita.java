package com.ararita.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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

    public java.util.List<String> spriteNames;
    public final int spriteFrameCols = 3;
    public final int baseEXP = 100;

    public final String settingsPath = "Data/Settings/settings.json";
    public final String stylesPath = "Pixthulhu/pixthulhu-ui.json";
    public final String fontPath = "Font/mainFontWhite.fnt";

    public final String spritesPath = "General/msprites.png";
    public final String coinPath = "Icons/coin.png";
    public final String enemyPath = "Enemies/";
    public final String handPath = "General/hand.png";

    public final String cityTheme = "Music/CityTheme.mp3";
    public final String battleTheme = "Music/BattleTheme.mp3";
    public final String fanfareTheme = "Music/Fanfare.mp3";

    public final String backgroundPaper = "Backgrounds/paperbg.png";
    public final String backgroundCity = "Backgrounds/city.png";
    public final String backgroundBattle = "Backgrounds";

    Skin skin;
    Music audio;

    int volume;
    int soundEffects;

    float statScaleX = Gdx.graphics.getWidth() / 686f;
    float statScaleY = Gdx.graphics.getHeight() / 284f;
    int otherLinesFactor = Gdx.graphics.getHeight() / 18;

    boolean newPlayer;

    public void create() {

        this.skin = new Skin(Gdx.files.internal(stylesPath));

        JsonValue jsonSettings = new JsonReader().parse(Gdx.files.local(settingsPath));
        volume = jsonSettings.getInt("Volume");
        soundEffects = jsonSettings.getInt("Sound Effects");
        newPlayer = jsonSettings.getBoolean("New");

        batch = new SpriteBatch();

        normalFont = new BitmapFont(Gdx.files.internal(fontPath));
        normalFont.getData().setScale(Gdx.graphics.getWidth() / 711f, Gdx.graphics.getHeight() / 296f);

        titleFont = new BitmapFont(Gdx.files.internal(fontPath));
        titleFont.getData().setScale(Gdx.graphics.getWidth() / 213f, Gdx.graphics.getHeight() / 83f);

        bigFont = new BitmapFont(Gdx.files.internal(fontPath));
        bigFont.getData().setScale(Gdx.graphics.getWidth() / 274f, Gdx.graphics.getHeight() / 108f);

        mediumFont = new BitmapFont(Gdx.files.internal(fontPath));
        mediumFont.getData().setScale(Gdx.graphics.getWidth() / 400f, Gdx.graphics.getHeight() / 160f);

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
            Path settingsAbsolutePath = Path.of(Gdx.files.internal(settingsPath).path());
            JSONObject jsonSettings = Global.getJSON(settingsAbsolutePath);
            jsonSettings.put("Volume", volume);
            jsonSettings.put("Sound Effects", soundEffects);
            jsonSettings.put("New", newPlayer);
            Global.writeJSON(settingsAbsolutePath, jsonSettings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void dispose() {
        batch.dispose();
        normalFont.dispose();
        titleFont.dispose();
        bigFont.dispose();
        if (audio != null) {
            audio.dispose();
        }
    }

    /**
     * If the audio is not playing, the audio is set playing the specified music.
     *
     * @param music The String containing a path to the music file.
     */
    public void playAudio(String music) {
        if (audio == null) {
            audio = Gdx.audio.newMusic(Gdx.files.local(music));
            audio.setVolume(volume / 1000f);
            audio.setLooping(true);
            audio.play();
        }
    }

    /**
     * If the music is playing, it is stopped.
     */
    public void stopAudio() {
        if (audio != null) {
            audio.dispose();
            audio = null;
        }
    }
}
