package platformer.world.entity;

import platformer.world.Location;
import platformer.world.World;
import platformer.world.WorldObj;

public class Entity extends WorldObj {

    private boolean stationary;
    private boolean damage;

    public Entity(Location location, World world) {
        super(location, world);
    }


}
