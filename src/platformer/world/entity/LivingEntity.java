package platformer.world.entity;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import platformer.GameUtil;
import platformer.MainClient;
import platformer.MainServer;
import platformer.connection.packets.EntityHealthModifyPacket;
import platformer.connection.packets.ObjectDeSpawnPacket;
import platformer.world.Location;
import platformer.world.World;
import platformer.world.WorldObj;

import java.util.ArrayList;

public class LivingEntity extends Entity {

    public static ArrayList<HostileEntity> entities = new ArrayList<>();
    public static ArrayList<PlayerEntity> players = new ArrayList<>();

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

        alive = true;
    }

    @Override
    public void update() {
        super.update();
        // removes from arraylist if they aren't alive.

//        if (players.size() > 0) {
//            for (LivingEntity e : entities) {
//                if (!e.isAlive()) {
//                    entities.remove(e);
//                }
//            }
//
//        }
        checkDamage();
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

        for (PlayerEntity currentPlayer : players) {
            if (playerColDetBottom(currentPlayer)) {
                System.out.println("Should damage the entity below");
            }
        }
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

    public boolean playerColDetTop(PlayerEntity currentPlayer) {

        Shape r = currentPlayer.getShape();

        for (HostileEntity currentHostile : entities) {

            Shape shape = currentHostile.getShape();

            if (shape != r) {


                if ((r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMinX() < shape.getBoundsInLocal().getMaxX()) || (r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMaxX() > shape.getBoundsInLocal().getMinX())) {
                    if (r.getBoundsInLocal().getMinY() - verticalSpeed() < shape.getBoundsInLocal().getMaxY() && r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean playerColDetBottom(PlayerEntity currentPlayer) {

        Shape r = currentPlayer.getShape();

        for (HostileEntity currentHostile : entities) {

            if ((currentPlayer.getLocation().getX() > currentHostile.getLocation().getX() && currentPlayer.getLocation().getX() < currentHostile.getLocation().getX() + currentHostile.getWidth()) || (currentPlayer.getLocation().getX() + currentPlayer.getWidth() > currentHostile.getLocation().getX() && currentPlayer.getLocation().getX() + currentPlayer.getWidth() < currentHostile.getLocation().getX() + currentHostile.getWidth())) {
//                System.out.println("between an enemy");

                if (currentPlayer.getLocation().getY() - currentPlayer.getHeight() > currentHostile.getLocation().getY()) {
//                    System.out.println("above");
//                    if (currentPlayer.getLocation().getY() - currentPlayer.getHeight() + verticalSpeed() < currentHostile.getLocation().getY()) {
//
//                        return true;
////                        currentHostile.decreaseHealth();
////                        currentHostile.updateDraw();
//                    }
                }
            }
//
//            if ((r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMinX() < shape.getBoundsInLocal().getMaxX()) || (r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMaxX() > shape.getBoundsInLocal().getMinX())) {
//                if ((r.getBoundsInLocal().getMaxY()) + verticalSpeed() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY()) {
//                    System.out.println("AAS");
//                    return true;
//                }
//            }


        }

        return false;
    }

    public boolean playerColDetRight() {

        Shape r = getShape();

        for (WorldObj object : MainClient.WORLD.getNearbyObjects(WorldObj.class, 0, 720, 0, 480)) {

            Shape shape = object.getShape();
            if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {
                if (r.getBoundsInLocal().getMaxX() + horizontalSpeed() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX()) {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean playerColDetLeft() {

        Shape r = getShape();

        for (WorldObj object : MainClient.WORLD.getNearbyObjects(WorldObj.class, 0, 720, 0, 480)) {

            Shape shape = object.getShape();
            if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {

                if (r.getBoundsInLocal().getMinX() - horizontalSpeed() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX()) {
                    return true;
                }
            }
        }

        return false;
    }
}
