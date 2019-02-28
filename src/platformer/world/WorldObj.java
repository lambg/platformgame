package platformer.world;

public class WorldObj {
    private Location location;

    public WorldObj(Location location) {
        this.location = location;
    }

    public void update() {
        // todo
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
