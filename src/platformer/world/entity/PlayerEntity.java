package platformer.world.entity;

import javafx.scene.Scene;
import javafx.scene.shape.Shape;
import platformer.MainClient;
import platformer.MainServer;
import platformer.connection.packets.ObjMovePacket;
import platformer.world.Location;
import platformer.world.World;

public class PlayerEntity extends LivingEntity {
    private final String name;
    private final int actualId;

    //Each key press
    boolean up = false;
    boolean down = false;
    boolean left = false;
    boolean right = false;
    boolean jump = false;
    boolean canJump = true;

    public double leftDistance = 0;
    public double verticalDistance = 0;

    public double playerX;
    public double playerY;

    public PlayerEntity(Location location, World world, String name, int actualId) {
        super(location, world);
        this.name = name;
        this.actualId = actualId;
    }

    @Override
    protected int createObjectId() {
        return actualId;
    }

    public String getName() {
        return name;
    }

    @Override
    public void update() {

        super.update();

        if (MainClient.PLAYER == this) {

            getKeyEvents(getScene());
            updateKeyEvents(getShape());
            updateLocation(); //TODO


        }

        MainServer.serverUpdate(networkServer -> networkServer.sendPacketToAll(new ObjMovePacket(getObjectId(), getLocation())));
    }

    public void getKeyEvents(Scene scene) {

        scene.setOnKeyPressed(e -> {
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
                    if (canJump) {
                        jump = true;
                        canJump = false;
                    }
                    break;
            }
        });

        scene.setOnKeyReleased(e -> {
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

    public void updateKeyEvents(Shape r) {

        if (right) {

            if (!playerColDetRight(r))
                leftDistance += horizontalSpeed();
        }

        if (left) {

            if (!playerColDetLeft(r))
                leftDistance -= horizontalSpeed();
        }

        if (up) {
            if (!playerColDetTop(r))
                verticalDistance -= verticalSpeed();
        }

        if (down) {
            if (!playerColDetBottom(r))
                verticalDistance += verticalSpeed();
        }

        if (jump) {

            System.out.println("Jump");

        }
        jump = false;
    }

    public void updateLocation() {

        playerY = 200 + verticalSpeed();
        playerX = 360 + leftDistance - getHeight();

        MainClient.PLAYER.setLocation(new Location(playerX, playerY));

    }
}
