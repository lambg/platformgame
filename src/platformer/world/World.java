package platformer.world;

import java.util.*;

public class World {
    // how many segments to update on either side of each player
    private static final int UPDATE_SEGMENTS = 5;
    // the size of UPDATE_SEGMENT segments
    private static final double UPDATE_SEGMENT_SIZE = WorldSegment.WORLD_SEGMENT_SIZE * UPDATE_SEGMENTS;
    private List<WorldSegment> positiveSegments = new ArrayList<>();
    private List<WorldSegment> negativeSegments = new ArrayList<>();

    public Collection<WorldSegment> getSegmentsAround(List<? extends WorldObj> objCol) {
        Set<WorldSegment> segments = new HashSet<>();
        for (WorldObj object : objCol) {
            WorldSegment here = getSegmentAt(object.getLocation());
            segments.addAll(getSegmentsFrom(here.getLeftPosX() - UPDATE_SEGMENT_SIZE, here.getRightPosX() + UPDATE_SEGMENT_SIZE));
        }
        return segments;
    }

    public void updateAround(List<? extends WorldObj> objects) {
        for (WorldSegment segment : getSegmentsAround(objects))
            segment.update();
        WorldSegment.transferObjects(); // move objects that switched segments over the course of this update
    }

    // todo - keep track of world segments
    // todo - when players advance, create new world segments

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
    }

    public void removeObjectToWorld(WorldObj obj) {
        getSegmentAt(obj.getLocation()).objects.remove(obj);
    }
}
