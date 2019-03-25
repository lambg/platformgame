package platformer.world;

import javafx.scene.shape.Rectangle;
import platformer.MainClient;

import java.util.*;

public class WorldSegment {
    // amount of segments per block
    public static final int BLOCKS_PER_SEGMENT = 10;
    // size of each block in segment
    public static final int TERRAIN_BLOCK_SIZE = 50;
    // size of each segment
    public static final int WORLD_SEGMENT_SIZE = BLOCKS_PER_SEGMENT * TERRAIN_BLOCK_SIZE;
    private static final List<Tuple> transferredObject = new ArrayList<>();
    private final World owner;
    private Block[] terrainBlocks = new Block[BLOCKS_PER_SEGMENT];
    private int terrainSegmentIndex;
    Set<WorldObj> objects = new HashSet<>();

    public WorldSegment(World owner, int terrainSegmentIndex) {
        this.owner = owner;
        this.terrainSegmentIndex = terrainSegmentIndex;

        for (int i = 0; i < terrainBlocks.length; i++) {
            terrainBlocks[i] = new Block(i);
        }
    }

    static void transferObjects() { // not the most efficient implementation, but application is basic
        for (Tuple transferObj : transferredObject) {
            transferObj.from.objects.remove(transferObj.obj);
            transferObj.to.objects.add(transferObj.obj);
        }
        transferredObject.clear();
    }

    public void update() {
        for (WorldObj obj : objects) {
            obj.update();

            WorldSegment currentSeg = owner.getSegmentAt(obj.getLocation());
            if (currentSeg != this)
                transferredObject.add(new Tuple(obj, this, currentSeg));
        }

        for (Block block : terrainBlocks) {
            block.rectangle.setX(getLocalOffset(block.id)); // todo - x should be set to offset relative to screen
        }
    }

    public Collection<WorldObj> getObjects() {
        return objects;
    }

    public int getLeftPosX() {
        return terrainSegmentIndex * WORLD_SEGMENT_SIZE;
    }

    public int getRightPosX() {
        return (terrainSegmentIndex + 1) * WORLD_SEGMENT_SIZE - 1;
    }

    // localPosX is the pos local to this segment; 0 represents the left corner of this segment, not the left corner of the world
    public double getTerrainHeightAtLocalPos(double localPosX) {
        return getBlockAtLocalPos(localPosX).height;
    }

    private Block getBlockAtLocalPos(double localPosX) {
        int blockIndex = (int) (localPosX % TERRAIN_BLOCK_SIZE);
        if (blockIndex >= terrainBlocks.length)
            throw new IllegalArgumentException("Block index \"" + blockIndex + "\" is out of bounds; localPosX should be relative to left corner of segment");
        return terrainBlocks[blockIndex];
    }

    public static int getSegmentAt(double x) {
        return (int) x / WORLD_SEGMENT_SIZE;
    }

    public static int getCurrentSegment() {
        return getSegmentAt(MainClient.getScreenLocation().getX());
    }

    private double getOffset(int index) {
        return getLocalOffset(index) + MainClient.getScreenLocation().getX();
    }

    private int getLocalOffset(int index) {
        return index * TERRAIN_BLOCK_SIZE;
    }

    private class Block {
        private Rectangle rectangle;
        private int height;
        private int id;

        public Block(int id) {
            this.id = id;

            // todo - generate proper height
            this.height = id == 0 ? 0 : getLeftBlock().height + 1;
            this.rectangle = new Rectangle(getLeftBlockPosX(), 0, TERRAIN_BLOCK_SIZE, height);
        }

        double getLeftBlockPosX() {
            return getLeftPosX() + id * TERRAIN_BLOCK_SIZE;
        }

        double getRightBlockPosX() {
            return getRightPosX() + id * TERRAIN_BLOCK_SIZE;
        }

        Block getLeftBlock() {
            return terrainBlocks[id - 1];
        }

        Block getRightBlock() {
            return terrainBlocks[id + 1];
        }
    }

    private static class Tuple {
        final WorldObj obj;
        final WorldSegment from, to;

        public Tuple(WorldObj obj, WorldSegment from, WorldSegment to) {
            this.obj = obj;
            this.from = from;
            this.to = to;
        }
    }

    // todo - create multiple rectangles (representing blocks of ground)
    // todo - update the location of the block depending on the camera location
    // todo - keep track of entities; if entity passes out of range, move the entity into the corresponding segment
    // todo - get ground height (based off rectangle)
}
