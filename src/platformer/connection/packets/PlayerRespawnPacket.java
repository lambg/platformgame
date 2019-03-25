package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.Location;

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

    public int getEntityId() {
        return entityId;
    }

    public Location getReSpawnLocation() {
        return reSpawnLocation;
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
