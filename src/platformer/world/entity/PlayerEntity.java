package platformer.world.entity;

public class PlayerEntity extends LivingEntity {
    private final String name;

    public PlayerEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void update() {

        if (getHealth() == 0) {
            alive = false;
        }

        //TODO - player movement, not sure weather timer with client will be used for keyEvents

        //TODO - send updated ObjMovePacket



    }
}
