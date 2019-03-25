package platformer;

import platformer.connection.NetworkServer;

import java.io.IOException;

public class MainServer {
    private static final long UPDATE_PERIOD = 50;

    public static void main(String[] args) throws IOException {
        NetworkServer server = new NetworkServer();
        while (!server.isClosed()) {
            long current = System.currentTimeMillis();
            server.update();
            try {
                long remainingTimeForTick = UPDATE_PERIOD - (System.currentTimeMillis() - current);
                if (remainingTimeForTick > 0)
                    Thread.sleep(remainingTimeForTick); // ~ 20 ticks per second
            } catch (InterruptedException ex) {
                // ignore
            }
        }
    }
}