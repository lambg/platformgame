package platformer.world;

import platformer.MainServer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorldObj implements Serializable {
    private static final Map<Integer, WorldObj> objectIdMap = new HashMap<>();
    private Location location;
    private int objectId;
    private boolean spawned;
    private World world;

    public WorldObj(Location location, World world) {
        this.location = location;
        this.world = world;
        this.objectId = createObjectId();
        spawned = true;

        if (objectIdMap.put(objectId, this) != null)
            throw new RuntimeException("Error: given id has already been assigned to another object.");
        world.addObjectToWorld(this);
    }

    // used by ObjectInputStream
    public WorldObj() {
        throw new RuntimeException();
        // todo - add object to objectIdMap
        // this should only ever be called client side
    }

    protected int createObjectId() {
        return MainServer.getServer().getNextObjectId();
    }

    public static WorldObj getObject(int id) {
        WorldObj obj = objectIdMap.get(id);
        if (obj == null)
            throw new IllegalArgumentException("Entity with id " + id + " not found.");
        return obj;
    }

    public int getObjectId() {
        return objectId;
    }

    public void update() {
        // todo
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean spawned() {
        return spawned;
    }

    public void kill() {
        spawned = false;

        world.removeObjectFromWorld(this);
    }
}