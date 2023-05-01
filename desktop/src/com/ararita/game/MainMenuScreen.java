package com.ararita.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Ararita game;

    OrthographicCamera camera;

    /**
     * The MainMenuScreen is created.
     *
     * @param game The game for the Main Menu.
     */
    public MainMenuScreen(final Ararita game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
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
        game.titleFont.draw(game.batch, "ARARITA", 300, 350);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            // game.setScreen(new CityScreen(game));

            game.batch.begin();
            game.normalFont.draw(game.batch, "You touched the screen!!! ", 100, 150);
            game.normalFont.draw(game.batch, "Wow!", 100, 100);
            game.batch.end();

            dispose();
        }
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

    }
}
