package platformer;

import platformer.connection.NetworkServer;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) throws IOException {
        NetworkServer server = new NetworkServer();
        while (!server.isClosed())
            server.update();
    }
}
