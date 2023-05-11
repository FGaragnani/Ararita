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

                """
                        Welcome to Ararita!
                        I'm the tutorial, pleased to meet you.
                        The game itself is fairly simple,
                        but some things - if left unexplained -
                        will give you an headache later on.""",

                """
                        First off, character creation. As soon as this tutorial ends,
                        you'll be able to create your first character! Yay!
                        This process is called 'recruiting'. To recruit a character
                        you'll need a name for it (which must be unique!) and a class.
                        A character's class is a character's inclination:
                        is the character a warrior or a magic wielder?
                        Is he good at swinging swords or at shooting arrows?
                        """,

                """
                        There are - to start - only four classes you can choose from.
                        In a little time, however, you'll be able to create your very own!
                        Let's get down to the basics, however.
                        A character has 6 main attributes:
                        STRENGTH: Determines its physical power;
                        INTELLIGENCE: Determines its magical power;
                        VIGOR: The higher it is, the sturdier is your character;
                        AGILITY: Determines how fast your character is;
                        SPIRIT: Magic linked to health;
                        increases also Wind, Light and Water spells' power;
                        ARCANE: Magic linked to damage;
                        increases also Earth, Chaos and Fire spells' power.
                        """,

                """
                        Moreover, a class has other two important elements:
                        PROFICIENCIES and SPELL TYPES.
                        The proficiencies indicate how a certain class is more able in using
                        a certain weapon type than others.
                        For example, Rangers are very good at handling bows.
                        The spell types are the types of spells
                        that a certain character may learn.
                        Black Mage are capable of learning only Fire and Chaos spells.
                        """,

                """
                        Then, after the creation of your first character,
                        you'll get to the so-called 'City':
                        it's a menu where you'll find everything you'll need.
                        There, you can recruit new characters (which is free!),
                        create new classes and spells (which isn't free),
                        go to the shop to buy items (the least free thing),
                        or manage your characters, switching them
                        between the party and the reserve.""",

                """
                        Talking of characters, they have certain characteristics.
                        HP: The health points - if they fall to 0, a character dies!
                        (But it can be brought back to life, don't worry.)
                        MP: The magic points - those are used to cast spells.
                        EXP: Defeating enemies grant you Experience Points!
                        Collect them, and you'll level up!
                        Levelling up implies a stat growth,
                        meaning that you'll character will become stronger!""",

                """
                        Let's talk about spells! They are identified by their name:
                        no two different spells may share the same name.
                        Spells, moreover, have a certain type, as we said before. Those are:
                        Water, Wind, Earth, Fire, Light and Chaos.
                        Spells also have a base power value, ranging from 1 to 5.
                        The higher the value, the higher damage the spell inflicts!
                        Spells also have an MP cost,
                        and they may even inflict status effects!
                        BLINDNESS & PARALYSIS: The enemy may won't attack!
                        BURN & POISON: The enemy will receive passive damage.
                        """,

                """
                        Now you are ready to venture in the world of Ararita!
                        Be aware, however, of the treat imposed by
                        the nefarious kabbalist Ararita himself.
                        Some say that he possess some wonderful treasures,
                        but no one managed to defeat him yet...
                        """

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
