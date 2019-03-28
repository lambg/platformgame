package platformer.world.entity;

import platformer.MainServer;
import platformer.connection.packets.ObjMovePacket;

public class PlayerEntity extends LivingEntity {
    private final String name;

    public PlayerEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void update() {

        if (getHealth() == 0) {
            alive = false;
        }

        //TODO - player movement, not sure weather timer with client will be used for keyEvents

        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new ObjMovePacket(getObjectId(), getLocation())));
    }
}
