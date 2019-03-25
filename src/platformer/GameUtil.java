package platformer;

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

    public static <T> T read(InputStream in) throws IOException, ClassNotFoundException {
        //noinspection unchecked
        return (T) new ObjectInputStream(in).readObject();
    }

    public static void write(OutputStream out, Object val) throws IOException {
        new ObjectOutputStream(out).writeObject(val);
    }

    private GameUtil() throws IllegalAccessException {
        throw new IllegalAccessException();
    }
}
