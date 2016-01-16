package framework;

/**
 * Created by Michael on 1/15/2016.
 */
public class Game {

    Screen mActiveScreen;
    Program program;

    public Game(){
        //Starting with a game screen for now until we have the main menu implemented
        mActiveScreen = new GameScreen();
    }

    public void update(){
        mActiveScreen.update();
    }

    public void render(Program program){
        mActiveScreen.render(program);
    }

    public void run(){
        update();
        render(program);
    }

    public void setActiveScreen(Screen screen){
        mActiveScreen = screen;
    }
}
