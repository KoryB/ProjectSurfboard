package framework;

import framework.math3d.vec2;

import java.util.Random;

/**
 * Created by kory on 1/23/16.
 */
public class Util
{
//    public static int WINDOW_HEIGHT = 1080;
//    public static int WINDOW_WIDTH = 1920;

    public static int WINDOW_HEIGHT = 768;
    public static int WINDOW_WIDTH = 1024;
    public static float WINDOW_ASPECT_RATIO = (float) WINDOW_WIDTH / (float) WINDOW_HEIGHT;
    public static int WINDOW_AREA = WINDOW_WIDTH * WINDOW_HEIGHT;

    public static int WINDOW_HALF_HEIGHT = WINDOW_HEIGHT / 2;
    public static int WINDOW_HALF_WIDTH = WINDOW_WIDTH / 2;

    public static float EPSILON = 0.0001f;

    public static Random rand = new Random(System.currentTimeMillis());

    public static double randRange(double min, double max)
    {
        return rand.nextFloat() * (max - min) + min;
    }
}
