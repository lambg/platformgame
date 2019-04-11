package platformer.world.entity;

import platformer.world.Location;
import platformer.world.World;

import java.util.Comparator;
import java.util.List;

public class HostileEntity extends LivingEntity {
    private static final byte RETARGET_EVERY = 5;

    private transient Location target;
    private transient byte lastUpdate = -1;

    public HostileEntity(Location location, World world, int objId) {
        super(location, world, objId);
    }

    public HostileEntity(Location location, World world) {
        super(location, world);
    }

    @Override
    public int damageTo(LivingEntity other) {
        return other instanceof PlayerEntity ? super.damageTo(other) : 0;
    }

    @Override
    public void update() {

        super.update();
        System.out.println(this.getHealth());

        if (++lastUpdate % RETARGET_EVERY == 0) {
            lastUpdate = 0;
            List<PlayerEntity> nearbyPlayers = getWorld().getNearbyObjects(getLocation(), PlayerEntity.class, 2000, 2000);
            nearbyPlayers.sort(Comparator.comparingDouble(player -> getLocation().distanceSquared(player.getLocation())));
            if (!nearbyPlayers.isEmpty()) {
                target = nearbyPlayers.get(0).getLocation();
            }
        }

        if (target != null) {
//            if(getLocation().distanceSquared(target) > verticalSpeed() * verticalSpeed()) {
            getLocation().setX(getLocation().getX() + (target.getX() > getLocation().getX() ? verticalSpeed() : -verticalSpeed()));
            sendLocationPacket();
//            }
        }
    }
}
