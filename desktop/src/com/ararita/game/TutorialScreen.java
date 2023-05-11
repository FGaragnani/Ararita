package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import java.io.IOException;
import java.util.List;

public class TutorialScreen implements Screen {

    final Ararita game;
    Stage stage;

    OrthographicCamera camera;
    Skin skin;

    TypingLabel tutorial;
    TextButton confirm;

    int i = 0;
    List<String> texts;

    public TutorialScreen(final Ararita game) {

        /*
            First initializations.
         */

        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        this.skin = new Skin(Gdx.files.internal(game.stylesPath));
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, 1920, 1080);

        texts = List.of(

                "Welcome to Ararita!\n" + "I'm the tutorial, pleased to meet you.\n" + "The game itself is fairly " +
                        "simple,\nbut some things - if left unexplained -\n" + "will give you an headache later on.",

                "First off, character creation. As soon as this tutorial ends,\nyou'll be able to create your " +
                        "first character! Yay!\n" + "This process is called 'recruiting'. To recruit a character\n" + "you'll need a name for it (which must be unique!) and a class.\n" + "A character's class is a character's inclination:\n" + "is the character a warrior or a magic wielder?\n" + "Is he good at swinging swords or at shooting arrows?\n",

                "There are - to start - only four classes you can choose from.\n" + "In a little time, however, " +
                        "you'll be able to create your very own!\n" + "Let's get down to the basics, however.\nA " +
                        "character has 6 main attributes:\n" + "STRENGTH: Determines its physical power;\n" +
                        "INTELLIGENCE: Determines its magical power;\n" + "VIGOR: The higher it is, the sturdier is " +
                        "your character;\n" + "AGILITY: Determines how fast your character is;\n" + "SPIRIT: Magic " +
                        "linked to health;\nincreases also Wind, Light and Water spells' power;\n" + "ARCANE: Magic " +
                        "linked to damage;\nincreases also Earth, Chaos and Fire spells' power.\n",

                "Moreover, a class has other two important elements:\nPROFICIENCIES and SPELL TYPES.\n" + "The " +
                        "proficiencies indicate how a certain class is more able in using\n" + "a certain weapon " +
                        "type than others.\n" + "For example, Rangers are very good at handling bows.\n" + "The spell" +
                        " types are the types of spells\nthat a certain character " + "may learn.\nBlack Mage are " +
                        "capable of learning only Fire and Chaos spells.\n",

                "Then, after the creation of your first character,\nyou'll get to the so-called 'City':\n" + "it's a " +
                        "menu where you'll find everything you'll need.\n" + "There, you can recruit new characters " +
                        "(which is free!),\n" + "create new classes and spells (which isn't free),\n" + "go to the " +
                        "shop to buy items (the least free thing),\n" + "or manage your characters, switching them\nbetween the party and the reserve.",

                "Talking of characters, they have certain characteristics.\n" +
                        "HP: The health points - if they fall to 0, a character dies!\n" +
                        "(But it can be brought back to life, don't worry.)\n" +
                        "MP: The magic points - those are used to cast spells.\n" +
                        "EXP: Defeating enemies grant you Experience Points!\n" +
                        "Collect them, and you'll level up!\nLevelling up implies a stat growth,\n" +
                        "meaning that you'll character will become stronger!",

                "Let's talk about spells! They are identified by their name:\n" +
                        "no two different spells may share the same name.\n" +
                        "Spells, moreover, have a certain type, as we said before. Those are:\n" +
                        "Water, Wind, Earth, Fire, Light and Chaos.\n" +
                        "Spells also have a base power value, ranging from 1 to 5.\n" +
                        "The higher the value, the higher damage the spell inflicts!\n" +
                        "Spells also have an MP cost,\nand they may even inflict status effects!\n" +
                        "BLINDNESS & PARALYSIS: The enemy may won't attack!\n" +
                        "BURN & POISON: The enemy will receive passive damage.\n",

                "Now you are ready to venture in the world of Ararita!\n" +
                        "Be aware, however, of the treat imposed by\nthe nefarious kabbalist Ararita himself.\n" +
                        "Some say that he possess some wonderful treasures,\nbut no one managed to defeat him yet...\n"

        );

        /*
            Setting the TypingLabel and the Button.
         */

        tutorial = new TypingLabel("", skin);
        tutorial.setPosition(100, Gdx.graphics.getHeight() - 200);
        confirm = new TextButton("Continue", game.textButtonStyle);
        confirm.setPosition((Gdx.graphics.getWidth() - confirm.getWidth()) / 2.0f, 100);
        confirm.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!tutorial.hasEnded()){
                    tutorial.skipToTheEnd();
                } else {
                    i++;
                    updateTutorialText();
                }
            }
        });

        /*
            Adding all actors to the stage.
         */

        stage.addActor(tutorial);
        stage.addActor(confirm);
        updateTutorialText();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.end();

        stage.draw();
        tutorial.act(delta);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    public void updateTutorialText(){
        if (i < texts.size()) {
            tutorial.setText("");
            tutorial = new TypingLabel(texts.get(i), skin);
            tutorial.setFontScale(5, 6);
            long otherLines = texts.get(i).chars().filter((ch) -> (ch == '\n')).count();
            tutorial.setPosition(100, Gdx.graphics.getHeight() - 200 - (otherLines - 4) * 35);
            stage.addActor(tutorial);
        } else {
            dispose();
            try {
                game.setScreen(new CharacterCreationScreen(game, true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
