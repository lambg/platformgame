package platformer.world.entity;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import platformer.MainClient;
import platformer.connection.packets.ObjMovePacket;
import platformer.connection.packets.PlayerDisconnectPacket;
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

    static boolean decrease = true;
    static boolean x = false;

    public double playerX;
    public double playerY;

    static double currentHeight = 0;
    static double jumpHeight = 0;

    double currentTime = 0;
    double nextTime = 0;

    public PlayerEntity(Location location, World world, String name) {
        super(location, world);
        this.name = name;
        players.add(this);

        playerX = this.getLocation().getX();
        playerY = this.getLocation().getY();
    }

    public static void setKeyListener(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            switch (e.getCode()) {
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
                case W:
                    if (isJumping) {

                        currentHeight = getObject(MainClient.PLAYER_ID).getLocation().getY();
                        jumpHeight = currentHeight + 250;

                        jump = true;
                        isJumping = false;
                    }
                    break;
                case X:
                    x = true;
                    break;
                case F:
                    up = true;
                    break;
            }
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            switch (e.getCode()) {
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
                case W:
                    jump = false;
                    break;
                case F:
                    up = false;
            }
        });
    }

    public String getName() {
        return name;
    }

    @Override
    public int damageTo(LivingEntity other) {
        return other instanceof HostileEntity ? super.damageTo(other) : 0;
    }

    @Override
    public void updateDraw() {
//        int health = getHealth();
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

            MainClient.setFurthestDistance(getLocation().getX());
        }
//        if (getHealth() != health) {
//            try {
//                MainClient.getClient().sendPacket(MainClient.getClient().getSocket(), new EntityHealthModifyPacket(getObjectId(), getHealth()));
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        if (MainClient.PLAYER == this) {
            MainClient.PLAYER = null;
            MainClient.PLAYER_ID = -1;
            right = false;
            left = false;
            jump = false;
            down = false;

            try {
                MainClient.getClient().sendPacket(MainClient.getClient().getSocket(), new PlayerDisconnectPacket());
                Button button = new Button("Re-spawn");
                button.setOnMouseClicked(click -> {
                    try {
                        MainClient.initScene();
                        MainClient.reconnect();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                Text text = new Text("You died!");
                text.setScaleX(5);
                text.setScaleY(5);
                VBox root = new VBox(50, text, button);
                root.setAlignment(Pos.CENTER);
                Scene deathScene = new Scene(root);
                MainClient.stage.setScene(deathScene);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void updateKeyEvents() {

        double horizontalDistance = 0;
        double verticalDistance = 0;

        currentTime = System.currentTimeMillis();

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

        if (decrease && x) {
            for (HostileEntity entity : getWorld().getNearbyObjects(getLocation(), HostileEntity.class, 1000,1000)) {
                entity.decreaseHealth();
            }
            super.updateDraw();
            decrease = false;
            nextTime = currentTime + 1000;
        }

        currentHeight = getObject(MainClient.PLAYER_ID).getLocation().getY();
        if (jump) {

            if (!playerColDetTop()) {
                isJumping = true;
                shouldJump = true;
            }
            jump = false;
        }

        //todo - don't allow jumping when in the air
        if (shouldJump) {
            if (!playerColDetTop()) {
                if (currentHeight < jumpHeight) {
                    verticalDistance += verticalSpeed() * 1.5;
                } else {
                    shouldJump = false;
                }
            }
        }


        if (currentTime > nextTime) {
            decrease = true;
            x = false;
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
