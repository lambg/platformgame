package platformer.world;

import platformer.MainServer;
import platformer.connection.packets.ObjectDeSpawnPacket;
import platformer.connection.packets.ObjectSpawnPacket;

import java.util.*;

public class World {
    // how many segments to update on either side of each player
    private static final int UPDATE_SEGMENTS = 5;
    // the size of UPDATE_SEGMENT segments
    private static final double UPDATE_SEGMENT_SIZE = WorldSegment.WORLD_SEGMENT_SIZE * UPDATE_SEGMENTS;
    private List<WorldSegment> positiveSegments = new ArrayList<>();
    private List<WorldSegment> negativeSegments = new ArrayList<>();
    private final Random random;
    private final int seed;

    public World(int seed) {
        random = new Random(seed);
        this.seed = seed;
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
        for (WorldSegment segment : getSegmentsAround(objects))
            segment.update();
        WorldSegment.transferObjects(); // move objects that switched segments over the course of this update
    }

    public <T extends WorldObj> Collection<T> getNearbyObjects(Class<T> cl, double lx, double ux, double ly, double uy) {
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

    public Collection<WorldSegment> getSegmentsFrom(double lx, double ux) {
        List<WorldSegment> segmentList = new ArrayList<>();
        for (double x = lx; x <= ux; x += WorldSegment.WORLD_SEGMENT_SIZE) {
            segmentList.add(getSegmentAt(x));
        }
        return segmentList;
    }

    public WorldSegment getSegmentAt(double x) {
        // get segment list
        List<WorldSegment> segments;
        if (x < 0) {
            x = -x;
            segments = negativeSegments;
        } else segments = positiveSegments;

        // generate missing segments
        int segmentIndex = (int) (x % WorldSegment.WORLD_SEGMENT_SIZE);
        while (segmentIndex >= segments.size()) {
            segments.add(new WorldSegment(this, segments.size()));
        }

        return segments.get(segmentIndex);
    }

    public WorldSegment getSegmentAt(Location location) {
        return getSegmentAt(location.getX());
    }

    public double getTerrainHeightAt(double x) {
        WorldSegment segment = getSegmentAt(x);
        return segment.getTerrainHeightAtLocalPos(x - segment.getLeftPosX());
    }

    public void addObjectToWorld(WorldObj obj) {
        getSegmentAt(obj.getLocation()).objects.remove(obj);

        MainServer.serverUpdate(server -> server.sendPacketToAll(new ObjectSpawnPacket(obj)));
    }

    public void removeObjectFromWorld(WorldObj obj) {
        getSegmentAt(obj.getLocation()).objects.remove(obj);

        MainServer.serverUpdate(server -> server.sendPacketToAll(new ObjectDeSpawnPacket(obj.getObjectId())));
    }
}
