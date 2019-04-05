package platformer.connection.packets;

import platformer.GameUtil;
import platformer.MainClient;
import platformer.MainServer;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.Location;
import platformer.world.World;
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
        toLocation = GameUtil.read(in);
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {
        WorldObj obj = WorldObj.getObject(objectId);
        World world = MainClient.WORLD != null ? MainClient.WORLD : MainServer.getServer().getWorld();

        // if this location update moves the world to another segment, make sure that segment change is reflected appropriately
        world.checkTransfer(obj, () -> obj.setLocation(toLocation));

        // if the packet is from a client, forward this packet to all clients (excluding the original client)
        MainServer.serverUpdate(s -> s.sendPacketToAll(this, socket));
    }
}
