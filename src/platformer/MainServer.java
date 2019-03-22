package platformer;

import platformer.connection.NetworkServer;

public class MainServer {
    public static void main(String[] args) {
        // todo - set up server
        // todo - server tells client which segments to update

        MainClient.SERVER = new NetworkServer();
        new Thread(() -> {
            // run server on alt thread
        }).start();

        MainClient.main(null);
    }
}
