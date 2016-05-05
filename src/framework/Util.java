package framework;

import framework.math3d.vec2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

/**
 * Created by kory on 1/23/16.
 */
public class Util
{
//    public static int WINDOW_HEIGHT = 1080;
//    public static int WINDOW_WIDTH = 1920;

    public static int WINDOW_WIDTH = 1024;
    public static int WINDOW_HEIGHT = 768;
    public static float WINDOW_ASPECT_RATIO = (float) WINDOW_WIDTH / (float) WINDOW_HEIGHT;
    public static int WINDOW_AREA = WINDOW_WIDTH * WINDOW_HEIGHT;

    public static int WINDOW_HALF_WIDTH = WINDOW_WIDTH / 2;
    public static int WINDOW_HALF_HEIGHT = WINDOW_HEIGHT / 2;

    public static float EPSILON = 0.0001f;

    static Random R = new Random(42);
    public static float randrange(float min, float max){
        float f = R.nextFloat();
        return min + f * (max-min);
    }

    public static byte[] toByteArray(float[] f){
        ByteBuffer b = ByteBuffer.allocate(f.length*4);
        b.order(ByteOrder.nativeOrder());
        b.asFloatBuffer().put(f);
        return b.array();
    }

    public static byte[] toByteArray(int[] f){
        ByteBuffer b = ByteBuffer.allocate(f.length*4);
        b.order(ByteOrder.nativeOrder());
        b.asIntBuffer().put(f);
        return b.array();
    }

    public static float[] toFloatArray(byte[] f){
        ByteBuffer b = ByteBuffer.allocate(f.length);
        b.order(ByteOrder.nativeOrder());
        b.put(f);
        float[] ff = new float[f.length/4];
        b.asFloatBuffer().get(ff);
        return ff;
    }
}
