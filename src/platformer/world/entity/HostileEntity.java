package platformer.world.entity;

import platformer.world.Location;
import platformer.world.World;

import java.util.Comparator;
import java.util.List;

public class HostileEntity extends LivingEntity {
    private static final byte RETARGET_EVERY = 5;

    private transient Location target;
    private transient byte lastUpdate = -1;
    private double speedModifier;

    public HostileEntity(Location location, World world) {
        super(location, world);

        entities.add(this);
//        System.out.println(entities.size() + "asdasd"); //todo - get rid of this
        speedModifier = Math.random() *0;
    }

    @Override
    public int damageTo(LivingEntity other) {
        return other instanceof PlayerEntity ? super.damageTo(other) : 0;
    }

    @Override
    public void update() {
        super.update();

        if (++lastUpdate % RETARGET_EVERY == 0) {
            lastUpdate = 0;
            List<PlayerEntity> nearbyPlayers = getWorld().getNearbyObjects(getLocation(), PlayerEntity.class, 2000, 2000);
            nearbyPlayers.sort(Comparator.comparingDouble(player -> getLocation().distanceSquared(player.getLocation())));
            if (!nearbyPlayers.isEmpty()) {
                target = nearbyPlayers.get(0).getLocation();
            }
        }

        if (target != null) {
            double distanceNow = target.getX() > getLocation().getX() ? verticalSpeed() * speedModifier : -verticalSpeed() * speedModifier;
            if (getLocation().distanceSquared(target) > distanceNow * distanceNow) {
                getLocation().setX(getLocation().getX() + distanceNow);
                sendLocationPacket();
            }
        }
    }
}
