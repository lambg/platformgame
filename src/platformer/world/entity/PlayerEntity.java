package platformer.world.entity;

public class PlayerEntity extends LivingEntity {
    private final String name;

    public PlayerEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
