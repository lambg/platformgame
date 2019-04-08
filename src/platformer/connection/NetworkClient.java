package platformer.connection;

import platformer.MainClient;
import platformer.world.WorldSegment;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class NetworkClient extends Communicator implements AutoCloseable {
    private Socket socket;

    public NetworkClient(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        listenTo(socket); // listen to communication sent by server
    }

    public NetworkClient(String ip) throws IOException {
        this(ip, Connection.PORT);
    }

    @Override
    public void update() {
        super.update();

        if (MainClient.PLAYER != null) { // otherwise world has not been loaded yet
            MainClient.WORLD.transferObjects();

            Collection<WorldSegment> localSegments = MainClient.WORLD.getSegmentsAround(Collections.singleton(MainClient.PLAYER));

            for (WorldSegment currentSegment : localSegments) {
                // segments to be shown this update
                currentSegment.updateShapes();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isClosed() {
        return socket == null;
    }

    @Override
    public void close() throws IOException {
        socket.close();

        socket = null;
    }
}
