package framework;

import framework.drawing.Program;

/**
 * Created by Michael on 1/16/2016.
 */
public class PauseScreen implements Screen {

    Screen mPreviousScreen;

    public PauseScreen(Screen previousScreen){
        mPreviousScreen = previousScreen;
    }

    @Override
    public void update() {

    }

    @Override
    public void draw(Program program) {

    }

    @Override
    public void show() {

    }

    @Override
    public void hide(Screen nextScreen) {

    }

    @Override
    public void pause() {

    }
}
