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
        System.out.println("Enter IP: ");
        NetworkClient client = new NetworkClient(new Scanner(System.in).nextLine());
        // todo - receive PLAYER packet
        // todo - receive WORLD packet
        client.sendPacket(client.getSocket(), new PlayerConnectPacket("Kevin"));
        while(!client.isClosed())
            client.update();
    }

    public static Location getScreenLocation() {
        return PLAYER.getLocation();
    }
}
