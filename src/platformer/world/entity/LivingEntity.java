package platformer.world.entity;

import platformer.MainServer;
import platformer.connection.packets.EntityHealthModifyPacket;

public class LivingEntity extends Entity {

    private int maxHealth;
    private int currentHealth;

    public LivingEntity(int health) {
        super();
        this.maxHealth = health;
        currentHealth = maxHealth;
    }

    public int getHealth() {
        return currentHealth;
    }

    public void increaseHealth(int value) {
        currentHealth += value;
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
    }

    public void increaseHealth() {
        currentHealth++;
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
    }

    public void decreaseHealth(int value) {
        currentHealth -= value;
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
    }

    public void decreaseHealth() {
        currentHealth--;
        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
    }


}
