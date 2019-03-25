package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.Location;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ObjMovePacket extends Packet {
    private int objectId;
    private Location toLocation;

    @Override
    protected void breakdown(OutputStream out) {

    }

    @Override
    protected void buildPacket(InputStream in) {

    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {

    }
}
