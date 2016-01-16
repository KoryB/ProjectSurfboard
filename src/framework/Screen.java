package framework;

/**
 * Created by Michael on 1/15/2016.
 */
public interface Screen {

    public void update();

    public void render(Program program);

    public void show();

    public void hide(Screen nextScreen);

    public void pause();
}
