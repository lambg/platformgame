package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PlayerConnectPacket extends Packet {
    private String name;

    public PlayerConnectPacket(String name) {
        if (name.length() > Byte.MAX_VALUE)
            throw new IllegalArgumentException("Length cannot be greater than " + Byte.MAX_VALUE);
        this.name = name;
    }

    // called reflectively
    public PlayerConnectPacket() {
    }

    @Override
    protected void breakdown(OutputStream out) throws IOException {
        out.write(name.length());
        for (char c : name.toCharArray())
            out.write(c);
    }

    @Override
    protected void buildPacket(InputStream in) throws IOException {
        char[] c = new char[in.read()];
        for (int i = 0; i < c.length; i++)
            c[i] = (char) in.read();
        name = new String(c);
    }

    @Override
    protected byte getId() {
        return 7;
    }

    @Override
    public void applyPacket(Communicator communicator) {
        System.out.println("(TEST) Player " + name + " connected.");
    }
}
