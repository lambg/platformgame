package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;

import java.io.InputStream;
import java.io.OutputStream;

public class ObjectSpawnPacket extends Packet {
    @Override
    protected void breakdown(OutputStream out) {

    }

    @Override
    protected void buildPacket(InputStream in) {

    }

    @Override
    protected byte getId() {
        return 4;
    }

    @Override
    public void applyPacket(Communicator communicator) {

    }
}
