package platformer.connection.packets;

import platformer.GameUtil;
import platformer.MainClient;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.Location;
import platformer.world.WorldObj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class PlayerRespawnPacket extends Packet {
    private int entityId;
    private Location reSpawnLocation;

    public PlayerRespawnPacket(int entityId, Location reSpawnLocation) {
        this.entityId = entityId;
        this.reSpawnLocation = reSpawnLocation;
    }

    // called reflectively
    public PlayerRespawnPacket() {
    }

    public int getEntityId() {
        return entityId;
    }

    public Location getReSpawnLocation() {
        return reSpawnLocation;
    }

    @Override
    protected void breakdown(OutputStream out) throws IOException {
        GameUtil.write(out, entityId);
        GameUtil.write(out, reSpawnLocation);
    }

    @Override
    protected void buildPacket(InputStream in) throws IOException, ClassNotFoundException {
        entityId = GameUtil.readInt(in);
        reSpawnLocation = GameUtil.read(in);
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {
        // todo - make sure this client does not send an update packet (move packet) back to server if the server calls this player to re-spawn at a specific location
        MainClient.WORLD.addObjectToWorld(WorldObj.getObject(entityId));
    }
}
