package platformer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import platformer.connection.NetworkClient;
import platformer.connection.packets.PlayerConnectPacket;
import platformer.connection.packets.PlayerDisconnectPacket;
import platformer.world.Location;
import platformer.world.World;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainClient extends Application {
    public static World WORLD;
    public static PlayerEntity PLAYER;
    private static int screenWidth, screenHeight;
    private static NetworkClient client;
    private static Timer timer;

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
