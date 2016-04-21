package framework;

import framework.drawing.Drawable;

/**
 * Created by Michael on 1/15/2016.
 */
public interface Screen extends Drawable{

    public void update(long dtime); // dtime is the amount of ms passed since last update

    public void show();

    public void hide(Screen nextScreen);

    public void pause();
}

