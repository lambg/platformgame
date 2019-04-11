package platformer.world.entity;

import platformer.world.Location;
import platformer.world.World;
import platformer.world.WorldObj;

import java.util.List;

public class Entity extends WorldObj {

    private boolean stationary;
    private boolean damage;

    public Entity(Location location, World world, int objId) {
        super(location, world, objId);
    }

    public Entity(Location location, World world) {
        super(location, world);
    }

    @Override
    public void update() {
        getLocation().setY(getLocation().getY() - 3);
        super.update();
    }

    @Override
    public void updateDraw() {
        getLocation().setY(getLocation().getY() - 3);
        super.updateDraw();
    }
}
