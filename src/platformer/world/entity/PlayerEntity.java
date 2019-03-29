package platformer.world.entity;

import platformer.MainServer;
import platformer.connection.packets.ObjMovePacket;
import platformer.world.Location;
import platformer.world.World;

public class PlayerEntity extends LivingEntity {
    private final String name;

    public PlayerEntity(Location location, World world, int maxHealth, String name) {
        super(location, world, maxHealth);
        this.name = name;
    }

    public PlayerEntity(Location location, World world, String name) {
        super(location, world);
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
