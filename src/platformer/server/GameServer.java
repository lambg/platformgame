package platformer.server;

import platformer.world.World;
import platformer.world.WorldSegment;

public interface GameServer {
    WorldSegment[] getSegmentsToUpdate(World world);

    World createNewWorld(long seed);

    int seed();
}
