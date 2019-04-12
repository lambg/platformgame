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

    double currentTime = 1;
    double nextTime = 0;

    boolean called = false;

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

        if (players.size() > 0) {
            for (LivingEntity e : entities) {
                if (!e.isAlive()) {
                    entities.remove(e);
                }
            }
        }
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
            checkDamages(currentPlayer);
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

    public void checkDamages(PlayerEntity currentPlayer) {

        currentTime = System.currentTimeMillis();

        boolean playerDeals = false;
        boolean hostileDeals = false;

        for (HostileEntity currentHostile : entities) {

            //Player X on the right side or left side is colliding with the bounds of the currentHostile
            if ((currentPlayer.getLocation().getX() > currentHostile.getLocation().getX() && currentPlayer.getLocation().getX() < currentHostile.getLocation().getX() + currentHostile.getWidth()) || (currentPlayer.getLocation().getX() + currentPlayer.getWidth() > currentHostile.getLocation().getX() && currentPlayer.getLocation().getX() + currentPlayer.getWidth() < currentHostile.getLocation().getX() + currentHostile.getWidth())) {

                //Player Y on the bottom is colliding with the currentHostile
                if (currentPlayer.getLocation().getY() - currentPlayer.getHeight() - verticalSpeed() < currentHostile.getLocation().getY()) {
                    //If the player is on the top ~10% of the currentHostile, it tells the server that the player is dealing damage.
                    //Else, the hostile will deal damage to the player.
                    if (currentPlayer.getLocation().getY() - currentPlayer.getHeight() - verticalSpeed() > currentHostile.getLocation().getY() - currentHostile.getHeight() + 60) {
                        playerDeals = true;
                    } else {
                        hostileDeals = true;
                    }
                }
            }


            // If the player is supposed to deal damage,
            // it will decrease the currentHostile's health by one.
            // Any damage will only be dealt/given by a pair of player and hostile entities once ever 2000 ms, or 2 seconds.

            //TODO - 1. THE COMMAND decreaseHealth() IS CALLED 3 TIMES SIMULTANEOUSLY WHEN EITHER THE PLAYER DEALS OR HOSTILE DEALS DAMAGE. EVEN THOUGH called SHOULD ONLY ALLOW ACCESS TO THE decreaseHealth() ONE TIME.

            //TODO - 2. the runLater() in the decreaseHealth method is broken, so I replaced the decrease health below with die() when something gets hit. but the despawning doesn't work for hostile entities so it's still broken.

            if (playerDeals) {
                if (currentTime >= nextTime) {
                    if (!called) { //todo - get rid of this if, this is just to make sure this only works once.
                        called = true;//todo - get rid of this if, this is just to make sure this only works once.

                        currentHostile.die();

                        System.out.println("Player jumped on hostile entity" + currentHostile.getObjectId());

                    }//todo - get rid of this if, this is just to make sure this only works once.

                    nextTime = currentTime + 2000;
                }

            } else if (hostileDeals) {
                if (currentTime >= nextTime) {
                    if (!called) {//todo - get rid of this if, this is just to make sure this only works once.
                        called = true;//todo - get rid of this if, this is just to make sure this only works once.

                        currentPlayer.die();
                        System.out.println("Hostile" + currentHostile.getObjectId() + " damaged player");

                    }//todo - get rid of this if, this is just to make sure this only works once.

                    nextTime = currentTime + 2000;
                }
            }

            playerDeals = false;
            hostileDeals = false;
        }
    }
}
