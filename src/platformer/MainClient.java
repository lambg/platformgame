package platformer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
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
    public static int PLAYER_ID;
    private static int screenWidth, screenHeight;
    private static NetworkClient client;
    private static Timer timer;

    //Window for application
    public static Pane root;

    //First Scene
    public static Scene scene;
    //First stage
    // public Stage stage = new Stage(StageStyle.DECORATED);

    //TODO - Optimize these variables below:


    //Each time key is inputted per tick from timer, the increment the shape/object/entity will be moved by
    double yMoveIncrement = 2;
    double xMoveIncrement = 2;

    //total arraylist of shapes spawned
    ArrayList<Shape> shapes;

    //player and floor shapes
    Rectangle player;
    Rectangle floor;

    /*
    verticalDistance is the rectangle height, should be able to change to (shape.getBoundsInLocal().getMinY + shape.getBoundsInLocal().getMaxY)/2
    Right now it has nothing to do with the location of shape, it just tells it where to spawn according to the pane
    variable name can change to playerStartingY or something, same for leftDistance. its just the leftmost X value for the shapes spawned in pane. can/should be changed in the future
    */

    public double leftDistance = 0;
    public double verticalDistance = 0;

    //player's x and y value, redundant if leftDistance and verticalDistance are used.


    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter IP: ");
//        client = new NetworkClient(scanner.nextLine());
        client = new NetworkClient("10.200.74.82"); // todo - use scanner instead of inline

        System.out.println("Enter username: ");
        client.sendPacket(client.getSocket(), new PlayerConnectPacket(scanner.nextLine()));
        // should receive confirmation

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        scene = new Scene(root);
        player = new Rectangle(50, 50);

        primaryStage.setHeight(480);
        primaryStage.setWidth(720);

        primaryStage.setScene(scene);
        primaryStage.show();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (client.isClosed())
                    Platform.exit();
                client.update();
            }
        }, 0, 16L); // every 16 ms is ~60 fps

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
}
