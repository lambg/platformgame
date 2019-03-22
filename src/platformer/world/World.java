package platformer.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class World {
    public void updateAround(WorldObj obj) {
        // todo - get segments around obj and update
        // todo - make sure multiple updates do not happen for single entity
//        for (WorldSegment segment : MainClient.SERVER.getSegmentsToUpdate(this)) {
//            segment.update();
//        }
    }

    // todo - keep track of world segments
    // todo - when players advance, create new world segments

    public <T extends WorldObj> Collection<T> getNearbyObjects(Class<T> cl, int lx, int ux, int ly, int uy) {
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

    public Collection<WorldSegment> getSegmentsFrom(int lw, int ux) {
        // todo
        return null;
    }

    // todo - shortcuts for world segment (i.e. getTerrainHeight)
    // todo - add entity to world, remove entity to world

}
