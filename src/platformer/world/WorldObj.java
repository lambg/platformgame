package platformer.world;

import javafx.scene.shape.Shape;
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
    private Shape shape;

    private double width;
    private double height;

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

    public double horizontalSpeed() {
        return 2.0;
    }

    public double verticalSpeed() {
        return 2.0;
    }

    public Shape getShape() {
        return shape;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public void kill() {
        spawned = false;

        world.removeObjectFromWorld(this);
    }

    public void setLocation() {

        this.location = new Location(shape.getBoundsInLocal().getMinX(), shape.getLayoutBounds().getMaxY());

    }

    public boolean playerColDetTop(Shape r) {

        for (Object object : World.getNearbyObjects()) {
            if (shape != r) {

                if ((r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMinX() < shape.getBoundsInLocal().getMaxX()) || (r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMaxX() > shape.getBoundsInLocal().getMinX())) {
                    if (r.getBoundsInLocal().getMinY() - verticalSpeed() < shape.getBoundsInLocal().getMaxY() && r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean playerColDetBottom(Shape r) {

        for (Shape shape : shapes) {
            if (shape != r) {

                if ((r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMinX() < shape.getBoundsInLocal().getMaxX()) || (r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMaxX() > shape.getBoundsInLocal().getMinX())) {
                    if ((r.getBoundsInLocal().getMaxY()) + verticalSpeed() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean playerColDetRight(Shape r) {

        for (Shape shape : shapes) {
            if (shape != r) {
                if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {
                    if (r.getBoundsInLocal().getMaxX() + horizontalSpeed() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean playerColDetLeft(Shape r) {

        for (Shape shape : shapes) {
            if (shape != r) {
                if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {

                    if (r.getBoundsInLocal().getMinX() - horizontalSpeed() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}