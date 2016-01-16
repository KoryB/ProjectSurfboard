package framework;

/**
 * Created by Michael on 1/15/2016.
 */
public class Game {

    Screen mActiveScreen;

    public Game(){
        //Starting with a game screen for now until we have the main menu implemented
        mActiveScreen = new GameScreen();
    }

    public void update(){
        mActiveScreen.update();
    }

    public void render(){
        mActiveScreen.render();
    }

    public void run(){
        update();
        render();
    }

    public void setActiveScreen(Screen screen){
        mActiveScreen = screen;
    }
}
