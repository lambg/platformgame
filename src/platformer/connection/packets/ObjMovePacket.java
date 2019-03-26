package platformer.connection.packets;

import platformer.GameUtil;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.Location;
import platformer.world.WorldObj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ObjMovePacket extends Packet {
    private int objectId;
    private Location toLocation;

    public ObjMovePacket(int objectId, Location toLocation) {
        this.objectId = objectId;
        this.toLocation = toLocation;
    }

    // called reflectively
    public ObjMovePacket() {
    }

    @Override
    protected void breakdown(OutputStream out) throws IOException {
        GameUtil.write(out, objectId);
        GameUtil.write(out, toLocation);
    }

    @Override
    protected void buildPacket(InputStream in) throws IOException, ClassNotFoundException {
        objectId = GameUtil.readInt(in);
        objectId = GameUtil.read(in);
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {
        WorldObj.getObject(objectId).setLocation(toLocation);
    }
}
