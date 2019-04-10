package platformer.world.entity;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import platformer.MainClient;
import platformer.connection.packets.ObjMovePacket;
import platformer.world.Location;
import platformer.world.World;

import java.io.IOException;

public class PlayerEntity extends LivingEntity {
    private final String name;

    //Each key press
    static boolean up = false;
    static boolean down = false;
    static boolean left = false;
    static boolean right = false;
    static boolean jump = false;
    static boolean isJumping = true;
    static boolean shouldJump = false;

    static boolean decreasing = true;
    static boolean x = false;

    public double playerX;
    public double playerY;

    static double currentHeight = 0;
    static double jumpHeight = 0;

    public PlayerEntity(Location location, World world, String name, int objId) {
        super(location, world, objId);
        this.name = name;
        this.setLocation(location);

        playerX = this.getLocation().getX();
        playerY = this.getLocation().getY();
    }

    public static void setKeyListener(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
                case W:
                    up = true;
                    break;
                case A:
                    left = true;
                    break;
                case S:
                    down = true;
                    break;
                case D:
                    right = true;
                    break;
                case SPACE:
                    if (isJumping) {

                        currentHeight = getObject(MainClient.PLAYER_ID).getLocation().getY();
                        jumpHeight = currentHeight + 100;

                        jump = true;
                        isJumping = false;
                    }
                    break;
                case X:

                   // decreaseHealth();
                    break;
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            switch (e.getCode()) {
                case W:
                    up = false;
                    break;
                case A:
                    left = false;
                    break;
                case S:
                    down = false;
                    break;
                case D:
                    right = false;
                    break;
                case SPACE:
                    jump = false;
                    break;
            }
        });
    }

    public String getName() {
        return name;
    }

    @Override
    public void updateDraw() {
        super.updateDraw();

        if (MainClient.PLAYER == this) {
            Location current = new Location(getLocation().getX(), getLocation().getY());
            updateKeyEvents();

            if (!current.equals(getLocation())) {
                try {
                    MainClient.getClient().sendPacket(MainClient.getClient().getSocket(), new ObjMovePacket(getObjectId(), getLocation()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void updateKeyEvents() {
        double horizontalDistance = 0;
        double verticalDistance = 0;

        if (right) {
            if (!playerColDetRight())
                horizontalDistance -= horizontalSpeed();
        }

        if (left) {

            if (!playerColDetLeft())
                horizontalDistance += horizontalSpeed();
        }

        //todo - get rid of this
        if (up) {
            if (!playerColDetTop())
                verticalDistance += verticalSpeed();
        }

        if (down) {
            isJumping = false;
            if (!playerColDetBottom())
                verticalDistance -= verticalSpeed();
        }


        currentHeight = getObject(MainClient.PLAYER_ID).getLocation().getY();
        if (jump) {

            if (!playerColDetTop()) {
                isJumping = true;
                shouldJump = true;
            }
            jump = false;
        }

        if (shouldJump) {
            if (!playerColDetTop()) {
                if (currentHeight < jumpHeight) {
                    verticalDistance += verticalSpeed() * 1.5;
                } else {
                    shouldJump = false;
                }
            }
        }

        //gravity
        verticalDistance -= 3;

        getLocation().setX(getLocation().getX() + horizontalDistance);
        getLocation().setY(getLocation().getY() + verticalDistance);
    }


    @Override
    public String toString() {
        return "PlayerEntity{" + name + "}";
    }
}
