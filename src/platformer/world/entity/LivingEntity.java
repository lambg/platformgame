package platformer.world.entity;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import platformer.GameUtil;
import platformer.MainClient;
import platformer.MainServer;
import platformer.connection.packets.EntityHealthModifyPacket;
import platformer.connection.packets.ObjectDeSpawnPacket;
import platformer.world.Location;
import platformer.world.World;

public class LivingEntity extends Entity {
    private static final int DEFAULT_HEALTH = 3;

    public boolean alive;
    private int maxHealth = 3;
    private int currentHealth = 3;
    private transient Rectangle currentHealthBar, totalHealthBar;

    public LivingEntity(Location location, World world, int objId) {
        super(location, world, objId);
        this.maxHealth = DEFAULT_HEALTH;
        alive = true;
    }

    public LivingEntity(Location location, World world) {
        super(location, world);
        this.maxHealth = DEFAULT_HEALTH;
        this.currentHealth = maxHealth;
        alive = true;
    }

    @Override
    public void updateDraw() {
        if (totalHealthBar == null) {

            maxHealth = 3;
            currentHealth = maxHealth;
            totalHealthBar = new Rectangle(getWidth(), 10, Color.GREEN);
            currentHealthBar = new Rectangle(getWidth(), 10, Color.RED);
            Platform.runLater(() -> {
                MainClient.root.getChildren().add(currentHealthBar);
                MainClient.root.getChildren().add(totalHealthBar);
            });
        }
        super.updateDraw();
        GameUtil.setRelativeTo(totalHealthBar, MainClient.getScreenLocation(), getLocation().getX(), getLocation().getY() + 20);
        GameUtil.setRelativeTo(currentHealthBar, MainClient.getScreenLocation(), getLocation().getX(), getLocation().getY() + 20);
        currentHealthBar.setWidth(getWidth() * (getHealth() / (float) maxHealth));
        System.out.println(getObject(getObjectId()) + "//" + getWidth() + " * " + (getHealth() + " / " + (float) maxHealth)); // todo - remove trace
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
        super.update();
        if (getHealth() == 0) {
            alive = false;
            MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new ObjectDeSpawnPacket(getObjectId())));
        }
    }
}
