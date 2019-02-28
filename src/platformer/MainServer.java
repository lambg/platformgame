package platformer;

import platformer.server.LocalServer;

public class MainServer {
    public static void main(String[] args) {
        // todo - set up server
        // todo - server tells client which segments to update

        MainClient.SERVER = new LocalServer();
        new Thread(() -> {
            // run server on alt thread
        }).start();

        MainClient.main(null);
    }
}
