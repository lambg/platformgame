package platformer;

import platformer.connection.NetworkClient;
import platformer.connection.packets.PlayerConnectPacket;
import platformer.world.Location;
import platformer.world.World;
import platformer.world.entity.PlayerEntity;

import java.io.IOException;
import java.util.Scanner;

public class MainClient {
    public static World WORLD;
    public static PlayerEntity PLAYER;
    private static int screenWidth, screenHeight;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter IP: ");
        NetworkClient client = new NetworkClient(scanner.nextLine());

        System.out.println("Enter username: ");
        client.sendPacket(client.getSocket(), new PlayerConnectPacket(scanner.nextLine()));
        // should receive confirmation
        while(!client.isClosed())
            client.update();
    }

    public static Location getScreenLocation() {
        return PLAYER.getLocation();
    }
}
