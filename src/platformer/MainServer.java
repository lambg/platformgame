package platformer;

import platformer.connection.NetworkServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainServer {
    private static final long UPDATE_PERIOD = 50;
    private static NetworkServer server;
    private static List<Runnable> runLaterList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        server = new NetworkServer();
        while (!server.isClosed()) {
            long current = System.currentTimeMillis();
            server.update();
            for (Runnable runnable : runLaterList) {
                try {
                    runnable.run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            runLaterList.clear();
            try {
                long remainingTimeForTick = UPDATE_PERIOD - (System.currentTimeMillis() - current);
                if (remainingTimeForTick > 0)
                    Thread.sleep(remainingTimeForTick); // ~ 20 ticks per second
            } catch (InterruptedException ex) {
                // ignore
            }
        }
    }

    public static void runLater(Runnable runnable) {
        runLaterList.add(runnable);
    }

    public static NetworkServer getServer() {
        return server;
    }

    public static void serverUpdate(Consumer<NetworkServer> update) {
        if (server != null)
            update.accept(server);
    }
}