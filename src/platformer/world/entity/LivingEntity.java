package platformer.world.entity;

import platformer.MainServer;
import platformer.connection.packets.EntityHealthModifyPacket;
import platformer.connection.packets.ObjectDeSpawnPacket;
import platformer.world.Location;
import platformer.world.World;

public class LivingEntity extends Entity {
    private static final int DEFAULT_HEALTH = 3;

    public boolean alive;
    private int maxHealth;
    private int currentHealth;


    public LivingEntity(Location location, World world, int maxHealth) {
        super(location, world);
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        alive = true;
    }

    public LivingEntity(Location location, World world) {
        this(location, world, DEFAULT_HEALTH);
    }

    public int getHealth() {
        return currentHealth;
    }

    public void increaseHealth(int value) {
        currentHealth += value;
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
    }

    public void increaseHealth() {

        if (currentHealth < maxHealth) {
            currentHealth++;
            MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
        }
    }

    public void decreaseHealth(int value) {
        currentHealth -= value;
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
    }

    public void decreaseHealth() {
        currentHealth--;
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
    }

    public boolean isAlive() {
        return alive;
    }

    public void update() {
        if (getHealth() == 0) {
            alive = false;
        }

        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new ObjectDeSpawnPacket(getObjectId())));
    }


}
