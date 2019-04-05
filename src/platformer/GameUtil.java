package platformer;

import javafx.scene.shape.Rectangle;
import platformer.world.Location;

import java.io.*;

public class GameUtil {
    public static void write(OutputStream out, int val) throws IOException {
        out.write(val);
        out.write(val << 8);
        out.write(val << 16);
        out.write(val << 24);
    }

    public static int readInt(InputStream in) throws IOException {
        return in.read() | in.read() >> 8 | in.read() >> 16 | in.read() >> 24;
    }

    public static void write(OutputStream out, float val) throws IOException {
        write(out, Float.floatToIntBits(val));
    }

    public static float readFloat(InputStream in) throws IOException {
        return Float.intBitsToFloat(readInt(in));
    }

    public static <T> T read(InputStream in) throws IOException, ClassNotFoundException {
        //noinspection unchecked
        return (T) new ObjectInputStream(in).readObject();
    }

    public static void write(OutputStream out, Object val) throws IOException {
        new ObjectOutputStream(out).writeObject(val);
    }

    public static void setRelativeTo(Rectangle rectangle, Location location, double x, double y) {
        rectangle.setX(location.getX() + x);
        rectangle.setY(location.getY() + y);
    }

    private GameUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}
