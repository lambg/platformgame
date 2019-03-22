package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;

import java.io.InputStream;
import java.io.OutputStream;

public class PlayerDisconnectPacket extends Packet {
    @Override
    protected void breakdown(OutputStream out) {

    }

    @Override
    protected void buildPacket(InputStream in) {

    }

    @Override
    protected int getId() {
        return 6;
    }

    @Override
    public void applyPacket(Communicator communicator) {

    }
}
