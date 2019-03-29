package platformer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import platformer.connection.NetworkClient;
import platformer.connection.packets.PlayerConnectPacket;
import platformer.connection.packets.PlayerDisconnectPacket;
import platformer.world.Location;
import platformer.world.World;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainClient extends Application {
    public static World WORLD;
    public static PlayerEntity PLAYER;
    private static int screenWidth, screenHeight;
    private static NetworkClient client;
    private static Timer timer;

    public Pane root;
    Scene scene;
    public Stage stage = new Stage(StageStyle.DECORATED);

    //TODO - Optimize these variables below:

    //Each key press
    boolean up = false;
    boolean down = false;
    boolean left = false;
    boolean right = false;
    boolean jump = false;
    boolean canJump = true;

    //Each time key is inputted per tick from timer, the increment the shape/object/entity will be moved by
    double yMoveIncrement = 2;
    double xMoveIncrement = 2;

    ArrayList<Shape> shapes;

    Rectangle player;
    Rectangle floor;

    public double leftDistance = 0;
    public double rightDistance = 720;

    public double verticalDistance = 0;

    public double playerX;
    public double playerY;


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter IP: ");
        client = new NetworkClient(scanner.nextLine());

        System.out.println("Enter username: ");
        client.sendPacket(client.getSocket(), new PlayerConnectPacket(scanner.nextLine()));
        // should receive confirmation

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();

        //TODO -Optimize initTest()
        initTest();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (client.isClosed())
                    Platform.exit();
                client.update();
                update(scene, player);
            }
        }, 0, 16L); // every 16 ms is ~60 fps

        //TODO - Optimize runTest()

        primaryStage.setOnCloseRequest(event -> Platform.exit());
    }

    @Override
    public void stop() throws IOException {
        if (!client.isClosed()) {
            client.sendPacket(client.getSocket(), new PlayerDisconnectPacket());
            client.close();
        }
        timer.cancel();
    }

    public static NetworkClient getClient() {
        return client;
    }

    public static Location getScreenLocation() {
        return PLAYER.getLocation();
    }

    public void initTest() {

        shapes = new ArrayList<>();

        root = new Pane();
        scene = new Scene(root);
        player = new Rectangle(50, 50);

    }

    public void runTest() {
        playerY = 200 + verticalDistance;
        playerX = 360 + leftDistance - player.getWidth();

        player.setY(playerY);
        player.setX(playerX);

        floor = new Rectangle(720, 200);
        floor.setX(leftDistance + 300);
        floor.setY(250);
        floor.setFill(Color.GREEN);

        shapes.add(player);
        shapes.add(floor);

        stage.setHeight(480);
        stage.setWidth(720);

        root.getChildren().add(floor);
        root.getChildren().add(player);

        stage.setScene(scene);

        System.out.println("test");
        stage.show();
    }

    public void update(Scene scene, Shape r) {

        getKeyEvents(scene);

        updateKeyEvents(r);

        // updateGravity(r); //TODO - Not sure if we want this to just be a constant increment down if collision on bottom is false

        // updateLocation(); //TODO


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
                    if (canJump == true) {
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
                leftDistance += xMoveIncrement;
        }

        if (left) {

            if (!playerColDetLeft(r))
                leftDistance -= xMoveIncrement;
        }

        if (up) {
            if (!playerColDetTop(r))
                verticalDistance -= yMoveIncrement;
        }

        if (down) {
            if (!playerColDetBottom(r))
                verticalDistance += yMoveIncrement;
        }

        if (jump) {

            System.out.println("Jump");

        }
        jump = false;
    }

    public boolean playerColDetTop(Shape r) {

        for (Shape shape : shapes) {
            if (shape != r) {

                if ((r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMinX() < shape.getBoundsInLocal().getMaxX()) || (r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMaxX() > shape.getBoundsInLocal().getMinX())) {
                    if (r.getBoundsInLocal().getMinY() - yMoveIncrement < shape.getBoundsInLocal().getMaxY() && r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean playerColDetBottom(Shape r) {

        for (Shape shape : shapes) {
            if (shape != r) {

                if ((r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMinX() < shape.getBoundsInLocal().getMaxX()) || (r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMaxX() > shape.getBoundsInLocal().getMinX())) {
                    if ((r.getBoundsInLocal().getMaxY()) + yMoveIncrement > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean playerColDetRight(Shape r) {

        for (Shape shape : shapes) {
            if (shape != r) {
                if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {
                    if (r.getBoundsInLocal().getMaxX() + xMoveIncrement > shape.getBoundsInLocal().getMinX() && r.getBoundsInLocal().getMaxX() < shape.getBoundsInLocal().getMaxX()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean playerColDetLeft(Shape r) {

        for (Shape shape : shapes) {
            if (shape != r) {
                if ((r.getBoundsInLocal().getMinY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMinY() < shape.getBoundsInLocal().getMaxY()) || (r.getBoundsInLocal().getMaxY() > shape.getBoundsInLocal().getMinY() && r.getBoundsInLocal().getMaxY() < shape.getBoundsInLocal().getMaxY())) {

                    if (r.getBoundsInLocal().getMinX() - xMoveIncrement < shape.getBoundsInLocal().getMaxX() && r.getBoundsInLocal().getMinX() > shape.getBoundsInLocal().getMinX()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
