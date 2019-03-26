package platformer.connection.packets;

import platformer.GameUtil;
import platformer.MainClient;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.WorldObj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ObjectDeSpawnPacket extends Packet {
    private int objId;

    public ObjectDeSpawnPacket(int objId) {
        this.objId = objId;
    }

    // called reflectively
    public ObjectDeSpawnPacket() {
    }

    public int getObjId() {
        return objId;
    }

    @Override
    protected void breakdown(OutputStream out) throws IOException {
        GameUtil.write(out, objId);
    }

    @Override
    protected void buildPacket(InputStream in) throws IOException {
        objId = GameUtil.readInt(in);
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {
        MainClient.WORLD.removeObjectFromWorld(WorldObj.getObject(objId));
    }
}
