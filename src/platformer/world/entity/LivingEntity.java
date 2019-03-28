package platformer.world.entity;

import platformer.MainServer;
import platformer.connection.packets.EntityHealthModifyPacket;
import platformer.connection.packets.ObjectDeSpawnPacket;

public class LivingEntity extends Entity {

    public boolean alive;
    private int DEFAULT_HEALTH = 3;
    private int maxHealth;
    private int currentHealth;

    public LivingEntity() {
        super();
        this.maxHealth = DEFAULT_HEALTH;
        this.currentHealth = DEFAULT_HEALTH;
        this.alive = true;
    }

    public LivingEntity(int health) {
        super();
        this.maxHealth = health;
        this.currentHealth = health;
        this.alive = true;
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
