package com.ararita.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
    public TextButton.TextButtonStyle textButtonNormalStyle;
    public Label.LabelStyle labelStyle;
    public SplitPane.SplitPaneStyle splitPaneStyle;
    public Slider.SliderStyle sliderStyle;
    public TextField.TextFieldStyle textFieldStyle;
    public SelectBox.SelectBoxStyle selectBoxStyle;
    public Label.LabelStyle titleStyle;

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
    public final String mainTheme = "Music/MainTheme.mp3";

    public final String backgroundPaper = "Backgrounds/paperbg.png";
    public final String backgroundCity = "Backgrounds/city.png";
    public final String backgroundBattle = "Backgrounds";

    Skin skin;
    Skin skin2;
    Music audio;

    int volume;
    int soundEffects;

    float statScaleX;
    float statScaleY;
    float descScaleX;
    float descScaleY;
    float width200;
    float width300;
    float width400;
    int otherLinesFactor;

    boolean newPlayer;

    public void create() {

        this.skin = new Skin(Gdx.files.internal(stylesPath));
        this.skin2 = new Skin(Gdx.files.internal(stylesPath));

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

        textButtonNormalStyle = skin2.get("default", TextButton.TextButtonStyle.class);
        textButtonNormalStyle.font = this.normalFont;

        splitPaneStyle = skin.get("default-horizontal", SplitPane.SplitPaneStyle.class);
        sliderStyle = skin.get("default-horizontal", Slider.SliderStyle.class);

        textFieldStyle = skin.get("default", TextField.TextFieldStyle.class);
        textFieldStyle.font = this.normalFont;

        titleStyle = skin2.get("default", Label.LabelStyle.class);
        titleStyle.font = this.titleFont;

        selectBoxStyle = skin.get("default", SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = this.normalFont;
        selectBoxStyle.listStyle.font = this.normalFont;
        selectBoxStyle.listStyle.selection.setTopHeight(10);

        spriteNames = new ArrayList<>();
        spriteNames.addAll(List.of("Fighter", "Magician", "Healer", "Ninja", "Archer", "Monk"));

        this.setScreen(new MainMenuScreen(this));

        statScaleX = Gdx.graphics.getWidth() / 686f;
        statScaleY = Gdx.graphics.getHeight() / 284f;
        descScaleX = Gdx.graphics.getWidth() / 640f;
        descScaleY = Gdx.graphics.getHeight() / 257f;

        otherLinesFactor = Gdx.graphics.getHeight() / 79;

        width200 = Gdx.graphics.getWidth() / 9.6f;
        width300 = Gdx.graphics.getWidth() / 6.4f;
        width400 = Gdx.graphics.getWidth() / 4.8f;
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

    @Override
    public void dispose() {
        mediumFont.dispose();
        normalFont.dispose();
        titleFont.dispose();
        bigFont.dispose();
        skin.dispose();
        if (audio != null) {
            audio.dispose();
        }
        skin2.dispose();
    }

    /**
     * If the audio is playing, its volumes is updated.
     */
    public void updateVolume() {
        if (audio != null) {
            audio.setVolume(volume / 1000f);
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
            updateVolume();
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

    public void createTitleCentered(String titleText, float yPos, Color color, Stage stage) {

        Label title = new Label(titleText, titleStyle);
        title.setPosition((Gdx.graphics.getWidth() - title.getWidth()) / 2f, yPos);
        title.setColor(color);
        stage.addActor(title);
    }

    public TextButton createMainButtonXCentered(String buttonText, float yPos, Stage stage) {

        TextButton mainButton = new TextButton(buttonText, textButtonStyle);
        mainButton.setPosition((Gdx.graphics.getWidth() - mainButton.getWidth()) / 2f, yPos);
        stage.addActor(mainButton);
        return mainButton;
    }

    public TextButton createNormalButton(String buttonText, Function<TextButton, Float> xPos, float yPos, Stage stage) {

        TextButton normalButton = new TextButton(buttonText, textButtonNormalStyle);
        normalButton.setPosition(xPos.apply(normalButton), yPos);
        stage.addActor(normalButton);
        return normalButton;
    }

    public void createLabelVoid(String labelText, float xPos, float yPos, Stage stage) {
        createLabel(labelText, xPos, yPos, stage);
    }

    public Label createLabel(String labelText, float xPos, float yPos, Stage stage) {
        Label label = new Label(labelText, labelStyle);
        label.setPosition(xPos, yPos);
        stage.addActor(label);
        return label;
    }

    public Slider createSlider(float min, float max, float stepsize, boolean vertical, float width, float value, float xPos, float yPos, Stage stage) {

        Slider slider = new Slider(min, max, stepsize, vertical, sliderStyle);
        slider.setWidth(width);
        slider.setValue(value);
        slider.setPosition(xPos, yPos);
        stage.addActor(slider);
        return slider;
    }

    public TextField createTextField(String initialText, float width, Function<TextField, Float> xPos, float yPos, Stage stage) {

        TextField textField = new TextField(initialText, textFieldStyle);
        textField.setWidth(width);
        textField.setPosition(xPos.apply(textField), yPos);
        stage.addActor(textField);
        return textField;
    }

    public <T> SelectBox<T> createSelectBox(float width, Function<SelectBox<T>, Float> xPos, float yPos, Stage stage){

        SelectBox<T> selectBox = new SelectBox<>(selectBoxStyle);
        selectBox.setWidth(width);
        selectBox.setPosition(xPos.apply(selectBox), yPos);
        stage.addActor(selectBox);
        return selectBox;
    }

    public Label createStatLabel(String initialText, Color color, float xPos, float yPos, Stage stage){

        Label stat = new Label(initialText, labelStyle);
        stat.setFontScale(statScaleX, statScaleY);
        stat.setColor(color);
        stat.setPosition(xPos, yPos);
        stage.addActor(stat);
        return stat;

    }

}
