package platformer.world.entity;

import platformer.MainServer;
import platformer.connection.packets.ObjectDeSpawnPacket;
import platformer.world.WorldObj;

public class Entity extends WorldObj {

    private boolean stationary;
    private boolean damage;

    public Entity() {
        super();
    }


}
