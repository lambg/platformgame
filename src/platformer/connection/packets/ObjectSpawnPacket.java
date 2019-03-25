package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.WorldObj;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ObjectSpawnPacket extends Packet {
    private WorldObj obj;

    public ObjectSpawnPacket(WorldObj obj) {
        this.obj = obj;
    }

    public WorldObj getObj() {
        return obj;
    }

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
