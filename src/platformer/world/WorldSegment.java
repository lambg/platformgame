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
    private final World world;
    private Block[] terrainBlocks = new Block[BLOCKS_PER_SEGMENT];
    private int terrainSegmentIndex;
    private boolean hidden = false;
    Set<WorldObj> objects = new HashSet<>();

    public WorldSegment(World world, int terrainSegmentIndex) {
        this.world = world;
        this.terrainSegmentIndex = terrainSegmentIndex;

        for (int i = 0; i < terrainBlocks.length; i++) {
            terrainBlocks[i] = new Block(i);
        }

        hideSegment(); // blocks should be invisible by default
    }

    public void updateObjects() {
        for (WorldObj obj : objects) {
            obj.update(); // todo - obj update method should update shape to be relative to screen location

            WorldSegment currentSeg = world.getSegmentAt(obj.getLocation());
            if (currentSeg != this)
                world.transferredObject.add(new World.Tuple(obj, this, currentSeg));
        }
    }

    public void updateShapes() {
        showSegment();
        Location screenLocation = MainClient.PLAYER.getLocation();
        for (Block block : terrainBlocks) {
            block.rectangle.setX(screenLocation.getX() + getLocalOffset(block.id));
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

    public void hideSegment() {
        if (hidden)
            return;

        for (Block block : terrainBlocks) {
            MainClient.root.getChildren().remove(block.rectangle);
        }

        for (WorldObj obj : getObjects()) {
            MainClient.root.getChildren().remove(obj.getShape());
        }

        hidden = true;
    }

    public void showSegment() {
        if (!hidden)
            return;

        for (Block block : terrainBlocks) {
            MainClient.root.getChildren().add(block.rectangle);
        }

        for (WorldObj obj : getObjects()) {
            MainClient.root.getChildren().add(obj.getShape());
        }

        hidden = false;
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
}
