package platformer.world;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import platformer.GameUtil;
import platformer.MainClient;
import platformer.MainServer;
import platformer.connection.packets.ObjMovePacket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class WorldObj implements Serializable {
    private static final Map<Integer, WorldObj> objectIdMap = new HashMap<>();
    private Location location;
    private int objectId;
    private boolean spawned;
    private World world;
    private Rectangle shape;

    public WorldObj(Location location, World world, int objId) {
        this.location = location;
        this.world = world;
        this.objectId = objId;
        spawned = true;
        shape = null;

        if (objectIdMap.put(objectId, this) != null)
            throw new RuntimeException("Error: given id has already been assigned to another object.");
        world.addObjectToWorld(this);
    }

    public WorldObj(Location location, World world) {
        this(location,world,MainServer.getServer().getNextObjectId());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeDouble(getWidth());
        out.writeDouble(getHeight());
        out.writeObject(location);
        out.writeInt(objectId);
        out.writeBoolean(spawned);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        shape = new Rectangle(in.readDouble(), in.readDouble());
        location = (Location) in.readObject();
        objectId = in.readInt();
        spawned = in.readBoolean();
        world = MainClient.WORLD;
        objectIdMap.put(objectId, this);
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
        // do nothing by default
    }

    // client side update; update shape positions
    public void updateDraw() {
        GameUtil.setRelativeTo(shape, MainClient.getScreenLocation(), location.getX(), location.getY());
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        sendLocationPacket();
    }

    protected void sendLocationPacket() {
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new ObjMovePacket(getObjectId(), getLocation())));
    }

    public boolean spawned() {
        return spawned;
    }

    public double horizontalSpeed() {
        return 4.0;
    }

    public double verticalSpeed() {
        return 20.0;
    }

    public Rectangle getShape() {
        return shape;
    }

    public double getHeight() {
        return 100;
    }

    public double getWidth() {
        return 80;
    }

    public void kill() {
        spawned = false;

        world.removeObjectFromWorld(this);
    }

    public void setLocation() {
        this.location = new Location(shape.getBoundsInLocal().getMinX(), shape.getLayoutBounds().getMaxY());
    }

    public boolean playerColDetTop() {

        Shape r = getShape();

        for (WorldObj object : MainClient.WORLD.getNearbyObjects(WorldObj.class, 0, 720, 0, 480)) {

            Shape shape = object.getShape();

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

    public boolean playerColDetBottom() {

        Shape r = getShape();

        for (WorldObj object : MainClient.WORLD.getNearbyObjects(WorldObj.class, 0, 720, 0, 480)) {

            Shape shape = object.getShape();
            if ((r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMinX() < shape.getBoundsInLocal().getMaxX()) || (r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMaxX() > shape.getBoundsInLocal().getMinX())) {
                if ((r.getBoundsInLocal().getMaxY()) + verticalSpeed() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY()) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean playerColDetRight() {

        Shape r = getShape();

        for (WorldObj object : MainClient.WORLD.getNearbyObjects(WorldObj.class, 0, 720, 0, 480)) {

            Shape shape = object.getShape();
            if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {
                if (r.getBoundsInLocal().getMaxX() + horizontalSpeed() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX()) {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean playerColDetLeft() {

        Shape r = getShape();

        for (WorldObj object : MainClient.WORLD.getNearbyObjects(WorldObj.class, 0, 720, 0, 480)) {

            Shape shape = object.getShape();
            if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {

                if (r.getBoundsInLocal().getMinX() - horizontalSpeed() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX()) {
                    return true;
                }
            }
        }

        return false;
    }

}