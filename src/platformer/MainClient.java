package platformer;

import platformer.server.GameServer;
import platformer.world.Location;
import platformer.world.World;
import platformer.world.entity.PlayerEntity;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainClient {
    public static final int PORT = 12345;
    public static final PlayerEntity PLAYER = new PlayerEntity();
    public static GameServer SERVER;
    public static World WORLD;
    public static AtomicBoolean keepGoing = new AtomicBoolean(true);
    private static int screenWidth, screenHeight;

    public static void main(String[] args) {
        // todo - create server, prompt for user for IP

        // todo - set screen width, set screen height

        new Thread(() -> {
            WORLD = SERVER.createNewWorld(SERVER.seed());
            while (keepGoing.get()) {
                WORLD.update();
            }
        }).start();
    }

    public static Location getScreenLocation() {
        return PLAYER.getLocation();
    }
}
