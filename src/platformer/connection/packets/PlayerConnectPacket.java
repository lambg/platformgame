package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
    public void applyPacket(Communicator communicator, Socket socket) throws IOException {
        System.out.println("(TEST) Player " + name + " connected.");
        PlayerEntity playerEntity = new PlayerEntity(name);
        communicator.sendPacket(socket, new PlayerConfirmConnectPacket(playerEntity.getObjectId()));
    }
}
