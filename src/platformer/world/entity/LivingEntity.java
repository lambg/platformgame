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

import java.util.ArrayList;

public class LivingEntity extends Entity {

    public ArrayList<Entity> entities = new ArrayList<>();

    private static final int DEFAULT_HEALTH = 3;

    public boolean alive;
    private int maxHealth;
    private int currentHealth = 3;
    private transient Rectangle currentHealthBar, totalHealthBar;

    public Rectangle getCurrentHealthBar() {
        return currentHealthBar;
    }

    public Rectangle getTotalHealthBar() {
        return totalHealthBar;
    }

    public void decreaseCurrentHealthBar() {

        currentHealthBar.setWidth(getWidth() - 25);
    }

    public LivingEntity(Location location, World world) {
        super(location, world);
        this.maxHealth = DEFAULT_HEALTH;
        this.currentHealth = maxHealth;
        entities.add(this);
        alive = true;
    }

    @Override
    public void updateDraw() {
        if (totalHealthBar == null) {
            maxHealth = 3;
            currentHealth = maxHealth;
            totalHealthBar = new Rectangle(getWidth(), 10, Color.GREEN);
            currentHealthBar = new Rectangle(getWidth(), 10, Color.RED);
//             = new Rectangle(getWidth(), 10, Color.RED);
            Platform.runLater(() -> {
                MainClient.root.getChildren().add(currentHealthBar);
                MainClient.root.getChildren().add(totalHealthBar);
            });
        }
        super.updateDraw();
        GameUtil.setRelativeTo(totalHealthBar, MainClient.getScreenLocation(), getLocation().getX(), getLocation().getY() + 20);
        GameUtil.setRelativeTo(currentHealthBar, MainClient.getScreenLocation(), getLocation().getX(), getLocation().getY() + 20);
        totalHealthBar.setWidth(getWidth() * (getHealth() / (float) maxHealth));
//        System.out.println(getObject(getObjectId()) + "//" + getWidth() + " * " + (getHealth() + " / " + (float) maxHealth)); // todo - remove trace
    }

    @Override
    protected void remove() {
        super.remove();
        MainClient.root.getChildren().remove(currentHealthBar);
        MainClient.root.getChildren().remove(totalHealthBar);
    }

    public int damageTo(LivingEntity other) {
        return 1;
    }

    public int getHealth() {
        return currentHealth;
    }

    public void increaseHealth(int value) {
        if (currentHealth < maxHealth) {
            currentHealth = Math.min(maxHealth, currentHealth + value);
            MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
        }
    }

    public void setHealth(int value) {
        currentHealth = value;
        // don't send update packet
    }

    public void increaseHealth() {
        increaseHealth(1);
    }

    public void decreaseHealth(int value) {
        if (alive) {
            currentHealth -= value;
            if (currentHealth <= 0) {
                die();
            } else
                MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new EntityHealthModifyPacket(getObjectId(), currentHealth)));
        }
    }

    public void checkDamage() {


    }

    public void die() {
        alive = false;
        Platform.runLater(() -> {
            getWorld().removeObjectFromWorld(this);
            MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new ObjectDeSpawnPacket(getObjectId())));
            onDeath();
        });
    }

    protected void onDeath() {
    }

    public void decreaseHealth() {
        decreaseHealth(1);
    }

    public boolean isAlive() {
        return alive;
    }
}
