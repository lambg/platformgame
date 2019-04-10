package platformer.connection.packets;

import platformer.GameUtil;
import platformer.MainClient;
import platformer.MainServer;
import platformer.connection.Communicator;
import platformer.connection.Packet;
import platformer.world.World;
import platformer.world.WorldObj;
import platformer.world.entity.LivingEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EntityHealthModifyPacket extends Packet {
    private int entityId;
    private int updatedHealth;

    public EntityHealthModifyPacket(int entityId, int updatedHealth) {
        this.entityId = entityId;
        this.updatedHealth = updatedHealth;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getUpdatedHealth() {
        return updatedHealth;
    }

    @Override
    protected void breakdown(OutputStream out) throws IOException {
        GameUtil.write(out, entityId);
        GameUtil.write(out, updatedHealth);
    }

    @Override
    protected void buildPacket(InputStream in) throws IOException {
        entityId = GameUtil.readInt(in);
        updatedHealth = GameUtil.readInt(in);
    }

    @Override
    public void applyPacket(Communicator communicator, Socket socket) {
        LivingEntity entity = ((LivingEntity) WorldObj.getObject(entityId));
        entity.increaseHealth(updatedHealth - entity.getHealth());

        // if the packet is from a client, forward this packet to all clients (excluding the original client)
        MainServer.serverUpdate(s -> s.sendPacketToAll(this, socket));
    }
}
