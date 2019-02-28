package platformer.world;

import javafx.scene.shape.Rectangle;
import platformer.MainClient;

import java.util.ArrayList;
import java.util.List;

public class WorldSegment {
    public static final int BLOCKS_PER_SEGMENT = 10;
    public static final int TERRAIN_BLOCK_SIZE = 50;
    public static final int WORLD_SEGMENT_SIZE = BLOCKS_PER_SEGMENT * TERRAIN_BLOCK_SIZE;
    private List<WorldObj> objects = new ArrayList<>();
    private Block[] terrainBlocks = new Block[BLOCKS_PER_SEGMENT];
    private int terrainSegmentIndex;

    public WorldSegment(int terrainSegmentIndex) {
        this.terrainSegmentIndex = terrainSegmentIndex;

        // todo - create terrain blocks
    }

    public void update() {
        for (WorldObj obj : objects) {
            obj.update();

            // todo - if the obj's new segment is not this segment, send the obj to the new segment
        }

        for (Block block : terrainBlocks) {
            block.rectangle.setX(getLocalOffset(block.id));
        }
        // todo - update entities in segment
    }

    public List<WorldObj> getObjects() {
        return objects;
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

    private static class Block {
        private Rectangle rectangle;
        private int height;
        private int id;

        public Block(Rectangle rectangle, int height, int id) {
            this.rectangle = rectangle;
            this.height = height;
            this.id = id;
        }
    }

    // todo - create multiple rectangles (representing blocks of ground)
    // todo - update the location of the block depending on the camera location
    // todo - keep track of entities; if entity passes out of range, move the entity into the corresponding segment
    // todo - get ground height (based off rectangle)
}
