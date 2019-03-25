package platformer.connection.packets;

import platformer.connection.Communicator;
import platformer.connection.Packet;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EntityHealthModifyPacket extends Packet {
    private int entityId;
    private float updatedHealth;

    public EntityHealthModifyPacket(int entityId, float updatedHealth) {
        this.entityId = entityId;
        this.updatedHealth = updatedHealth;
    }

    public int getEntityId() {
        return entityId;
    }

    public float getUpdatedHealth() {
        return updatedHealth;
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
