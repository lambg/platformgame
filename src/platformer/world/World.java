package platformer.world;

import platformer.MainClient;

import java.util.Collection;

public class World {
    public void update() {
        for (WorldSegment segment : MainClient.SERVER.getSegmentsToUpdate(this)) {
            segment.update();
        }
    }

    // todo - keep track of world segments
    // todo - when players advance, create new world segments

    public <T extends WorldObj> Collection<T> getNearbyObjects(Class<T> cl, int lx, int ux, int ly, int uy) {
        // todo
    }

    public WorldSegment getNearbySegments(int lw, int ux, int ly, int uy) {
        // todo
    }

    // todo - shortcuts for world segment (i.e. getTerrainHeight)
    // todo - add entity to world, remove entity to world

}
