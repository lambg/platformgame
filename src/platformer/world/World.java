package platformer.world;

import javafx.application.Platform;
import platformer.MainClient;
import platformer.MainServer;
import platformer.connection.packets.ObjectDeSpawnPacket;
import platformer.connection.packets.ObjectSpawnPacket;
import platformer.world.entity.HostileEntity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class World implements Serializable {
    private static final long serialVersionUID = -5044978860575837034L;
    // how many segments to update on either side of each player
    private static final int UPDATE_SEGMENTS = 5;
    // the size of UPDATE_SEGMENT segments
    private static final double UPDATE_SEGMENT_SIZE = WorldSegment.WORLD_SEGMENT_SIZE * UPDATE_SEGMENTS;
    private List<WorldSegment> positiveSegments = new ArrayList<>();
    private List<WorldSegment> negativeSegments = new ArrayList<>();
    private Random random;
    private int seed;
    private boolean playerDead = false;
    final List<Tuple> transferredObject = new ArrayList<>();

    private double hostileEntityLocationOffset;

    public World(int seed) {
        seed = 0;
        this.seed = seed;
        random = new Random(seed);
        hostileEntityLocationOffset = 300 + Math.random() * 1500;
    }

    private void readObject(ObjectInputStream in) throws IOException {
        seed = in.readInt();
        random = new Random(seed);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(seed);
    }

    public int getSeed() {
        return seed;
    }

    public Random getRandom() {
        // used for generation
        return random;
    }

    public Collection<WorldSegment> getSegmentsAround(Collection<? extends WorldObj> objCol) {
        Set<WorldSegment> segments = new HashSet<>();
        for (WorldObj object : objCol) {
            WorldSegment here = getSegmentAt(object.getLocation());
            segments.addAll(getSegmentsFrom(here.getLeftPosX() - UPDATE_SEGMENT_SIZE, here.getRightPosX() + UPDATE_SEGMENT_SIZE));
        }
        return segments;
    }

    public void updateAround(Collection<? extends WorldObj> objects) {
        for (WorldSegment segment : getSegmentsAround(objects)) {
            segment.updateObjects();
        }

        transferObjects();
    }

    public Collection<WorldSegment> getPositiveLoadedSegments() {
        return positiveSegments;
    }

    public List<WorldSegment> getNegativeLoadedSegments() {
        return negativeSegments;
    }

    public <T extends WorldObj> List<T> getNearbyObjects(Class<T> cl, double lx, double ux, double ly, double uy) {
        List<T> objects = new ArrayList<>();
        for (WorldSegment segment : getSegmentsFrom(lx, ux)) {
            for (WorldObj obj : segment.getObjects()) {
                if (obj.getLocation().inside(lx, ux, ly, uy) && cl.isAssignableFrom(obj.getClass()))
                    //noinspection unchecked
                    objects.add((T) obj);
            }
        }
        return objects;
    }

    public <T extends WorldObj> List<T> getNearbyObjects(Location location, Class<T> cl, double x, double y) {
        return getNearbyObjects(cl, location.getX() - x, location.getX() + x, location.getY() - y, location.getY() + y);
    }

    public void checkTransfer(WorldObj obj, Runnable runnable) {
        WorldSegment segment = getSegmentAt(obj.getLocation());
        runnable.run();
        WorldSegment updated = getSegmentAt(obj.getLocation());
        if (segment != updated) {
            transferredObject.add(new Tuple(obj, segment, updated));
        }
    }

    public void transferObjects() { // not the most efficient implementation, but application is basic
        for (Tuple transferObj : transferredObject) {
            transferObj.from.objects.remove(transferObj.obj);
            transferObj.to.objects.add(transferObj.obj);
        }
        transferredObject.clear();
    }

    public Collection<WorldSegment> getSegmentsFrom(double lx, double ux) {
        List<WorldSegment> segmentList = new ArrayList<>();
        for (double x = lx; x <= ux; x += WorldSegment.WORLD_SEGMENT_SIZE) {
            segmentList.add(getSegmentAt(x));
        }
        return segmentList;
    }

    public WorldSegment getSegmentAt(double x) {
        return getSegmentIndex((int) (x / WorldSegment.WORLD_SEGMENT_SIZE));
    }

    public WorldSegment getSegmentIndex(int segmentIndex) {
        // get segment list
        List<WorldSegment> segments;
        boolean negative = false;
        if (segmentIndex < 0) {
            segmentIndex = -(segmentIndex + 1);
            segments = negativeSegments;
            negative = true;
        } else segments = positiveSegments;

        // generate missing segments

        while (segmentIndex >= segments.size()) {
//            System.out.println("NextSegmentIndex: " + segments.size() + "; negative: " + negative + "; segmentIndex: " + segmentIndex);
//            System.out.println("SIZE: " + "?:" + negative + " " + segments.size() + ";" + segments); // todo - remove trace
            WorldSegment segment = new WorldSegment(this, negative ? -segments.size() - 1 : segments.size());
            segments.add(segment);

            // todo - generate hostile entities
            // generate entities in this segment
            MainServer.serverUpdate(s -> {
                for (int i = 0; i < 3; i++) {
                    new HostileEntity(new Location(segment.getLeftPosX() + hostileEntityLocationOffset + i * 5 , 30), this).addObject();
                }
            });
        }

        return segments.get(segmentIndex);
    }

    public WorldSegment getSegmentAt(Location location) {
        return getSegmentAt(location.getX());
    }

    public double getTerrainWidthAt(double x) {
        WorldSegment segment = getSegmentAt(x);
        return segment.getTerrainWidthAtLocalPos(x - segment.getLeftPosX());
    }

    public double getTerrainHeightAt(double x) {
        WorldSegment segment = getSegmentAt(x);
        return segment.getTerrainHeightAtLocalPos(x - segment.getLeftPosX());
    }

    public void addObjectToWorld(WorldObj obj) {
        getSegmentAt(obj.getLocation()).objects.add(obj);

        MainServer.serverUpdate(server -> server.sendPacketToAll(new ObjectSpawnPacket(obj)));
    }

    public void removeObjectFromWorld(WorldObj obj) {
        getSegmentAt(obj.getLocation()).objects.remove(obj);

        MainServer.serverUpdate(server -> server.sendPacketToAll(new ObjectDeSpawnPacket(obj.getObjectId())));
        if (MainClient.PLAYER != null) {
            Platform.runLater(obj::remove);

            if (MainClient.PLAYER == obj) {
                playerDead = true;
            }
        }
    }

    static class Tuple {
        final WorldObj obj;
        final WorldSegment from, to;

        public Tuple(WorldObj obj, WorldSegment from, WorldSegment to) {
            this.obj = obj;
            this.from = from;
            this.to = to;
        }
    }
}
