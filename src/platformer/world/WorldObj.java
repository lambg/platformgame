package platformer.world;

import platformer.MainServer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorldObj implements Serializable {
    private static final Map<Integer, WorldObj> objectIdMap = new HashMap<>();
    private Location location;
    private int objectId;

    public WorldObj(Location location) {
        this.location = location;
        this.objectId = MainServer.getServer().getNextObjectId();

        if (objectIdMap.put(objectId, this) != null)
            throw new RuntimeException("Error: given id has already been assigned to another object.");
    }

    // used by ObjectInputStream
    public WorldObj() {
        // this should only ever be called client side
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
}
